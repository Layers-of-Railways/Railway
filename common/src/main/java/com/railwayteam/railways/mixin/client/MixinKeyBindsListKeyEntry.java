package com.railwayteam.railways.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.registry.CRKeys;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyBindsList.KeyEntry.class)
public class MixinKeyBindsListKeyEntry {
    @WrapOperation(method = "refreshEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;same(Lnet/minecraft/client/KeyMapping;)Z"))
    private boolean railways$createKeybindsDontConflictWithOurs(KeyMapping instance, KeyMapping otherKeyModifier, Operation<Boolean> original) {
        if (CRKeys.NON_CONFLICTING_KEYMAPPINGS.contains(instance) && CRKeys.NON_CONFLICTING_KEYMAPPINGS.contains(otherKeyModifier))
            return false;
        return original.call(instance, otherKeyModifier);
    }
}
