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

import com.railwayteam.railways.annotation.event.MultiLoaderEvent;
import com.railwayteam.railways.registry.CRKeys;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.util.EntityUtils;
import com.railwayteam.railways.util.packet.TagCycleSelectionPacket;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TagCycleHandlerClient {
    public static final TagCycleTracker CYCLE_TRACKER = new TagCycleTracker();
    @ApiStatus.Internal
    public static int COOLDOWN = 0;

    static {
        CRPalettes.CYCLE_GROUPS.values().forEach(CYCLE_TRACKER::registerCycle);
        CYCLE_TRACKER.scheduleRecompute();
    }

    @MultiLoaderEvent
    public static void clientTick() {
        if (COOLDOWN > 0 && !CRKeys.CYCLE_MENU.isPressed())
            COOLDOWN--;
    }

    static void select(Item target) {
        CRPackets.PACKETS.send(new TagCycleSelectionPacket(target));
    }

    @MultiLoaderEvent
    public static void onKeyInput(int key, boolean pressed) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        if (key != CRKeys.CYCLE_MENU.getBoundCode() || !pressed)
            return;
        if (COOLDOWN > 0)
            return;
        LocalPlayer player = mc.player;
        if (player == null)
            return;

        MutableObject<TagKey<Item>> cycleTag = new MutableObject<>();
        MutableObject<@Nullable CompoundTag> stackTag = new MutableObject<>();
        if (!EntityUtils.isHolding(player, stack -> {
            Item item = stack.getItem();
            TagKey<Item> tag = CYCLE_TRACKER.getCycleTag(item);
            if (tag != null) {
                cycleTag.setValue(tag);
                stackTag.setValue(stack.getTag());
            }
            return tag != null;
        })) return;

        ScreenOpener.open(new RadialTagCycleMenu(cycleTag.getValue(), CYCLE_TRACKER.getCycle(cycleTag.getValue()), stackTag.getValue()));
    }

    @MultiLoaderEvent
    public static void onTagsUpdated() {
        CYCLE_TRACKER.scheduleRecompute();
    }
}
