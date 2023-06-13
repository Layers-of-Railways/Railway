package com.railwayteam.railways.content.conductor.toolbox.forge;

import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;

public class MountedToolboxImpl {
	public static void openMenu(ServerPlayer player, MountedToolbox toolbox) {
		NetworkHooks.openGui(player, toolbox, toolbox::sendToMenu);
	}
}
