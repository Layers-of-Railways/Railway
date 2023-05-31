package com.railwayteam.railways.mixin;

import com.railwayteam.railways.util.CustomTrackChecks;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = GirderBlock.class, remap = false)
public class MixinGirderBlock {
	@ModifyArg(
			method = "updateState",
			at = @At(
					value = "INVOKE",
					target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z",
					remap = true
			)
	)
	private static BlockState railway$allowCustomTracks(BlockState state) {
		return CustomTrackChecks.check(state);
	}
}
