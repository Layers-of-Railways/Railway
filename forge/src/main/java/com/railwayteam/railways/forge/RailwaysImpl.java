package com.railwayteam.railways.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.multiloader.Env;
import com.railwayteam.railways.registry.forge.CRCreativeModeTabsImpl;
import com.railwayteam.railways.registry.forge.CRParticleTypesParticleEntryImpl;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
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

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

@Mod(Railways.MODID)
@Mod.EventBusSubscriber
public class RailwaysImpl {
	static IEventBus bus;

	public RailwaysImpl() {
		bus = FMLJavaModLoadingContext.get().getModEventBus();
		Railways.init();
		CRParticleTypesParticleEntryImpl.register(bus);
		CRCreativeModeTabsImpl.register(RailwaysImpl.bus);
		//noinspection Convert2MethodRef
		Env.CLIENT.runIfCurrent(() -> () -> RailwaysClientImpl.init());

		// inject into Launcher.INSTANCE.launchPlugins
		try {
			Launcher launcher = Launcher.INSTANCE;
			Field launchPlugins = Launcher.class.getDeclaredField("launchPlugins");
			launchPlugins.setAccessible(true);

			LaunchPluginHandler handler = (LaunchPluginHandler) launchPlugins.get(launcher);
			Field plugins = LaunchPluginHandler.class.getDeclaredField("plugins");
			plugins.setAccessible(true);

			//noinspection unchecked
			Map<String, ILaunchPluginService> map = (Map<String, ILaunchPluginService>) plugins.get(handler);
			ILaunchPluginService plugin = new CRLaunchPluginService();
			try {
				map.put(plugin.name(), plugin);
			} catch (UnsupportedOperationException ignored) {
				Railways.LOGGER.error("Failed to inject launch plugin, trying to create a new map");
				Map<String, ILaunchPluginService> newMap = new HashMap<>(map);
				newMap.put(plugin.name(), plugin);
				plugins.set(handler, newMap);
			}
		} catch (NoSuchFieldException | IllegalAccessException ignored) {}
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

	private static final Set<BiConsumer<CommandDispatcher<CommandSourceStack>, Boolean>> commandConsumers = new HashSet<>();

	public static void registerCommands(BiConsumer<CommandDispatcher<CommandSourceStack>, Boolean> consumer) {
		commandConsumers.add(consumer);
	}

	@SubscribeEvent
	public static void onCommandRegistration(RegisterCommandsEvent event) {
		CommandSelection selection = event.getCommandSelection();
		boolean dedicated = selection == CommandSelection.ALL || selection == CommandSelection.DEDICATED;
		commandConsumers.forEach(consumer -> consumer.accept(event.getDispatcher(), dedicated));
	}

	public static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {
		ModLoadingContext.get().registerConfig(type, spec);
	}
}
