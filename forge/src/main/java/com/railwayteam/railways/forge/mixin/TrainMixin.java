package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Train.class, remap = false)
public class TrainMixin {
    @Shadow public double speed;
    @Shadow public List<Carriage> carriages;
    @Shadow public int fuelTicks;

    @Inject(method = "burnFuel", at = @At("TAIL"))
    private void burnFuel(CallbackInfo ci) {
        boolean iterateFromBack = speed < 0;
        int carriageCount = carriages.size();

        for (int index = 0; index < carriageCount; index++) {
            int i = iterateFromBack ? carriageCount - 1 - index : index;
            Carriage carriage = carriages.get(i);
            IFluidHandler fuelItems = ((IFuelInventory) carriage.storage).snr$getFuelFluids();
            if (fuelItems == null)
                continue;

            for (int tanks = 0; tanks < fuelItems.getTanks(); tanks++) {
                // Extract 100 Mb worth of fluid (1/10th of a bucket)
                FluidStack fluidStack = fuelItems.drain(100, IFluidHandler.FluidAction.EXECUTE);
                int burnTime = ForgeHooks.getBurnTime(fluidStack.getFluid().getBucket().getDefaultInstance(), null);
                if (burnTime <= 0)
                    continue;
                // Divide burnTime by 100 to get burnTime for 1/10th of a bucket and then by divide by 4,
                // so it isn't so strong
                fuelTicks += (burnTime / 100) / 4;
                return;
            }
        }
    }
}

