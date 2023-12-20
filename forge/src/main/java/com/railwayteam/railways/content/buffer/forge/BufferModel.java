package com.railwayteam.railways.content.buffer.forge;

import com.railwayteam.railways.content.buffer.IDyedBuffer;
import com.railwayteam.railways.content.buffer.IMaterialAdaptingBuffer;
import com.simibubi.create.foundation.model.BakedModelHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

import static com.railwayteam.railways.content.buffer.BufferModelUtils.combineSwappers;
import static com.railwayteam.railways.content.buffer.BufferModelUtils.getSwapper;

@MethodsReturnNonnullByDefault
public class BufferModel implements BakedModel {

    private final BakedModel wrapped;

    public BufferModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    private static final ModelProperty<@Nullable BlockState> MATERIAL = new ModelProperty<>();
    private static final ModelProperty<@Nullable DyeColor> COLOR = new ModelProperty<>();

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        BlockState material = null;
        DyeColor color = null;

        if (level.getBlockEntity(pos) instanceof IDyedBuffer be) {
            color = be.getColor();
        }

        if (level.getBlockEntity(pos) instanceof IMaterialAdaptingBuffer be) {
            material = be.getMaterial();
        }

        return modelData.derive()
            .with(MATERIAL, material)
            .with(COLOR, color)
            .build();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        BlockState material = data.get(MATERIAL);
        DyeColor color = data.get(COLOR);
        if (material != null || color != null) {
            return BakedModelHelper.swapSprites(wrapped.getQuads(state, side, rand), combineSwappers(getSwapper(material), getSwapper(color)));
        } else {
            return wrapped.getQuads(state, side, rand);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource random) {
        return Collections.emptyList();
    }

    // WAY too complicated method of getting custom item models to work
    @Override
    public List<BakedModel> getRenderPasses(@NotNull ItemStack stack, boolean fabulous) {
        UnaryOperator<TextureAtlasSprite> materialSwapper = null;
        UnaryOperator<TextureAtlasSprite> colorSwapper = null;

        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
                CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
                if (blockEntityTag.contains("Material", Tag.TAG_COMPOUND)) {
                    materialSwapper = getSwapper(NbtUtils.readBlockState(blockEntityTag.getCompound("Material")));
                }
                if (blockEntityTag.contains("Color", Tag.TAG_INT)) {
                    colorSwapper = getSwapper(DyeColor.byId(blockEntityTag.getInt("Color")));
                }
            }
        }
        final UnaryOperator<TextureAtlasSprite> finalMaterialSwapper = materialSwapper;
        final UnaryOperator<TextureAtlasSprite> finalColorSwapper = colorSwapper;
        return List.of(new BufferModel(wrapped) {
            @SuppressWarnings("deprecation")
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource random) {
                if (finalMaterialSwapper != null || finalColorSwapper != null) {
                    return BakedModelHelper.swapSprites(wrapped.getQuads(state, direction, random), combineSwappers(finalMaterialSwapper, finalColorSwapper));
                } else {
                    return wrapped.getQuads(state, direction, random);
                }
            }
        });
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
        return wrapped.getOverrides();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemTransforms getTransforms() {
        return wrapped.getTransforms();
    }
}
