package com.railwayteam.railways.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.railwayteam.railways.compat.tracks.TrackCompatUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootTables;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*
These different mappings may all be needed to ensure that this mixin works in all environments

 */
@Mixin(LootTables.class)
public class MixinLootTables {
    private static boolean ignoreNextError = false;
    @Inject(method = "m_upgvhpqp", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            require = 0)
    private static void cancelError(ImmutableMap.Builder<?, ?> builder, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(id);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "method_20711", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            require = 0)
    private static void cancelError1(ImmutableMap.Builder<?, ?> builder, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(id);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "a", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            require = 0)
    private static void cancelError2(ImmutableMap.Builder<?, ?> builder, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(id);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "lambda$apply$0", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            require = 0)
    private void cancelError3(ResourceManager manager, ImmutableMap.Builder<?, ?> builder, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(id);
    }

    @SuppressWarnings({"UnresolvedMixinReference"})
    @Redirect(method = {
            "m_upgvhpqp",
            "method_20711",
            "a"
    }, at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0)
    private static void snr$error(Logger instance, String s, Object o1, Object o2) {
        if (ignoreNextError) {
            ignoreNextError = false;
            return;
        }
        instance.error(s, o1, o2);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Redirect(method = "lambda$apply$0", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0)
    private void snr$error2(Logger instance, String s, Object o1, Object o2) {
        if (ignoreNextError) {
            ignoreNextError = false;
            return;
        }
        instance.error(s, o1, o2);
    }
}
