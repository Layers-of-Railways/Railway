package com.railwayteam.railways.base;

import com.railwayteam.railways.Railways;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import net.minecraft.resources.ResourceLocation;

public class CTSpriteShifts {
  private static final ResourceLocation boiler   = new ResourceLocation(Railways.MODID, "block/boiler/boiler_side");
  private static final ResourceLocation boilerCT = new ResourceLocation(Railways.MODID, "block/boiler/boiler_connected");
  public  static final CTSpriteShiftEntry BOILER = CTSpriteShifter.getCT(CTSpriteShifter.CTType.OMNIDIRECTIONAL, boiler, boilerCT);
}
