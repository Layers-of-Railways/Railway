package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.DispenserContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DispenserScreen extends ContainerScreen<DispenserContainer> {
   private static final ResourceLocation DISPENSER_GUI_TEXTURES = new ResourceLocation("textures/gui/container/dispenser.png");

   public DispenserScreen(DispenserContainer p_i51093_1_, PlayerInventory p_i51093_2_, ITextComponent p_i51093_3_) {
      super(p_i51093_1_, p_i51093_2_, p_i51093_3_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String s = this.title.getFormattedText();
      this.font.drawString(s, (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(DISPENSER_GUI_TEXTURES);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.blit(i, j, 0, 0, this.xSize, this.ySize);
   }
}