package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRParticleTypes;
import com.simibubi.create.foundation.particle.ICustomParticleData;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CRParticleTypesParticleEntryImpl {
    private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Railways.MODID);
    public static void register(String id, Supplier<ParticleType<?>> supplier) {
        REGISTER.register(id, supplier);
    }

    public static void register(IEventBus modEventBus) {
        CRParticleTypes.init();
        REGISTER.register(modEventBus);
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("deprecation")
    private static <T extends ParticleOptions> void registerFactory(ParticleType<T> object, ParticleEngine engine, ICustomParticleData<T> customParticleData) {
        engine.register(object, customParticleData.getFactory());
    }
}
