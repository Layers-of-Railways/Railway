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

package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.registry.CREntities;
import com.simibubi.create.content.trains.schedule.hat.TrainHatArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrainHatArmorLayer.class)
public class MixinTrainHatArmorLayer {
    @Inject(method = "shouldRenderOn", at = @At("HEAD"), cancellable = true)
    private void railways$shouldRenderOn(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        // If its conductor and the config says to not render the double cap then dont render it
        if (entity != null && CREntities.CONDUCTOR.is(entity) && !CRConfigs.client().renderNormalCap.get())
            cir.setReturnValue(false);
    }
}
