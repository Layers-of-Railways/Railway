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
