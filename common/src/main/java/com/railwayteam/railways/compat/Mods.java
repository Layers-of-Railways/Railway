/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.compat;

import com.simibubi.create.foundation.utility.Lang;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * For compatibility with and without another mod present, we have to define load conditions of the specific code
 */
public enum Mods {
	JOURNEYMAP("journeymap-fabric"),
	BIOMESOPLENTY("biomesoplenty"),
	BLUE_SKIES("blue_skies"),
	BYG("byg"),
	HEXCASTING("hexcasting",true),
	TWILIGHTFOREST("twilightforest"),
	SODIUM("sodium"),
	VOICECHAT("voicechat"),
	FIGURA("figura"),
	NATURES_SPIRIT("natures_spirit"),
    // Dreams and Desires
    CREATE_DD("create_dd"),
	QUARK("quark"),
	TFC("tfc"),
	MALILIB("malilib"),
	TWEAKEROO("tweakeroo"),
	EXTENDEDFLYWHEELS("extendedflywheels")
	;

	public final boolean isLoaded;
	public final boolean requiredForDataGen;
	public final @Nullable String fabricId;

	Mods(@Nullable String fabricId) {
		this(fabricId, false);
	}

	Mods(@Nullable String fabricId, boolean requiredForDataGen) {
		this.fabricId = fabricId;
		this.isLoaded = isModLoaded(asId(), fabricId);
		this.requiredForDataGen = requiredForDataGen;
	}

	/**
	 * @return the mod id
	 */
	public String asId() {
		return Lang.asId(name());
	}

	public String asFabricId() {
		return fabricId != null ? fabricId : asId();
	}

	/**
	 * Simple hook to run code if a mod is installed
	 * @param toRun will be run only if the mod is loaded
	 * @return Optional.empty() if the mod is not loaded, otherwise an Optional of the return value of the given supplier
	 */
	public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
		if (isLoaded)
			return Optional.of(toRun.get().get());
		return Optional.empty();
	}

	/**
	 * Simple hook to execute code if a mod is installed
	 * @param toExecute will be executed only if the mod is loaded
	 */
	public void executeIfInstalled(Supplier<Runnable> toExecute) {
		if (isLoaded) {
			toExecute.get().run();
		}
	}

	public void assertForDataGen() {
		assert (!requiredForDataGen || isLoaded);
	}

	@ExpectPlatform
	public static boolean isModLoaded(String id, @Nullable String fabricId) {
		throw new AssertionError();
	}
}
