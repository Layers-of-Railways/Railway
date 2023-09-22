package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.fuel.tank.FuelTankBlock;
import com.railwayteam.railways.content.fuel.tank.FuelTankGenerator;
import com.railwayteam.railways.content.fuel.tank.FuelTankItem;
import com.railwayteam.railways.content.fuel.tank.FuelTankModel;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class CRBlocksImpl {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final BlockEntry<FuelTankBlock> FUEL_TANK = REGISTRATE.block("fuel_tank", FuelTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .blockstate(new FuelTankGenerator()::generate)
            .onRegister(CreateRegistrate.blockModel(() -> FuelTankModel::standard))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(FuelTankItem::new)
            .model(AssetLookup.customBlockItemModel("_", "block_single_window"))
            .build()
            .register();

    public static void platformBasedRegistration() {}
}
