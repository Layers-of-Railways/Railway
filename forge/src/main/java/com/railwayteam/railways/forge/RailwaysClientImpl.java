package com.railwayteam.railways.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysClient;
import com.simibubi.create.foundation.ModFilePackResources;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.forgespi.locating.IModFile;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber(Dist.CLIENT)
public class RailwaysClientImpl {
	public static void init() {
		RailwaysClient.init();
		RailwaysImpl.bus.addListener(RailwaysClientImpl::onModelLayerRegistration);
		RailwaysImpl.bus.addListener(RailwaysClientImpl::onBuiltinPackRegistration);
	}

	// region -- Client Commands ---

	private static final Set<Consumer<CommandDispatcher<SharedSuggestionProvider>>> clientCommandConsumers = new HashSet<>();

	public static void registerClientCommands(Consumer<CommandDispatcher<SharedSuggestionProvider>> consumer) {
		clientCommandConsumers.add(consumer);
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // jank!
	@SubscribeEvent
	public static void onClientCommandRegistration(RegisterClientCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		CommandDispatcher<SharedSuggestionProvider> casted = (CommandDispatcher) dispatcher;
		clientCommandConsumers.forEach(consumer -> consumer.accept(casted));
	}

	// endregion

	// region --- Model Layers ---

	private static final Map<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers = new HashMap<>();

	public static void registerModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
		modelLayers.put(layer, definition);
	}

	public static void onModelLayerRegistration(EntityRenderersEvent.RegisterLayerDefinitions event) {
		modelLayers.forEach(event::registerLayerDefinition);
		modelLayers.clear();
	}

	// endregion

	// region --- Built-in Packs ---

	private record PackInfo(String id, String name) {}
	private static final List<PackInfo> packs = new ArrayList<>();

	public static void registerBuiltinPack(String id, String name) {
		packs.add(new PackInfo(id, name));
	}

	// based on Create's impl
	public static void onBuiltinPackRegistration(AddPackFindersEvent event) {
		if (event.getPackType() != PackType.CLIENT_RESOURCES)
			return;
		IModFile modFile = ModList.get().getModFileById(Railways.MODID).getFile();

		packs.forEach(pack -> event.addRepositorySource((consumer, constructor) -> consumer.accept(
				Pack.create(Railways.asResource(pack.id).toString(),
						false,
						() -> new ModFilePackResources(pack.name, modFile, "resourcepacks/" + pack.id),
						constructor,
						Pack.Position.TOP,
						PackSource.DEFAULT
				)
		)));
		packs.clear();
	}

	// endregion
}
