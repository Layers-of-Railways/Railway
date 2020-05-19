package net.minecraft.client.gui.screen.inventory;

import net.minecraft.client.gui.recipebook.SmokerRecipeGui;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.SmokerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmokerScreen extends AbstractFurnaceScreen<SmokerContainer> {
   private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

   public SmokerScreen(SmokerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, new SmokerRecipeGui(), inv, titleIn, GUI_TEXTURE);
   }
}