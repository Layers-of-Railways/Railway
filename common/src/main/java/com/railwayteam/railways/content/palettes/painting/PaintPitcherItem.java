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
import com.railwayteam.railways.mixin.AccessorBlockGetter;
import com.railwayteam.railways.multiloader.fluid.FluidUnits;
import com.railwayteam.railways.registry.CRAdvancements;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRPalettes;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.railwayteam.railways.util.ItemUtils.oppositeHand;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class PaintPitcherItem extends Item {
    public static final int MAX_LEVELS = 8;
    public static final long FLUID_PER_LEVEL = FluidUnits.bucket() / 8;
    protected final PalettesColor color;

    public PaintPitcherItem(Properties properties, PalettesColor color) {
        super(properties);
        this.color = color;
    }

    @ExpectPlatform
    public static PaintPitcherItem create(Properties properties, PalettesColor color) {
        throw new AssertionError();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return color.getDiffuseColor();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float)getLevels(stack) * 13.0F / (float)MAX_LEVELS);
    }

    public PalettesColor getColor() {
        return color;
    }

    public static void usePaint(Player player, InteractionHand hand) {
        if (player.level.isClientSide) return;
        if (player.getAbilities().instabuild) return;

        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof PaintPitcherItem item)) return;

        int levels = item.getLevels(stack) - 1;
        if (levels <= 0) {
            player.setItemInHand(hand, item.copyAsFilledStack(stack, 0));
        } else {
            setLevels(stack, levels);
        }
    }

    private static void setLevels(ItemStack stack, int levels) {
        if (!(stack.getItem() instanceof PaintPitcherItem)) return;
        if (levels <= 0 || levels > MAX_LEVELS) {
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
        levels = Mth.clamp(levels, 0, MAX_LEVELS);
        if (levels == 0) {
            ItemStack stack = CRItems.EMPTY_PAINT_PITCHER.asStack();
            stack.setTag(base.getTag() != null ? base.getTag().copy() : null);
            stack.removeTagKey("FillLevel");
            return stack;
        } else {
            ItemStack stack = new ItemStack(this);
            stack.setTag(base.getTag() != null ? base.getTag().copy() : null);
            setLevels(stack, levels);
            return stack;
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
        if (CRItems.PAINT_BRUSH.isIn(player.getItemInHand(oppositeHand(usedHand))))
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));

        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer serverPlayer)
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);

        if (!level.isClientSide) {
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

    /**
     * Repaints a block at the given position in the level.
     * Returns true if the block was successfully repainted, false otherwise.
     */
    static boolean repaint(Level level, BlockPos pos, BlockState state, PalettesColor color) {
        BlockState newState = CRPalettes.getPaintedState(state, color);
        if (newState == null) return false;

        if (newState.hasProperty(DoorBlock.HALF)) {
            boolean lower = newState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
            BlockPos otherPos = lower ? pos.above() : pos.below();
            BlockState otherNewState = CRPalettes.getPaintedState(level.getBlockState(otherPos), color);
            if (otherNewState == null) return false;

            // To successfully repaint a door, we need to:
            // 1. Clear the top block
            // 2. Set the bottom block to the new state
            // 3. Set the top block to the new state
            if (lower) {
                BlockPos tmp = pos;
                pos = otherPos;
                otherPos = tmp;

                BlockState tmpState = newState;
                newState = otherNewState;
                otherNewState = tmpState;
            }

            level.setBlock(
                pos,
                level.isWaterAt(pos)
                    ? Blocks.WATER.defaultBlockState()
                    : Blocks.AIR.defaultBlockState(),
                Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_IMMEDIATE | Block.UPDATE_ALL
            );

            if (!level.setBlock(otherPos, otherNewState, Block.UPDATE_ALL)) {
                return false;
            }
        }

        return level.setBlock(pos, newState, Block.UPDATE_ALL);
    }

    @SuppressWarnings("ConstantValue") // IntelliJ is hallucinating that the nested loops never terminate
    public void projectilePaint(ItemStack stack, Level level, BlockHitResult hit) {
        if (!(stack.getItem() == this)) return;
        if (level.isClientSide()) return;

        int levels = getLevels(stack);
        if (levels <= 0) return;

        final BlockPos hitPos = hit.getBlockPos();
        final BlockState hitState = level.getBlockState(hitPos);

        final var hitStyle = CRPalettes.getStyleForBlock(hitState.getBlock());
        PalettesColor hitColor = hitStyle == null ? null : hitStyle.getSecond();
        if (hitColor == color) hitColor = null;

        // start by painting the hit block
        if (hitColor != null) {
            if (repaint(level, hitPos, hitState, color)) {
                if (--levels <= 0) {
                    return;
                }
            }
        }

        final BlockPos splashSource = hitPos.relative(hit.getDirection());
        final Vec3 splashSourceVec = splashSource.getCenter();

        List<Pair<BlockPos, BlockState>> paintTargets = new ArrayList<>();

        final int r = 8;
        final int rActual = 4;
        for (int x0 = -r; x0 <= r; x0++) {
            for (int y0 = -r; y0 <= r; y0++) {
                for (int z0 = -r; z0 <= r; z0++) {
                    // Only consider faces of the cube
                    if (!(x0 == -r || x0 == r || y0 == -r || y0 == r || z0 == -r || z0 == r)) continue;

                    float dist = Mth.sqrt((float)(x0 * x0 + y0 * y0 + z0 * z0));
                    float dx = rActual * x0 / dist;
                    float dy = rActual * y0 / dist;
                    float dz = rActual * z0 / dist;

                    BlockPos paintTarget = AccessorBlockGetter.callTraverseBlocks(
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
                    if (paintTarget == null) continue;

                    BlockState state = level.getBlockState(paintTarget);
                    var style = CRPalettes.getStyleForBlock(state.getBlock());

                    if (style == null || style.getSecond() == color) continue;
                    if (hitColor != null && hitColor != style.getSecond()) continue;

                    paintTargets.add(Pair.of(paintTarget, state));
                }
            }
        }

        paintTargets.sort((a, b) -> {
            double distA = a.getFirst().distSqr(hitPos);
            double distB = b.getFirst().distSqr(hitPos);

            int cmp = Double.compare(distA, distB);
            if (cmp != 0) return cmp;

            return Integer.compare(a.hashCode(), b.hashCode());
        });

        for (int i = 0; i < paintTargets.size() && levels > 0; i++) {
            Pair<BlockPos, BlockState> target = paintTargets.get(i);
            BlockPos pos = target.getFirst();
            BlockState state = target.getSecond();

            if (repaint(level, pos, state, color)) {
                levels--;
            }
        }
    }
}
