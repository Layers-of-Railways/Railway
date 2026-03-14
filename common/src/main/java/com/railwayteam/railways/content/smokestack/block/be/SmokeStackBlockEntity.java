/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2026 The Railways Team
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

package com.railwayteam.railways.content.smokestack.block.be;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.smokestack.SmokeEmissionParams;
import com.railwayteam.railways.content.smokestack.block.SmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.variable.SmokeStackExtenderBlock;
import com.railwayteam.railways.content.smokestack.block.variable.VariableSmokeStackBlock;
import com.railwayteam.railways.util.ColorUtils;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmokeStackBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    protected @Nullable DyeColor color = null;
    protected boolean isSoul = false;
    protected int height = 0;

    public SmokeStackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    public @Nullable DyeColor getColor() {
        return color;
    }

    public void setColor(@Nullable DyeColor color) {
        if (this.color == color) return;
        this.color = color;
        this.isSoul = false;
        notifyUpdate();
    }

    public boolean isSoul() {
        return color == null && isSoul;
    }

    public void setSoul(boolean isSoul) {
        if (this.isSoul == isSoul) return;
        this.color = null;
        this.isSoul = isSoul;
        notifyUpdate();
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("color", Tag.TAG_INT)) {
            int colorOrdinal = tag.getInt("color");
            color = DyeColor.byId(colorOrdinal);
        } else {
            color = null;
        }
        isSoul = tag.getBoolean("isSoul");

        height = Math.max(0, tag.getInt("height"));
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (color != null) {
            tag.putInt("color", color.getId());
        }
        tag.putBoolean("isSoul", isSoul());

        if (height > 0) {
            tag.putInt("height", height);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (isSoul) {
            Lang.builder(Railways.MOD_ID)
                .translate("smokestack.goggle.tooltip", Component.translatable("railways.smokestack.goggle.tooltip.style.soul"))
                .forGoggles(tooltip);
        } else {
            DyeColor color = this.color != null ? this.color : DyeColor.BLACK;
            Lang.builder(Railways.MOD_ID)
                .translate("smokestack.goggle.tooltip.color", Component.translatable("color.minecraft." + color.getName()))
                .forGoggles(tooltip);
        }

        return true;
    }

    @Override
    public ItemStack getIcon(boolean isPlayerSneaking) {
        if (color != null)
            return ColorUtils.getDyeColorDyeItem(color).getDefaultInstance();

        return isSoul ? Items.SOUL_SOIL.getDefaultInstance() : Items.BLACK_DYE.getDefaultInstance();
    }

    private void setHeight(int height) {
        if (this.height == height) return;
        this.height = height;
        notifyUpdate();
    }

    public void updateHeight() {
        if (level == null) return;

        if (!(getBlockState().getBlock() instanceof VariableSmokeStackBlock baseBlock)) {
            setHeight(0);
            return;
        }

        switch (getBlockState().getValue(baseBlock.partProperty())) {
            case SINGLE -> {
                setHeight(0);
                return;
            }
            case DOUBLE -> {
                setHeight(1);
                return;
            }
        }

        SmokeStackExtenderBlock extenderBlock = baseBlock.extenderBlock();
        MutableBlockPos currentPos = getBlockPos().mutable().move(Direction.UP);
        int newHeight;
        Loop: for (newHeight = 1; newHeight < 24; newHeight += 2) {
            BlockState state = level.getBlockState(currentPos);
            if (!state.is(extenderBlock)) break;

            switch (state.getValue(extenderBlock.partProperty())) {
                case SINGLE -> {
                    newHeight += 1;
                    break Loop;
                }
                case DOUBLE -> {
                    newHeight += 2;
                    break Loop;
                }
            }

            currentPos.move(Direction.UP);
        }

        setHeight(newHeight);
    }

    public static double getHeightOffset(int height) {
        // odd height is a double stack
        int fullBlocks = height / 2;
        return fullBlocks + (height % 2 == 1 ? 0.5 : 0);
    }

    protected void animateTick() {
        if (level == null || !level.isClientSide) return;

        BlockState state = getBlockState();
        RandomSource random = level.getRandom();
        SmokeStackBlock block = ((SmokeStackBlock) state.getBlock());

        if (!state.getValue(SmokeStackBlock.ENABLED)) return;
        if (!block.createsStationarySmoke) return;
        if (random.nextFloat() >= 0.11f) return;

        double heightOffset = getHeightOffset(height);

        SmokeEmissionParams emissionParams = block.emissionParams;
        if (random.nextFloat() < emissionParams.particleSpawnChance() * 1.5) {
            int n = random.nextInt((emissionParams.maxParticles() - emissionParams.minParticles())) + emissionParams.minParticles();
            for (int i = 0; i < n; ++i) {
                emissionParams.makeParticles(
                    level, Vec3.atLowerCornerOf(getBlockPos()).add(0, heightOffset, 0),
                    random.nextBoolean(),
                    1.0, true,
                    getColor(), null, isSoul()
                );
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        animateTick();
    }
}
