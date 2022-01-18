package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.*;
import com.railwayteam.railways.content.Boiler.BoilerBlock;
import com.railwayteam.railways.content.Firebox.FireboxBlock;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;

public class CRBlocks {
  public static BlockEntry<FireboxBlock>         BLOCK_FIREBOX;
  public static BlockEntry<BoilerBlock>          BLOCK_BOILER;
  public static BlockEntry<HydraulicPistonBlock> BLOCK_HYDRAULIC_PISTON;

  public static void register(Registrate reg) {
    BLOCK_FIREBOX = reg.block("firebox", FireboxBlock::new)
    .lang("Firebox")
    .register();

    BLOCK_BOILER = reg.block("boiler", BoilerBlock::new)
    .lang("Boiler")
    .register();

    BLOCK_HYDRAULIC_PISTON = reg.block("hydraulic_piston", HydraulicPistonBlock::new)
    .lang("Hydraulic Piston")
    .register();
  }
}
