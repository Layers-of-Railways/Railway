package com.railwayteam.railways.mixin.client;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockElement;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockElement.Deserializer.class)
public class MixinBlockElement_Deserializer {
    @Inject(method = "getFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockElement$Deserializer;getVector3f(Lcom/google/gson/JsonObject;Ljava/lang/String;)Lorg/joml/Vector3f;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void shutUpSizeLimitFrom(JsonObject json, CallbackInfoReturnable<Vector3f> cir, Vector3f vector3f) {
        cir.setReturnValue(vector3f);
    }

    @Inject(method = "getTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockElement$Deserializer;getVector3f(Lcom/google/gson/JsonObject;Ljava/lang/String;)Lorg/joml/Vector3f;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void shutUpSizeLimitTo(JsonObject json, CallbackInfoReturnable<Vector3f> cir, Vector3f vector3f) {
        cir.setReturnValue(vector3f);
    }
}
