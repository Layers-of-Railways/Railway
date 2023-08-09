package com.railwayteam.railways.forge.mixin;

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

@Mixin(Train.class)
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
            IFluidHandler fuelItems = carriage.storage.getFluids();
            if (fuelItems == null)
                continue;

            for (int tanks = 0; tanks < fuelItems.getTanks(); tanks++) {
                FluidStack fluidStack = fuelItems.drain(81000, IFluidHandler.FluidAction.EXECUTE);
                int burnTime = ForgeHooks.getBurnTime(fluidStack.getFluid().getBucket().getDefaultInstance(), null);
                if (burnTime <= 0)
                    continue;

                fuelTicks += burnTime / 4;
                return;
            }
        }
    }
}

