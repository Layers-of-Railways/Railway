package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.boiler.BoilerBigOutlines;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "pick(F)V", at = @At("TAIL"))
    private void railways$bigShapePickModifiedFromCreate(CallbackInfo ci) {
        BoilerBigOutlines.pick();
    }
}