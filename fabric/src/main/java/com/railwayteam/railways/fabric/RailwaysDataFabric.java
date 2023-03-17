package com.railwayteam.railways.fabric;

import com.railwayteam.railways.Railways;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class RailwaysDataFabric implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		Path railwaysResources = Paths.get(System.getProperty(ExistingFileHelper.EXISTING_RESOURCES));
		ExistingFileHelper helper = new ExistingFileHelper(
				Set.of(railwaysResources), Set.of("create"), true, null, null
		);
		Railways.registrate().setupDatagen(gen, helper);
		Railways.gatherData(gen);
	}
}
