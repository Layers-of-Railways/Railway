package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MountedFluidStorage.class, remap = false)
public abstract class MountedFluidStorageMixin {
    @Shadow
    protected abstract void onFluidStackChanged(FluidStack fs);

    @Shadow private BlockEntity blockEntity;

    @Shadow private SmartFluidTank tank;

    @Inject(method = "canUseAsStorage", at = @At("HEAD"), cancellable = true)
    private static void canUseAsStorage(BlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((be instanceof FluidTankBlockEntity && ((FluidTankBlockEntity) be).isController()) ||
                (be instanceof FuelTankBlockEntity && ((FuelTankBlockEntity) be).isController()));
    }

    @Inject(method = "createMountedTank", at = @At("HEAD"), cancellable = true)
    private void createMountedTank(BlockEntity be, CallbackInfoReturnable<SmartFluidTank> cir) {
        if (be instanceof FuelTankBlockEntity)
            cir.setReturnValue(new SmartFluidTank(
                    ((FuelTankBlockEntity) be).getTotalTankSize() * FuelTankBlockEntity.getCapacityMultiplier(),
                    this::onFluidStackChanged));
    }

    @Inject(method = "tick", at = @At(value = "CONSTANT", args = "classValue=com/simibubi/create/content/fluids/tank/FluidTankBlockEntity"))
    public void tick(Entity entity, BlockPos pos, boolean isRemote, CallbackInfo ci) {
        if (blockEntity instanceof FuelTankBlockEntity tank)
            tank.getFluidLevel().tickChaser();
    }

    @Inject(method = "updateFluid", at = @At("HEAD"), cancellable = true)
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
            IFluidTank tankInventory = tank.getTankInventory();
            if (tankInventory instanceof SmartFluidTank)
                ((SmartFluidTank) tankInventory).setFluid(fluid);
        } else if (blockEntity instanceof FuelTankBlockEntity) {
            float fillState = tank.getFluidAmount() / (float) tank.getCapacity();
            FuelTankBlockEntity tank = (FuelTankBlockEntity) blockEntity;
            if (tank.getFluidLevel() == null)
                tank.setFluidLevel(LerpedFloat.linear()
                        .startWithValue(fillState));
            tank.getFluidLevel()
                    .chase(fillState, 0.5, LerpedFloat.Chaser.EXP);
            IFluidTank tankInventory = tank.getTankInventory();
            if (tankInventory instanceof SmartFluidTank)
                ((SmartFluidTank) tankInventory).setFluid(fluid);
        } else {
            return;
        }

        ci.cancel();
    }
}
