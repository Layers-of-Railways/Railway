package net.minecraft.client.gui.spectator;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISpectatorMenuRecipient {
   void onSpectatorMenuClosed(SpectatorMenu menu);
}