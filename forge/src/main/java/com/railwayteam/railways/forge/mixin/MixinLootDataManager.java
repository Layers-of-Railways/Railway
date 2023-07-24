package com.railwayteam.railways.forge.mixin;

import com.google.gson.JsonElement;
import com.railwayteam.railways.compat.tracks.TrackCompatUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootDataManager.class)
public class MixinLootDataManager {
    @Inject(method = {
        "lambda$scheduleElementParse$4"
    }, at = @At("HEAD"), cancellable = true)
    private static void skipMissingLoot(LootDataType lootDataType, ResourceManager resourceManager, Map map, ResourceLocation id, JsonElement json, CallbackInfo ci) {
        if (TrackCompatUtils.mixinIgnoreErrorForMissingItem(id)) {
            ci.cancel();
        }
    }
}
