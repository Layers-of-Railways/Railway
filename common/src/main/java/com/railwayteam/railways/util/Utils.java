package com.railwayteam.railways.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.railwayteam.railways.Railways;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.foundation.utility.Couple;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Locale;

public class Utils {
	@ExpectPlatform
	public static boolean isModLoaded(String id, @Nullable String fabricId) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Path configDir() {
		throw new AssertionError();
	}

	@ExpectPlatform
	@Contract // shut
	public static boolean isDevEnv() {
		throw new AssertionError();
	}

	public static boolean isEnvVarTrue(String name) {
		try {
			String result = System.getenv(name);
			return result != null && result.toLowerCase(Locale.ROOT).equals("true");
		} catch (SecurityException e) {
			Railways.LOGGER.warn("Caught a security exception while trying to access environment variable `"+name+"`.");
			return false;
		}
	}

	@ExpectPlatform
	@Environment(EnvType.CLIENT)
	public static boolean isActiveAndMatches(KeyMapping mapping, InputConstants.Key keyCode) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void sendCreatePacketToServer(SimplePacketBase packet) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void sendHonkPacket(Train train, boolean isHonk) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void postChunkEventClient(LevelChunk chunk, boolean load) {
		throw new AssertionError();
	}

	public static Couple<BlockPos> getBounds(BlockPos base, Iterable<BlockPos> positions) {
		BlockPos.MutableBlockPos minPos = base.mutable();
		BlockPos.MutableBlockPos maxPos = base.mutable();

		for (BlockPos pos : positions) {
			minPos.setX(Math.min(pos.getX(), minPos.getX()));
			minPos.setY(Math.min(pos.getY(), minPos.getY()));
			minPos.setZ(Math.min(pos.getZ(), minPos.getZ()));


			maxPos.setX(Math.max(pos.getX(), maxPos.getX()));
			maxPos.setY(Math.max(pos.getY(), maxPos.getY()));
			maxPos.setZ(Math.max(pos.getZ(), maxPos.getZ()));
		}
		return Couple.create(minPos.immutable(), maxPos.immutable());
	}
}
