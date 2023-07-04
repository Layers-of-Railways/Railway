package com.railwayteam.railways.content.switches;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.switches.TrackSwitchBlock.SwitchConstraint;
import com.railwayteam.railways.content.switches.TrackSwitchBlock.SwitchState;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.railwayteam.railways.registry.CRIcons;
import com.simibubi.create.content.contraptions.ITransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.*;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.railwayteam.railways.content.switches.TrackSwitchBlock.LOCKED;
import static java.util.stream.Collectors.toSet;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;


public class TrackSwitchTileEntity extends SmartBlockEntity implements ITransformableBlockEntity, IHaveGoggleInformation {
    public TrackTargetingBehaviour<TrackSwitch> edgePoint;
    private SwitchState state;
    private int lastAnalogOutput = 0;
    private final boolean[] previousPower = new boolean[6];

    protected ScrollOptionBehaviour<AutoMode> autoMode;

    int exitCount = 0; // client only

    /**
     * Only for use in ponders
     */
    @ApiStatus.Internal
    public void setStatePonder(SwitchState state) {
        this.state = state;
    }

    public record PonderData(Vec3 basePos, @Nullable Vec3 leftBranch, @Nullable Vec3 straightBranch, @Nullable Vec3 rightBranch) {
        Map<SwitchState, Vec3> getBranches() {
            Map<SwitchState, Vec3> branches = new HashMap<>();
            if (leftBranch != null)
                branches.put(SwitchState.REVERSE_LEFT, leftBranch);
            if (straightBranch != null)
                branches.put(SwitchState.NORMAL, straightBranch);
            if (rightBranch != null)
                branches.put(SwitchState.REVERSE_RIGHT, rightBranch);
            return branches;
        }
    }
    @ApiStatus.Internal
    public @Nullable PonderData ponderData;

    enum AutoMode implements INamedIconOptions {
        MANUAL_ONLY(CRIcons.I_SWITCH_MANUAL),
        AUTO(CRIcons.I_SWITCH_AUTO)
        ;

        private final String translationKey;
        private final AllIcons icon;

        AutoMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "railways.switch.auto_mode." + Lang.asId(name());
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

    @Nullable
    public TrackSwitch getSwitch() {
        return edgePoint == null ? null : edgePoint.getEdgePoint();
    }

    final LerpedFloat lerpedAngle = LerpedFloat.angular().chase(0.0, 0.3, LerpedFloat.Chaser.EXP);

    public TrackSwitchTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(edgePoint = new TrackTargetingBehaviour<>(this, CREdgePointTypes.SWITCH));
        if (isAutomatic()) {
            autoMode = new ScrollOptionBehaviour<>(AutoMode.class, Components.translatable("railways.switch.auto_mode"),
                    this, new ValueBoxTransform() {
                @Override
                public Vec3 getLocalOffset(BlockState state) {
                    Vec3 base = new Vec3(12 / 16.0, 4.5 / 16.0, 4 / 16.0);
                    base = VecHelper.rotateCentered(base, AngleHelper.horizontalAngle(state.getValue(FACING)), Direction.Axis.Y);
                    return base;
                }

                @Override
                public void rotate(BlockState state, PoseStack ms) {
                    TransformStack.cast(ms)
                            .rotateY(AngleHelper.horizontalAngle(state.getValue(FACING)) - 90)
                            .rotateX(90);
                }

                @Override
                public boolean testHit(BlockState state, Vec3 localHit) {
                    Vec3 offset = getLocalOffset(state);
                    if (offset == null)
                        return false;
                    return localHit.distanceTo(offset) < scale / 3;
                }
            });
            autoMode.withCallback(ordinal -> {
                AutoMode mode = AutoMode.values()[ordinal];
                TrackSwitch sw = getSwitch();
                if (sw != null)
                    sw.setAutoTrainsSwitch(mode == AutoMode.AUTO);
            });
            autoMode.requiresWrench();
            behaviours.add(autoMode);
        }
    }

