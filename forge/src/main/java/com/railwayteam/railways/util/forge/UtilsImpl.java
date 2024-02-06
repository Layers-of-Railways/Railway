package com.railwayteam.railways.util.forge;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.HonkPacket;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;

import java.nio.file.Path;

public class UtilsImpl {
	public static Path configDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	public static boolean isDevEnv() {
		return !FMLLoader.isProduction();
	}

    public static void sendCreatePacketToServer(SimplePacketBase packet) {
		AllPackets.getChannel().sendToServer(packet);
    }

    public static void sendHonkPacket(Train train, boolean isHonk) {
		AllPackets.getChannel().send(PacketDistributor.ALL.noArg(), new HonkPacket(train, isHonk));
    }

    public static void postChunkEventClient(LevelChunk chunk, boolean load) {
		if (load) {
			MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk, false));
		} else {
			MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
		}
    }

    public static Path modsDir() {
		return FMLPaths.MODSDIR.get();
    }
}
