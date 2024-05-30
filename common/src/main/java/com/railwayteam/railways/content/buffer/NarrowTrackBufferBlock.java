/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.Railways;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NarrowTrackBufferBlock extends WoodVariantTrackBufferBlock {
    public static final EnumProperty<Style> STYLE = EnumProperty.create("style", Style.class);
    public NarrowTrackBufferBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(STYLE, Style.STANDARD));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(STYLE));
    }

    @Override
    protected BlockState getCycledStyle(BlockState originalState, Direction targetedFace) {
        return originalState.cycle(STYLE);
    }

    public enum Style implements StringRepresentable {
        STANDARD("narrow_buffer_stop"),
        MONO("narrow_mono_buffer_stop")
        ;

        private final String model;
        Style(String model) {
            this.model = model;
        }

        public ResourceLocation getModel() {
            return Railways.asResource("block/buffer/" + model);
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
