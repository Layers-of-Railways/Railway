package com.railwayteam.railways.fabric.mixin;

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.content.trains.entity.Train;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;

@Mixin(Train.class)
public class TrainMixin {
    @ModifyExpressionValue(method = "burnFuel", at = @At(value = "INVOKE", target = "Lio/github/fabricators_of_create/porting_lib/transfer/TransferUtil;getNonEmpty(Lnet/fabricmc/fabric/api/transfer/v1/storage/Storage;)Ljava/lang/Iterable;"), remap = false)
    private <T> Iterable<? extends StorageView<T>> railways$disableFuelConsumptionBasedOnTag(Iterable<? extends StorageView<T>> original) {
        return () -> (Iterator) Iterators.filter(original.iterator(), it ->
                !((ItemVariant) it.getResource()).getItem().getDefaultInstance().is(CRTags.AllItemTags.NOT_TRAIN_FUEL.tag)
        );
    }
}
