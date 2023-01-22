package com.railwayteam.railways.compat.journeymap;

import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public enum UsernameUtils {
    INSTANCE;

    private final HashMap<UUID, String> uuidNameMap = new HashMap<>();
    private final Set<UUID> tried = new HashSet<>();

    private static final String url = "https://sessionserver.mojang.com/session/minecraft/profile/";

    public String getName(UUID uuid) {
        if (uuid == null) return "Unknown";
        if (!uuidNameMap.containsKey(uuid)) {
            if (Minecraft.getInstance().getUser().getUuid().equals(uuid.toString())) {
                uuidNameMap.put(uuid, Minecraft.getInstance().getUser().getName());
                return uuidNameMap.get(uuid);
            }
            if (!tried.contains(uuid)) {
                CompletableFuture.runAsync(() -> {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder(URI.create(url + uuid.toString().replace("-", "")))
                        .GET()
                        .build();
                    try {
                        String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                        uuidNameMap.put(uuid, JsonParser.parseString(body).getAsJsonObject().get("name").getAsString());
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                });
                tried.add(uuid);
            }
            return "Unknown Player";
        }
        return uuidNameMap.get(uuid);
    }
}
