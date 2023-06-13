package com.railwayteam.railways.content.conductor.toolbox.fabric;

import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class MountedToolboxImpl {
	public static void openMenu(ServerPlayer player, MountedToolbox toolbox) {
		player.openMenu(new ExtendedScreenHandlerFactory() {
			@Override
			public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
				toolbox.sendToMenu(buf);
			}

			@Override
			public Component getDisplayName() {
				return toolbox.getDisplayName();
			}

			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
				return toolbox.createMenu(i, inventory, player);
			}
		});
	}
}
