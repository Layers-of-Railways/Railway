package com.railwayteam.railways.util.forge;

import com.railwayteam.railways.forge.ConductorFakePlayerForge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
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

	public static double getReachDistance(Player player) {
		// fixme might need to use Player#getEntityReach
		return player.getBlockReach();
	}

	public static boolean handleUseEvent(Player player, InteractionHand hand, BlockHitResult hit) {
		PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(player, InteractionHand.MAIN_HAND, hit.getBlockPos(), hit);
		return event.getResult() != Result.DENY;
	}
}
