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

package com.railwayteam.railways.mixin;

import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.mixin_interfaces.IShadowTrain;
import com.railwayteam.railways.mixin_interfaces.RailwaySavedDataDuck;
import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Mixin(RailwaySavedData.class)
public class MixinRailwaySavedData implements RailwaySavedDataDuck {
    @Shadow private Map<UUID, Train> trains;

    @Unique
    private Map<UUID, Train> railways$shadowTrains = new HashMap<>();

    @Unique
    private Map<ResourceLocation, UUID> railways$shadowKeys = new HashMap<>();

    @Override
    public Map<UUID, Train> railway$getShadowTrains() {
        return railways$shadowTrains;
    }

    @Override
    public Map<ResourceLocation, UUID> railways$getShadowKeys() {
        return railways$shadowKeys;
    }

    @Inject(method = "lambda$load$5", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
        cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private static void railways$deleteEmptyTrains(RailwaySavedData sd, DimensionPalette dimensions, CompoundTag c,
                                              CallbackInfo ci, Train train) { // delete trains with 0 carriages
        if (train.carriages.isEmpty()) // just don't add it to the list of trains
            ci.cancel();
    }

    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;)Lcom/simibubi/create/content/trains/RailwaySavedData;", at = @At("RETURN"))
    private static void loadShadowTrains(CompoundTag nbt, CallbackInfoReturnable<RailwaySavedData> cir) {
        RailwaySavedData sd = cir.getReturnValue();
        if (sd == null) return;

        MixinRailwaySavedData self = (MixinRailwaySavedData) (Object) sd;
        self.railways$shadowTrains = new HashMap<>();
        self.railways$shadowKeys = new HashMap<>();

        var iter = self.trains.values().iterator();
        while (iter.hasNext()) {
            Train train = iter.next();
            ResourceLocation shadowKey = ((IShadowTrain) train).railways$getShadowKey();
            if (shadowKey != null) {
                iter.remove();
                self.railways$shadowTrains.put(train.id, train);
                self.railways$shadowKeys.put(shadowKey, train.id);
            }
        }
    }

    @WrapOperation(
        method = "save",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/utility/NBTHelper;writeCompoundList(Ljava/lang/Iterable;Ljava/util/function/Function;)Lnet/minecraft/nbt/ListTag;",
            ordinal = 2 // trains
        )
    )
    private <T> ListTag saveShadowTrains(Iterable<T> list, Function<T, CompoundTag> serializer, Operation<ListTag> original) {
        return original.call(Iterables.concat(list, railways$shadowTrains.values()), serializer);
    }
}
