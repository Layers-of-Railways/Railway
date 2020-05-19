package net.minecraft.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LoadingGui extends AbstractGui implements IRenderable {
   public boolean isPauseScreen() {
      return true;
   }
}