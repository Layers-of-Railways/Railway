package net.minecraft.client.gui.spectator;

import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISpectatorMenuView {
   List<ISpectatorMenuObject> getItems();

   ITextComponent getPrompt();
}