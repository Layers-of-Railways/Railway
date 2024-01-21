package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.utility.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IGenericCrossingTrackBE {
    @Nullable
    Pair<TrackMaterial, TrackShape> railways$getFirstCrossingPiece();
    @Nullable
    Pair<TrackMaterial, TrackShape> railways$getSecondCrossingPiece();

    @Environment(EnvType.CLIENT)
    static @NotNull BakedModel getModel(@NotNull Pair<TrackMaterial, TrackShape> data) {
        return getModel(data.getFirst(), data.getSecond());
    }

    @Environment(EnvType.CLIENT)
    static @NotNull BakedModel getModel(@NotNull TrackMaterial material, @NotNull TrackShape shape) {
        TrackBlock track = material.getBlock();
        return Minecraft.getInstance().getModelManager().getBlockModelShaper()
            .getBlockModel(track.defaultBlockState()
                .setValue(TrackBlock.SHAPE, shape)
            );
    }

    @Environment(EnvType.CLIENT)
    static @NotNull List<BakedQuad> getQuads(@NotNull Pair<TrackMaterial, TrackShape> data, @Nullable Direction side, @NotNull RandomSource rand) {
        return getQuads(data.getFirst(), data.getSecond(), side, rand);
    }

    @Environment(EnvType.CLIENT)
    static @NotNull List<BakedQuad> getQuads(@NotNull TrackMaterial material, @NotNull TrackShape shape, @Nullable Direction side, @NotNull RandomSource rand) {
        BakedModel model = getModel(material, shape);
        return model.getQuads(material.getBlock().defaultBlockState().setValue(TrackBlock.SHAPE, shape), side, rand);
    }
}
