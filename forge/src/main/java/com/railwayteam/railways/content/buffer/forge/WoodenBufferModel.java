package com.railwayteam.railways.content.buffer.forge;

import com.railwayteam.railways.content.buffer.WoodVariantTrackBufferBlockEntity;
import com.simibubi.create.foundation.model.BakedModelHelper;
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

import static com.railwayteam.railways.content.buffer.WoodenBufferUtils.getSwapper;

@MethodsReturnNonnullByDefault
public class WoodenBufferModel implements BakedModel {

    private final BakedModel wrapped;

    public WoodenBufferModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    private static final ModelProperty<BlockState> MATERIAL = new ModelProperty<>();

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        if (level.getBlockEntity(pos) instanceof WoodVariantTrackBufferBlockEntity be) {
            return modelData.derive()
                .with(MATERIAL, be.getMaterial())
                .build();
        } else {
            return modelData;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        BlockState material;
        if ((material = data.get(MATERIAL)) != null) {
            return BakedModelHelper.swapSprites(wrapped.getQuads(state, side, rand), getSwapper(material));
        } else {
            return wrapped.getQuads(state, side, rand);
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
