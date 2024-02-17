package com.railwayteam.railways.mixin.client;

import com.simibubi.create.content.equipment.toolbox.ToolboxHandlerClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ToolboxHandlerClient.class, remap = false)
public interface AccessorToolboxHandlerClient {
  @Accessor("COOLDOWN")
  static int getCOOLDOWN() {
    throw new AssertionError();
  }

  @Accessor("COOLDOWN")
  static void setCOOLDOWN(int i) {
    throw new AssertionError();
  }
}
