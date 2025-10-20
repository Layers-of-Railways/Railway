/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import net.createmod.catnip.data.Pair;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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

    @OnlyIn(Dist.CLIENT)
    static @NotNull BakedModel getModel(@NotNull Pair<TrackMaterial, TrackShape> data) {
        return getModel(data.getFirst(), data.getSecond());
    }

    @OnlyIn(Dist.CLIENT)
    static @NotNull BakedModel getModel(@NotNull TrackMaterial material, @NotNull TrackShape shape) {
        TrackBlock track = material.getBlock();
        return Minecraft.getInstance().getModelManager().getBlockModelShaper()
            .getBlockModel(track.defaultBlockState()
                .setValue(TrackBlock.SHAPE, shape)
            );
    }

    @OnlyIn(Dist.CLIENT)
    static @NotNull List<BakedQuad> getQuads(@NotNull Pair<TrackMaterial, TrackShape> data, @Nullable Direction side, @NotNull RandomSource rand) {
        return getQuads(data.getFirst(), data.getSecond(), side, rand);
    }

    @OnlyIn(Dist.CLIENT)
    static @NotNull List<BakedQuad> getQuads(@NotNull TrackMaterial material, @NotNull TrackShape shape, @Nullable Direction side, @NotNull RandomSource rand) {
        BakedModel model = getModel(material, shape);
        return model.getQuads(material.getBlock().defaultBlockState().setValue(TrackBlock.SHAPE, shape), side, rand);
    }
}