    @Override
    public void transform(StructureTransform transform) {
        edgePoint.transform(transform);
    }

    public boolean isAutomatic() {
        if (getBlockState().getBlock() instanceof TrackSwitchBlock block) {
            return block.isAutomatic;
        }
        return false;
    }

    public boolean isLocked() {
        return getBlockState().getValue(TrackSwitchBlock.LOCKED);
    }

    private void enterState(SwitchState state) {
        if (this.state == state)
            return;
        this.state = state;
        notifyUpdate();
    }

    @NotNull
    public SwitchState getState() {
        return state == null ? SwitchState.NORMAL : state;
//        return getBlockState().getValue(STATE);
    }

    /*public void setState(SwitchState state) {
        updateSwitchState(trackSwitch -> {
            trackSwitch.setSwitchState(state);
            onStateChange();
        });
    }*/

    public boolean isNormal() {
        return getState() == SwitchState.NORMAL;
    }

    public boolean isReverseLeft() {
        return getState() == SwitchState.REVERSE_LEFT;
    }

    public boolean isReverseRight() {
        return getState() == SwitchState.REVERSE_RIGHT;
    }

    public boolean hasExit(SwitchState state) {
        TrackSwitch sw = getSwitch();
        if (sw == null)
            return false;
        return switch (state) {
            case NORMAL -> sw.hasStraightExit();
            case REVERSE_RIGHT -> sw.hasRightExit();
            case REVERSE_LEFT -> sw.hasLeftExit();
        };
    }

    public PartialModel getOverlayModel() {
        TrackSwitch sw = edgePoint.getEdgePoint();
        if (sw == null) {
            return null;
        }

        if (sw.hasStraightExit() && sw.hasLeftExit() && sw.hasRightExit()) {
            if (isNormal()) {
                return CRBlockPartials.SWITCH_3WAY_STRAIGHT;
            } else if (isReverseLeft()) {
                return CRBlockPartials.SWITCH_3WAY_LEFT;
            } else if (isReverseRight()) {
                return CRBlockPartials.SWITCH_3WAY_RIGHT;
            }
        } else if (sw.hasStraightExit() && sw.hasLeftExit()) {
            return isNormal() ? CRBlockPartials.SWITCH_LEFT_STRAIGHT
                    : CRBlockPartials.SWITCH_LEFT_TURN;
        } else if (sw.hasStraightExit() && sw.hasRightExit()) {
            return isNormal() ? CRBlockPartials.SWITCH_RIGHT_STRAIGHT
                    : CRBlockPartials.SWITCH_RIGHT_TURN;
        } else if (sw.hasLeftExit() && sw.hasRightExit()) {
            return isReverseLeft() ? CRBlockPartials.SWITCH_2WAY_LEFT
                    : CRBlockPartials.SWITCH_2WAY_RIGHT;
        }

        return CRBlockPartials.SWITCH_NONE;
    }

    void calculateExits(TrackSwitch sw) {
        TrackGraphLocation loc;
        try {
            loc = edgePoint.determineGraphLocation();
        } catch (ClassCastException ignored) { // if we are targeting air, catch the crash
            return;
        }
        if (loc == null)
            return;
        TrackGraph graph = loc.graph;
        TrackEdge edge = graph
                .getConnectionsFrom(graph.locateNode(loc.edge.getFirst()))
                .get(graph.locateNode(loc.edge.getSecond()));

        Set<TrackNodeLocation> exits = graph.getConnectionsFrom(edge.node2).values()
                .stream()
                .filter(e -> e != edge)
                // Edges with reversed nodes, i.e. (a, b) and (b, a)
                .filter(e -> !e.node1.getLocation().equals(edge.node2.getLocation())
                        || !e.node2.getLocation().equals(edge.node1.getLocation()))
                .map(e -> e.node2.getLocation())
                .collect(toSet());

        if (Math.abs(loc.position - (edge.getLength()-0.5)) > 0.5) {
            exits = Set.of();
        }

        sw.updateExits(edge.node2.getLocation(), exits);
    }

