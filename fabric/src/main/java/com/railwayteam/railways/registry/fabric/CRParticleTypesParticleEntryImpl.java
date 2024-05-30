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

package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRParticleTypes;
import com.simibubi.create.foundation.particle.ICustomParticleData;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

public class CRParticleTypesParticleEntryImpl {
    private static final LazyRegistrar<ParticleType<?>> REGISTER = LazyRegistrar.create(Registry.PARTICLE_TYPE, Railways.MODID);

    public static void register(String id, Supplier<ParticleType<?>> supplier) {
        REGISTER.register(id, supplier);
    }

    public static void register() {
        CRParticleTypes.init();
        REGISTER.register();
    }

    @Environment(EnvType.CLIENT)
    public static <T extends ParticleOptions> void registerFactory(ParticleType<T> object, ParticleEngine engine, ICustomParticleData<T> customParticleData) {
        customParticleData.register(object, engine);
    }
}
