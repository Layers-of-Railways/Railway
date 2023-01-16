package com.railwayteam.railways.content.custom_bogeys.monobogey;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.registry.CRBlocks;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock;
import com.simibubi.create.content.logistics.trains.IBogeyBlock;
import com.simibubi.create.content.logistics.trains.entity.BogeyInstance;
import com.simibubi.create.content.logistics.trains.entity.CarriageBogey;
import com.simibubi.create.content.schematics.ISpecialBlockItemRequirement;
import com.simibubi.create.content.schematics.ItemRequirement;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MonoBogeyBlock extends Block implements IBogeyBlock, ITE<MonoBogeyTileEntity>, ProperWaterloggedBlock, ISpecialBlockItemRequirement {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public MonoBogeyBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, WATERLOGGED);
        super.createBlockStateDefinition(builder);
    }

    static final EnumSet<Direction> STICKY_X = EnumSet.of(Direction.EAST, Direction.WEST);
    static final EnumSet<Direction> STICKY_Z = EnumSet.of(Direction.SOUTH, Direction.NORTH);

    @Override
    public EnumSet<Direction> getStickySurfaces(BlockGetter world, BlockPos pos, BlockState state) {
        return state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X ? STICKY_X : STICKY_Z;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
                                  LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        updateWater(pLevel, pState, pCurrentPos);
        return pState;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return fluidState(pState);
    }

    @Override
    public double getWheelPointSpacing() {
        return 2; //TODO
    }

    @Override
    public double getWheelRadius() {
        return 6.5; //TODO
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 1);
    } //TODO

    @Override
    public boolean allowsSingleBogeyCarriage() {
        return true;
    }

    @Override
    public BlockState getMatchingBogey(Direction upDirection, boolean axisAlongFirst) {
        if (upDirection != Direction.UP)
            return null;
        return defaultBlockState().setValue(AXIS, axisAlongFirst ? Direction.Axis.X : Direction.Axis.Z);
    }

    @Override
    public boolean isTrackAxisAlongFirstCoordinate(BlockState state) {
        return state.getValue(AXIS) == Direction.Axis.X;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nullable BlockState state, float wheelAngle, PoseStack ms, float partialTicks, MultiBufferSource buffers,
                       int light, int overlay) {
        if (state != null) {
            ms.translate(.5f, .5f, .5f);
            if (state.getValue(AXIS) == Direction.Axis.X)
                ms.mulPose(Vector3f.YP.rotationDegrees(90));
        }

        ms.translate(0, -1.5 - 1 / 128f, 0);

        VertexConsumer vb = buffers.getBuffer(RenderType.cutoutMipped());
        BlockState air = Blocks.AIR.defaultBlockState();

        for (int i : Iterate.zeroAndOne)
            CachedBufferer.block(AllBlocks.SHAFT.getDefaultState()
                    .setValue(ShaftBlock.AXIS, Direction.Axis.Z))
                .translate(-.5f, .25f, i * -1)
                .centre()
                .rotateZ(wheelAngle)
                .unCentre()
                .light(light)
                .renderInto(ms, vb);

        renderBogey(wheelAngle, ms, light, vb, air);
    }

    private void renderBogey(float wheelAngle, PoseStack ms, int light, VertexConsumer vb, BlockState air) {
        CachedBufferer.partial(AllBlockPartials.BOGEY_FRAME, air)
            .scale(1 - 1 / 512f)
            .light(light)
            .renderInto(ms, vb);

        for (int side : Iterate.positiveAndNegative) {
            ms.pushPose();
            CachedBufferer.partial(AllBlockPartials.SYMMETRY_CROSSPLANE, air)
                .translate(0, 12 / 16f, side)
                .rotateX(wheelAngle)
                .light(light)
                .renderInto(ms, vb);
            ms.popPose();
        }
    }

    @Override
    public BogeyInstance createInstance(MaterialManager materialManager, CarriageBogey bogey) {
        return new BogeyInstance.Frame(bogey, materialManager); //TODO
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return switch (pRotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> pState.cycle(AXIS);
            default -> pState;
        };
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
                                       Player player) {
        return AllBlocks.RAILWAY_CASING.asStack();
    }

    @Override
    public Class<MonoBogeyTileEntity> getTileEntityClass() {
        return MonoBogeyTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends MonoBogeyTileEntity> getTileEntityType() {
        return CRBlockEntities.MONO_BOGEY.get();
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity te) {
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, AllBlocks.RAILWAY_CASING.asStack());
    }

    @Override
    public BlockState getRotatedBlockState(BlockState state, Direction targetedFace) {
        return state;
    }
}
