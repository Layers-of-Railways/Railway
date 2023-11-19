package com.railwayteam.railways.base.data.fabric;

import com.railwayteam.railways.content.buffer.WoodVariantTrackBufferBlock;
import com.railwayteam.railways.content.buffer.fabric.WoodenBufferModel;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.fabric.GenericCrossingModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

public class BuilderTransformersImpl {
    public static <B extends GenericCrossingBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> genericCrossing() {
        return b -> b.onRegister(CreateRegistrate.blockModel(() -> GenericCrossingModel::new));
    }

    public static <B extends WoodVariantTrackBufferBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> woodVariantBuffer() {
        return b -> b.onRegister(CreateRegistrate.blockModel(() -> WoodenBufferModel::new));
    }
}
