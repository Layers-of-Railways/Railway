/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

package com.railwayteam.railways.content.smokestack;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.smokestack.particles.legacy.SmokeParticleData;
import com.railwayteam.railways.content.smokestack.particles.puffs.PuffSmokeParticle;
import com.railwayteam.railways.content.smokestack.particles.puffs.PuffSmokeParticleData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record SmokeEmissionParams(
    Vec3 particleSpawnOffset,
    Vec3 particleSpawnDelta,
    double particleSpawnChance,
    int minParticles,
    int maxParticles
) {
    public SmokeEmissionParams(double xOffset, double yOffset, double zOffset) {
        this(new Vec3(xOffset, yOffset, zOffset));
    }

    public SmokeEmissionParams(Vec3 particleSpawnOffset) {
        this(particleSpawnOffset, new Vec3(0.3, 2.0, 0.3));
    }

    public SmokeEmissionParams(Vec3 particleSpawnOffset, Vec3 particleSpawnDelta) {
        this(particleSpawnOffset, particleSpawnDelta, 2, 4);
    }

    public SmokeEmissionParams(Vec3 particleSpawnOffset, Vec3 particleSpawnDelta, int minParticles, int maxParticles) {
        this(particleSpawnOffset, particleSpawnDelta, 1.0F, minParticles, maxParticles);
    }

    public void makeParticles(Level level, Vec3 pos, boolean isSignalFire, double speedMultiplier, boolean stationary, @Nullable DyeColor color, @Nullable Boolean small, boolean isSoul) {
        RandomSource random = level.getRandom();
        SmokeType smokeType = CRConfigs.client().smokeType.get();
        if (small == null) small = random.nextDouble() < 0.33;

        switch (smokeType) {
            case VANILLA -> {
                SimpleParticleType particleType = isSignalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
                level.addAlwaysVisibleParticle(particleType, true,
                    pos.x() + particleSpawnOffset.x + random.nextDouble() * particleSpawnDelta.x * (double)(random.nextBoolean() ? 1 : -1),
                    pos.y() + random.nextDouble() * particleSpawnDelta.y + particleSpawnOffset.y,
                    pos.z() + particleSpawnOffset.z + random.nextDouble() * particleSpawnDelta.z * (double)(random.nextBoolean() ? 1 : -1),
                    0.0D, 0.07D*speedMultiplier / (stationary ? 1. : 25.), 0.0D);
            }
            case OLD -> {
                ParticleOptions particleType;
                if (color != null) {
                    float[] c = color.getTextureDiffuseColors();
                    particleType = new SmokeParticleData(stationary, c[0], c[1], c[2]);
                } else {
                    particleType = new SmokeParticleData(stationary);
                }
                level.addAlwaysVisibleParticle(particleType, true,
                    pos.x() + particleSpawnOffset.x + random.nextDouble() * particleSpawnDelta.x * (random.nextDouble() * 2 - 1),
                    pos.y() + random.nextDouble() * particleSpawnDelta.y + particleSpawnOffset.y + 0.5,
                    pos.z() + particleSpawnOffset.z + random.nextDouble() * particleSpawnDelta.z * (random.nextDouble() * 2 - 1),
                    0.0D, 0.07D * speedMultiplier * (stationary ? 25 : 1), 0.0D);
            }
            case CARTOON -> {
                ParticleOptions particleType;
                if (isSoul) {
                    particleType = PuffSmokeParticleData.create(small, stationary, -2, -2, -2);
                } else if (color != null) {
                    particleType = PuffSmokeParticleData.create(small, stationary, color);
                } else {
                    particleType = PuffSmokeParticleData.create(small, stationary);
                }
                level.addAlwaysVisibleParticle(particleType, true,
                    pos.x() + particleSpawnOffset.x + random.nextDouble() * particleSpawnDelta.x * (random.nextDouble() * 2 - 1),
                    pos.y() + random.nextDouble() * particleSpawnDelta.y + particleSpawnOffset.y + 0.5,
                    pos.z() + particleSpawnOffset.z + random.nextDouble() * particleSpawnDelta.z * (random.nextDouble() * 2 - 1),
                    0.0D, Mth.equal(speedMultiplier, -1) ? PuffSmokeParticle.DOUBLE_SPEED_SENTINEL : 2.1, 0.0D);
            }
        }

        // extra
        if (smokeType != SmokeType.CARTOON) {
            level.addParticle(ParticleTypes.SMOKE,
                pos.x() + particleSpawnOffset.x + random.nextDouble() * particleSpawnDelta.x * 0.75d * (double)(random.nextBoolean() ? 1 : -1),
                pos.y() + particleSpawnOffset.y - 0.1d,
                pos.z() + particleSpawnOffset.z + random.nextDouble() * particleSpawnDelta.z * 0.75d * (double)(random.nextBoolean() ? 1 : -1),
                0.0D, 0.005D*speedMultiplier, 0.0D);
        }
    }
}
