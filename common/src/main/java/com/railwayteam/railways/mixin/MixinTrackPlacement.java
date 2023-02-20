package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.util.BlockStateUtils;
import com.railwayteam.railways.util.CustomTrackChecks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement.PlacementInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrackPlacement.class, remap = false)
public class MixinTrackPlacement {
	@ModifyVariable(
			method = "tryConnect",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;",
					remap = true
			)
	)
	private static PlacementInfo railway$storeMaterialInInfo(PlacementInfo info,
														   Level level, Player player, BlockPos pos2, BlockState state2,
														   ItemStack stack, boolean girder, boolean maximiseTurn) {
		if (stack.getItem() instanceof BlockItem block && block.getBlock() instanceof IHasTrackMaterial materialProvider) {
			TrackMaterial material = materialProvider.getMaterial();
			((IHasTrackMaterial) info).setMaterial(material);
		}
		return info;
	}

	@ModifyArg(
			method = "tryConnect",
			at = @At(
					value = "INVOKE",
					target = "Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement;placeTracks(Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Z)Lcom/simibubi/create/content/logistics/trains/track/TrackPlacement$PlacementInfo;",
					remap = true
			)
	)
	private static PlacementInfo railway$storeMaterialInCurve(PlacementInfo info) {
		BezierConnection curve = ((AccessorTrackPlacement_PlacementInfo) info).getCurve();
		if (curve != null) {
			TrackMaterial material = ((IHasTrackMaterial) info).getMaterial();
			((IHasTrackMaterial) curve).setMaterial(material);
		}
		return info;
	}

	@Unique
	private static final ThreadLocal<ItemStack> railway$heldStack = new ThreadLocal<>(); // this is used on server and client

	@Inject(
			method = "tryConnect",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;getInventory()Lnet/minecraft/world/entity/player/Inventory;",
					remap = true
			)
	)
	private static void railway$grabHeldStack(Level level, Player player, BlockPos pos2, BlockState state2,
											  ItemStack stack, boolean girder, boolean maximiseTurn,
											  CallbackInfoReturnable<PlacementInfo> cir) {
		railway$heldStack.set(stack);
	}

	@ModifyArg(
			method = "tryConnect",
			at = @At(
					value = "INVOKE",
					target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z",
					remap = true
			)
	)
	private static ItemStack railway$consumeCorrectItem(ItemStack stackInSlot) {
		// target: AllBlocks.TRACK.isIn(stackInSlot)
		// desired: true when held == stack in slot
		if (AllBlocks.TRACK.isIn(stackInSlot))
			return stackInSlot;
		boolean isTrack = railway$heldStack.get().sameItem(stackInSlot);
		return isTrack ? AllBlocks.TRACK.asStack() : stackInSlot;
	}

	@ModifyVariable(method = "placeTracks", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private static BlockState railway$modifyFirstPlacedTrackBlockMaterial(BlockState original,
																		  Level level, PlacementInfo info, BlockState state1, BlockState state2,
																		  BlockPos targetPos1, BlockPos targetPos2, boolean simulate) {
		TrackMaterial material = ((IHasTrackMaterial) info).getMaterial();
		if (material != null) {
			TrackBlock customTrack = material.getTrackBlock().get();
			return BlockStateUtils.trackWith(customTrack, original);
		}
		return original;
	}

	@ModifyVariable(method = "placeTracks", at = @At("HEAD"), ordinal = 1, argsOnly = true)
	private static BlockState railway$modifySecondPlacedTrackBlockMaterial(BlockState original,
																		  Level level, PlacementInfo info, BlockState state1, BlockState state2,
																		  BlockPos targetPos1, BlockPos targetPos2, boolean simulate) {
		TrackMaterial material = ((IHasTrackMaterial) info).getMaterial();
		if (material != null) {
			TrackBlock customTrack = material.getTrackBlock().get();
			return BlockStateUtils.trackWith(customTrack, original);
		}
		return original;
	}

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
