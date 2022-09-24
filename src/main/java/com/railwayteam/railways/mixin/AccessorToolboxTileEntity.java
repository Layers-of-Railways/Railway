package com.railwayteam.railways.mixin;

import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ToolboxTileEntity.class, remap = false)
public interface AccessorToolboxTileEntity {
  @Accessor("inventory")
  ToolboxInventory getInventory();
}
