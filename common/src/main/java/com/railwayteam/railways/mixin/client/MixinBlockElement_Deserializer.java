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

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.model.BlockElement;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockElement.Deserializer.class)
public class MixinBlockElement_Deserializer {
    @Inject(method = "getFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockElement$Deserializer;getVector3f(Lcom/google/gson/JsonObject;Ljava/lang/String;)Lorg/joml/Vector3f;", shift = At.Shift.BY, by = 2), cancellable = true)
    private void shutUpSizeLimitFrom(JsonObject json, CallbackInfoReturnable<Vector3f> cir, @Local Vector3f vector3f) {
        cir.setReturnValue(vector3f);
    }

    @Inject(method = "getTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockElement$Deserializer;getVector3f(Lcom/google/gson/JsonObject;Ljava/lang/String;)Lorg/joml/Vector3f;", shift = At.Shift.BY, by = 2), cancellable = true)
    private void shutUpSizeLimitTo(JsonObject json, CallbackInfoReturnable<Vector3f> cir, @Local Vector3f vector3f) {
        cir.setReturnValue(vector3f);
    }
}
