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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.smokestack.particles.chimneypush.ChimneyPushParticleData;
import com.railwayteam.railways.content.smokestack.particles.legacy.SmokeParticleData;
import com.railwayteam.railways.content.smokestack.particles.puffs.PuffSmokeParticleData;
import com.simibubi.create.foundation.particle.ICustomParticleData;
import com.simibubi.create.foundation.utility.Lang;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

public enum CRParticleTypes {

	SMOKE(SmokeParticleData::new),
	SMOKE_PUFF_SMALL(PuffSmokeParticleData.Small::new),
	SMOKE_PUFF_MEDIUM(PuffSmokeParticleData.Medium::new),
	CHIMNEYPUSH_SMALL(ChimneyPushParticleData.Small::new),
	CHIMNEYPUSH_MEDIUM(ChimneyPushParticleData.Medium::new),
	;

	private final ParticleEntry<?> entry;

	<D extends ParticleOptions> CRParticleTypes(Supplier<? extends ICustomParticleData<D>> typeFactory) {
		String name = Lang.asId(name());
		entry = new ParticleEntry<>(name, typeFactory);
	}

	public ParticleType<?> get() {
		return entry.object;
	}

	public String parameter() {
		return entry.name;
	}

	public static void init() {}

	@Environment(EnvType.CLIENT)
	public static void registerFactories() {
		for (CRParticleTypes particle : values())
			particle.entry.registerFactory(Minecraft.getInstance().particleEngine);
	}

	private static class ParticleEntry<D extends ParticleOptions> {
		//private static final LazyRegistrar<ParticleType<?>> REGISTER = LazyRegistrar.create(Registry.PARTICLE_TYPE, Railways.MODID);

		private final String name;
		private final Supplier<? extends ICustomParticleData<D>> typeFactory;
		private final ParticleType<D> object;

		public ParticleEntry(String name, Supplier<? extends ICustomParticleData<D>> typeFactory) {
			this.name = name;
			this.typeFactory = typeFactory;

			object = this.typeFactory.get().createType();
			register(name, () -> object);
		}

		@ExpectPlatform
		private static void register(String id, Supplier<ParticleType<?>> supplier) {
			throw new AssertionError();//REGISTER.register(id, supplier);
		}

		@Environment(EnvType.CLIENT)
		public void registerFactory(ParticleEngine engine) {
			registerFactory(object, engine, typeFactory.get());
		}

		@Environment(EnvType.CLIENT)
		@ExpectPlatform
		private static <T extends ParticleOptions> void registerFactory(ParticleType<T> object, ParticleEngine engine, ICustomParticleData<T> customParticleData) {
			throw new AssertionError();
		}
	}

}
