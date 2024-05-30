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

package com.railwayteam.railways.base.data.forge;

import com.railwayteam.railways.content.buffer.forge.BufferModel;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBarsBlock;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBlock;
import com.railwayteam.railways.content.buffer.headstock.forge.CopycatHeadstockBarsModel;
import com.railwayteam.railways.content.buffer.headstock.forge.CopycatHeadstockModel;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.forge.GenericCrossingModel;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class BuilderTransformersImpl {
    public static <B extends GenericCrossingBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> genericCrossing() {
        return b -> b.onRegister(CreateRegistrate.blockModel(() -> GenericCrossingModel::new));
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> variantBuffer() {
        return b -> b.onRegister(CreateRegistrate.blockModel(() -> BufferModel::new));
    }

    public static <I extends Item, P> NonNullUnaryOperator<ItemBuilder<I, P>> variantBufferItem() {
        return i -> i.onRegister(CreateRegistrate.itemModel(() -> BufferModel::new));
    }

    public static <B extends CopycatHeadstockBarsBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> copycatHeadstockBars() {
        return b -> b
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                .forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(p.models()
                        .getExistingFile(p.modLoc("block/buffer/headstock/copycat_headstock_bars"+
                            (state.getValue(CopycatHeadstockBarsBlock.UPSIDE_DOWN) ? "_upside_down" : "")
                        ))
                    )
                    .rotationY(((int) state.getValue(CopycatHeadstockBarsBlock.FACING).toYRot() + 180) % 360)
                    .build()
                )
            )
            .onRegister(CreateRegistrate.blockModel(() -> CopycatHeadstockBarsModel::new));
    }

    @SuppressWarnings("removal") // Create uses these, I can too
    public static <B extends CopycatHeadstockBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> copycatHeadstock() {
        return b -> b
            .properties(p -> p.noOcclusion()
                .color(MaterialColor.NONE))
            .addLayer(() -> RenderType::solid)
            .addLayer(() -> RenderType::cutout)
            .addLayer(() -> RenderType::cutoutMipped)
            .addLayer(() -> RenderType::translucent)
            .color(() -> CopycatBlock::wrappedColor)
            .onRegister(CreateRegistrate.blockModel(() -> CopycatHeadstockModel::new));
    }

    public static <I extends Item, P> NonNullUnaryOperator<ItemBuilder<I, P>> copycatHeadstockItem() {
        return i -> i
            .color(() -> CopycatHeadstockBlock::wrappedItemColor)
            .onRegister(CreateRegistrate.itemModel(() -> CopycatHeadstockModel::new));
    }
}
