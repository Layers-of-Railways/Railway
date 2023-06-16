package com.railwayteam.railways.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.railwayteam.railways.compat.tracks.TrackCompatUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

@Mixin(value = RecipeManager.class, priority = 100)
public class MixinRecipeManager {
    private static boolean ignoreNextError = false;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void cancelError(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager,
                             ProfilerFiller profiler, CallbackInfo ci, Map<?, ?> map, ImmutableMap.Builder<?, ?> builder,
                             Iterator<?> var6, Map.Entry<?, ?> entry, ResourceLocation resourceLocation) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(resourceLocation);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
    @Inject(method = "apply(Ljava/lang/Object;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void cancelError2(Object object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci, Map<?, ?> map, ImmutableMap.Builder<?, ?> builder, Iterator<?> var6, Map.Entry<?, ?> entry, ResourceLocation resourceLocation) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(resourceLocation);
    }

    @Redirect(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0)
    private void snr$error(Logger instance, String s, Object o1, Object o2) {
        if (ignoreNextError) {
            ignoreNextError = false;
            return;
        }
        instance.error(s, o1, o2);
    }
}
