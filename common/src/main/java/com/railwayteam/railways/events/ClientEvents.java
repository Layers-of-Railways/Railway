package com.railwayteam.railways.events;

import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerClient;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.registry.CRExtraRegistration;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.ConfigureDevCapeC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

public class ClientEvents {

    @ApiStatus.Internal
    public static boolean previousDevCapeSetting = false;

    public static void onClientTickStart(Minecraft mc) {
        PhantomSpriteManager.tick(mc);
        if (DummyRailwayMarkerHandler.getInstance() == null)
            return;

        Level level = mc.level;
        long ticks = level == null ? 1 : level.getGameTime();
        if (ticks % 40 == 0 && previousDevCapeSetting != (previousDevCapeSetting = CRConfigs.client().useDevCape.get())) {
            CRPackets.PACKETS.send(new ConfigureDevCapeC2SPacket(previousDevCapeSetting));
        }
        if (ticks % CRConfigs.client().journeymapRemoveObsoleteTicks.get() == 0) {
            DummyRailwayMarkerHandler.getInstance().removeObsolete();
            DummyRailwayMarkerHandler.getInstance().reloadMarkers();
        }
//            DummyRailwayMarkerHandler.getInstance().removeObsolete(CreateClient.RAILWAYS.trains.keySet());

        if (ticks % CRConfigs.client().journeymapUpdateTicks.get() == 0) {
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
        CRExtraRegistration.register();
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
