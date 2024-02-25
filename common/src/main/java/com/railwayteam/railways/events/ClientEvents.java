package com.railwayteam.railways.events;

import com.railwayteam.railways.compat.journeymap.DummyRailwayMarkerHandler;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import com.railwayteam.railways.content.legacy.selection_menu.BogeyCategoryHandlerClient;
import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import com.railwayteam.railways.content.palettes.cycle_menu.TagCycleHandlerClient;
import com.railwayteam.railways.content.qol.TrackEdgePointHighlighter;
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

        Level level = mc.level;
        long ticks = level == null ? 1 : level.getGameTime();
        if (ticks % 40 == 0 && previousDevCapeSetting != (previousDevCapeSetting = CRConfigs.client().useDevCape.get())) {
            CRPackets.PACKETS.send(new ConfigureDevCapeC2SPacket(previousDevCapeSetting));
        }

        if (DummyRailwayMarkerHandler.getInstance() != null) {
            if (ticks % CRConfigs.client().journeymapRemoveObsoleteTicks.get() == 0) {
                DummyRailwayMarkerHandler.getInstance().removeObsolete();
                DummyRailwayMarkerHandler.getInstance().reloadMarkers();
            }

            if (ticks % CRConfigs.client().journeymapUpdateTicks.get() == 0) {
                DummyRailwayMarkerHandler.getInstance().runUpdates();
            }
        }

        if (isGameActive()) {
            BogeyCategoryHandlerClient.clientTick();
            TagCycleHandlerClient.clientTick();
            ConductorPossessionController.onClientTick(mc, true);
            TrackEdgePointHighlighter.clientTick(mc);
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
        if (Minecraft.getInstance().screen != null)
            return;
        TagCycleHandlerClient.onKeyInput(key, pressed);
    }

    public static void onTagsUpdated() {
        TagCycleHandlerClient.onTagsUpdated();
    }
}