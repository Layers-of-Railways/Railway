package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.registry.CRItems;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnderMan.class)
public class EnderManMixin {
	@Inject(
			method = "isLookingAtMe",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private void railways$conductorCapsPreventAnger(Player player, CallbackInfoReturnable<Boolean> cir,
												   ItemStack helmet) {
		if (helmet.is(CRItems.CONDUCTOR_CAPS))
			cir.setReturnValue(false);
	}
}
