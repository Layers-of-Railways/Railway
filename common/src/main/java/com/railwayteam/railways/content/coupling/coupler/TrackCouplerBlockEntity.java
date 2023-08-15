package com.railwayteam.railways.content.coupling.coupler;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.coupling.TrainUtils;
import com.railwayteam.railways.mixin.AccessorTrackTargetingBehavior;
import com.railwayteam.railways.mixin_interfaces.IOccupiedCouplers;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.TrackCouplerClientInfoPacket;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.ITransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class TrackCouplerBlockEntity extends SmartBlockEntity implements ITransformableBlockEntity, IHaveGoggleInformation {

    private BlockState cachedTrackState = null;
    private BlockState cachedSecondaryTrackState = null;
    private boolean edgePointsOk = false;
    private boolean lastReportedPower = false;
    private int lastAnalogOutput = 0;
    protected int edgeSpacing = 5;
    private int lastEdgeSpacing = 5;
    private MutableComponent error = null;
    private MutableComponent error2 = null;
    private ClientInfo clientInfo;

    public TrackTargetingBehaviour<TrackCoupler> edgePoint;
    public TrackTargetingBehaviour<TrackCoupler> secondEdgePoint;
    protected ScrollValueBehaviour edgeSpacingScroll;

    public TrackCouplerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
        //if (clientPacket && clientInfo != null)
        //    tag.put("ClientInfo", clientInfo.write());
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        edgePointsOk = tag.getBoolean("EdgePointsOk");
        lastReportedPower = tag.getBoolean("Power");
        lastAnalogOutput = tag.getInt("AnalogOutput");
        edgeSpacing = tag.getInt("EdgeSpacing");
        lastEdgeSpacing = tag.getInt("LastEdgeSpacing");
        edgeSpacingScroll.setValue(edgeSpacing);
        //if (clientPacket)
        //    clientInfo = new ClientInfo(tag.getCompound("ClientInfo"));
        invalidateRenderBoundingBox();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(edgePoint = new TrackTargetingBehaviour<>(this, CREdgePointTypes.COUPLER));
        behaviours.add(secondEdgePoint = new SecondaryTrackTargetingBehaviour<>(this, CREdgePointTypes.COUPLER));
        edgeSpacingScroll = new ScrollValueBehaviour(Components.translatable("railways.coupler.edge_spacing"), this, new TrackCouplerValueBoxTransform(true));
        edgeSpacingScroll.between(3, 15);
        edgeSpacingScroll.withFormatter(i -> String.valueOf(Components.translatable("railways.coupler.edge_spacing.meters")));
        edgeSpacingScroll.withFormatter(i -> i + "m");
        edgeSpacingScroll.withCallback(i -> this.edgeSpacing = i);
        edgeSpacingScroll.requiresWrench();
        behaviours.add(edgeSpacingScroll);
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
        if (level == null || level.isClientSide)
            return;
//        this.getSecondaryCoupler().blockEntityAdded(this, false); //FIX_ME remove this
        OperationInfo info = getOperationInfo();
        switch (info.mode) {
            case DECOUPLING -> {
                Train train = info.frontCarriage.train;
                int numberOffEnd = train.carriages.size() - train.carriages.indexOf(info.backCarriage); // all carriages after and including the back carriage
                TrainUtils.splitTrain(train, numberOffEnd);
            }
            case COUPLING -> {
                Train frontTrain = info.frontCarriage.train;
                Train backTrain = info.backCarriage.train;
                if (frontTrain == backTrain)
                    break;
                TrainUtils.combineTrains(frontTrain, backTrain, getBlockPos().above(), level, getEdgeSpacing());
            }
            case NONE -> {
            }
        }
    }

    protected void onUnpowered() {
    }

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

    private @Nullable
    BlockState getSecondaryTrackState() {
        return getDesiredSecondaryEdgePos().map(pos -> edgePoint.getWorld().getBlockState(pos.offset(this.getBlockPos()))).orElse(null);
    }

    private void setError(Component component) {
        error = Components.empty().append(component);
    }

    private void setError2(Component component) {
        error2 = Components.empty().append(component);
    }

    private void clearErrors() {
        error = null;
    }

    private void clearError2() {
        error2 = null;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level == null || level.isClientSide)
            return;
        BlockState trackState = edgePoint.getTrackBlockState();
        BlockState secondaryTrackState = getSecondaryTrackState();
        if (trackState != cachedTrackState || secondaryTrackState != cachedSecondaryTrackState || edgeSpacing != lastEdgeSpacing) {
            invalidateRenderBoundingBox();
            cachedTrackState = trackState;
            cachedSecondaryTrackState = secondaryTrackState;
            lastEdgeSpacing = edgeSpacing;
            BlockPos newPos = isOkExceptGraph() ? getDesiredSecondaryEdgePos().orElse(BlockPos.ZERO) : BlockPos.ZERO;
            if (!newPos.equals(((AccessorTrackTargetingBehavior) secondEdgePoint).getTargetTrack())) {
                ((AccessorTrackTargetingBehavior) secondEdgePoint).setTargetTrack(newPos);
                TrackCoupler point = secondEdgePoint.getEdgePoint();
                if (point != null && secondEdgePoint.hasValidTrack()) { // don't want to try anything if not on an actual track
                    TrackGraphLocation location = secondEdgePoint.determineGraphLocation();
                    if (location != null && location.graph != null) {
                        location.graph.removePoint(CREdgePointTypes.COUPLER, point.id);
                        Create.RAILWAYS.trains.forEach((uuid, train) -> {
                            ((IOccupiedCouplers) train).getOccupiedCouplers().remove(point.id);
                            if (uuid == point.getCurrentTrain() || train.graph == location.graph) {
                                train.updateSignalBlocks = true;
                            }
                        });
                    }
                }
                ((AccessorTrackTargetingBehavior) secondEdgePoint).setEdgePoint(null);
//                secondEdgePoint.edgePoint = secondEdgePoint.createEdgePoint();  // - this is taken care of by the behaviour's tick method
                if (isOkExceptGraph())
                    ((AccessorTrackTargetingBehavior) secondEdgePoint).setTargetDirection(((AccessorTrackTargetingBehavior) edgePoint).getTargetDirection().opposite());
                sendData();
            }
        }
        updateOK();
        clientInfo = new ClientInfo(this);
        clearErrors();
        clearError2();
        if (level instanceof ServerLevel serverLevel) {
            CRPackets.PACKETS.sendTo(PlayerSelection.tracking(serverLevel, getBlockPos()), new TrackCouplerClientInfoPacket(this));
        }
    }

    private boolean isOkExceptGraph() {
        return cachedTrackState.getBlock() instanceof ITrackBlock && cachedSecondaryTrackState.getBlock() instanceof ITrackBlock &&
                cachedTrackState.hasProperty(TrackBlock.SHAPE) && cachedSecondaryTrackState.hasProperty(TrackBlock.SHAPE) &&
                cachedTrackState.getValue(TrackBlock.SHAPE) == cachedSecondaryTrackState.getValue(TrackBlock.SHAPE);
    }

    protected void updateOK() {
        if (!isOkExceptGraph()) {
            setError2(Components.literal("Wrong blocks or track shapes"));
            edgePointsOk = false;
            return;
        }
        if (((AccessorTrackTargetingBehavior) secondEdgePoint).getTargetTrack().equals(BlockPos.ZERO) || ((AccessorTrackTargetingBehavior) edgePoint).getTargetTrack().equals(BlockPos.ZERO)) {
            setError2(Components.literal("Missing edge point(s)"));
            edgePointsOk = false;
            return;
        }

        if (edgePoint.determineGraphLocation() == null || secondEdgePoint.determineGraphLocation() == null) {
            setError2(Components.literal("Edge point(s) missing graph location"));
            edgePointsOk = false;
            return;
        }

        if (edgePoint.determineGraphLocation().graph != secondEdgePoint.determineGraphLocation().graph) {
            setError2(Components.literal("Edge points not on same graph"));
            edgePointsOk = false;
            return;
        }

        //check that edgePoint and secondEdgePoint are on the same or adjacent TrackEdge
        Couple<TrackNodeLocation> edgePointLocations = edgePoint.determineGraphLocation().edge;
        Couple<TrackNodeLocation> secondEdgePointLocations = secondEdgePoint.determineGraphLocation().edge;
        if (edgePointLocations == null || secondEdgePointLocations == null) {
            setError2(Components.literal("Edge point(s) missing edge location"));
            edgePointsOk = false;
            return;
        }
        if (CRConfigs.server().strictCoupler.get()) {
            edgePointsOk = edgePointLocations.getFirst().equals(secondEdgePointLocations.getFirst()) || edgePointLocations.getFirst().equals(secondEdgePointLocations.getSecond()) ||
                    edgePointLocations.getSecond().equals(secondEdgePointLocations.getFirst()) || edgePointLocations.getSecond().equals(secondEdgePointLocations.getSecond());
            if (!edgePointsOk)
                setError2(Components.literal("Edge points not on same or adjacent edges"));
        } else {
            edgePointsOk = true;
        }
    }

    public boolean areEdgePointsOk() {
        return (level != null && level.isClientSide && clientInfo != null) ? clientInfo.edgePointsOk : edgePointsOk;
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
    @Nullable
    protected Carriage getCarriageOnPoint(@NotNull Train train, @NotNull TrackCoupler coupler, @NotNull TrackTargetingBehaviour<TrackCoupler> edgePoint, boolean leading) {
        for (Carriage carriage : train.carriages) {
            if (isCarriageWheelOnPoint(carriage, coupler, edgePoint, leading))
                return carriage;
        }
        return null;
    }

    protected boolean isCarriageWheelOnPoint(Carriage carriage, TrackCoupler coupler, TrackTargetingBehaviour<TrackCoupler> edgePoint, boolean leading) {
        TravellingPoint relevantPoint = leading ? carriage.leadingBogey().leading() : carriage.trailingBogey().trailing();
        TravellingPoint relevantPoint2 = leading ? carriage.leadingBogey().trailing() : carriage.trailingBogey().leading();
        CarriageBogey relevantBogey = leading ? carriage.leadingBogey() : carriage.trailingBogey();
        boolean upsideDown = relevantBogey.isUpsideDown();
        double couplerPosition = coupler.getLocationOn(relevantPoint.edge);
        Vec3 wheelPosition = relevantPoint.getPosition(carriage.train.graph).add(relevantPoint2.getPosition(carriage.train.graph)).scale(0.5).add(0, upsideDown ? 2 : 0, 0);
        Vec3 couplerSpatialPosition = Vec3.atBottomCenterOf(edgePoint.getGlobalPosition().above());
//        return (coupler.isPrimary(relevantPoint.node1) || coupler.isPrimary(relevantPoint.node2)) && Math.abs(relevantPoint.position - (couplerPosition+0.5)) < .75;
        return (coupler.isPrimary(relevantPoint.node1) || coupler.isPrimary(relevantPoint.node2) ||
                coupler.isPrimary(relevantPoint2.node1) || coupler.isPrimary(relevantPoint2.node2)) &&
                wheelPosition.distanceToSqr(couplerSpatialPosition) < .8 * .8;
    }

    public AllowedOperationMode getAllowedOperationMode() {
        return getBlockState().getValue(TrackCouplerBlock.MODE);
    }

    public OperationInfo getOperationInfo() {
        clearErrors();
        OperationInfo info = getOperationInfo(false);
        if (info.mode == OperationMode.NONE) {
            MutableComponent backupError = error;
            clearErrors();
            info = getOperationInfo(true);
            if (info.mode == OperationMode.NONE)
                error = backupError;
        }
        if (!info.mode.permitted(getAllowedOperationMode())) {
            clearErrors();
            setError(Components.translatable("railways.tooltip.coupler.error.mode_not_permitted"));
            return OperationInfo.NONE;
        }
        return info;
    }

    protected OperationInfo getOperationInfo(boolean reversed) {
        TrackCoupler coupler1 = reversed ? getSecondaryCoupler() : getCoupler();
        TrackCoupler coupler2 = reversed ? getCoupler() : getSecondaryCoupler();

        TrackTargetingBehaviour<TrackCoupler> edgePoint1 = reversed ? secondEdgePoint : edgePoint;
        TrackTargetingBehaviour<TrackCoupler> edgePoint2 = reversed ? edgePoint : secondEdgePoint;
        if (coupler1 != null && coupler2 != null && coupler1.isActivated() && coupler2.isActivated()) {
            Train primaryTrain = Create.RAILWAYS.trains.get(coupler1.getCurrentTrain());
            Train secondaryTrain = Create.RAILWAYS.trains.get(coupler2.getCurrentTrain());
            if (primaryTrain != null && primaryTrain == secondaryTrain) {
                //Decoupling, if back wheels of a carriage are on the secondary coupler and the front wheels of the carriage behind it are on the primary coupler
                Carriage frontCarriage = getCarriageOnPoint(primaryTrain, coupler2, edgePoint2, false);
                if (frontCarriage == null)
                    setError(Components.translatable("railways.tooltip.coupler.error.carriage_alignment"));
                if (frontCarriage != null && primaryTrain.carriages.indexOf(frontCarriage) < primaryTrain.carriages.size() - 1) {
                    Carriage backCarriage = primaryTrain.carriages.get(primaryTrain.carriages.indexOf(frontCarriage) + 1);
                    if (isCarriageWheelOnPoint(backCarriage, coupler1, edgePoint1, true) &&
                            Math.abs(primaryTrain.carriages.indexOf(frontCarriage) - primaryTrain.carriages.indexOf(backCarriage)) == 1) //Make sure that the carriages are actually next to each other
                        return new OperationInfo(OperationMode.DECOUPLING, frontCarriage, backCarriage);
                    else
                        setError(Components.translatable("railways.tooltip.coupler.error.carriage_alignment"));
                } else {
                    setError(Components.translatable("railways.tooltip.coupler.error.carriage_alignment"));
                }
            } else if (primaryTrain != null && secondaryTrain != null) {
                //Coupling if the front wheels of primaryTrain are on coupler1 and the back wheels of secondaryTrain are on coupler2
                Carriage primaryCarriage = getCarriageOnPoint(primaryTrain, coupler1, edgePoint1, true);
                Carriage secondaryCarriage = getCarriageOnPoint(secondaryTrain, coupler2, edgePoint2, false);
                if (primaryCarriage != null && secondaryCarriage != null && primaryTrain.carriages.indexOf(primaryCarriage) == 0 &&
                        secondaryTrain.carriages.indexOf(secondaryCarriage) == secondaryTrain.carriages.size() - 1) {
                    // ensure correct order when only one bogey (if 'outer' points are closer together than 'inner' points, then something is off
                    double outerLength = secondaryCarriage.getLeadingPoint().getPosition(secondaryTrain.graph)
                        .subtract(primaryCarriage.getTrailingPoint().getPosition(primaryTrain.graph))
                        .lengthSqr();
                    double innerLength = secondaryCarriage.getTrailingPoint().getPosition(secondaryTrain.graph)
                        .subtract(primaryCarriage.getLeadingPoint().getPosition(primaryTrain.graph))
                        .lengthSqr();
                    if (outerLength < innerLength) {
                        return OperationInfo.NONE;
                    } else {
                        return new OperationInfo(OperationMode.COUPLING, secondaryCarriage, primaryCarriage);
                    }
                } else {
                    if (primaryCarriage != null && getCarriageOnPoint(secondaryTrain, coupler2, edgePoint2, true) != null) {
                        setError(Components.translatable("railways.tooltip.coupler.error.carriage_orientation"));
                    } else if (secondaryCarriage != null && getCarriageOnPoint(primaryTrain, coupler1, edgePoint1, false) != null) {
                        setError(Components.translatable("railways.tooltip.coupler.error.carriage_orientation"));
                    } else {
                        setError(Components.translatable("railways.tooltip.coupler.error.carriage_alignment"));
                    }
                }
            } else {
                setError(Components.translatable("railways.tooltip.coupler.error.missing_train"));
            }
        } else {
            setError(Components.translatable("railways.tooltip.coupler.error.missing_train"));
        }
        return OperationInfo.NONE;
    }

    public OperationMode getOperationMode() {
        return getOperationInfo().mode;
    }

    public ClientInfo getClientInfo() {
        return clientInfo != null ? clientInfo : ClientInfo.FALLBACK;
    }

    public void setClientInfo(ClientInfo info) {
        clientInfo = info;
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

    public enum AllowedOperationMode implements StringRepresentable {
        BOTH(true, true),
        COUPLING(true, false),
        DECOUPLING(false, true);

        public final boolean canCouple;
        public final boolean canDecouple;

        AllowedOperationMode(boolean canCouple, boolean canDecouple) {
            this.canCouple = canCouple;
            this.canDecouple = canDecouple;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public Component getTranslatedName() {
            return Components.translatable("railways.coupler.mode." + getSerializedName());
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
            out += 4;
        else if (mode == OperationMode.COUPLING)
            out += 8;
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

    public static class ClientInfo {

        public static final ClientInfo FALLBACK = new ClientInfo(OperationMode.NONE, "??", "??", false, Components.literal("??"), Components.literal("??"));

        public OperationMode mode;
        public String trainName1;
        public String trainName2;
        public boolean edgePointsOk;
        public MutableComponent error;
        public MutableComponent error2;

        private ClientInfo(OperationMode mode, String trainName1, String trainName2, boolean edgePointsOk, MutableComponent error, MutableComponent error2) {
            this.mode = mode;
            this.trainName1 = trainName1;
            this.trainName2 = trainName2;
            this.edgePointsOk = edgePointsOk;
            this.error = error;
            this.error2 = error2;
        }

        protected ClientInfo(TrackCouplerBlockEntity te) {
            mode = te.getOperationMode();
            trainName1 = "None";
            trainName2 = "None";
            if (te.getCoupler() != null && te.getCoupler().isActivated()) {
                UUID trainId = te.getCoupler().getCurrentTrain();
                Train train = Create.RAILWAYS.trains.get(trainId);
                if (train != null)
                    trainName1 = train.name.getString();
            }
            if (te.getSecondaryCoupler() != null && te.getSecondaryCoupler().isActivated()) {
                UUID trainId = te.getSecondaryCoupler().getCurrentTrain();
                Train train = Create.RAILWAYS.trains.get(trainId);
                if (train != null)
                    trainName2 = train.name.getString();
            }
            edgePointsOk = te.edgePointsOk;
            error = te.error;
            error2 = te.error2;
        }

        public ClientInfo(CompoundTag tag) {
            mode = NBTHelper.readEnum(tag, "mode", OperationMode.class);
            trainName1 = tag.getString("trainName1");
            trainName2 = tag.getString("trainName2");
            edgePointsOk = tag.getBoolean("edgePointsOk");
            error = tag.contains("error") ? Component.Serializer.fromJson(tag.getString("error")) : null;
            error2 = tag.contains("error2") ? Component.Serializer.fromJson(tag.getString("error2")) : null;
        }

        public CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            NBTHelper.writeEnum(tag, "mode", mode);
            tag.putString("trainName1", trainName1);
            tag.putString("trainName2", trainName2);
            tag.putBoolean("edgePointsOk", edgePointsOk);
            if (error != null)
                tag.putString("error", Component.Serializer.toJson(error));
            if (error2 != null)
                tag.putString("error2", Component.Serializer.toJson(error2));
            return tag;
        }
    }

    private static LangBuilder b() {
        return Lang.builder(Railways.MODID);
    }

    /**
     * this method will be called when looking at a BlockEntity that implemented this
     * interface
     *
     * @param tooltip
     * @param isPlayerSneaking
     * @return {@code true} if the tooltip creation was successful and should be
     * displayed, or {@code false} if the overlay should not be displayed
     */
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        b().translate("tooltip.coupler.header").forGoggles(tooltip);
        b().translate("tooltip.coupler.mode")
                .style(ChatFormatting.YELLOW)
                .forGoggles(tooltip);
        b().translate("coupler.mode." + getAllowedOperationMode().getSerializedName())
                .style(ChatFormatting.YELLOW)
                .forGoggles(tooltip);

        String train1 = clientInfo == null ? "None" : clientInfo.trainName1;
        String train2 = clientInfo == null ? "None" : clientInfo.trainName2;
        OperationMode operationMode = clientInfo == null ? OperationMode.NONE : clientInfo.mode;
        b().translate("tooltip.coupler.train1", train1)
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip);
        b().translate("tooltip.coupler.train2", train2)
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip);

        b().translate("tooltip.coupler.action." + operationMode.name().toLowerCase(Locale.ROOT))
                .style(ChatFormatting.GREEN)
                .forGoggles(tooltip);
        if (clientInfo != null) {
            if (clientInfo.error != null) {
                b().add(clientInfo.error)
                        .style(ChatFormatting.DARK_RED)
                        .forGoggles(tooltip);
            }
            if (clientInfo.error2 != null && CRConfigs.client().showExtendedCouplerDebug.get()) {
                b().add(clientInfo.error2)
                        .style(ChatFormatting.DARK_PURPLE)
                        .forGoggles(tooltip);
            }
        }
        return true;
    }
}
