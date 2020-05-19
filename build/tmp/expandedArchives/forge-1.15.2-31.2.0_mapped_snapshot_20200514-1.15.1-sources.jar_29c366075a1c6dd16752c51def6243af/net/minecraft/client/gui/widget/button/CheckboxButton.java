package net.minecraft.client.gui.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CheckboxButton extends AbstractButton {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
   boolean checked;

   public CheckboxButton(int xIn, int yIn, int widthIn, int heightIn, String msg, boolean isChecked) {
      super(xIn, yIn, widthIn, heightIn, msg);
      this.checked = isChecked;
   }

   public void onPress() {
      this.checked = !this.checked;
   }

   public boolean isChecked() {
      return this.checked;
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getTextureManager().bindTexture(TEXTURE);
      RenderSystem.enableDepthTest();
      FontRenderer fontrenderer = minecraft.fontRenderer;
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      blit(this.x, this.y, 0.0F, this.checked ? 20.0F : 0.0F, 20, this.height, 32, 64);
      this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
      int i = 14737632;
      this.drawString(fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
   }
}