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

package com.railwayteam.railways.util;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Contract;

import java.util.function.Predicate;

public class EntityUtils {
    @ExpectPlatform
    public static CompoundTag getPersistentData(Entity entity) {
        throw new AssertionError();
    }

    /**
     * Gives a player an item. Plays the pickup sound, and drops whatever can't be picked up.
     */
    @ExpectPlatform
    public static void givePlayerItem(Player player, ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ServerPlayer createConductorFakePlayer(ServerLevel level, ConductorEntity conductor) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getReachDistance(Player player) {
        throw new AssertionError();
    }

    /**
     * Fire a use event.
     *
     * @return true if the use is allowed, false otherwise
     */
    @ExpectPlatform
    @Contract // shut
    public static boolean handleUseEvent(Player player, InteractionHand hand, BlockHitResult hit) {
        throw new AssertionError();
    }

    public static boolean isHolding(Player player, Predicate<ItemStack> predicate) {
        return predicate.test(player.getItemInHand(InteractionHand.MAIN_HAND))
                || predicate.test(player.getItemInHand(InteractionHand.OFF_HAND));
    }

    public static boolean isHoldingItem(Player player, Predicate<Item> predicate) {
        return isHolding(player, (stack) -> predicate.test(stack.getItem()));
    }
}
