package com.railwayteam.railways.content.palettes.cycle_menu;

import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.util.EntityUtils;
import com.railwayteam.railways.util.packet.TagCycleSelectionPacket;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public class TagCycleHandlerClient {
    private static final TagCycleTracker CYCLE_TRACKER = new TagCycleTracker();
    @ApiStatus.Internal
    public static int COOLDOWN = 0;

    static {
        CRPalettes.CYCLE_GROUPS.values().forEach(CYCLE_TRACKER::registerCycle);
        CYCLE_TRACKER.scheduleRecompute();
    }

    public static void clientTick() {
        if (COOLDOWN > 0 && !AllKeys.TOOL_MENU.isPressed())
            COOLDOWN--;
    }

    static void select(Item target) {
        CRPackets.PACKETS.send(new TagCycleSelectionPacket(target));
    }

    public static void onKeyInput(int key, boolean pressed) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        if (key != AllKeys.TOOL_MENU.getBoundCode() || !pressed)
            return;
        if (COOLDOWN > 0)
            return;
        LocalPlayer player = mc.player;
        if (player == null)
            return;

        MutableObject<TagKey<Item>> cycleTag = new MutableObject<>();
        if (!EntityUtils.isHoldingItem(player, item -> {
            TagKey<Item> tag = CYCLE_TRACKER.getCycleTag(item);
            if (tag != null) cycleTag.setValue(tag);
            return tag != null;
        })) return;

        ScreenOpener.open(new RadialTagCycleMenu(cycleTag.getValue(), CYCLE_TRACKER.getCycle(cycleTag.getValue())));
    }

    public static void onTagsUpdated() {
        CYCLE_TRACKER.scheduleRecompute();
    }
}
