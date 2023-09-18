package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MountedFluidStorage.class)
public abstract class MountedFluidStorageMixin {
    @Shadow protected abstract void onFluidStackChanged(FluidStack fs);

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
}
