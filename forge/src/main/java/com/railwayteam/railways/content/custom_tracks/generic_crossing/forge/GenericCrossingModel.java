package com.railwayteam.railways.content.custom_tracks.generic_crossing.forge;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.mixin_interfaces.IGenericCrossingTrackBE;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@MethodsReturnNonnullByDefault
public class GenericCrossingModel implements BakedModel {

    private final BakedModel wrapped;

    public GenericCrossingModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    private static final ModelProperty<@Nullable Pair<TrackMaterial, TrackShape>> FIRST_PIECE = new ModelProperty<>();
    private static final ModelProperty<@Nullable Pair<TrackMaterial, TrackShape>> SECOND_PIECE = new ModelProperty<>();

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        if (level.getBlockEntity(pos) instanceof IGenericCrossingTrackBE genericCrossing) {
            return modelData.derive()
                .with(FIRST_PIECE, genericCrossing.railways$getFirstCrossingPiece())
                .with(SECOND_PIECE, genericCrossing.railways$getSecondCrossingPiece())
                .build();
        } else {
            return modelData;
        }
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        Pair<TrackMaterial, TrackShape> first;
        Pair<TrackMaterial, TrackShape> second;
        if ((first = data.get(FIRST_PIECE)) != null && (second = data.get(SECOND_PIECE)) != null) {
            return ImmutableList.<BakedQuad>builder()
                .addAll(IGenericCrossingTrackBE.getQuads(first, side, rand))
                .addAll(IGenericCrossingTrackBE.getQuads(second, side, rand))
                .build();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return wrapped.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return wrapped.getParticleIcon(data);
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
