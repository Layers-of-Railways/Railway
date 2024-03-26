package com.railwayteam.railways.compat.tweakeroo;

import com.railwayteam.railways.Railways;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@Environment(EnvType.CLIENT)
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
