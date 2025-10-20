/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxDisposeAllPacket;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxEquipPacket;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.content.equipment.toolbox.RadialToolboxMenu;
import com.simibubi.create.content.equipment.toolbox.RadialToolboxMenu.State;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import net.createmod.catnip.gui.AbstractSimiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RadialToolboxMenu.class, remap = false)
public abstract class MixinRadialToolboxMenu extends AbstractSimiScreen {
	@Shadow
	private ToolboxBlockEntity selectedBox;
	@Shadow
	private boolean scrollMode;
	@Shadow
	private int scrollSlot;
	@Shadow
	private int hoveredSlot;
	@Shadow
	private State state;

	@Inject(method = "lambda$removed$0", at = @At("HEAD"), cancellable = true)
	private static void railways$sendConductorToolboxDisposeAllPacketsToAll(ToolboxBlockEntity te, CallbackInfo ci) {
		if (te instanceof MountedToolbox mounted) {
			CRPackets.PACKETS.send(new MountedToolboxDisposeAllPacket(mounted.getParent()));
			ci.cancel(); // lambda only sends packet, skip it
		}
	}

	@Inject(
			method = "removed",
			remap = true,
			at = @At(
					value = "INVOKE",
					target = "Lcom/simibubi/create/content/equipment/toolbox/ToolboxDisposeAllPacket;<init>(Lnet/minecraft/core/BlockPos;)V",
					remap = true
			),
			cancellable = true
	)
	private void railways$sendConductorToolboxDisposeAllPacket(CallbackInfo ci) {
		if (selectedBox instanceof MountedToolbox mounted) {
			CRPackets.PACKETS.send(new MountedToolboxDisposeAllPacket(mounted.getParent()));
			ci.cancel(); // returns early anyway after send
		}
	}

	@Inject(
			method = "removed",
			remap = true,
			at = {
					// skip first one, unequip works fine
					@At(
							value = "INVOKE",
							target = "Lcom/simibubi/create/content/equipment/toolbox/ToolboxEquipPacket;<init>(Lnet/minecraft/core/BlockPos;II)V",
							ordinal = 1,
							remap = true
					),
					@At(
							value = "INVOKE",
							target = "Lcom/simibubi/create/content/equipment/toolbox/ToolboxEquipPacket;<init>(Lnet/minecraft/core/BlockPos;II)V",
							ordinal = 2,
							remap = true
					),
			},
			cancellable = true
	)
	private void railways$sendConductorToolboxEquipPacketOnRemove(CallbackInfo ci) {
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
			remap = true,
			at = @At(
					value = "INVOKE",
					target = "Lcom/simibubi/create/content/equipment/toolbox/ToolboxEquipPacket;<init>(Lnet/minecraft/core/BlockPos;II)V",
					remap = true
			),
			cancellable = true
	)
	private void railways$sendConductorToolboxEquipPacketOnClick(CallbackInfoReturnable<Boolean> cir) {
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
