package com.railwayteam.railways.events;

import com.railwayteam.railways.Config;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    private static final String ITEM_PREFIX = "item." + Railways.MODID;
    private static final String BLOCK_PREFIX = "block." + Railways.MODID;

    @SubscribeEvent
    public static void addToItemTooltip(ItemTooltipEvent event) {
        if (!AllConfigs.CLIENT.tooltips.get())
            return;
        if (event.getEntity() == null)
            return;

        ItemStack stack = event.getItemStack();
        String translationKey = stack.getItem()
            .getDescriptionId(stack);

        if (translationKey.startsWith(ITEM_PREFIX) || translationKey.startsWith(BLOCK_PREFIX))
            if (TooltipHelper.hasTooltip(stack, event.getPlayer())) {
                List<Component> itemTooltip = event.getToolTip();
                List<Component> toolTip = new ArrayList<>();
                toolTip.add(itemTooltip.remove(0));
                TooltipHelper.getTooltip(stack)
                    .addInformation(toolTip);
                itemTooltip.addAll(0, toolTip);
            }
    }

    @SubscribeEvent
    public static void tickTrains(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || DummyRailwayMarkerHandler.getInstance() == null) return;
        Level level = Minecraft.getInstance().level;
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

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        DummyRailwayMarkerHandler.getInstance().onJoinWorld();
    }
}
