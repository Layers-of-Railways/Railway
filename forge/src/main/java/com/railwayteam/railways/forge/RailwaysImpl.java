package com.railwayteam.railways.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.multiloader.Env;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod(Railways.MODID)
public class RailwaysImpl {
	static IEventBus bus;

	public RailwaysImpl() {
		bus = FMLJavaModLoadingContext.get().getModEventBus();
		Railways.init();
		//noinspection Convert2MethodRef
		Env.CLIENT.runIfCurrent(() -> () -> RailwaysClientImpl.init());
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

	private static final Set<TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, CommandSelection>> commandConsumers = new HashSet<>();

	public static void registerCommands(TriConsumer<CommandDispatcher<CommandSourceStack>, CommandBuildContext, CommandSelection> consumer) {
		commandConsumers.add(consumer);
	}

	@SubscribeEvent
	public static void onCommandRegistration(RegisterCommandsEvent event) {
		commandConsumers.forEach(consumer -> consumer.accept(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
	}

	public static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {
		ModLoadingContext.get().registerConfig(type, spec);
	}
}
