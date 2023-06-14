package com.railwayteam.railways.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.TrackCompatUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), cancellable = true, require = 0, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void cancelError(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager,
                             ProfilerFiller profiler, CallbackInfo ci, Map<?, ?> map, ImmutableMap.Builder<?, ?> builder,
                             Iterator<?> var6, Map.Entry<?, ?> entry, ResourceLocation resourceLocation) {
        if (resourceLocation.getNamespace().equals(Railways.MODID)) {
            for (String compatMod : TrackCompatUtils.TRACK_COMPAT_MODS) {
                if (resourceLocation.getPath().contains(compatMod) && Mods.valueOf(compatMod.toUpperCase()).isLoaded) {
                    ci.cancel();
                    return;
                }
            }
        }
    }

    @SuppressWarnings({"UnresolvedMixinReference", "InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
    @Inject(method = "apply(Ljava/lang/Object;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), cancellable = true, require = 0, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void cancelError2(Object object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci, Map<?, ?> map, ImmutableMap.Builder<?, ?> builder, Iterator<?> var6, Map.Entry<?, ?> entry, ResourceLocation resourceLocation) {
        if (resourceLocation.getNamespace().equals(Railways.MODID)) {
            for (String compatMod : TrackCompatUtils.TRACK_COMPAT_MODS) {
                if (resourceLocation.getPath().contains(compatMod) && Mods.valueOf(compatMod.toUpperCase()).isLoaded) {
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
