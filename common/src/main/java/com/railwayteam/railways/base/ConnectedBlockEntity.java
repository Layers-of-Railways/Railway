package com.railwayteam.railways.base;

import com.railwayteam.railways.Railways;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.tileEntity.IMultiTileContainer;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConnectedBlockEntity extends SmartTileEntity implements /*IHaveGoggleInformation,*/ IMultiTileContainer {
  public static final int CAPACITY = 1; // buckets
  protected LazyOptional<IFluidHandler> fluidCapability;
  protected FluidTank fluidContainer;

  BlockPos controller;
  protected int width;
  protected int length;
  protected int height;

  private static final int SYNC_RATE = 8;
  protected int syncCooldown;
  protected boolean queuedSync;

  public ConnectedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    fluidContainer = new SmartFluidTank(1000 * CAPACITY, this::onFluidStackChanged);
    fluidCapability = LazyOptional.of(()->fluidContainer);
    refreshCapability();
    controller = pos;
    width  = 1;
    length = 1;
    height = 1;
  }

  public float getFillState() {
    return (float) fluidContainer.getFluidAmount() / fluidContainer.getCapacity();
  }

  protected void onFluidStackChanged(FluidStack newFluidStack) {
    if (!hasLevel()) return;

    Railways.LOGGER.error("tank now has " + newFluidStack.getAmount() + "mB");

    if (!level.isClientSide) {
			setChanged();
			sendData();
		}
  }

  @Override
	public void initialize() {
		super.initialize();
		sendData();
	}

  @Override
  public void tick() {
    super.tick();
    if (syncCooldown > 0) {
      syncCooldown--;
      if (syncCooldown == 0 && queuedSync)
        sendData();
    }
  }

  @Override
  public void sendData() {
    if (syncCooldown > 0) {
      queuedSync = true;
      return;
    }
    super.sendData();
    queuedSync = false;
    syncCooldown = SYNC_RATE;
  }

  public void sendDataImmediately() {
		syncCooldown = 0;
		queuedSync = false;
		sendData();
	}

  @Override
  protected void write(CompoundTag tag, boolean clientPacket) {
  //  Railways.LOGGER.error("Writing tank packet. We are " + (isController()?"":"not ") + "the controller.");
    if (isController()) {
      tag.put("tankContents", fluidContainer.writeToNBT(new CompoundTag()));
    }
    else tag.put("controller", NbtUtils.writeBlockPos(controller));
    super.write(tag, clientPacket);
  }

  @Override
  protected void read(CompoundTag tag, boolean clientPacket) {
    super.read(tag, clientPacket);
    if (tag.contains("controller")) {
      controller = NbtUtils.readBlockPos(tag.getCompound("controller"));
    }
    else {
      fluidContainer.setCapacity(1000 * CAPACITY);
      fluidContainer.readFromNBT(tag.getCompound("tankContents"));
      Railways.LOGGER.error("Updating tank from packet to contain " + fluidContainer.getFluidAmount());
    }
  }

  private void refreshCapability() {
    LazyOptional<IFluidHandler> oldCap = fluidCapability;
    fluidCapability = LazyOptional.of(() -> isController() ? fluidContainer
    : getControllerTE() != null ? getControllerTE().fluidContainer : new FluidTank(0));
    oldCap.invalidate();
  }

  public ConnectedBlockEntity getControllerTE() {
    if (isController())
      return this;
    BlockEntity tileEntity = level.getBlockEntity(controller);
    if (tileEntity instanceof ConnectedBlockEntity)
      return (ConnectedBlockEntity) tileEntity;
    return null;
  }

  @NotNull
  @Override
  public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (!fluidCapability.isPresent()) refreshCapability();
    if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return fluidCapability.cast();
    return super.getCapability(cap, side);
  }

  ///* SmartTileEntity
  @Override
  public void addBehaviours(List<TileEntityBehaviour> behaviours) {}
  // */

  ///* MultiTileContainer
  @Override
  public BlockPos getController() {
    return worldPosition;
  }

  @Override
  public boolean isController() {
    return true;
  }

  @Override
  public void setController(BlockPos pos) {
    if (level.isClientSide && !isVirtual())
			return;
		if (pos.equals(this.controller))
			return;
		this.controller = pos;
		refreshCapability();
		setChanged();
		sendData();
  }

  @Override
  public void removeController(boolean keepContents) {

  }

  @Override
  public BlockPos getLastKnownPos() {
    return this.getBlockPos();
  }

  @Override
  public void preventConnectivityUpdate() {

  }

  @Override
  public void notifyMultiUpdated() {

  }

  @Override
  public Direction.Axis getMainConnectionAxis() { return getMainAxisOf(this); }

  @Override
  public int getMaxLength(Direction.Axis longAxis, int width) {
    if (longAxis == Direction.Axis.Y) return getMaxWidth();
    return getMaxLength(width);
  }

  public static int getMaxLength(int radius) {
    return radius * 3;
  }

  @Override
  public int getMaxWidth() {
    return 3;
  }

  @Override
  public int getHeight() { return length; }

  @Override
  public int getWidth() { return width; }

  @Override
  public void setHeight(int height) { this.length = height; }

  @Override
  public void setWidth(int width) { this.width = width; }
}
