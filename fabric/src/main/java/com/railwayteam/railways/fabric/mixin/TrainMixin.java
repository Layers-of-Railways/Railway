package com.railwayteam.railways.fabric.mixin;

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(value = Train.class, remap = false)
public class TrainMixin {
    @Shadow public double speed;
    @Shadow public List<Carriage> carriages;
    @Shadow public int fuelTicks;

    @ModifyExpressionValue(method = "burnFuel", at = @At(value = "INVOKE", target = "Lio/github/fabricators_of_create/porting_lib/transfer/TransferUtil;getNonEmpty(Lnet/fabricmc/fabric/api/transfer/v1/storage/Storage;)Ljava/lang/Iterable;"))
    private <T> Iterable<? extends StorageView<T>> railways$disableFuelConsumptionBasedOnTag(Iterable<? extends StorageView<T>> original) {
        return () -> (Iterator) Iterators.filter(original.iterator(), it ->
                !((ItemVariant) it.getResource()).getItem().getDefaultInstance().is(CRTags.AllItemTags.NOT_TRAIN_FUEL.tag)
        );
    }

    @Inject(method = "burnFuel", at = @At("TAIL"))
    private void railways$burnFuel(CallbackInfo ci) {
        boolean iterateFromBack = speed < 0;
        int carriageCount = carriages.size();

        for (int index = 0; index < carriageCount; index++) {
            int i = iterateFromBack ? carriageCount - 1 - index : index;
            Carriage carriage = carriages.get(i);
            CombinedTankWrapper fuelFluids = ((IFuelInventory) carriage.storage).railways$getFuelFluids();
            if (fuelFluids == null)
                continue;

            try (Transaction t = TransferUtil.getTransaction()) {
                for (StorageView<FluidVariant> view : TransferUtil.getNonEmpty(fuelFluids)) {
                    FluidVariant held = view.getResource();

                    Integer burnTime = FuelRegistry.INSTANCE.get(held.getFluid().getBucket());
                    if (burnTime == null || burnTime <= 0)
                        continue;
                    // Extract 100 Mb worth of fluid (1/10th of a bucket)
                    if (view.extract(held, 8100, t) != 8100)
                        continue;
                    // Divide burnTime by 100 to get burnTime for 1/10th of a bucket and then by divide by 4,
                    // so it isn't so strong
                    fuelTicks += (burnTime / 100) / 4;
                    t.commit();
                    return;
                }
            }
        }
    }
}
