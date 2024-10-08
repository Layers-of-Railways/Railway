/*
 * Steam 'n' Rails
 * Copyright (c) 2024 The Railways Team
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

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysBuildInfo;
import com.railwayteam.railways.multiloader.Loader;
import net.minecraft.SharedConstants;
import net.minecraft.Util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class RailwaysUpdateCheck {
    public static void execute() {
        // TODO switch to nonCriticalIoPool() in 1.21.1
        Util.ioPool().submit(() -> {
            String uri = String.format(
                    "https://update.api.ithundxr.dev/update-check?mod_id=%s&mod_version=%s&mc_version=%s&loader=%s&dev=%s",
                    Railways.MOD_ID,
                    RailwaysBuildInfo.VERSION,
                    SharedConstants.getCurrentVersion().getName(),
                    Loader.getActual(),
                    Utils.isDevEnv()
            );
            
            HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();
            
            try {
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10L))
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .build()
                        .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (IOException | InterruptedException ignored) {}
        });
    }
}