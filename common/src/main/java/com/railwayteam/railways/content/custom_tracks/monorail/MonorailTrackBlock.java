package com.railwayteam.railways.content.custom_tracks.monorail;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackPropagator;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Couple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class MonorailTrackBlock extends TrackBlock {
    public MonorailTrackBlock(Properties properties, TrackMaterial material) {
        super(properties, material);
    }

    @Override
    public BlockState getBogeyAnchor(BlockGetter world, BlockPos pos, BlockState state) {
        return CRBlocks.MONO_BOGEY.getDefaultState()
            .setValue(BlockStateProperties.HORIZONTAL_AXIS, state.getValue(SHAPE) == TrackShape.XO ? Direction.Axis.X : Direction.Axis.Z);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
                                        CollisionContext pContext) {
        return switch (pState.getValue(SHAPE)) {
            case AE, AW, AN, AS -> Shapes.empty();
            default -> CRShapes.MONORAIL_COLLISION;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getFullShape(state);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter pLevel, BlockPos pPos) {
        return getFullShape(state);
    }

    private VoxelShape getFullShape(BlockState state) {
        switch (state.getValue(SHAPE)) {
            case AE -> {
                return CRShapes.MONORAIL_TRACK_ASC.get(Direction.EAST);
            }
            case AW -> {
                return CRShapes.MONORAIL_TRACK_ASC.get(Direction.WEST);
            }
            case AN -> {
                return CRShapes.MONORAIL_TRACK_ASC.get(Direction.NORTH);
            }
            case AS -> {
                return CRShapes.MONORAIL_TRACK_ASC.get(Direction.SOUTH);
            }
            case CR_D -> {
                return CRShapes.MONORAIL_TRACK_CROSS_DIAG;
            }
            case CR_NDX -> {
                return CRShapes.MONORAIL_TRACK_CROSS_ORTHO_DIAG.get(Direction.SOUTH);
            }
            case CR_NDZ -> {
                return CRShapes.MONORAIL_TRACK_CROSS_DIAG_ORTHO.get(Direction.SOUTH);
            }
            case CR_O -> {
                return CRShapes.MONORAIL_TRACK_CROSS;
            }
            case CR_PDX -> {
                return CRShapes.MONORAIL_TRACK_CROSS_DIAG_ORTHO.get(Direction.EAST);
            }
            case CR_PDZ -> {
                return CRShapes.MONORAIL_TRACK_CROSS_ORTHO_DIAG.get(Direction.EAST);
            }
            case ND -> {
                return CRShapes.MONORAIL_TRACK_DIAG.get(Direction.SOUTH);
            }
            case PD -> {
                return CRShapes.MONORAIL_TRACK_DIAG.get(Direction.EAST);
            }
            case XO -> {
                return CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.EAST);
            }
            case ZO -> {
                return CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.SOUTH);
            }
            case TE -> {
                return CRShapes.MONORAIL_TRACK_ORTHO_LONG.get(Direction.EAST);
            }
            case TW -> {
                return CRShapes.MONORAIL_TRACK_ORTHO_LONG.get(Direction.WEST);
            }
            case TS -> {
                return CRShapes.MONORAIL_TRACK_ORTHO_LONG.get(Direction.SOUTH);
            }
            case TN -> {
                return CRShapes.MONORAIL_TRACK_ORTHO_LONG.get(Direction.NORTH);
            }
            default -> {
            }
        }
        return CRShapes.MONORAIL_TRACK_FALLBACK;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public PartialModel prepareAssemblyOverlay(BlockGetter world, BlockPos pos, BlockState state, Direction direction,
                                               PoseStack ms) {
        TransformStack.cast(ms)
            .rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(direction)))
            .translateY(14/16f);
        return CRBlockPartials.MONORAIL_TRACK_ASSEMBLING_OVERLAY;
    }

    @Override
    @SuppressWarnings("deprecation") // deprecated to call, fine to implement
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level,
                           @NotNull BlockPos pos, @NotNull Random random) {
        if (!state.hasProperty(SHAPE)) return;
        TrackGraphLocation location = TrackGraphHelper.getGraphLocationAt(level, pos,
            Direction.AxisDirection.POSITIVE, state.getValue(SHAPE).getAxes().get(0));
        if (location == null) return;
        Couple<TrackNode> nodes = location.edge.map((e) -> location.graph.locateNode(e));
        if (nodes.either(Objects::isNull)) return;
        TrackEdge edge = location.graph.getConnection(nodes);
        if (edge == null) return;
        if (edge.getTrackMaterial() != getMaterial())
            TrackPropagator.onRailAdded(level, pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = super.use(state, world, pos, player, hand, hit);
        if (result.consumesAction())
            return result;

        if (!world.isClientSide && AllItems.BRASS_HAND.isIn(player.getItemInHand(hand))) {
            TrackPropagator.onRailAdded(world, pos, state);
            return InteractionResult.SUCCESS;
        }
        return result;
    }
}
