package com.railwayteam.railways.content.switches;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.switches.TrackSwitchBlock.SwitchState;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.contraptions.components.structureMovement.ITransformableTE;
import com.simibubi.create.content.contraptions.components.structureMovement.StructureTransform;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.railwayteam.railways.content.switches.TrackSwitchBlock.*;

public class TrackSwitchTileEntity extends SmartTileEntity implements ITransformableTE, IHaveGoggleInformation {
  public TrackTargetingBehaviour<TrackSwitch> edgePoint;

  public TrackSwitchTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void addBehaviours(List<TileEntityBehaviour> behaviours) {
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

  public boolean isPowered() {
    return getBlockState().getValue(TrackSwitchBlock.POWERED);
  }

  public boolean isNormal() {
    return getBlockState().getValue(STATE) == SwitchState.NORMAL;
  }

  public boolean isReverse() {
    SwitchState s = getBlockState().getValue(STATE);
    return s == SwitchState.REVERSE_LEFT || s == SwitchState.REVERSE_RIGHT;
  }

  public boolean isReverseLeft() {
    return getBlockState().getValue(STATE) == SwitchState.REVERSE_LEFT;
  }

  public boolean isReverseRight() {
    return getBlockState().getValue(STATE) == SwitchState.REVERSE_RIGHT;
  }

  public boolean hasLeftExit() {
    SwitchExits exits = getBlockState().getValue(EXITS);
    return exits == SwitchExits.LEFT || exits == SwitchExits.BOTH;
  }

  public boolean hasRightExit() {
    SwitchExits exits = getBlockState().getValue(EXITS);
    return exits == SwitchExits.RIGHT || exits == SwitchExits.BOTH;
  }

  public boolean hasBothExits() {
    return getBlockState().getValue(TrackSwitchBlock.EXITS) == SwitchExits.BOTH;
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
    b().translate("switch.state." + getBlockState().getValue(STATE).getSerializedName())
      .style(ChatFormatting.YELLOW)
      .forGoggles(tooltip);

    return true;
  }

  @Override
  public void tick() {
    super.tick();

    BlockState state = getBlockState();
    Level level = getLevel();
    BlockPos pos = getBlockPos();

    boolean alreadyPowered = isPowered();
    boolean hasSignal = level.hasNeighborSignal(pos);

    if (hasSignal && !alreadyPowered) {
      level.setBlockAndUpdate(pos, setPowered(state));
    } else if (!hasSignal && alreadyPowered) {
      level.setBlockAndUpdate(pos, setUnpowered(state));
    }
  }
}
