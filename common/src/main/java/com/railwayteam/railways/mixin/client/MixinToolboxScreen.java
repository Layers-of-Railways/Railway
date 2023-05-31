package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxDisposeAllPacket;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.content.equipment.toolbox.ToolboxScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ToolboxScreen.class, remap = false)
public abstract class MixinToolboxScreen extends AbstractSimiContainerScreen<ToolboxScreen> {
	public MixinToolboxScreen(ToolboxScreen container, Inventory inv, Component title) {
		super(container, inv, title);
	}

	@Inject(method = "lambda$init$1", at = @At("HEAD"), cancellable = true)
	private void railway$disposeConductorToolbox(CallbackInfo ci) {
		if (menu.contentHolder instanceof MountedToolbox mounted) {
			CRPackets.PACKETS.send(new MountedToolboxDisposeAllPacket(mounted.getParent()));
			ci.cancel();
		}
	}
}
