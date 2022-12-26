package com.railwayteam.railways.content.coupling.coupler;

import com.railwayteam.railways.content.coupling.TrainUtils;
import com.railwayteam.railways.mixin.AccessorTrackTargetingBehavior;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.railwayteam.railways.registry.CRIcons;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.structureMovement.ITransformableTE;
import com.simibubi.create.content.contraptions.components.structureMovement.StructureTransform;
import com.simibubi.create.content.logistics.trains.ITrackBlock;
import com.simibubi.create.content.logistics.trains.TrackNodeLocation;
import com.simibubi.create.content.logistics.trains.entity.Carriage;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.entity.TravellingPoint;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalBlock;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.INamedIconOptions;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TrackCouplerTileEntity extends SmartTileEntity implements ITransformableTE {

    private BlockState cachedTrackState = null;
    private BlockState cachedSecondaryTrackState = null;
    private boolean edgePointsOk = false;
    private boolean lastReportedPower = false;
    private int lastAnalogOutput = 0;
    protected int edgeSpacing = 3;
    private int lastEdgeSpacing = 3;

    public TrackTargetingBehaviour<TrackCoupler> edgePoint;
    public TrackTargetingBehaviour<TrackCoupler> secondEdgePoint;
    protected ScrollValueBehaviour edgeSpacingScroll;
    protected ScrollOptionBehaviour<AllowedOperationMode> allowedOperationMode;

    public TrackCouplerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("EdgePointsOk", edgePointsOk);
        tag.putBoolean("Power", lastReportedPower);
        tag.putInt("AnalogOutput", lastAnalogOutput);
        tag.putInt("EdgeSpacing", edgeSpacing);
        tag.putInt("LastEdgeSpacing", lastEdgeSpacing);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        edgePointsOk = tag.getBoolean("EdgePointsOk");
        lastReportedPower = tag.getBoolean("Power");
        lastAnalogOutput = tag.getInt("AnalogOutput");
        edgeSpacing = tag.getInt("EdgeSpacing");
        lastEdgeSpacing = tag.getInt("LastEdgeSpacing");
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        behaviours.add(edgePoint = new TrackTargetingBehaviour<>(this, CREdgePointTypes.COUPLER));
        behaviours.add(secondEdgePoint = new SecondaryTrackTargetingBehaviour<>(this, CREdgePointTypes.COUPLER));
        edgeSpacingScroll = new ScrollValueBehaviour(Components.translatable("railways.coupler.edge_spacing"), this, new TrackCouplerValueBoxTransform(true));
        edgeSpacingScroll.between(3, 8);
        edgeSpacingScroll.withUnit(i -> Components.translatable("railways.coupler.edge_spacing.meters"));
        edgeSpacingScroll.withFormatter(i -> i + "m");
        edgeSpacingScroll.withCallback(i -> this.edgeSpacing = i);
        edgeSpacingScroll.requiresWrench();
        behaviours.add(edgeSpacingScroll);

        allowedOperationMode = new ScrollOptionBehaviour<>(AllowedOperationMode.class, Lang.translateDirect("railways.coupler.coupling_mode"),
            this, new TrackCouplerValueBoxTransform(false));
        allowedOperationMode.requiresWrench();
        behaviours.add(allowedOperationMode);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide())
            return;

        BlockState blockState = getBlockState();

        blockState.getOptionalValue(SignalBlock.POWERED).ifPresent(powered -> {
            if (lastReportedPower == powered)
                return;
            lastReportedPower = powered;
            if (powered)
                onPowered();
            else
                onUnpowered();
            notifyUpdate();
        });

        if (getTargetAnalogOutput() != lastAnalogOutput) {
            lastAnalogOutput = getTargetAnalogOutput();
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
        }
