package com.railwayteam.railways.content.smokestack.particles.puffs;

import com.railwayteam.railways.content.smokestack.particles.CustomAnimatedTextureSheetParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PuffSmokeParticle extends CustomAnimatedTextureSheetParticle {
    public static final int DOUBLE_SPEED_SENTINEL = 42;
    protected final boolean stationarySource;
    protected final RandomSource random;
    protected PuffSmokeParticle(ClientLevel level, double x, double y, double z, RandomSource random, boolean stationarySource, double ySpeed) {
        super(level, x, y, z, 0.0, ySpeed, 0.0);
        if (Mth.equal(DOUBLE_SPEED_SENTINEL, ySpeed)) {
            this.yd *= 1.5;
        }
        this.quadSize = 1.0f;
        this.friction = 0.99f;
        this.random = random;
        this.stationarySource = stationarySource;
        setLifetime(random.nextIntBetweenInclusive(65, 105) + (stationarySource ? 40 : 0));
    }

    @Override
    protected double getAnimationProgress() {
        int age = Math.min(this.age, this.lifetime - 1);
        double lifeFactor = age / (double) this.lifetime;
        double f = 0.1;
        lifeFactor = this.stationarySource ? lifeFactor * lifeFactor : Math.max(0, ((lifeFactor - f) * (lifeFactor - f)) - (f * f));
        return lifeFactor;
    }

    @Override
    public void tick() {
        super.tick();

        float diffusionScale = stationarySource ? 800.0f : 500.0f;
        if (this.age > 350) {
            diffusionScale = 5000.0f;
        } else if (this.age > 300) {
            diffusionScale = Mth.lerp((age-300) / 50.0f, stationarySource ? 800.0f : 500.0f, 5000.0f);
        }
        this.xd += this.random.nextFloat() / diffusionScale * (float)(this.random.nextBoolean() ? 1 : -1);
        this.zd += this.random.nextFloat() / diffusionScale * (float)(this.random.nextBoolean() ? 1 : -1);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory<T extends PuffSmokeParticleData<T>> implements ParticleProvider<T> {
        private final SpriteSet spriteSet;
        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull T type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            PuffSmokeParticle particle = new PuffSmokeParticle(level, x, y, z, level.getRandom(), type.stationary, ySpeed);
            int textureCount = 3;
            int idx = 0;
            if (Mth.equal(type.red, -1) && Mth.equal(type.green, -1) && Mth.equal(type.blue, -1)) {
                idx = 1;
            } else if (Mth.equal(type.red, -2) && Mth.equal(type.green, -2) && Mth.equal(type.blue, -2)) {
                idx = 2;
            }
            particle.setSprite(spriteSet.get(idx, textureCount - 1));
            particle.age = level.getRandom().nextInt(5);
            if (idx == 0) {
                particle.rCol = type.red;
                particle.gCol = type.green;
                particle.bCol = type.blue;
            }
            particle.quadSize = type.getQuadSize();
            return particle;
        }
    }
}
