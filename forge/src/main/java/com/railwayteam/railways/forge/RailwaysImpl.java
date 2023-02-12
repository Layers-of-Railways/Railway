package com.railwayteam.railways.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.List;

@Mod(Railways.MODID)
public class RailwaysImpl {
	static IEventBus bus;

	public RailwaysImpl() {
		bus = FMLJavaModLoadingContext.get().getModEventBus();
		Railways.init();
		//noinspection Convert2MethodRef
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RailwaysClientImpl.init());
	}

	public static String findVersion() {
		String versionString = "UNKNOWN";

		List<IModInfo> infoList = ModList.get().getModFileById(Railways.MODID).getMods();
		if (infoList.size() > 1) {
			Railways.LOGGER.error("Multiple mods for MOD_ID: " + Railways.MODID);
		}
		for (IModInfo info : infoList) {
			if (info.getModId().equals(Railways.MODID)) {
				versionString = MavenVersionStringHelper.artifactVersionToString(info.getVersion());
				break;
			}
		}
		return versionString;
	}

	public static void finalizeRegistrate() {
		Railways.registrate().registerEventListeners(bus);
	}
}
