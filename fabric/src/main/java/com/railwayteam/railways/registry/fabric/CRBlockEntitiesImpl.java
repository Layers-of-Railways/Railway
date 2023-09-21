package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.fuel.psi.PortableFuelInterfaceBlockEntity;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlockEntity;
import com.railwayteam.railways.content.fuel.tank.FuelTankRenderer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.actors.psi.PSIInstance;
import com.simibubi.create.content.contraptions.actors.psi.PortableFluidInterfaceBlockEntity;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.simibubi.create.Create.REGISTRATE;

public class CRBlockEntitiesImpl {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final BlockEntityEntry<FuelTankBlockEntity> FUEL_TANK = REGISTRATE
            .blockEntity("fuel_tank", FuelTankBlockEntity::new)
            .validBlocks(CRBlocksImpl.FUEL_TANK)
            .renderer(() -> FuelTankRenderer::new)
            .register();

    public static final BlockEntityEntry<PortableFuelInterfaceBlockEntity> PORTABLE_FUEL_INTERFACE =
            REGISTRATE
                    .blockEntity("portable_fuel_interface", PortableFuelInterfaceBlockEntity::new)
                    .instance(() -> PSIInstance::new)
                    .validBlocks(CRBlocksImpl.PORTABLE_FUEL_INTERFACE)
                    .renderer(() -> PortableStorageInterfaceRenderer::new)
                    .register();

    public static void platformBasedRegistration() {}
}
