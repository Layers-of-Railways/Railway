/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.palettes.painting;

import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.mixin_interfaces.ItemStackDuck;
import com.railwayteam.railways.multiloader.fluid.FluidUnits;
import com.railwayteam.railways.registry.CRAdvancements;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.ItemUtils;
import com.simibubi.create.foundation.utility.Components;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.railwayteam.railways.util.ItemUtils.copyStackData;
import static com.railwayteam.railways.util.ItemUtils.oppositeHand;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class PaintPitcherItem extends Item {
    public static final int MAX_LEVELS = 32;
    public static final long FLUID_PER_LEVEL = FluidUnits.bucket() / 8;
    public static final int LEVELS_PER_CANNON_SHOT = 8;
    protected final @Nullable PalettesColor color;

    public PaintPitcherItem(Properties properties, @Nullable PalettesColor color) {
        super(properties);
        this.color = color;
    }

    @ExpectPlatform
    public static PaintPitcherItem create(Properties properties, @Nullable PalettesColor color) {
        throw new AssertionError();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return color == null ? 0xfffdcb : color.getDiffuseColor();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float)getLevels(stack) * 13.0F / (float)MAX_LEVELS);
    }

    protected boolean railways$shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || newStack.getItem() != oldStack.getItem();
    }

    public @Nullable PalettesColor getColor() {
        return color;
    }

    public static void usePaint(Player player, InteractionHand hand) {
        if (player.level.isClientSide) return;
        if (player.getAbilities().instabuild) return;

        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof PaintPitcherItem item)) return;
        if (ItemUtils.isUnbreakable(stack)) return;

        int levels = item.getLevels(stack) - 1;
        if (levels <= 0) {
            player.setItemInHand(hand, item.copyAsFilledStack(stack, 0));
        } else {
            setLevels(stack, levels);
        }
    }

    private static void setLevels(ItemStack stack, int levels) {
        if (!(stack.getItem() instanceof PaintPitcherItem)) return;
        if (levels <= 0) {
            throw new IllegalArgumentException("Levels must be between 1 and " + MAX_LEVELS);
        }

        var tag = stack.getOrCreateTag();
        tag.putInt("FillLevel", levels);
    }

    public int getLevels(ItemStack stack) {
        if (!(stack.getItem() instanceof PaintPitcherItem)) return 0;

        var tag = stack.getTag();
        if (tag == null || !tag.contains("FillLevel", Tag.TAG_INT))
            return MAX_LEVELS;

        return tag.getInt("FillLevel");
    }

    public long getFluidAmount(ItemStack stack) {
        return getLevels(stack) * FLUID_PER_LEVEL;
    }

    public ItemStack copyAsFilledStack(ItemStack base, int levels) {
        levels = Math.max(levels, 0);
        if (levels == 0) {
            ItemStack stack = CRItems.EMPTY_PAINT_PITCHER.asStack();
            copyStackData(base, stack);
            stack.removeTagKey("FillLevel");
            return stack;
        } else {
            ItemStack stack = new ItemStack(this);
            copyStackData(base, stack);
            setLevels(stack, levels);
            return stack;
        }
    }

    public void setFillInPlace(ItemStack stack, int levels) {
        if (levels <= 0) {
            stack.removeTagKey("FillLevel");
            ((ItemStackDuck) (Object) stack).railways$setItem(CRItems.EMPTY_PAINT_PITCHER.get());
        } else {
            ((ItemStackDuck) (Object) stack).railways$setItem(this);
            setLevels(stack, levels);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 42;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (CRTags.AllItemTags.PAINT_DRINK_BLOCKERS.matches(player.getItemInHand(oppositeHand(usedHand))))
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));

        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer serverPlayer)
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);

        if (!level.isClientSide && color != null) {
            int levels = getLevels(stack);
            livingEntity.addEffect(new MobEffectInstance(
                MobEffects.POISON, levels * 20, 0,
                false, true, true
            ));

            if (livingEntity instanceof ServerPlayer player)
                CRAdvancements.STRANGE_TEA.awardTo(player);
        }

        if (livingEntity instanceof Player player && player.getAbilities().instabuild) {
            return stack;
        }

        return copyAsFilledStack(stack, 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        int levels = getLevels(stack);
        tooltipComponents.add(Components.translatable("item.railways.paint_pitcher.paint_level", levels, MAX_LEVELS));
    }

    @SuppressWarnings("ConstantValue") // IntelliJ is hallucinating that the nested loops never terminate
    public void projectilePaint(ItemStack stack, Level level, BlockHitResult hit) {
        final PalettesColor color = this.color == null ? PalettesColor.NETHERITE : this.color;

        if (!(stack.getItem() == this)) return;
        if (level.isClientSide()) return;

        int levels = getLevels(stack);
        if (levels <= 0) return;

        final BlockPos hitPos = hit.getBlockPos();
        final BlockState hitState = level.getBlockState(hitPos);

        final var hitTarget = RepaintingTarget.get(level, hitPos, hitState);
        PalettesColor hitColor = hitTarget == null ? null : hitTarget.getColor();
        if (hitColor == color) hitColor = null;

        // start by painting the hit block
        if (hitColor != null) {
            if (hitTarget.repaint(color)) {
                if (--levels <= 0) {
                    return;
                }
            }
        }

        final BlockPos splashSource = hitPos.relative(hit.getDirection());
        final Vec3 splashSourceVec = splashSource.getCenter();

        List<RepaintingTarget> paintTargets = new ArrayList<>();

        final int r = 16;
        final int rActual = 5;
        for (int x0 = -r; x0 <= r; x0++) {
            for (int y0 = -r; y0 <= r; y0++) {
                for (int z0 = -r; z0 <= r; z0++) {
                    // Only consider faces of the cube
                    if (!(x0 == -r || x0 == r || y0 == -r || y0 == r || z0 == -r || z0 == r)) continue;

                    float dist = Mth.sqrt((float)(x0 * x0 + y0 * y0 + z0 * z0));
                    float dx = rActual * x0 / dist;
                    float dy = rActual * y0 / dist;
                    float dz = rActual * z0 / dist;

                    BlockPos paintTargetPos = BlockGetter.traverseBlocks(
                        splashSourceVec,
                        splashSourceVec.add(dx, dy, dz),
                        level,
                        (lvl, pos) -> {
                            if (!lvl.getBlockState(pos).canBeReplaced()) {
                                return pos;
                            } else {
                                return null;
                            }
                        },
                        ($) -> null
                    );
                    if (paintTargetPos == null) continue;

                    BlockState state = level.getBlockState(paintTargetPos);
                    RepaintingTarget paintTarget = RepaintingTarget.get(level, paintTargetPos, state);

                    if (paintTarget == null || paintTarget.getColor() == color) continue;
                    if (hitColor != null && hitColor != paintTarget.getColor()) continue;

                    paintTargets.add(paintTarget);
                }
            }
        }

        paintTargets.sort((a, b) -> {
            double distA = a.getPos().distSqr(hitPos);
            double distB = b.getPos().distSqr(hitPos);

            int cmp = Double.compare(distA, distB);
            if (cmp != 0) return cmp;

            return Integer.compare(a.hashCode(), b.hashCode());
        });

        for (int i = 0; i < paintTargets.size() && levels > 0; i++) {
            if (paintTargets.get(i).repaint(color)) {
                levels--;
            }
        }
    }
}