    private static LangBuilder b() {
        return Lang.builder(Railways.MODID);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        b().translate("tooltip.switch.header").forGoggles(tooltip);
        b().translate("tooltip.switch.state")
                .style(ChatFormatting.YELLOW)
                .forGoggles(tooltip);
        b().translate("switch.state." + getState().getSerializedName())
                .style(ChatFormatting.YELLOW)
                .forGoggles(tooltip);

        return true;
    }

    private final int clientLazyTickRate = 10;
    private int clientLazyTickCounter = 0;

    @Override
    public void tick() {
        super.tick();

        if (level != null) {
            TrackSwitch sw = getSwitch();
            if (sw != null) {
                sw.setLocked(isLocked());
            }
            if (level.isClientSide) {
                if (sw != null) {
                    sw.setSwitchState(state);
                    exitCount = sw.getExits().size();
                }
                lerpedAngle.tickChaser();
                clientLazyTickCounter++;
                if (clientLazyTickCounter >= clientLazyTickRate || (sw != null && sw.doForceTickClient())) {
                    clientLazyTick();
                    clientLazyTickCounter = 0;
                }
            } else {
                if (sw == null) {
                    enterState(SwitchState.NORMAL);
                    return;
                }
                enterState(sw.getSwitchState());
                if (getTargetAnalogOutput() != lastAnalogOutput) {
                    lastAnalogOutput = getTargetAnalogOutput();
                    level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
                }
            }
        }

        //checkRedstoneInputs();
    }

    // Borrowed from Create's StationBlockEntity
    /*private boolean updateSwitchState(Consumer<TrackSwitch> updateState) {
        TrackSwitch trackSwitch = getSwitch();
        TrackGraphLocation graphLocation = edgePoint.determineGraphLocation();
        if (trackSwitch == null || graphLocation == null)
            return false;

        updateState.accept(trackSwitch);
//        Create.RAILWAYS.sync.pointAdded(graphLocation.graph, trackSwitch);
//        Create.RAILWAYS.markTracksDirty();
        return true;
    }*/

    /*protected void onStateChange() {
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
            if (level.isClientSide)
                clientLazyTick();
        }
    }*/

    boolean cycleState() {
        return cycleState(SwitchConstraint.NONE);
    }

    boolean cycleState(SwitchConstraint constraint) {
        SwitchState oldState = getState();

        TrackSwitch sw = getSwitch();
        if (sw == null) {
            return false;
        }

        SwitchState newState = oldState.nextStateFor(sw, constraint);
        if (oldState != newState)
            return sw.setSwitchState(newState);
        return false;
    }

