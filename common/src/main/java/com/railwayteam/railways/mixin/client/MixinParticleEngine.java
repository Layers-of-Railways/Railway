package com.railwayteam.railways.mixin.client;

import com.google.common.collect.ImmutableList;
import com.railwayteam.railways.content.smokestack.particles.legacy.SmokeParticle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ParticleEngine.class)
public class MixinParticleEngine {
    @Mutable
    @Shadow @Final private static List<ParticleRenderType> RENDER_ORDER;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void railways$addRenderType(CallbackInfo ci) {
        RENDER_ORDER = ImmutableList.<ParticleRenderType>builder()
            .addAll(RENDER_ORDER)
            .add(SmokeParticle.TRANSPARENT_SMOKE)
            .build();
    }
}
