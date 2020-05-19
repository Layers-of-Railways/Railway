package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectatorGui extends AbstractGui implements ISpectatorMenuRecipient {
   private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation SPECTATOR_WIDGETS = new ResourceLocation("textures/gui/spectator_widgets.png");
   private final Minecraft mc;
   private long lastSelectionTime;
   private SpectatorMenu menu;

   public SpectatorGui(Minecraft mcIn) {
      this.mc = mcIn;
   }

   public void onHotbarSelected(int p_175260_1_) {
      this.lastSelectionTime = Util.milliTime();
      if (this.menu != null) {
         this.menu.selectSlot(p_175260_1_);
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }

   private float getHotbarAlpha() {
      long i = this.lastSelectionTime - Util.milliTime() + 5000L;
      return MathHelper.clamp((float)i / 2000.0F, 0.0F, 1.0F);
   }

   public void renderTooltip(float p_195622_1_) {
      if (this.menu != null) {
         float f = this.getHotbarAlpha();
         if (f <= 0.0F) {
            this.menu.exit();
         } else {
            int i = this.mc.getMainWindow().getScaledWidth() / 2;
            int j = this.getBlitOffset();
            this.setBlitOffset(-90);
            int k = MathHelper.floor((float)this.mc.getMainWindow().getScaledHeight() - 22.0F * f);
            SpectatorDetails spectatordetails = this.menu.getCurrentPage();
            this.func_214456_a(f, i, k, spectatordetails);
            this.setBlitOffset(j);
         }
      }
   }

   protected void func_214456_a(float p_214456_1_, int p_214456_2_, int p_214456_3_, SpectatorDetails p_214456_4_) {
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, p_214456_1_);
      this.mc.getTextureManager().bindTexture(WIDGETS);
      this.blit(p_214456_2_ - 91, p_214456_3_, 0, 0, 182, 22);
      if (p_214456_4_.getSelectedSlot() >= 0) {
         this.blit(p_214456_2_ - 91 - 1 + p_214456_4_.getSelectedSlot() * 20, p_214456_3_ - 1, 0, 22, 24, 22);
      }

      for(int i = 0; i < 9; ++i) {
         this.renderSlot(i, this.mc.getMainWindow().getScaledWidth() / 2 - 90 + i * 20 + 2, (float)(p_214456_3_ + 3), p_214456_1_, p_214456_4_.getObject(i));
      }

      RenderSystem.disableRescaleNormal();
      RenderSystem.disableBlend();
   }

   private void renderSlot(int p_175266_1_, int p_175266_2_, float p_175266_3_, float p_175266_4_, ISpectatorMenuObject p_175266_5_) {
      this.mc.getTextureManager().bindTexture(SPECTATOR_WIDGETS);
      if (p_175266_5_ != SpectatorMenu.EMPTY_SLOT) {
         int i = (int)(p_175266_4_ * 255.0F);
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)p_175266_2_, p_175266_3_, 0.0F);
         float f = p_175266_5_.isEnabled() ? 1.0F : 0.25F;
         RenderSystem.color4f(f, f, f, p_175266_4_);
         p_175266_5_.renderIcon(f, i);
         RenderSystem.popMatrix();
         String s = String.valueOf((Object)this.mc.gameSettings.keyBindsHotbar[p_175266_1_].getLocalizedName());
         if (i > 3 && p_175266_5_.isEnabled()) {
            this.mc.fontRenderer.drawStringWithShadow(s, (float)(p_175266_2_ + 19 - 2 - this.mc.fontRenderer.getStringWidth(s)), p_175266_3_ + 6.0F + 3.0F, 16777215 + (i << 24));
         }
      }

   }

   public void renderSelectedItem() {
      int i = (int)(this.getHotbarAlpha() * 255.0F);
      if (i > 3 && this.menu != null) {
         ISpectatorMenuObject ispectatormenuobject = this.menu.getSelectedItem();
         String s = ispectatormenuobject == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt().getFormattedText() : ispectatormenuobject.getSpectatorName().getFormattedText();
         if (s != null) {
            int j = (this.mc.getMainWindow().getScaledWidth() - this.mc.fontRenderer.getStringWidth(s)) / 2;
            int k = this.mc.getMainWindow().getScaledHeight() - 35;
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.mc.fontRenderer.drawStringWithShadow(s, (float)j, (float)k, 16777215 + (i << 24));
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
         }
      }

   }

   public void onSpectatorMenuClosed(SpectatorMenu menu) {
      this.menu = null;
      this.lastSelectionTime = 0L;
   }

   public boolean isMenuActive() {
      return this.menu != null;
   }

   public void onMouseScroll(double amount) {
      int i;
      for(i = this.menu.getSelectedSlot() + (int)amount; i >= 0 && i <= 8 && (this.menu.getItem(i) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(i).isEnabled()); i = (int)((double)i + amount)) {
         ;
      }

      if (i >= 0 && i <= 8) {
         this.menu.selectSlot(i);
         this.lastSelectionTime = Util.milliTime();
      }

   }

   public void onMiddleClick() {
      this.lastSelectionTime = Util.milliTime();
      if (this.isMenuActive()) {
         int i = this.menu.getSelectedSlot();
         if (i != -1) {
            this.menu.selectSlot(i);
         }
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }
}