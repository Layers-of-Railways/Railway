package com.railwayteam.railways.compat.journeymap;

import com.railwayteam.railways.Railways;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.model.TextProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RailwayMarkerHandler implements IRailwayMarkerHandler {
    private static final MapImage TRAIN_IMAGE = new MapImage(Railways.asResource("textures/gui/journeymap_train.png"), 60, 60)
        .setDisplayWidth(27)
        .setDisplayHeight(27)
        .centerAnchors();

    private IClientAPI jmAPI;
    private HashMap<UUID, MarkerOverlay> markers;
    private HashMap<UUID, TrainMarkerData> trainData;
    private Set<UUID> needingUpdates;
    private ResourceKey<Level> currentDimension;
    private ResourceKey<Level> lastDimension;
    private boolean reload = false;
    private boolean enabled = true;

    private RailwayMarkerHandler(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
        this.markers = new HashMap<>();
        this.trainData = new HashMap<>();
        this.needingUpdates = new HashSet<>();
        this.lastDimension = null;
    }

    public static void init(IClientAPI jmAPI) {
        DummyRailwayMarkerHandler.instance = new RailwayMarkerHandler(jmAPI);
    }

    public void enable() {
        this.enabled = true;
        this.reload = true;
        this.runUpdates();
    }

    public void disable() {
        this.enabled = false;
        markers.forEach((u, overlay) -> jmAPI.remove(overlay));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void removeTrain(UUID uuid) {
        if (markers.containsKey(uuid)) {
            jmAPI.remove(markers.get(uuid));
            markers.remove(uuid);
        }
        trainData.remove(uuid);
    }

    @Override
    public void removeObsolete() {
        for (UUID uuid : markers.keySet().stream().toList()) { //Prevent modifying while iterating
            if (!trainData.containsKey(uuid)) {
                removeTrain(uuid);
            }
        }
    }

    private void addOrUpdateTrain(UUID uuid) {

        TrainMarkerData data = trainData.get(uuid);


        ResourceKey<Level> dimension = data.dimension();
        BlockPos blockPos = data.pos();

        if (currentDimension != dimension && currentDimension != null) {
            if (currentDimension != lastDimension && markers.containsKey(uuid)) {
                this.jmAPI.remove(markers.get(uuid));
            }
            return;
        }

        MarkerOverlay marker;
        if (markers.containsKey(uuid)) {
            marker = markers.get(uuid);
            marker.setDimension(dimension);
            marker.setPoint(blockPos);
        } else {
            marker = new MarkerOverlay(Railways.MODID, "train_marker_" + uuid.toString(), blockPos, TRAIN_IMAGE);
            marker.setDimension(dimension);
            markers.put(uuid, marker);
            try {
                this.jmAPI.show(marker);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        marker.setTextProperties(new TextProperties().setOffsetY(25).setOffsetX(1).setColor(0xffbb43));
        marker.setLabel(data.name());
        String info = "";
        info += data.name();
        info += "\n > Carriage count: "+data.carriageCount();
        info += "\n > Owner: "+ UsernameUtils.INSTANCE.getName(data.owner());
//        info += "\n > Destination: "+data.destination(); //Gets out of sync

        marker.setTitle(info);
        marker.flagForRerender();

        if (currentDimension != lastDimension || reload) {
            try {
                this.jmAPI.show(marker);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void runUpdates() {
        if (!enabled)
            return;
        currentDimension = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.dimension();
        boolean forceUpdate = reload || currentDimension != lastDimension;
        for (UUID uuid : (forceUpdate ? trainData.keySet() : needingUpdates)) {
            if (trainData.containsKey(uuid)) {
                addOrUpdateTrain(uuid);
            }
        }
        needingUpdates.clear();
        lastDimension = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.dimension();
        reload = false;
    }

    @Override
    public void registerData(UUID uuid, TrainMarkerData data) {
        this.trainData.put(uuid, data);
        this.needingUpdates.add(uuid);
    }

    @Override
    public void reloadMarkers() {
        reload = true;
        trainData.clear();
        removeObsolete();
    }

    @Override
    public void onJoinWorld() {
        reload = true;
        trainData.clear();
        needingUpdates.clear();
        removeObsolete();
    }
}
