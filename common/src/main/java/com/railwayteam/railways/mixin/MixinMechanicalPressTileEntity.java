package com.railwayteam.railways.mixin;

import com.railwayteam.railways.util.CustomTrackChecks;
import com.simibubi.create.content.contraptions.components.press.MechanicalPressTileEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = MechanicalPressTileEntity.class, remap = false)
public class MixinMechanicalPressTileEntity {
	@ModifyArg(
			method = "onItemPressed",
			at = @At(
					value = "INVOKE",
					target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z",
					remap = true
			)
	)
	private ItemStack railway$allowCustomTracks(ItemStack held) {
		return CustomTrackChecks.check(held);
	}
}
