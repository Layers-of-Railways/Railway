package net.minecraft.client.gui.recipebook;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IRecipeShownListener {
   void recipesUpdated();

   RecipeBookGui getRecipeGui();
}