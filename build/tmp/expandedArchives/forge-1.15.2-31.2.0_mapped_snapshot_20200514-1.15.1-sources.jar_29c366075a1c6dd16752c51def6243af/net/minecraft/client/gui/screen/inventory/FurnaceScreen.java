package net.minecraft.client.gui.screen.inventory;

import net.minecraft.client.gui.recipebook.FurnaceRecipeGui;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FurnaceScreen extends AbstractFurnaceScreen<FurnaceContainer> {
   private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");

   public FurnaceScreen(FurnaceContainer p_i51089_1_, PlayerInventory p_i51089_2_, ITextComponent p_i51089_3_) {
      super(p_i51089_1_, new FurnaceRecipeGui(), p_i51089_2_, p_i51089_3_, FURNACE_GUI_TEXTURES);
   }
}