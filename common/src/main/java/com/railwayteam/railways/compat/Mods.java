package com.railwayteam.railways.compat;

import com.railwayteam.railways.util.Utils;
import com.simibubi.create.foundation.utility.Lang;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * For compatibility with and without another mod present, we have to define load conditions of the specific code
 */
public enum Mods {
	EXTENDEDBOGEYS("extendedbogeys"),
	JOURNEYMAP("journeymap-fabric"),
	BIOMESOPLENTY("biomesoplenty"),
	BLUE_SKIES("blue_skies"),
	BYG("byg"),
	HEXCASTING("hexcasting",true),
	TWILIGHTFOREST("twilightforest"),
	SODIUM("sodium"),
	VOICECHAT("voicechat"),
   // Dreams and Desires
   CREATE_DD("create_dd"),
	QUARK("quark"),
	MALILIB("malilib"),
	TWEAKEROO("tweakeroo"),
	;

	public final boolean isLoaded;
	public final boolean requiredForDataGen;
	public final @Nullable String fabricId;

	Mods() {
		this(null, false);
	}

	Mods(@Nullable String fabricId) {
		this(fabricId, false);
	}

	Mods(boolean requiredForDataGen) {
		this(null, requiredForDataGen);
	}

	Mods(@Nullable String fabricId, boolean requiredForDataGen) {
		this.fabricId = fabricId;
		this.isLoaded = Utils.isModLoaded(asId(), fabricId);
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
}
