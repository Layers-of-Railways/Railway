package net.minecraft.client.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImageButton extends Button {
   private final ResourceLocation resourceLocation;
   private final int xTexStart;
   private final int yTexStart;
   private final int yDiffText;
   private final int textureWidth;
   private final int textureHeight;

   public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, Button.IPressable onPressIn) {
      this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, 256, 256, onPressIn);
   }

   public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, int p_i51135_9_, int p_i51135_10_, Button.IPressable onPressIn) {
      this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, p_i51135_9_, p_i51135_10_, onPressIn, "");
   }

   public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, int p_i51136_9_, int p_i51136_10_, Button.IPressable onPressIn, String textIn) {
      super(xIn, yIn, widthIn, heightIn, textIn, onPressIn);
      this.textureWidth = p_i51136_9_;
      this.textureHeight = p_i51136_10_;
      this.xTexStart = xTexStartIn;
      this.yTexStart = yTexStartIn;
      this.yDiffText = yDiffTextIn;
      this.resourceLocation = resourceLocationIn;
   }

   public void setPosition(int xIn, int yIn) {
      this.x = xIn;
      this.y = yIn;
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getTextureManager().bindTexture(this.resourceLocation);
      RenderSystem.disableDepthTest();
      int i = this.yTexStart;
      if (this.isHovered()) {
         i += this.yDiffText;
      }

      blit(this.x, this.y, (float)this.xTexStart, (float)i, this.width, this.height, this.textureWidth, this.textureHeight);
      RenderSystem.enableDepthTest();
   }
}