package com.railwayteam.railways.content.buffer.headstock.fabric;

import com.railwayteam.railways.content.buffer.IDyedBuffer;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBlock;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.railwayteam.railways.content.buffer.BufferModelUtils.getSwapper;
import static com.simibubi.create.content.decoration.copycat.CopycatModel.getModelOf;

@Environment(EnvType.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CopycatHeadstockModel extends ForwardingBakedModel {

    protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);

    public CopycatHeadstockModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    private void gatherOcclusionData(BlockAndTintGetter world, BlockPos pos, BlockState state, BlockState material,
                                     OcclusionData occlusionData, CopycatBlock copycatBlock) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction face : Iterate.directions) {
            if (!copycatBlock.canFaceBeOccluded(state, face))
                continue;
            BlockPos.MutableBlockPos neighbourPos = mutablePos.setWithOffset(pos, face);
            if (!Block.shouldRenderFace(material, world, pos, face, neighbourPos))
                occlusionData.occlude(face);
        }
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    private static boolean filterCopycatParts(BakedQuad quad) {
        return !quad.getSprite().getName().equals(new ResourceLocation("create", "block/copycat_base"));
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        BlockState material;
        UnaryOperator<TextureAtlasSprite> colorSwapper = null;

        if (blockView.getBlockEntity(pos) instanceof IDyedBuffer be) {
            colorSwapper = getSwapper(be.getColor());
        }

        if (blockView.getBlockEntity(pos) instanceof CopycatHeadstockBlockEntity be) {
            material = be.getMaterial();
        } else {
            material = AllBlocks.COPYCAT_BASE.getDefaultState();
        }

        if (colorSwapper != null) {
            context.bakedModelConsumer().accept(new SpriteReplacingBakedModel(colorSwapper, CopycatHeadstockModel::filterCopycatParts), state);
        } else {
            context.bakedModelConsumer().accept(new SpriteReplacingBakedModel(CopycatHeadstockModel::filterCopycatParts), state);
        }

        // copycat model face emission

        OcclusionData occlusionData = new OcclusionData();
        if (state.getBlock() instanceof CopycatBlock copycatBlock) {
            gatherOcclusionData(blockView, pos, state, material, occlusionData, copycatBlock);
        }

        CullFaceRemovalData cullFaceRemovalData = new CullFaceRemovalData();
        if (state.getBlock() instanceof CopycatBlock copycatBlock) {
            for (Direction cullFace : Iterate.directions) {
                if (copycatBlock.shouldFaceAlwaysRender(state, cullFace)) {
                    cullFaceRemovalData.remove(cullFace);
                }
            }
        }

        emitBlockQuadsInner(blockView, state, pos, randomSupplier, context, material, cullFaceRemovalData, occlusionData);
    }

    protected void emitBlockQuadsInner(@Nullable BlockAndTintGetter blockView, @Nullable BlockState state, @Nullable BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context, BlockState material, CullFaceRemovalData cullFaceRemovalData, OcclusionData occlusionData) {
        Direction facing = state == null ? Direction.NORTH : state.getOptionalValue(CopycatHeadstockBlock.FACING)
            .orElse(Direction.NORTH);
        boolean upsideDown = state != null && state.getValue(CopycatHeadstockBlock.UPSIDE_DOWN);

        BakedModel model = getModelOf(material);

        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 normalScaled14 = normal.scale(14 / 16f);

        SpriteFinder spriteFinder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS));

        // Use a mesh to defer quad emission since quads cannot be emitted inside a transform
        MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = meshBuilder.getEmitter();
        context.pushTransform(quad -> {
            if (cullFaceRemovalData.shouldRemove(quad.cullFace())) {
                quad.cullFace(null);
            } else if (occlusionData.isOccluded(quad.cullFace())) {
                // Add quad to mesh and do not render original quad to preserve quad render order
                // copyTo does not copy the material
                RenderMaterial quadMaterial = quad.material();
                quad.copyTo(emitter);
                emitter.material(quadMaterial);
                emitter.emit();
                return false;
            }

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

                    if (upsideDown)
                        offset = offset.add(0, -4 / 16., 0);

                    //noinspection ConstantValue
                    if (false) { // debug explode
                        if (front) {
                            offset = offset.add(-normal.x / 16, 0, -normal.z / 16);
                        }
                        if (top) {
                            offset = offset.add(0, 1 / 16., 0);
                        }
                    }

                    Direction direction = quad.lightFace();

                    if (front && direction == facing)
                        continue;
                    if (!front && direction == facing.getOpposite())
                        continue;
                    if (top && direction == Direction.DOWN)
                        continue;
                    if (!top && direction == Direction.UP)
                        continue;

                    // copyTo does not copy the material
                    RenderMaterial quadMaterial = quad.material();
                    quad.copyTo(emitter);
                    emitter.material(quadMaterial);
                    BakedModelHelper.cropAndMove(emitter, spriteFinder.find(emitter, 0), bb, offset);
                    emitter.emit();
                }
            }

            return false;
        });
        if (blockView == null || pos == null || state == null) {
            ((FabricBakedModel) model).emitItemQuads(new ItemStack(material.getBlock().asItem()), randomSupplier, context);
        } else {
            ((FabricBakedModel) model).emitBlockQuads(blockView, material, pos, randomSupplier, context);
        }
        context.popTransform();
        context.meshConsumer().accept(meshBuilder.build());
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        BlockState material = AllBlocks.COPYCAT_BASE.getDefaultState();
        UnaryOperator<TextureAtlasSprite> colorSwapper = null;

        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
                CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
                if (blockEntityTag.contains("Material", Tag.TAG_COMPOUND)) {
                    material = NbtUtils.readBlockState(blockEntityTag.getCompound("Material"));
                }
                if (blockEntityTag.contains("Color", Tag.TAG_INT)) {
                    colorSwapper = getSwapper(DyeColor.byId(blockEntityTag.getInt("Color")));
                }
            }
        }
        if (colorSwapper != null) {
            context.bakedModelConsumer().accept(new SpriteReplacingBakedModel(colorSwapper, CopycatHeadstockModel::filterCopycatParts));
        } else {
            context.bakedModelConsumer().accept(new SpriteReplacingBakedModel(CopycatHeadstockModel::filterCopycatParts));
        }

        // copycat model face emission

        OcclusionData occlusionData = new OcclusionData();

        CullFaceRemovalData cullFaceRemovalData = new CullFaceRemovalData();

        emitBlockQuadsInner(null, null, null, randomSupplier, context, material, cullFaceRemovalData, occlusionData);
    }

    private class SpriteReplacingBakedModel implements BakedModel {

        private final UnaryOperator<TextureAtlasSprite> spriteSwapper;
        private final Predicate<BakedQuad> filter;

        private SpriteReplacingBakedModel(UnaryOperator<TextureAtlasSprite> spriteSwapper) {
            this(spriteSwapper, quad -> true);
        }

        private SpriteReplacingBakedModel(Predicate<BakedQuad> filter) {
            this(sprite -> sprite, filter);
        }

        private SpriteReplacingBakedModel(UnaryOperator<TextureAtlasSprite> spriteSwapper, Predicate<BakedQuad> filter) {
            this.spriteSwapper = spriteSwapper;
            this.filter = filter;
        }

        protected @NotNull List<BakedQuad> filterQuads(@NotNull List<BakedQuad> quads) {
            return quads.stream().filter(filter).toList();
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
            return BakedModelHelper.swapSprites(filterQuads(wrapped.getQuads(state, direction, random)), this.spriteSwapper);
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

    protected static class OcclusionData {
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

    protected static class CullFaceRemovalData {
        private final boolean[] shouldRemove;

        public CullFaceRemovalData() {
            shouldRemove = new boolean[6];
        }

        public void remove(Direction face) {
            shouldRemove[face.get3DDataValue()] = true;
        }

        public boolean shouldRemove(Direction face) {
            return face == null ? false : shouldRemove[face.get3DDataValue()];
        }
    }
}
