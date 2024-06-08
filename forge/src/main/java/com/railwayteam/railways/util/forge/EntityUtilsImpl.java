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

package com.railwayteam.railways.util.forge;

import com.railwayteam.railways.content.conductor.ConductorEntity;
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

	public static ServerPlayer createConductorFakePlayer(ServerLevel level, ConductorEntity conductor) {
		return new ConductorFakePlayerForge(level, conductor);
	}

	public static double getReachDistance(Player player) {
		return player.getBlockReach();
	}

	public static boolean handleUseEvent(Player player, InteractionHand hand, BlockHitResult hit) {
		PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(player, InteractionHand.MAIN_HAND, hit.getBlockPos(), hit);
		return event.getResult() != Result.DENY;
	}
}
