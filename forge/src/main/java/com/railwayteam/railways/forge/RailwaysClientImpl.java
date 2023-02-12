package com.railwayteam.railways.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysClient;
import com.simibubi.create.foundation.ModFilePackResources;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RailwaysClientImpl {
	public static void init() {
		RailwaysClient.init();
		RailwaysImpl.bus.addListener(RailwaysClientImpl::onModelLayerRegistration);
		RailwaysImpl.bus.addListener(RailwaysClientImpl::onBuiltinPackRegistration);
	}

	// region --- Model Layers ---
	// forge: must store and save for event

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
	// forge: must store and save for event

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
