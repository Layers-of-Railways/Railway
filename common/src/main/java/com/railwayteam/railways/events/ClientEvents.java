package com.railwayteam.railways.events;

import com.railwayteam.railways.Config;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ClientEvents {
    private static final String ITEM_PREFIX = "item." + Railways.MODID;
    private static final String BLOCK_PREFIX = "block." + Railways.MODID;

    public static void onTooltip(ItemStack stack, TooltipFlag flags, List<Component> tooltip) {
        if (!AllConfigs.CLIENT.tooltips.get())
            return;

        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;

        String translationKey = stack.getItem()
            .getDescriptionId(stack);

        if (translationKey.startsWith(ITEM_PREFIX) || translationKey.startsWith(BLOCK_PREFIX))
            if (TooltipHelper.hasTooltip(stack, player)) {
                List<Component> toolTip = new ArrayList<>();
                toolTip.add(tooltip.remove(0));
                TooltipHelper.getTooltip(stack)
                    .addInformation(toolTip);
                tooltip.addAll(0, toolTip);
            }
    }

    public static void onClientTickStart(Minecraft mc) {
        if (DummyRailwayMarkerHandler.getInstance() == null)
            return;

        Level level = mc.level;
        long ticks = level == null ? 1 : level.getGameTime();
        if (ticks % Config.JOURNEYMAP_REMOVE_OBSOLETE_TICKS.get() == 0) {
            DummyRailwayMarkerHandler.getInstance().removeObsolete();
            DummyRailwayMarkerHandler.getInstance().reloadMarkers();
        }
//            DummyRailwayMarkerHandler.getInstance().removeObsolete(CreateClient.RAILWAYS.trains.keySet());

        if (ticks % Config.JOURNEYMAP_UPDATE_TICKS.get() == 0) {
            DummyRailwayMarkerHandler.getInstance().runUpdates();
/*            for (Train train : CreateClient.RAILWAYS.trains.values()) {
                DummyRailwayMarkerHandler.getInstance().addOrUpdateTrain(train);
            }*/
        }
    }

    public static void onClientWorldLoad(Level level) {
        DummyRailwayMarkerHandler.getInstance().onJoinWorld();
    }
}