//        DisplayLinkBlock.notifyGatherers(level, worldPosition);
    }

    protected void onPowered() {
        OperationInfo info = getOperationInfo();
        switch (info.mode) {
            case DECOUPLING:
                Train train = info.frontCarriage.train;
                int numberOffEnd = train.carriages.size() - train.carriages.indexOf(info.backCarriage); // all carriages after and including the back carriage
                TrainUtils.splitTrain(train, numberOffEnd);
                break;
            case COUPLING:
                Train frontTrain = info.frontCarriage.train;
                Train backTrain = info.backCarriage.train;
                if (frontTrain == backTrain)
                    break;
                TrainUtils.combineTrains(frontTrain, backTrain, getBlockPos().above(), level, getEdgeSpacing() + 2);
                break;
            case NONE:
                break;
        }
    }

    protected void onUnpowered() {}

    public boolean getReportedPower() {
        return lastReportedPower;
    }

    public int getEdgeSpacing() {
        return edgeSpacing;
    }

    private Optional<BlockPos> getDesiredSecondaryEdgePos() {
        BlockState trackState = edgePoint.getTrackBlockState();
        if (!trackState.hasProperty(TrackBlock.SHAPE))
            return Optional.empty();

        double distance = -getEdgeSpacing() * edgePoint.getTargetDirection().getStep();
        Vec3 offset = trackState.getValue(TrackBlock.SHAPE).getAxes().get(0).scale(distance);
        return Optional.of(((AccessorTrackTargetingBehavior) edgePoint).getTargetTrack().offset(offset.x, offset.y, offset.z));
    }

    private @Nullable BlockState getSecondaryTrackState() {
        return getDesiredSecondaryEdgePos().map(pos -> edgePoint.getWorld().getBlockState(pos.offset(this.getBlockPos()))).orElse(null);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        BlockState trackState = edgePoint.getTrackBlockState();
        BlockState secondaryTrackState = getSecondaryTrackState();
        if (trackState != cachedTrackState || secondaryTrackState != cachedSecondaryTrackState || edgeSpacing != lastEdgeSpacing) {
            cachedTrackState = trackState;
            cachedSecondaryTrackState = secondaryTrackState;
            lastEdgeSpacing = edgeSpacing;
            BlockPos newPos = isOkExceptGraph() ? getDesiredSecondaryEdgePos().orElse(BlockPos.ZERO) : BlockPos.ZERO;
            if (!newPos.equals(((AccessorTrackTargetingBehavior) secondEdgePoint).getTargetTrack())) {
                ((AccessorTrackTargetingBehavior) secondEdgePoint).setTargetTrack(newPos);
                ((AccessorTrackTargetingBehavior) secondEdgePoint).setEdgePoint(null);
                secondEdgePoint.createEdgePoint();
                if (isOkExceptGraph())
                    ((AccessorTrackTargetingBehavior) secondEdgePoint).setTargetDirection(((AccessorTrackTargetingBehavior) edgePoint).getTargetDirection().opposite());
            }
        }
        updateOK();
    }

    private boolean isOkExceptGraph() {
        return cachedTrackState.getBlock() instanceof ITrackBlock && cachedSecondaryTrackState.getBlock() instanceof ITrackBlock &&
            cachedTrackState.hasProperty(TrackBlock.SHAPE) && cachedSecondaryTrackState.hasProperty(TrackBlock.SHAPE) &&
            cachedTrackState.getValue(TrackBlock.SHAPE) == cachedSecondaryTrackState.getValue(TrackBlock.SHAPE);
    }

    protected void updateOK() {
        if (!isOkExceptGraph()) {
            edgePointsOk = false;
            return;
        }
        if (((AccessorTrackTargetingBehavior) secondEdgePoint).getTargetTrack().equals(BlockPos.ZERO) || ((AccessorTrackTargetingBehavior) edgePoint).getTargetTrack().equals(BlockPos.ZERO)) {
            edgePointsOk = false;
            return;
        }

        if (edgePoint.determineGraphLocation() == null || secondEdgePoint.determineGraphLocation() == null) {
            edgePointsOk = false;
            return;
        }

        if (edgePoint.determineGraphLocation().graph != secondEdgePoint.determineGraphLocation().graph) {
            edgePointsOk = false;
            return;
        }

        //check that edgePoint and secondEdgePoint are on the same or adjacent TrackEdge
        Couple<TrackNodeLocation> edgePointLocations = edgePoint.determineGraphLocation().edge;
        Couple<TrackNodeLocation> secondEdgePointLocations = secondEdgePoint.determineGraphLocation().edge;
        if (edgePointLocations == null || secondEdgePointLocations == null) {
            edgePointsOk = false;
            return;
        }
        edgePointsOk = edgePointLocations.getFirst().equals(secondEdgePointLocations.getFirst()) || edgePointLocations.getFirst().equals(secondEdgePointLocations.getSecond()) ||
            edgePointLocations.getSecond().equals(secondEdgePointLocations.getFirst()) || edgePointLocations.getSecond().equals(secondEdgePointLocations.getSecond());
    }

    public boolean areEdgePointsOk() {
        return edgePointsOk;
    }

    @Nullable
    public TrackCoupler getCoupler() {
        return edgePoint.getEdgePoint();
    }

    @Nullable
    public TrackCoupler getSecondaryCoupler() {
        return secondEdgePoint.getEdgePoint();
    }

    /**
     * Carriage must have its wheels on the point for it to count
     */
    @Nullable protected Carriage getCarriageOnPoint(@NotNull Train train, @NotNull TrackCoupler coupler, boolean leading) {
        for (Carriage carriage : train.carriages) {
            if (isCarriageWheelOnPoint(carriage, coupler, leading))
                return carriage;
        }
        return null;
    }

    protected boolean isCarriageWheelOnPoint(Carriage carriage, TrackCoupler coupler, boolean leading) {
        TravellingPoint relevantPoint = leading ? carriage.getLeadingPoint() : carriage.getTrailingPoint();
        return (coupler.isPrimary(relevantPoint.node1) || coupler.isPrimary(relevantPoint.node2)) && Math.abs(relevantPoint.position - (coupler.position + 0.5)) < .75;
    }
    
    public OperationInfo getOperationInfo() {
        OperationInfo info = getOperationInfo(false);
        if (info.mode == OperationMode.NONE)
            info = getOperationInfo(true);
        if (!info.mode.permitted(allowedOperationMode.get()))
            return OperationInfo.NONE;
        return info;
    }

    protected OperationInfo getOperationInfo(boolean reversed) {
        TrackCoupler coupler1 = reversed ? getSecondaryCoupler() : getCoupler();
        TrackCoupler coupler2 = reversed ? getCoupler() : getSecondaryCoupler();
        if (coupler1 != null && coupler2 != null && coupler1.isActivated() && coupler2.isActivated()) {
            Train primaryTrain = Create.RAILWAYS.trains.get(coupler1.getCurrentTrain());
            Train secondaryTrain = Create.RAILWAYS.trains.get(coupler2.getCurrentTrain());
            if (primaryTrain != null && primaryTrain == secondaryTrain) {
                //Decoupling, if back wheels of a carriage are on the secondary coupler and the front wheels of the carriage behind it are on the primary coupler
                Carriage frontCarriage = getCarriageOnPoint(primaryTrain, coupler2, false);
                if (frontCarriage != null && primaryTrain.carriages.indexOf(frontCarriage) < primaryTrain.carriages.size() - 1) {
                    Carriage backCarriage = primaryTrain.carriages.get(primaryTrain.carriages.indexOf(frontCarriage) + 1);
                    if (isCarriageWheelOnPoint(backCarriage, coupler1, true))
                        return new OperationInfo(OperationMode.DECOUPLING, frontCarriage, backCarriage);
                }
            } else if (primaryTrain != null && secondaryTrain != null) {
                //Coupling if the front wheels of primaryTrain are on coupler1 and the back wheels of secondaryTrain are on coupler2
                Carriage primaryCarriage = getCarriageOnPoint(primaryTrain, coupler1, true);
                Carriage secondaryCarriage = getCarriageOnPoint(secondaryTrain, coupler2, false);
                if (primaryCarriage != null && secondaryCarriage != null && primaryTrain.carriages.indexOf(primaryCarriage) == 0 &&
                    secondaryTrain.carriages.indexOf(secondaryCarriage) == secondaryTrain.carriages.size() - 1)
                    return new OperationInfo(OperationMode.COUPLING, secondaryCarriage, primaryCarriage);
            }
        }
        return OperationInfo.NONE;
    }

    public OperationMode getOperationMode() {
        return getOperationInfo().mode;
    }

    public record OperationInfo(OperationMode mode, Carriage frontCarriage, Carriage backCarriage) {
        public static final OperationInfo NONE = new OperationInfo(OperationMode.NONE, null, null);
    }

    public enum OperationMode {
        NONE, COUPLING, DECOUPLING;

        public boolean permitted(AllowedOperationMode allowedMode) {
            return this == OperationMode.NONE || allowedMode == AllowedOperationMode.BOTH || (allowedMode == AllowedOperationMode.COUPLING && this == OperationMode.COUPLING) ||
                (allowedMode == AllowedOperationMode.DECOUPLING && this == OperationMode.DECOUPLING);
        }
    }

    public enum AllowedOperationMode implements INamedIconOptions {
        BOTH(CRIcons.I_COUPLING_BOTH),
        COUPLING(CRIcons.I_COUPLING_COUPLE),
        DECOUPLING(CRIcons.I_COUPLING_DECOUPLE);

        private final String translationKey;
        private final AllIcons icon;

        AllowedOperationMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "railways.coupler.coupling_mode." + Lang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }
    }

    public int getTargetAnalogOutput() {
        int out = 0;
        if (getCoupler() != null && getCoupler().isActivated())
            out += 1;

        if (getSecondaryCoupler() != null && getSecondaryCoupler().isActivated())
            out += 2;
        OperationMode mode = getOperationMode();
        if (mode == OperationMode.DECOUPLING)
            return 15;
        else if (mode == OperationMode.COUPLING)
            return 14;
        return out;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition, edgePoint.getGlobalPosition())
            .minmax(new AABB(worldPosition, secondEdgePoint.getGlobalPosition()))
            .inflate(2);
    }

    @Override
    public void transform(StructureTransform transform) {
        edgePoint.transform(transform);
        secondEdgePoint.transform(transform);
    }

    private static class TrackCouplerValueBoxTransform extends CenteredSideValueBoxTransform {

        public TrackCouplerValueBoxTransform(boolean vertical) {
            super((state, d) -> d.getAxis().isVertical() == vertical);
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16);
        }

    }
}
