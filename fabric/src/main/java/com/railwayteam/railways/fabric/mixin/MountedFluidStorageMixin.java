package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.content.contraptions.sync.ContraptionFluidPacket;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MountedFluidStorage.class)
public abstract class MountedFluidStorageMixin {
    @Shadow protected abstract void onFluidStackChanged(FluidStack fs);

    @Shadow private int packetCooldown;

    @Shadow private boolean sendPacket;

    @Shadow private SmartFluidTank tank;

    @Shadow private BlockEntity blockEntity;

    @Inject(method = "canUseAsStorage", at = @At("HEAD"), cancellable = true)
    private static void canUseAsStorage(BlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((be instanceof FluidTankBlockEntity && ((FluidTankBlockEntity) be).isController()) ||
                (be instanceof FuelTankBlockEntity && ((FuelTankBlockEntity) be).isController()));
    }

    @Inject(method = "createMountedTank", at = @At("HEAD"), cancellable = true)
    private void createMountedTank(BlockEntity be, CallbackInfoReturnable<FuelTankBlockEntity.FuelFluidHandler> cir) {
        if (be instanceof FuelTankBlockEntity)
            cir.setReturnValue(new FuelTankBlockEntity.FuelFluidHandler(
                    ((FuelTankBlockEntity) be).getTotalTankSize() * FuelTankBlockEntity.getCapacityMultiplier(),
                    this::onFluidStackChanged));
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(Entity entity, BlockPos pos, boolean isRemote, CallbackInfo ci) {
        if (!isRemote) {
            if (packetCooldown > 0)
                packetCooldown--;
            else if (sendPacket) {
                sendPacket = false;
                AllPackets.getChannel().sendToClientsTracking(new ContraptionFluidPacket(entity.getId(), pos, tank.getFluid()), entity);
                packetCooldown = 8;
            }
            return;
        }

        if (blockEntity instanceof FuelTankBlockEntity) {
            FuelTankBlockEntity tank = (FuelTankBlockEntity) blockEntity;
            tank.getFluidLevel().tickChaser();
        } else if (blockEntity instanceof FluidTankBlockEntity) {
            FluidTankBlockEntity tank = (FluidTankBlockEntity) blockEntity;
            tank.getFluidLevel().tickChaser();
        } else {
            return;
        }

        ci.cancel();
    }

    @Inject(method = "updateFluid", at = @At("HEAD"), cancellable = true, remap = false)
    public void updateFluid(FluidStack fluid, CallbackInfo ci) {
        tank.setFluid(fluid);
        if (blockEntity instanceof FluidTankBlockEntity) {
            float fillState = tank.getFluidAmount() / (float) tank.getCapacity();
            FluidTankBlockEntity tank = (FluidTankBlockEntity) blockEntity;
            if (tank.getFluidLevel() == null)
                tank.setFluidLevel(LerpedFloat.linear()
                        .startWithValue(fillState));
            tank.getFluidLevel()
                    .chase(fillState, 0.5, LerpedFloat.Chaser.EXP);
            FluidTank tankInventory = tank.getTankInventory();
            if (tankInventory instanceof SmartFluidTank)
                tankInventory.setFluid(fluid);
        } else if (blockEntity instanceof FuelTankBlockEntity) {
            float fillState = tank.getFluidAmount() / (float) tank.getCapacity();
            FuelTankBlockEntity tank = (FuelTankBlockEntity) blockEntity;
            if (tank.getFluidLevel() == null)
                tank.setFluidLevel(LerpedFloat.linear()
                        .startWithValue(fillState));
            tank.getFluidLevel()
                    .chase(fillState, 0.5, LerpedFloat.Chaser.EXP);
            FluidTank tankInventory = tank.getTankInventory();
            if (tankInventory instanceof SmartFluidTank)
                tankInventory.setFluid(fluid);
        } else {
            return;
        }

        ci.cancel();
    }
}
