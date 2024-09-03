/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.util;

import com.google.common.collect.ImmutableMap;
import com.simibubi.create.content.trains.track.TrackBlock;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

import static com.simibubi.create.content.trains.track.TrackBlock.HAS_BE;
import static com.simibubi.create.content.trains.track.TrackBlock.SHAPE;
import static com.simibubi.create.foundation.block.ProperWaterloggedBlock.WATERLOGGED;


public class BlockStateUtils {
    /**
     * @param block an instance of TrackBlock
     * @param state reference BlockState
     * @return block with state applied to it
     */
    public static BlockState trackWith(TrackBlock block, BlockState state) {
        return block.defaultBlockState()
                .setValue(SHAPE, state.getValue(SHAPE))
                .setValue(HAS_BE, state.getValue(HAS_BE))
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
    }

    @ExpectPlatform
    public static SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        throw new AssertionError();
    }

    private static final Map<Block, DyeColor> WOOL_MAP = ImmutableMap.<Block, DyeColor>builder()
            .putAll(ImmutableMap.of(
                    Blocks.RED_WOOL, DyeColor.RED,
                    Blocks.ORANGE_WOOL, DyeColor.ORANGE,
                    Blocks.YELLOW_WOOL, DyeColor.YELLOW,
                    Blocks.LIME_WOOL, DyeColor.LIME,
                    Blocks.GREEN_WOOL, DyeColor.GREEN,
                    Blocks.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE,
                    Blocks.CYAN_WOOL, DyeColor.CYAN,
                    Blocks.BLUE_WOOL, DyeColor.BLUE))
            .putAll(ImmutableMap.of(
                    Blocks.PURPLE_WOOL, DyeColor.PURPLE,
                    Blocks.MAGENTA_WOOL, DyeColor.MAGENTA,
                    Blocks.PINK_WOOL, DyeColor.PINK,
                    Blocks.BROWN_WOOL, DyeColor.BROWN,
                    Blocks.BLACK_WOOL, DyeColor.BLACK,
                    Blocks.GRAY_WOOL, DyeColor.GRAY,
                    Blocks.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY,
                    Blocks.WHITE_WOOL, DyeColor.WHITE))
            .build();

    private static final Map<DyeColor, Block> WOOL_MAP_REVERSE = new HashMap<>();

    static {
        for (Map.Entry<Block, DyeColor> entry : WOOL_MAP.entrySet()) {
            WOOL_MAP_REVERSE.put(entry.getValue(), entry.getKey());
        }
    }

    public static DyeColor getWoolColor(Block block) {
        return WOOL_MAP.getOrDefault(block, DyeColor.WHITE);
    }

    public static Block getWoolBlock(DyeColor color) {
        return WOOL_MAP_REVERSE.getOrDefault(color, Blocks.WHITE_WOOL);
    }
}
