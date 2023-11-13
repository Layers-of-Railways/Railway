package com.railwayteam.railways.mixin.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface AccessorLevelRenderer {
    @Invoker
    @Nullable
    Particle callAddParticleInternal(ParticleOptions options, boolean force, boolean decreased,
                                     double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
}
