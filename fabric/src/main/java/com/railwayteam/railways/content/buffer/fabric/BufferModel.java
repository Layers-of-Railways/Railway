/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.content.buffer.fabric;

import com.mojang.math.Axis;
import com.railwayteam.railways.content.buffer.IDyedBuffer;
import com.railwayteam.railways.content.buffer.IMaterialAdaptingBuffer;
import com.railwayteam.railways.content.buffer.TrackBufferBlock;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.railwayteam.railways.content.buffer.BufferModelUtils.combineSwappers;
import static com.railwayteam.railways.content.buffer.BufferModelUtils.getSwapper;

@Environment(EnvType.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BufferModel extends ForwardingBakedModel {

    public BufferModel(BakedModel wrapped) {
        this.wrapped  = wrapped;
    }

    private static final Matrix3f diagonalTransform = getDiagonalRotationMatrix();
    private static final RenderContext.QuadTransform diagonalTransformer = quad -> {
        Vector3f faceNormal = quad.faceNormal();
        faceNormal.mulTranspose(diagonalTransform);
        for (int i = 0; i < 4; i++) {
            Vector3f vertexPos = quad.copyPos(i, new Vector3f());
            vertexPos.sub(new Vector3f(0.5f, 0.5f, 0.5f));
            vertexPos.mulTranspose(diagonalTransform);
            vertexPos.add(new Vector3f(0.5f, 0.5f, 0.5f));
            quad.pos(i, vertexPos);
            quad.normal(i, faceNormal);
        }
        
        return true;
    };

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        boolean isDiagonal = state.getValue(TrackBufferBlock.DIAGONAL);
        if (isDiagonal) context.pushTransform(diagonalTransformer);
        
        UnaryOperator<TextureAtlasSprite> materialSwapper = null;
        UnaryOperator<TextureAtlasSprite> colorSwapper = null;

        if (blockView.getBlockEntity(pos) instanceof IDyedBuffer be) {
            colorSwapper = getSwapper(be.getColor());
        }

        if (blockView.getBlockEntity(pos) instanceof IMaterialAdaptingBuffer be) {
            materialSwapper = getSwapper(be.getMaterial());
        }

        if (materialSwapper != null || colorSwapper != null) {
            new SpriteReplacingBakedModel(combineSwappers(materialSwapper, colorSwapper)).emitBlockQuads(blockView, state, pos, randomSupplier, context);
//            context.bakedModelConsumer().accept(new SpriteReplacingBakedModel(combineSwappers(materialSwapper, colorSwapper)), state);
        } else {
            super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        }
        if (isDiagonal) context.popTransform();
    }

    private static Matrix3f getDiagonalRotationMatrix() {
        Matrix3f matrix = new Matrix3f();
        matrix.mul(Axis.YP.rotationDegrees(45).get(new Matrix3f()));
        return matrix;
    }


    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        UnaryOperator<TextureAtlasSprite> materialSwapper = null;
        UnaryOperator<TextureAtlasSprite> colorSwapper = null;

        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
                CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
                if (blockEntityTag.contains("Material", Tag.TAG_COMPOUND)) {
                    materialSwapper = getSwapper(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), blockEntityTag.getCompound("Material")));
                }
                if (blockEntityTag.contains("Color", Tag.TAG_INT)) {
                    colorSwapper = getSwapper(DyeColor.byId(blockEntityTag.getInt("Color")));
                }
            }
        }
        if (materialSwapper != null || colorSwapper != null) {
            new SpriteReplacingBakedModel(combineSwappers(materialSwapper, colorSwapper)).emitItemQuads(stack, randomSupplier, context);
        } else {
            super.emitItemQuads(stack, randomSupplier, context);
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
