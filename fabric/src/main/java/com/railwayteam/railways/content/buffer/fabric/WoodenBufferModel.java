package com.railwayteam.railways.content.buffer.fabric;

import com.railwayteam.railways.content.buffer.WoodVariantTrackBufferBlockEntity;
import com.simibubi.create.foundation.model.BakedModelHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.railwayteam.railways.content.buffer.WoodenBufferUtils.getSwapper;

@Environment(EnvType.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WoodenBufferModel extends ForwardingBakedModel {
    public WoodenBufferModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        if (blockView.getBlockEntity(pos) instanceof WoodVariantTrackBufferBlockEntity be) {
            context.bakedModelConsumer().accept(new SpriteReplacingBakedModel(getSwapper(be.getMaterial())), state);
        } else {
            super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        }
    }

    private class SpriteReplacingBakedModel implements BakedModel {

        private final UnaryOperator<TextureAtlasSprite> spriteSwapper;

        private SpriteReplacingBakedModel(UnaryOperator<TextureAtlasSprite> spriteSwapper) {
            this.spriteSwapper = spriteSwapper;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
            return BakedModelHelper.swapSprites(wrapped.getQuads(state, direction, random), this.spriteSwapper);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return wrapped.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return wrapped.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return wrapped.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return wrapped.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return wrapped.getParticleIcon();
        }

        @Override
        public ItemTransforms getTransforms() {
            return wrapped.getTransforms();
        }

        @Override
        public ItemOverrides getOverrides() {
            return wrapped.getOverrides();
        }
    }
}
