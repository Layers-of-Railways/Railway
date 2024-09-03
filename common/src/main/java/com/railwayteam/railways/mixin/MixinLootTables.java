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
@Mixin(value = LootTables.class, priority = 10000)
public class MixinLootTables {
    private static boolean ignoreNextError = false;

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = "m_upgvhpqp", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            require = 0)
    private static void cancelError(ImmutableMap.Builder<?, ?> builder, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        ignoreNextError = TrackCompatUtils.mixinIgnoreErrorForMissingItem(id);
    }

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
    private static void railways$error(Logger instance, String s, Object o1, Object o2) {
        if (ignoreNextError) {
            ignoreNextError = false;
            return;
        }
        instance.error(s, o1, o2);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Redirect(method = "lambda$apply$0", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0)
    private void railways$error2(Logger instance, String s, Object o1, Object o2) {
        if (ignoreNextError) {
            ignoreNextError = false;
            return;
        }
        instance.error(s, o1, o2);
    }
}
