package com.railwayteam.railways.content.switches;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.switches.TrackSwitchBlock.SwitchConstraint;
import com.railwayteam.railways.content.switches.TrackSwitchBlock.SwitchState;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CREdgePointTypes;
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
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.railwayteam.railways.content.switches.TrackSwitchBlock.LOCKED;
import static java.util.stream.Collectors.toSet;

/*
todo automatic switches should be auto-settable by moving trains (and therefore also not block navigation)
 */

public class TrackSwitchTileEntity extends SmartBlockEntity implements ITransformableBlockEntity, IHaveGoggleInformation {
    public TrackTargetingBehaviour<TrackSwitch> edgePoint;
    private SwitchState state;

    @Nullable
    public TrackSwitch getSwitch() {
        return edgePoint.getEdgePoint();
    }

    final LerpedFloat lerpedAngle = LerpedFloat.angular().chase(0.0, 0.3, LerpedFloat.Chaser.EXP);

    public TrackSwitchTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(edgePoint = new TrackTargetingBehaviour<>(this, CREdgePointTypes.SWITCH));
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

    public SwitchState getState() {
        return state;
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
        TrackGraphLocation loc = edgePoint.determineGraphLocation();
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
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    void onProjectileHit() {
        if (!isLocked()) {
            cycleState();
//            level.setBlockAndUpdate(getBlockPos(), getBlockState());
        }
    }

    private boolean hasSignal(Direction direction) {
        if (direction != Direction.UP && direction != Direction.DOWN) {
            direction = Direction.from2DDataValue(direction.get2DDataValue()
                    + getBlockState().getValue(TrackSwitchBlock.FACING).get2DDataValue());
        }
        return getLevel() != null && getLevel().hasSignal(getBlockPos().relative(direction), direction);
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
        if (hasSignal(Direction.EAST) || hasSignal(Direction.WEST)) {
            sw.setSwitchState(SwitchState.NORMAL);
        } else if (hasSignal(Direction.NORTH)) {
            sw.setSwitchState(SwitchState.REVERSE_RIGHT);
        } else if (hasSignal(Direction.SOUTH)) {
            sw.setSwitchState(SwitchState.REVERSE_LEFT);
        } else if (hasSignal(Direction.UP)) {
            cycleState();
        }
    }

    public void clientLazyTick() {
        if (getSwitch() != null && edgePoint.determineGraphLocation() != null)
            getSwitch().updateEdges(edgePoint.determineGraphLocation().graph);
    }

    /*protected void followAutomaticSwitching() {
        if (isAutomatic() && edgePoint.getEdgePoint() != null && edgePoint.determineGraphLocation() != null) {
            edgePoint.getEdgePoint().switchForEdges(edgePoint.determineGraphLocation().graph);
        }
    }*/

    protected void restoreEdges() {
        if (edgePoint.getEdgePoint() != null && edgePoint.determineGraphLocation() != null)
            edgePoint.getEdgePoint().setEdgesActive(edgePoint.determineGraphLocation().graph);
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
    }
}
