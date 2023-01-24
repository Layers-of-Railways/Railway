package com.railwayteam.railways.content.custom_tracks.monorail;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.particle.CubeParticleData;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.content.logistics.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.logistics.trains.track.TrackRenderer;
import com.simibubi.create.content.logistics.trains.track.TrackShape;
import com.simibubi.create.content.logistics.trains.track.TrackTileEntity;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import static com.simibubi.create.AllShapes.*;
import static com.simibubi.create.AllShapes.TRACK_ORTHO_LONG;

public class MonorailTrackBlock extends CustomTrackBlock{
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
            case AE:
                return CRShapes.MONORAIL_TRACK_ASC.get(Direction.EAST);
            case AW:
                return CRShapes.MONORAIL_TRACK_ASC.get(Direction.WEST);
            case AN:
                return CRShapes.MONORAIL_TRACK_ASC.get(Direction.NORTH);
            case AS:
                return CRShapes.MONORAIL_TRACK_ASC.get(Direction.SOUTH);
            case CR_D:
                return CRShapes.MONORAIL_TRACK_CROSS_DIAG;
            case CR_NDX:
                return TRACK_CROSS_ORTHO_DIAG.get(Direction.SOUTH);
            case CR_NDZ:
                return TRACK_CROSS_DIAG_ORTHO.get(Direction.SOUTH);
            case CR_O:
                return CRShapes.MONORAIL_TRACK_CROSS;
            case CR_PDX:
                return CRShapes.MONORAIL_TRACK_CROSS_DIAG_ORTHO.get(Direction.EAST);
            case CR_PDZ:
                return CRShapes.MONORAIL_TRACK_CROSS_ORTHO_DIAG.get(Direction.EAST);
            case ND:
                return CRShapes.MONORAIL_TRACK_DIAG.get(Direction.SOUTH);
            case PD:
                return CRShapes.MONORAIL_TRACK_DIAG.get(Direction.EAST);
            case XO:
                return CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.EAST);
            case ZO:
                return CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.SOUTH);
            case TE:
                return CRShapes.MONORAIL_TRACK_ORTHO_LONG.get(Direction.EAST);
            case TW:
                return CRShapes.MONORAIL_TRACK_ORTHO_LONG.get(Direction.WEST);
            case TS:
                return CRShapes.MONORAIL_TRACK_ORTHO_LONG.get(Direction.SOUTH);
            case TN:
                return CRShapes.MONORAIL_TRACK_ORTHO_LONG.get(Direction.NORTH);
            case NONE:
            default:
        }
        return CRShapes.MONORAIL_TRACK_FALLBACK;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public PartialModel prepareAssemblyOverlay(BlockGetter world, BlockPos pos, BlockState state, Direction direction,
                                               PoseStack ms) { //TODO move up
        TransformStack.cast(ms)
            .rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(direction)))
            .translateY(14/16f);
        return CRBlockPartials.MONORAIL_TRACK_ASSEMBLING_OVERLAY;
    }
}
