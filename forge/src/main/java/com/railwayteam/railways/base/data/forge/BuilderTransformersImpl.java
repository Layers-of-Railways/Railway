package com.railwayteam.railways.base.data.forge;

import com.railwayteam.railways.content.buffer.forge.BufferModel;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.forge.GenericCrossingModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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
}
