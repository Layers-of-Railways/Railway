package com.railwayteam.railways.forge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.registry.CRKeys;
import com.simibubi.create.AllKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AllKeys.class)
public class AllKeysMixin {
    @Inject(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/RegisterKeyMappingsEvent;register(Lnet/minecraft/client/KeyMapping;)V"), remap = false)
    private static void railways$addCreateKeysToNonConflictSet(CallbackInfo ci, @Local AllKeys key) {
        CRKeys.NON_CONFLICTING_KEYMAPPINGS.add(key.getKeybind());
    }
}

