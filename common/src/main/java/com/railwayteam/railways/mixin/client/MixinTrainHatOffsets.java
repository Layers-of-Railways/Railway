/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.conductor.ConductorEntityModel;
import com.simibubi.create.content.trains.schedule.TrainHatOffsets;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrainHatOffsets.class, remap = false)
public class MixinTrainHatOffsets {
  @Inject(at = @At("HEAD"), method = "getOffset", cancellable = true)
  private static void inj$getOffset(EntityModel<?> model, CallbackInfoReturnable<Vec3> cir) {
    if (model instanceof ConductorEntityModel) {
      cir.setReturnValue(new Vec3(0f, -1f, 0f));
    }
  }
}
