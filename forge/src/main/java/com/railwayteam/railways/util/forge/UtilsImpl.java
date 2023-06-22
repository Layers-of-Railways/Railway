package com.railwayteam.railways.util.forge;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class UtilsImpl {
	public static boolean isModLoaded(String id, @Nullable String fabricId) {
		return ModList.get().isLoaded(id);
	}

	public static Path configDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	public static boolean isDevEnv() {
		return !FMLLoader.isProduction();
	}

    @OnlyIn(Dist.CLIENT)
    public static boolean isActiveAndMatches(KeyMapping mapping, InputConstants.Key keyCode) {
		return mapping.isActiveAndMatches(keyCode);
    }

    public static void sendCreatePacketToServer(SimplePacketBase packet) {
		AllPackets.getChannel().sendToServer(packet);
    }
}
