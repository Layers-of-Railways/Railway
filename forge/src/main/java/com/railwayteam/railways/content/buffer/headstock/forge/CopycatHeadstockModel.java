package com.railwayteam.railways.content.buffer.headstock.forge;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.content.buffer.IDyedBuffer;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.content.decoration.copycat.FilteredBlockAndTintGetter;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static com.railwayteam.railways.content.buffer.BufferModelUtils.getSwapper;
import static com.simibubi.create.content.decoration.copycat.CopycatModel.getModelOf;

@MethodsReturnNonnullByDefault
public class CopycatHeadstockModel implements BakedModel {

    protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);

    private final BakedModel wrapped;

    public CopycatHeadstockModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    private static final ModelProperty<@Nullable BlockState> MATERIAL = CopycatModel.MATERIAL_PROPERTY;
    private static final ModelProperty<@Nullable DyeColor> COLOR = new ModelProperty<>();
    private static final ModelProperty<OcclusionData> OCCLUSION_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<ModelData> WRAPPED_DATA_PROPERTY = new ModelProperty<>();

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        DyeColor color = null;

        if (level.getBlockEntity(pos) instanceof IDyedBuffer be) {
            color = be.getColor();
        }

        ModelData.Builder builder = modelData.derive()
            .with(COLOR, color);

        BlockState material = getMaterial(modelData);

        if (!(state.getBlock() instanceof CopycatBlock copycatBlock) || material == null)
            return builder.build();

        OcclusionData occlusionData = new OcclusionData();
        gatherOcclusionData(level, pos, state, material, occlusionData, copycatBlock);
        builder.with(OCCLUSION_PROPERTY, occlusionData);

        ModelData wrappedData = getModelOf(material).getModelData(
            new FilteredBlockAndTintGetter(level,
                targetPos -> copycatBlock.canConnectTexturesToward(level, pos, targetPos, state)),
            pos, material, ModelData.EMPTY);
        return builder.with(WRAPPED_DATA_PROPERTY, wrappedData).build();
    }

    private void gatherOcclusionData(BlockAndTintGetter world, BlockPos pos, BlockState state, BlockState material,
                                     OcclusionData occlusionData, CopycatBlock copycatBlock) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction face : Iterate.directions) {

            // Rubidium: Run an additional IForgeBlock.hidesNeighborFace check because it
            // seems to be missing in Block.shouldRenderFace
            BlockPos.MutableBlockPos neighbourPos = mutablePos.setWithOffset(pos, face);
            BlockState neighbourState = world.getBlockState(neighbourPos);
            if (state.supportsExternalFaceHiding()
                && neighbourState.hidesNeighborFace(world, neighbourPos, state, face.getOpposite())) {
                occlusionData.occlude(face);
                continue;
            }

            if (!copycatBlock.canFaceBeOccluded(state, face))
                continue;
            if (!Block.shouldRenderFace(material, world, pos, face, neighbourPos))
                occlusionData.occlude(face);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        DyeColor color = data.get(COLOR);
        List<BakedQuad> quads;
        if (color != null) {
            quads = new ArrayList<>(filterQuads(CopycatHeadstockModel::filterCopycatParts,
                BakedModelHelper.swapSprites(wrapped.getQuads(state, side, rand), getSwapper(color))));
        } else {
            quads = new ArrayList<>(filterQuads(CopycatHeadstockModel::filterCopycatParts,
                wrapped.getQuads(state, side, rand)));
        }

        quads.addAll(getCopycatExtensionQuads(state, side, rand, data, renderType));
        return ImmutableList.copyOf(quads);
    }

    public static @NotNull BlockState getMaterial(@Nullable ModelData data) {
        BlockState material = data == null ? null : data.get(MATERIAL);
        return material == null ? AllBlocks.COPYCAT_BASE.getDefaultState() : material;
    }

    private List<BakedQuad> getCopycatExtensionQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        // Rubidium: see below
        if (side != null && state.getBlock() instanceof CopycatBlock ccb && ccb.shouldFaceAlwaysRender(state, side))
            return Collections.emptyList();

        BlockState material = getMaterial(data);

        OcclusionData occlusionData = data.get(OCCLUSION_PROPERTY);
        if (occlusionData != null && occlusionData.isOccluded(side))
            return List.of();

        ModelData wrappedData = data.get(WRAPPED_DATA_PROPERTY);
        if (wrappedData == null)
            wrappedData = ModelData.EMPTY;
        if (renderType != null && !Minecraft.getInstance()
            .getBlockRenderer()
            .getBlockModel(material)
            .getRenderTypes(material, rand, wrappedData)
            .contains(renderType))
            return List.of();

        List<BakedQuad> croppedQuads = getCroppedQuads(state, side, rand, material, wrappedData, renderType);

        // Rubidium: render side!=null versions of the base material during side==null,
        // to avoid getting culled away
        if (side == null && state.getBlock() instanceof CopycatBlock ccb)
            for (Direction nonOcclusionSide : Iterate.directions)
                if (ccb.shouldFaceAlwaysRender(state, nonOcclusionSide))
                    croppedQuads.addAll(getCroppedQuads(state, nonOcclusionSide, rand, material, wrappedData, renderType));

        return croppedQuads;
    }

    protected List<BakedQuad> getCroppedQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, BlockState material,
                                              ModelData wrappedData, @Nullable RenderType renderType) {
        Direction facing = state == null ? Direction.NORTH : state.getOptionalValue(CopycatHeadstockBlock.FACING)
            .orElse(Direction.NORTH);

        BakedModel model = getModelOf(material);
        List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);
        int size = templateQuads.size();

        List<BakedQuad> quads = new ArrayList<>();

        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 normalScaled14 = normal.scale(14 / 16f);

        // 4 Pieces
        for (boolean top : Iterate.trueAndFalse) {
            for (boolean front : Iterate.trueAndFalse) {
                Vec3 offset = normal.scale(front ? 0 : -13 / 16f);
                float contract = 16 - (front ? 1 : 2);
                AABB bb = CUBE_AABB.contract(normal.x * contract / 16, 10 / 16., normal.z * contract / 16);
                if (!front)
                    bb = bb.move(normalScaled14);
                if (top)
                    bb = bb.move(0, 10 / 16., 0);
                else
                    offset = offset.add(0, 4 / 16., 0);

                //noinspection ConstantValue
                if (false) { // debug explode
                    if (front) {
                        offset = offset.add(-normal.x / 16, 0, -normal.z / 16);
                    }
                    if (top) {
                        offset = offset.add(0, 1 / 16., 0);
                    }
                }

                for (int i = 0; i < size; i++) {
                    BakedQuad quad = templateQuads.get(i);
                    Direction direction = quad.getDirection();

                    if (front && direction == facing)
                        continue;
                    if (!front && direction == facing.getOpposite())
                        continue;
                    if (top && direction == Direction.DOWN)
                        continue;
                    if (!top && direction == Direction.UP)
                        continue;

                    quads.add(BakedQuadHelper.cloneWithCustomGeometry(quad,
                        BakedModelHelper.cropAndMove(quad.getVertices(), quad.getSprite(), bb, offset)));
                }

            }
        }

        return quads;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource random) {
        return Collections.emptyList();
    }

    private static boolean filterCopycatParts(BakedQuad quad) {
        return !quad.getSprite().contents().name().equals(new ResourceLocation("create", "block/copycat_base"));
    }

    private static @NotNull List<BakedQuad> filterQuads(@NotNull Predicate<BakedQuad> filter, @NotNull List<BakedQuad> quads) {
        return quads.stream().filter(filter).toList();
    }

    // WAY too complicated method of getting custom item models to work
    @Override
    public List<BakedModel> getRenderPasses(@NotNull ItemStack stack, boolean fabulous) {
        BlockState material = AllBlocks.COPYCAT_BASE.getDefaultState();
        UnaryOperator<TextureAtlasSprite> colorSwapper = null;

        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
                CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
                if (blockEntityTag.contains("Material", Tag.TAG_COMPOUND)) {
                    material = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), blockEntityTag.getCompound("Material"));
                }
                if (blockEntityTag.contains("Color", Tag.TAG_INT)) {
                    colorSwapper = getSwapper(DyeColor.byId(blockEntityTag.getInt("Color")));
                }
            }
        }
        final BlockState finalMaterial = material;
        final UnaryOperator<TextureAtlasSprite> finalColorSwapper = colorSwapper;
        return List.of(new CopycatHeadstockModel(wrapped) {
            @SuppressWarnings("deprecation")
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource random) {
                List<BakedQuad> quads;
                if (finalColorSwapper != null) {
                    quads = BakedModelHelper.swapSprites(wrapped.getQuads(state, direction, random), finalColorSwapper);
                } else {
                    quads = wrapped.getQuads(state, direction, random);
                }
                List<BakedQuad> mutableQuads = new ArrayList<>(filterQuads(CopycatHeadstockModel::filterCopycatParts, quads));
                mutableQuads.addAll(getCroppedQuads(null, direction, random, finalMaterial, ModelData.EMPTY, null));
                return ImmutableList.copyOf(mutableQuads);
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

    private static class OcclusionData {
        private final boolean[] occluded;

        public OcclusionData() {
            occluded = new boolean[6];
        }

        public void occlude(Direction face) {
            occluded[face.get3DDataValue()] = true;
        }

        public boolean isOccluded(Direction face) {
            return face == null ? false : occluded[face.get3DDataValue()];
        }
    }
}
