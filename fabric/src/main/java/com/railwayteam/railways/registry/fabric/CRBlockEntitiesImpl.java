package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.railwayteam.railways.content.fuel.tank.FuelTankRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CRBlockEntitiesImpl {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final BlockEntityEntry<FuelTankBlockEntity> FUEL_TANK = REGISTRATE
            .blockEntity("fuel_tank", FuelTankBlockEntity::new)
            .validBlocks(CRBlocksImpl.FUEL_TANK)
            .renderer(() -> FuelTankRenderer::new)
            .register();

    public static void platformBasedRegistration() {}
}
