/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.mixin_interfaces.IShadowTrain;
import com.railwayteam.railways.mixin_interfaces.RailwaySavedDataDuck;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GlobalRailwayManager.class)
public class MixinGlobalRailwayManager {
    @Shadow private RailwaySavedData savedData;

    @WrapOperation(method = "tickTrains", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/entity/Train;invalid:Z", opcode = Opcodes.GETFIELD))
    private boolean removeShadowTrains(Train instance, Operation<Boolean> original) {
        if (instance instanceof IShadowTrain shadowTrain && shadowTrain.railways$isShadow()) {
            // write all carriages to store their entities
            DimensionPalette dimensions = new DimensionPalette();
            for (Carriage carriage : instance.carriages) {
                carriage.write(dimensions);
            }
            ((RailwaySavedDataDuck) savedData).railway$getShadowTrains().put(instance.id, instance);
            ((RailwaySavedDataDuck) savedData).railways$getShadowKeys().put(shadowTrain.railways$getShadowKey(), instance.id);
            savedData.setDirty();
            return true;
        }
        return original.call(instance);
    }
}