    InteractionResult onUse(boolean reverseDirection) {
        if (!isLocked()) {
            cycleState(reverseDirection ? SwitchConstraint.TO_LEFT : SwitchConstraint.TO_RIGHT);
//            level.setBlockAndUpdate(getBlockPos(), getBlockState());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    boolean onProjectileHit() {
        if (!isLocked()) {
            cycleState();
//            level.setBlockAndUpdate(getBlockPos(), getBlockState());
            return true;
        }
        return false;
    }

    private boolean hasSignal(Direction direction) {
        if (direction != Direction.UP && direction != Direction.DOWN) {
            direction = Direction.from2DDataValue(direction.get2DDataValue()
                    + getBlockState().getValue(FACING).get2DDataValue());
        }
        return getLevel() != null && getLevel().hasSignal(getBlockPos().relative(direction), direction);
    }

    private boolean hasNewSignal(Direction direction) {
        return hasNewSignal(direction, true);
    }

    private boolean hasNewSignal(Direction direction, boolean updatePreviousPower) {
        int i = direction.ordinal();
        boolean powered = hasSignal(direction);
        boolean ret = !previousPower[i] && powered;
        if (updatePreviousPower)
            previousPower[i] = powered;
        return ret;
    }

    void checkRedstoneInputs() {
        BlockState state = getBlockState();
        Level level = getLevel();
        BlockPos pos = getBlockPos();

        boolean alreadyLocked = isLocked();
        boolean shouldLock = hasSignal(Direction.DOWN);

        if (shouldLock ^ alreadyLocked) {
            level.setBlockAndUpdate(pos, state.setValue(LOCKED, shouldLock));
            TrackSwitch sw = getSwitch();
            if (sw != null) {
                sw.setLocked(shouldLock);
            }
//            updateSwitchState(sw -> sw.setLocked(shouldLock));
        }

        /*
        NORTH - right
        SOUTH - left
        EAST / WEST - straight
         */
        TrackSwitch sw = getSwitch();
        if (sw == null)
            return;
        if (hasNewSignal(Direction.EAST) || hasNewSignal(Direction.WEST)) {
            sw.setSwitchState(SwitchState.NORMAL);
        } else if (hasNewSignal(Direction.NORTH)) {
            sw.setSwitchState(SwitchState.REVERSE_RIGHT);
        } else if (hasNewSignal(Direction.SOUTH)) {
            sw.setSwitchState(SwitchState.REVERSE_LEFT);
        } else if (hasNewSignal(Direction.UP)) {
            cycleState();
        }
    }

    public void clientLazyTick() {
        try {
            if (getSwitch() != null && edgePoint.determineGraphLocation() != null)
                getSwitch().updateEdges(edgePoint.determineGraphLocation().graph);
        } catch (ClassCastException ignored) {} // if we are targeting air, catch the crash
    }

    /*protected void followAutomaticSwitching() {
        if (isAutomatic() && edgePoint.getEdgePoint() != null && edgePoint.determineGraphLocation() != null) {
            edgePoint.getEdgePoint().switchForEdges(edgePoint.determineGraphLocation().graph);
        }
    }*/

    protected void restoreEdges() {
        try {
            if (edgePoint.getEdgePoint() != null && edgePoint.determineGraphLocation() != null)
                edgePoint.getEdgePoint().setEdgesActive(edgePoint.determineGraphLocation().graph);
        } catch (ClassCastException ignored) {} // if we are targeting air, catch the crash
    }

    @Override
    public void remove() {
        super.remove();
        restoreEdges();
    }

    @Override
    public void destroy() {
        restoreEdges();
        super.destroy();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (getSwitch() != null) {
            calculateExits(getSwitch());
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (clientPacket)
            tag.putString("SwitchState", (state == null ? SwitchState.NORMAL : state).getSerializedName());
        tag.putInt("AnalogOutput", lastAnalogOutput);
        byte previousPowerState = 0;
        for (int i = 0; i < 6; i++) {
            previousPowerState |= (previousPower[i] ? 1 : 0) << i;
        }
        tag.putByte("PreviousPowerState", previousPowerState);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (clientPacket) {
            String switchState = tag.getString("SwitchState").toUpperCase(Locale.ROOT);
            try {
                state = SwitchState.valueOf(switchState);
            } catch (IllegalArgumentException e) {
                Railways.LOGGER.error("Failed to read SwitchState", e);
            }
        }
        lastAnalogOutput = tag.getInt("AnalogOutput");
        byte previousPowerState = tag.getByte("PreviousPowerState");
        for (int i = 0; i < 6; i++) {
            previousPower[i] = (previousPowerState & (1 << i)) != 0;
        }
    }

    public int getTargetAnalogOutput() {
        if (state == null)
            return 0;
        return switch (state) {
            case NORMAL -> 0;
            case REVERSE_LEFT -> 1;
            case REVERSE_RIGHT -> 2;
        };
    }
}
