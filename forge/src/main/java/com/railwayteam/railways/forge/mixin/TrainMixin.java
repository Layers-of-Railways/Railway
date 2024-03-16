package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Train.class, remap = false)
public class TrainMixin {
    @Shadow public double speed;
    @Shadow public List<Carriage> carriages;
    @Shadow public int fuelTicks;

    @ModifyArg(method = "burnFuel", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getBurnTime(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/RecipeType;)I"))
    private ItemStack railways$disableFuelConsumptionBasedOnTag(ItemStack stack) {
        if (stack.is(CRTags.AllItemTags.NOT_TRAIN_FUEL.tag)) {
            return Items.AIR.getDefaultInstance();
        }
        return stack;
    }

    @Inject(method = "burnFuel", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", shift = At.Shift.AFTER))
    private void railways$burnFuel(CallbackInfo ci) {
        boolean iterateFromBack = speed < 0;
        int carriageCount = carriages.size();

        for (int index = 0; index < carriageCount; index++) {
            int i = iterateFromBack ? carriageCount - 1 - index : index;
            Carriage carriage = carriages.get(i);
            IFluidHandler fuelItems = ((IFuelInventory) carriage.storage).railways$getFuelFluids();
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

