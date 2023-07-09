package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BeltHelper.class, remap = false)
public class MixinBeltHelper {
    @Inject(method = "isItemUpright", at = @At("HEAD"), cancellable = true)
    private static void skipCalculationIfNeeded(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (Railways.skipUprightCalculation) {
            cir.setReturnValue(false);
            Railways.skipUprightCalculation = false;
        }
    }
}
