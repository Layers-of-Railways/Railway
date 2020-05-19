package net.minecraft.client.gui.recipebook;

import java.util.List;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IRecipeUpdateListener {
   void recipesShown(List<IRecipe<?>> recipes);
}