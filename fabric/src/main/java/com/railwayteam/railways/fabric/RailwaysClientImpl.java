package com.railwayteam.railways.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysClient;
import com.railwayteam.railways.content.conductor.fabric.ConductorCapItemRenderer;
import com.railwayteam.railways.fabric.events.ClientEventsFabric;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RailwaysClientImpl implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RailwaysClient.init();
		ClientEventsFabric.init();
		ConductorCapItemRenderer.register();
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // jank!
	public static void registerClientCommands(Consumer<CommandDispatcher<SharedSuggestionProvider>> consumer) {
		CommandDispatcher<FabricClientCommandSource> dispatcher = ClientCommandManager.getActiveDispatcher();
		 CommandDispatcher<SharedSuggestionProvider> casted = (CommandDispatcher) dispatcher;
		consumer.accept(casted);
	}

	public static void registerModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
		EntityModelLayerRegistry.registerModelLayer(layer, definition::get);
	}

	public static void registerBuiltinPack(String id, String name) {
		ModContainer mod = FabricLoader.getInstance().getModContainer(Railways.MODID).orElseThrow();
		ResourceManagerHelper.registerBuiltinResourcePack(
				Railways.asResource(id), mod, name, ResourcePackActivationType.NORMAL
		);
	}
}
