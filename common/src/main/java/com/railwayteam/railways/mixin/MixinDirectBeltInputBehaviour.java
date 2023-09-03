package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.CRConfigs;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DirectBeltInputBehaviour.class)
public class MixinDirectBeltInputBehaviour {
    @Inject(method = "handleInsertion(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"))
    private void recordSimulated(ItemStack stack, Direction side, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        if (simulate && CRConfigs.server().optimization.optimizeFunnelBeltInteraction.get())
            Railways.skipUprightCalculation = true; // reset by MixinBeltHelper
    }
}
