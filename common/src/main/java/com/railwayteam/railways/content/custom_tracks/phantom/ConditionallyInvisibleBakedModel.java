package com.railwayteam.railways.content.custom_tracks.phantom;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class ConditionallyInvisibleBakedModel implements BakedModel {

    protected final BakedModel originalModel;
    protected final Supplier<Boolean> visibleCondition;

    public ConditionallyInvisibleBakedModel(BakedModel originalModel, Supplier<Boolean> visibleCondition) {
        this.originalModel = originalModel;
        this.visibleCondition = visibleCondition;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand) {
        return visibleCondition.get() ? originalModel.getQuads(state, side, rand) : ImmutableList.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return originalModel.isCustomRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }
}
