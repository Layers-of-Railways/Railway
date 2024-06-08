/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.util.fabric;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.fabric.ConductorFakePlayerFabric;
import com.simibubi.create.foundation.utility.fabric.ReachUtil;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

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
			RandomSource r = level.random;
			float pitch = ((r.nextFloat() - r.nextFloat()) * 0.7f + 1.0f) * 2.0f;
			level.playSound(
					null,
					player.getX(), player.getY() + 0.5, player.getZ(),
					SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, pitch
			);
		}
	}

	public static ServerPlayer createConductorFakePlayer(ServerLevel level, ConductorEntity conductor) {
		return new ConductorFakePlayerFabric(level, conductor);
	}

	public static double getReachDistance(Player player) {
		return ReachUtil.reach(player);
	}

	public static boolean handleUseEvent(Player player, InteractionHand hand, BlockHitResult hit) {
		InteractionResult result = UseBlockCallback.EVENT.invoker().interact(player, player.level, hand, hit);
		return result != InteractionResult.FAIL;
	}
}
