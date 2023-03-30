package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.util.CustomTrackChecks;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = TrackPlacement.class, remap = false)
public class MixinTrackPlacementClient {
	@ModifyArg(
			method = "clientTick",
			at = @At(
					value = "INVOKE",
					target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z",
					remap = true
			)
	)
	private static ItemStack railway$allowCustomTracks(ItemStack held) {
		return CustomTrackChecks.check(held);
	}
}
