package com.railwayteam.railways.events;

import com.railwayteam.railways.Config;
import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerClient;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientEvents {

    public static void onClientTickStart(Minecraft mc) {
        PhantomSpriteManager.tick(mc);
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

        if (isGameActive()) {
            BogeyCategoryHandlerClient.clientTick();
            ConductorPossessionController.onClientTick(mc, true);
        }
    }

    public static void onClientTickEnd(Minecraft mc) {
        if (isGameActive()) {
            ConductorPossessionController.onClientTick(mc, false);
        }
    }

    public static void onClientWorldLoad(Level level) {
        DummyRailwayMarkerHandler.getInstance().onJoinWorld();
        PhantomSpriteManager.firstRun = true;
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }

    public static void onKeyInput(int key, boolean pressed) {
        if (Minecraft.getInstance().screen != null)
            return;
        BogeyCategoryHandlerClient.onKeyInput(key, pressed);
    }
}
