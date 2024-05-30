/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.cycle_menu;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.annotation.event.MultiLoaderEvent;
import com.railwayteam.railways.registry.CRPalettes;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TagCycleHandlerServer {
    public static final TagCycleTracker CYCLE_TRACKER = new TagCycleTracker();

    static {
        CRPalettes.CYCLE_GROUPS.values().forEach(CYCLE_TRACKER::registerCycle);
        CYCLE_TRACKER.scheduleRecompute();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean select(ServerPlayer player, Item target, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        if (handStack.getItem() == target) return true;
        TagKey<Item> handTag = CYCLE_TRACKER.getCycleTag(handStack.getItem());
        TagKey<Item> targetTag = CYCLE_TRACKER.getCycleTag(target);
        if (handTag == null || !handTag.equals(targetTag)) return false;
        ItemStack newStack = new ItemStack(target, handStack.getCount());
        newStack.setTag(handStack.getTag());
        player.setItemInHand(hand, newStack);
        return true;
    }

    public static void select(ServerPlayer player, Item target) {
        if (!select(player, target, InteractionHand.MAIN_HAND) && !select(player, target, InteractionHand.OFF_HAND)) {
            Railways.LOGGER.warn("Player {} tried to select {} through tag cycling but failed", player.getName().getString(), target.getDescription().getString());
            player.connection.disconnect(Components.literal("Invalid tag selection"));
        }
    }

    @MultiLoaderEvent
    public static void onTagsUpdated() {
        CYCLE_TRACKER.scheduleRecompute();
    }
}
