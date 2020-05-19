package net.minecraft.client.gui.spectator;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISpectatorMenuObject {
   void selectItem(SpectatorMenu menu);

   ITextComponent getSpectatorName();

   void renderIcon(float brightness, int alpha);

   boolean isEnabled();
}