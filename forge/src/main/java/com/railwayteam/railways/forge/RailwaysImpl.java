/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.forge.CRConfigsImpl;
import com.railwayteam.railways.multiloader.CommandRegistrar;
import com.railwayteam.railways.multiloader.Env;
import com.railwayteam.railways.registry.forge.CRCreativeModeTabsImpl;
import com.railwayteam.railways.registry.forge.CRParticleTypesParticleEntryImpl;
import com.railwayteam.railways.util.Utils;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Mod(Railways.MOD_ID)
@Mod.EventBusSubscriber
public class RailwaysImpl {
	static IEventBus bus;

	public RailwaysImpl() {
		restoreLoggers();
		bus = FMLJavaModLoadingContext.get().getModEventBus();
		CRCreativeModeTabsImpl.register(RailwaysImpl.bus);
		Railways.init();
		CRConfigsImpl.register(ModLoadingContext.get());
		CRParticleTypesParticleEntryImpl.register(bus);
		//noinspection Convert2MethodRef
		Env.CLIENT.runIfCurrent(() -> () -> RailwaysClientImpl.init());

		bus.addListener(RailwaysImpl::onCommonSetup);
	}

	public static void onCommonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(Railways::postRegistrationInit);
	}

	public static void finalizeRegistrate() {
		Railways.registrate().registerEventListeners(bus);
	}

	private static final Set<CommandRegistrar> commandRegistrars = new HashSet<>();

	public static void registerCommands(CommandRegistrar registrar) {
		commandRegistrars.add(registrar);
	}

	@SubscribeEvent
	public static void onCommandRegistration(RegisterCommandsEvent event) {
		CommandSelection selection = event.getCommandSelection();
		boolean dedicated = selection == CommandSelection.ALL || selection == CommandSelection.DEDICATED;
		commandRegistrars.forEach(registrar -> registrar.register(event.getDispatcher(), dedicated, event.getBuildContext()));
	}

	private static void restoreLoggers() {
		if (Utils.isDevEnv()) {
			// restore our logging config, since forge likes to nuke it for fun
			for (String prop : new String[] {"log4j.configurationFile", "log4j2.configurationFile"}) {
				String file = System.getProperty(prop);
				if (file != null) {
					Configurator.reconfigure(ConfigurationFactory.getInstance().getConfiguration(
						LoggerContext.getContext(),
						ConfigurationSource.fromUri(URI.create(file))
					));
					break;
				}
			}
		}
	}
}
