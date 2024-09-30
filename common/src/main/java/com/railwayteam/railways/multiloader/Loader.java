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

package com.railwayteam.railways.multiloader;

import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.util.TextUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Locale;
import java.util.function.Supplier;

public enum Loader {
	FORGE, NEOFORGE, FABRIC, QUILT;

	public static final Loader CURRENT = getCurrent();

	public boolean isCurrent() {
		return this == CURRENT;
	}

	public void runIfCurrent(Supplier<Runnable> run) {
		if (isCurrent())
			run.get().run();
	}
	
	public static String getFormatted() {
		return TextUtils.titleCaseConversion(getActual().name().toLowerCase(Locale.ROOT));
	}

	// Returns the actual loader, ex: quilt on quilt instead of fabric for quilt
	public static Loader getActual() {
		//noinspection ConstantValue
		if (FABRIC.isCurrent() && Mods.isModLoaded("quilt_loader"))
			return QUILT;
		return CURRENT;
	}

	@Internal
	@ExpectPlatform
	private static Loader getCurrent() {
		throw new AssertionError();
	}
}
