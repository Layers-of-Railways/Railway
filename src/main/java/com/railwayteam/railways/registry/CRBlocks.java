package com.railwayteam.railways.registry;

import com.simibubi.create.repack.registrate.Registrate;

public class CRBlocks {
    //public static BlockEntry<WayPointBlock> R_BLOCK_WAYPOINT;

    public static void register(Registrate reg) {
      /*
        R_BLOCK_WAYPOINT = reg.block(WayPointBlock.name, WayPointBlock::new)// tell Registrate how to create it
                .recipe((ctx, prov) -> {
                    ctx.getEntry().recipe(ctx, prov, AllBlocks.SAIL.get());
                    ctx.getEntry().recipe(ctx, prov, AllBlocks.SAIL_FRAME.get());
                })
                .properties(p -> p.hardnessAndResistance(5.0f, 6.0f))    // set block properties
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),                 // block state determines the model
                        prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName())) // hence why that's tucked in here
                ))
                .simpleItem()     // nothing special about the item right now
                .lang("Waypoint") // give it a friendly name
                .register();      // pack it up for Registrate

       */
    }
}
