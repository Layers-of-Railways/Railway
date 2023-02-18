package com.railwayteam.railways.multiloader.forge;

import com.railwayteam.railways.forge.ConductorFakePlayerForge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class EntityUtilsImpl {
	public static CompoundTag getPersistentData(Entity entity) {
		return entity.getPersistentData();
	}

	public static void givePlayerItem(Player player, ItemStack stack) {
		ItemHandlerHelper.giveItemToPlayer(player, stack);
	}

	public static ServerPlayer createConductorFakePlayer(ServerLevel level) {
		return new ConductorFakePlayerForge(level);
	}
}
