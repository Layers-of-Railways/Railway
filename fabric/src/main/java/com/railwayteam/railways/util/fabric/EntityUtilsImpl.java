package com.railwayteam.railways.util.fabric;

import com.railwayteam.railways.fabric.ConductorFakePlayerFabric;
import com.simibubi.create.foundation.utility.fabric.ReachUtil;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;

public class EntityUtilsImpl {
	public static CompoundTag getPersistentData(Entity entity) {
		return entity.getExtraCustomData();
	}

	public static void givePlayerItem(Player player, ItemStack stack) {
		try (Transaction t = TransferUtil.getTransaction()) {
			PlayerInventoryStorage inv = PlayerInventoryStorage.of(player);
			inv.offerOrDrop(ItemVariant.of(stack), stack.getCount(), t);
			t.commit();

			Level level = player.level;
			Random r = level.random;
			float pitch = ((r.nextFloat() - r.nextFloat()) * 0.7f + 1.0f) * 2.0f;
			level.playSound(
					null,
					player.getX(), player.getY() + 0.5, player.getZ(),
					SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, pitch
			);
		}
	}

	public static ServerPlayer createConductorFakePlayer(ServerLevel level) {
		return new ConductorFakePlayerFabric(level);
	}

	public static double getReachDistance(Player player) {
		return ReachUtil.reach(player);
	}
}
