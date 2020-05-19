package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseInventoryScreen extends ContainerScreen<HorseInventoryContainer> {
   private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/horse.png");
   /** The EntityHorse whose inventory is currently being accessed. */
   private final AbstractHorseEntity horseEntity;
   /** The mouse x-position recorded during the last rendered frame. */
   private float mousePosx;
   /** The mouse y-position recorded during the last renderered frame. */
   private float mousePosY;

   public HorseInventoryScreen(HorseInventoryContainer p_i51084_1_, PlayerInventory p_i51084_2_, AbstractHorseEntity p_i51084_3_) {
      super(p_i51084_1_, p_i51084_2_, p_i51084_3_.getDisplayName());
      this.horseEntity = p_i51084_3_;
      this.passEvents = false;
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.blit(i, j, 0, 0, this.xSize, this.ySize);
      if (this.horseEntity instanceof AbstractChestedHorseEntity) {
         AbstractChestedHorseEntity abstractchestedhorseentity = (AbstractChestedHorseEntity)this.horseEntity;
         if (abstractchestedhorseentity.hasChest()) {
            this.blit(i + 79, j + 17, 0, this.ySize, abstractchestedhorseentity.getInventoryColumns() * 18, 54);
         }
      }

      if (this.horseEntity.canBeSaddled()) {
         this.blit(i + 7, j + 35 - 18, 18, this.ySize + 54, 18, 18);
      }

      if (this.horseEntity.wearsArmor()) {
         if (this.horseEntity instanceof LlamaEntity) {
            this.blit(i + 7, j + 35, 36, this.ySize + 54, 18, 18);
         } else {
            this.blit(i + 7, j + 35, 0, this.ySize + 54, 18, 18);
         }
      }

      InventoryScreen.drawEntityOnScreen(i + 51, j + 60, 17, (float)(i + 51) - this.mousePosx, (float)(j + 75 - 50) - this.mousePosY, this.horseEntity);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.mousePosx = (float)p_render_1_;
      this.mousePosY = (float)p_render_2_;
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }
}