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

package com.railwayteam.railways.compat.tweakeroo;

import com.railwayteam.railways.Railways;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@OnlyIn(Dist.CLIENT)
public class TweakerooCompat {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> boolean inFreecam() {
        try {
            // Get the class
            Class<?> clazz = Class.forName("fi.dy.masa.tweakeroo.config.FeatureToggle");
            if (!clazz.isEnum()) return false;

            // Get the enum
            T tweakFreeCameraEnum = Enum.valueOf((Class<T>) clazz, "TWEAK_FREE_CAMERA");

            // Get the method handle
            MethodType methodType = MethodType.methodType(boolean.class);
            MethodHandle getBooleanValue = lookup.findVirtual(tweakFreeCameraEnum.getClass(), "getBooleanValue", methodType);

            // Invoke getBooleanValue() on TWEAK_FREE_CAMERA
            return (boolean) getBooleanValue.invoke(tweakFreeCameraEnum);
        } catch (Throwable e) {
            Railways.LOGGER.error(e.toString());
            return false;
        }
    }
}
