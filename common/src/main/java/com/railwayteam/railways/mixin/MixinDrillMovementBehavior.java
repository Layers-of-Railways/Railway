package com.railwayteam.railways.mixin;

import com.railwayteam.railways.util.CustomTrackChecks;
import com.simibubi.create.content.contraptions.components.actors.DrillMovementBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = DrillMovementBehaviour.class, remap = false)
public class MixinDrillMovementBehavior {
	@ModifyArg(
			method = "canBreak",
			at = @At(
					value = "INVOKE",
					target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z",
					remap = true
			)
	)
	private BlockState railway$allowCustomTracks(BlockState state) {
		return CustomTrackChecks.check(state);
	}
}
