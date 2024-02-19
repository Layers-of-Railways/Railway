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
