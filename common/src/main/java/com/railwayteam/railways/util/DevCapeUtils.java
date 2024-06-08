/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
