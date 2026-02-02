/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2026 The Railways Team
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

package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.content.switches.TrainHUDSwitchExtension;
import com.railwayteam.railways.mixin_interfaces.ITrueMaxSpeedTrain;
import com.simibubi.create.content.trains.TrainHUD;
import com.simibubi.create.content.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrainHUD.class, remap = false)
public class MixinTrainHUD {
    @Inject(method = "tick", at = @At("HEAD"))
    private static void tickHook(CallbackInfo ci) {
        TrainHUDSwitchExtension.tick();
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;maxSpeed()F"))
    private static float unlimitMaxSpeed(Train instance, Operation<Float> original) {
        ((ITrueMaxSpeedTrain) instance).railways$setLimitBypass(true);
        float result = original.call(instance);
        ((ITrueMaxSpeedTrain) instance).railways$setLimitBypass(false);
        return result;
    }
}
