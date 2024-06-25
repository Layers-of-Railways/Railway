/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(method = "burnFuel", at = @At(value = "INVOKE", target = "Lio/github/fabricators_of_create/porting_lib/transfer/TransferUtil;getNonEmpty(Lnet/fabricmc/fabric/api/transfer/v1/storage/Storage;)Ljava/lang/Iterable;"), remap = false)
    private <T> Iterable<? extends StorageView<T>> railways$disableFuelConsumptionBasedOnTag(Iterable<? extends StorageView<T>> original) {
        return () -> (Iterator<StorageView<T>>) Iterators.filter(original.iterator(), it ->
                !((ItemVariant) it.getResource()).getItem().getDefaultInstance().is(CRTags.AllItemTags.NOT_TRAIN_FUEL.tag)
        );
    }
}
