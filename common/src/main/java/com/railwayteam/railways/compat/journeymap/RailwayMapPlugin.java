package com.railwayteam.railways.compat.journeymap;

import com.railwayteam.railways.Railways;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.MAPPING_STOPPED;

@ClientPlugin
public class RailwayMapPlugin implements IClientPlugin {
    private IClientAPI api;

    @Override
    public void initialize(@NotNull IClientAPI api) {
        this.api = api;
        this.api.subscribe(getModId(), EnumSet.of(MAPPING_STOPPED));
        RailwayMarkerHandler.init(api);
    }

    @Override
    public String getModId() {
        return Railways.MODID;
    }

    @Override
    public void onEvent(@NotNull ClientEvent clientEvent) {
        if (clientEvent.type == MAPPING_STOPPED) {
            this.api.removeAll(getModId());
        }
    }

    private static JourneymapPlatformEventListener listener; // just holding on to this

    public static void load() {
        Railways.LOGGER.info("Loaded JourneyMap plugin");
        listener = JourneymapPlatformEventListener.create();
    }

    /*@SubscribeEvent
    public static void testEvent(ClientChatEvent event) {
        if (event.getMessage().contains("rwmap:")) {
            String path = event.getMessage().replace("rwmap:", "");
            RailwayMap.LOGGER.info("adding overlay from chat: "+path);

            MarkerOverlay overlay = new MarkerOverlay(RailwayMap.MODID, "test2", BlockPos.ZERO.offset(20, 80, 0), TRAIN_IMAGE);
            overlay.setDimension(Minecraft.getInstance().level.dimension());
            overlay.setLabel("test label");
            overlay.setTitle("test title");
            try {
                RailwayMap.api.show(overlay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
}
