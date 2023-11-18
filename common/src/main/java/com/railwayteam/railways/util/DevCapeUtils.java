package com.railwayteam.railways.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.railwayteam.railways.Railways;
import net.minecraft.world.entity.player.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public enum DevCapeUtils {
    INSTANCE;

    public static final Map<UUID, Boolean> usageStatusServerside = new HashMap<>();
    public static final Map<UUID, Boolean> usageStatusClientside = new HashMap<>();

    private final Set<UUID> registeredDevs = new HashSet<>();

    private static final String url = "https://raw.githubusercontent.com/Layers-of-Railways/data/main/dev_capes.json";

    private boolean initialized = false;

    public void init() {
        if (initialized)
            return;
        initialized = true;
        refresh();
    }

    public void refresh() {
        CompletableFuture.runAsync(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .build();
            try {
                String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                JsonArray devArray = JsonParser.parseString(body).getAsJsonObject().getAsJsonArray("dev");
                Set<UUID> fetched = new HashSet<>();
                for (JsonElement element : devArray) {
                    if (element instanceof JsonObject object) {
                        if (object.has("id")) {
                            fetched.add(UUID.fromString(object.get("id").getAsString()));
                        }
                    }
                }
                registeredDevs.clear();
                registeredDevs.addAll(fetched);
            } catch (Exception e) {
                Railways.LOGGER.error("Failed to fetch dev cape data", e);
            }
        });
    }

    public boolean isDev(Player player) {
        return isDev(player.getUUID());
    }

    public boolean isDev(UUID id) {
        return registeredDevs.contains(id) || Utils.isDevEnv();
    }

    public boolean useDevCape(UUID id) {
        return isDev(id) && usageStatusClientside.getOrDefault(id, true);
    }
}
