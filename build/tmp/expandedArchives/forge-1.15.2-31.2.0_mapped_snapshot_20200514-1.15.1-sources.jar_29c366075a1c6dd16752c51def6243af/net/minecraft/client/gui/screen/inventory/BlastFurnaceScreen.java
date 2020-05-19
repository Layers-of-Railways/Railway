package net.minecraft.client.gui.screen.inventory;

import net.minecraft.client.gui.recipebook.BlastFurnaceRecipeGui;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.BlastFurnaceContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlastFurnaceScreen extends AbstractFurnaceScreen<BlastFurnaceContainer> {
   private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/blast_furnace.png");

   public BlastFurnaceScreen(BlastFurnaceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, new BlastFurnaceRecipeGui(), inv, titleIn, GUI_TEXTURE);
   }
}