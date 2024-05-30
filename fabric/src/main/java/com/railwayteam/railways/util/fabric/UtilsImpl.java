/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.util.fabric;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.HonkPacket;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.chunk.LevelChunk;

import java.nio.file.Path;

public class UtilsImpl {
	public static Path configDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static boolean isDevEnv() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

    public static void sendCreatePacketToServer(SimplePacketBase packet) {
		AllPackets.getChannel().sendToServer(packet);
    }

    public static void sendHonkPacket(Train train, boolean isHonk) {
		AllPackets.getChannel().sendToClientsInCurrentServer(new HonkPacket(train, isHonk));
    }

    public static void postChunkEventClient(LevelChunk chunk, boolean load) {
		if (load) {
			ClientChunkEvents.CHUNK_LOAD.invoker().onChunkLoad(Minecraft.getInstance().level, chunk);
		} else {
			ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload(Minecraft.getInstance().level, chunk);
		}
    }

    public static Path modsDir() {
		return FabricLoader.getInstance().getGameDir().resolve("mods");
    }
}
