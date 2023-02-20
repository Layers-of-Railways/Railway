package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxDisposeAllPacket;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxEquipPacket;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.content.curiosities.toolbox.RadialToolboxMenu;
import com.simibubi.create.content.curiosities.toolbox.RadialToolboxMenu.State;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RadialToolboxMenu.class, remap = false)
public abstract class MixinRadialToolboxMenu extends AbstractSimiScreen {
	@Shadow
	private ToolboxTileEntity selectedBox;
	@Shadow
	private boolean scrollMode;
	@Shadow
	private int scrollSlot;
	@Shadow
	private int hoveredSlot;
	@Shadow
	private State state;

	@Inject(method = "lambda$removed$0", at = @At("HEAD"), cancellable = true)
	private static void railway$sendConductorToolboxDisposeAllPacketsToAll(ToolboxTileEntity te, CallbackInfo ci) {
		if (te instanceof MountedToolbox mounted) {
			CRPackets.PACKETS.send(new MountedToolboxDisposeAllPacket(mounted.getParent()));
			ci.cancel(); // lambda only sends packet, skip it
		}
	}

	@Inject(
			method = "removed",
			at = @At(
					value = "INVOKE",
					target = "Lcom/simibubi/create/content/curiosities/toolbox/ToolboxDisposeAllPacket;<init>(Lnet/minecraft/core/BlockPos;)V",
					remap = true
			),
			cancellable = true
	)
	private void railway$sendConductorToolboxDisposeAllPacket(CallbackInfo ci) {
		if (selectedBox instanceof MountedToolbox mounted) {
			CRPackets.PACKETS.send(new MountedToolboxDisposeAllPacket(mounted.getParent()));
			ci.cancel(); // returns early anyway after send
		}
	}

	@Inject(
			method = "removed",
			remap = false,
			at = {
					// skip first one, unequip works fine
					@At(
							value = "INVOKE",
							target = "Lcom/simibubi/create/content/curiosities/toolbox/ToolboxEquipPacket;<init>(Lnet/minecraft/core/BlockPos;II)V",
							ordinal = 1
					),
					@At(
							value = "INVOKE",
							target = "Lcom/simibubi/create/content/curiosities/toolbox/ToolboxEquipPacket;<init>(Lnet/minecraft/core/BlockPos;II)V",
							ordinal = 2
					),
			},
			cancellable = true
	)
	private void railway$sendConductorToolboxEquipPacketOnRemove(CallbackInfo ci) {
		if (!(selectedBox instanceof MountedToolbox mounted))
			return;
		int selected = (scrollMode ? scrollSlot : hoveredSlot);
		int hotbarSlot = minecraft.player.getInventory().selected;
		CRPackets.PACKETS.send(new MountedToolboxEquipPacket(mounted.getParent(), selected, hotbarSlot));
		// cancel is safe
		// for first, returns afterwards anyway
		// for second, happens at tail
		ci.cancel();
	}

	@Inject(
			method = "mouseClicked",
			remap = false,
			at = @At(
					value = "INVOKE",
					target = "Lcom/simibubi/create/content/curiosities/toolbox/ToolboxEquipPacket;<init>(Lnet/minecraft/core/BlockPos;II)V"
			),
			cancellable = true
	)
	private void railway$sendConductorToolboxEquipPacketOnClick(CallbackInfoReturnable<Boolean> cir) {
		if (!(selectedBox instanceof MountedToolbox mounted))
			return;
		int selected = (scrollMode ? scrollSlot : hoveredSlot);
		int hotbarSlot = minecraft.player.getInventory().selected;
		CRPackets.PACKETS.send(new MountedToolboxEquipPacket(mounted.getParent(), selected, hotbarSlot));
		// cancel is safe, sets state and returns true, replicated here
		state = State.SELECT_BOX;
		cir.setReturnValue(true);
	}
}
