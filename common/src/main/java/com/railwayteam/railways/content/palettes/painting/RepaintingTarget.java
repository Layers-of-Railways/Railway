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
import com.railwayteam.railways.mixin_interfaces.CopycatDuck;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRPalettes.Styles;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import static com.railwayteam.railways.util.ItemUtils.copyStackData;

public abstract class RepaintingTarget {
    protected final Level level;
    protected final BlockPos pos;
    protected final BlockState state;

    protected RepaintingTarget(Level level, BlockPos pos, BlockState state) {
        this.level = level;
        this.pos = pos;
        this.state = state;
    }

    public BlockPos getPos() {
        return pos;
    }

    public abstract PalettesColor getColor();
    public abstract boolean repaint(PalettesColor color);

    public static @Nullable RepaintingTarget get(Level level, BlockPos pos, BlockState state) {
        if (CRTags.AllBlockTags.PAINTING_BLACKLIST.matches(state)) return null;

        Pair<Styles, PalettesColor> style = CRPalettes.getStyleForBlock(state.getBlock());
        if (style != null) {
            return new Simple(level, pos, state, style);
        }

        if (state.getBlock() instanceof CopycatBlock && level.getBlockEntity(pos) instanceof CopycatBlockEntity cbe) {
            BlockState material = cbe.getMaterial();
            Pair<Styles, PalettesColor> materialStyle = CRPalettes.getStyleForBlock(material.getBlock());
            if (materialStyle != null) {
                return new Copycat(level, pos, state, material, materialStyle, cbe);
            }
        }

        return null;
    }

    protected static class Simple extends RepaintingTarget {
        protected final Pair<Styles, PalettesColor> style;

        protected Simple(Level level, BlockPos pos, BlockState state, Pair<Styles, PalettesColor> style) {
            super(level, pos, state);
            this.style = style;
        }

        @Override
        public PalettesColor getColor() {
            return style.getSecond();
        }

        @Override
        public boolean repaint(PalettesColor color) {
            if (level.isClientSide) return false;

            BlockPos pos = this.pos;

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
    }

    private static class Copycat extends RepaintingTarget {
        protected final BlockState material;
        protected final Pair<Styles, PalettesColor> materialStyle;
        protected final CopycatBlockEntity copycat;

        public Copycat(Level level, BlockPos pos, BlockState state, BlockState material, Pair<Styles, PalettesColor> materialStyle, CopycatBlockEntity copycat) {
            super(level, pos, state);
            this.material = material;
            this.materialStyle = materialStyle;
            this.copycat = copycat;
        }

        @Override
        public PalettesColor getColor() {
            return materialStyle.getSecond();
        }

        @Override
        public boolean repaint(PalettesColor color) {
            if (level.isClientSide) return false;

            BlockState newMaterial = CRPalettes.getPaintedState(material, color);
            if (newMaterial == null) return false;

            // we don't use CopycatBlockEntity#setMaterial here because it does wonky things with the block state
            ((CopycatDuck) copycat).railways$setMaterialSimple(newMaterial);

            ItemStack consumedItem = copycat.getConsumedItem();
            ItemStack newItem = new ItemStack(newMaterial.getBlock().asItem());
            copyStackData(consumedItem, newItem);
            copycat.setConsumedItem(newItem);

            return true;
        }
    }
}