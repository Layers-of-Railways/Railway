/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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
import com.railwayteam.railways.content.custom_bogeys.special.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.InvisibleMonoBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.createmod.catnip.data.Couple;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CarriageBogey.class, remap = false)
public class MixinCarriageBogey {
    @Shadow AbstractBogeyBlock<?> type;

    @Shadow public Couple<Vec3> couplingAnchors;

    // ensure that railways:mono_bogey_upside_down is updated to railways:mono_bogey and upside_down is set to true
    @WrapOperation(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getString(Ljava/lang/String;)Ljava/lang/String;", remap = true))
    private static String railways$updateMonoBogey(CompoundTag instance, String key, Operation<String> original) {
        String value = original.call(instance, key);
        if (value.equals("railways:mono_bogey_upside_down")) {
            value = "railways:mono_bogey";
            instance.putString(key, value);
            instance.putBoolean("UpsideDown", true);
        }
        return value;
    }

    @Inject(method = "updateCouplingAnchor", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/data/Couple;set(ZLjava/lang/Object;)V"), cancellable = true)
    private void railways$hideInvisibleCoupler(Vec3 entityPos, float entityXRot, float entityYRot, int bogeySpacing,
                                          float partialTicks, boolean leading, CallbackInfo ci) {
        if (type instanceof InvisibleBogeyBlock || type instanceof InvisibleMonoBogeyBlock) {
            couplingAnchors.set(leading, null);
            ci.cancel();
        }
    }
}
