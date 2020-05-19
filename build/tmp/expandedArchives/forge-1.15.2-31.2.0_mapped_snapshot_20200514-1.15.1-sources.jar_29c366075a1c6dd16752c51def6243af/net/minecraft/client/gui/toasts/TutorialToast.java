package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TutorialToast implements IToast {
   private final TutorialToast.Icons icon;
   private final String title;
   private final String subtitle;
   private IToast.Visibility visibility = IToast.Visibility.SHOW;
   private long lastDelta;
   private float displayedProgress;
   private float currentProgress;
   private final boolean hasProgressBar;

   public TutorialToast(TutorialToast.Icons iconIn, ITextComponent titleComponent, @Nullable ITextComponent subtitleComponent, boolean drawProgressBar) {
      this.icon = iconIn;
      this.title = titleComponent.getFormattedText();
      this.subtitle = subtitleComponent == null ? null : subtitleComponent.getFormattedText();
      this.hasProgressBar = drawProgressBar;
   }

   public IToast.Visibility draw(ToastGui toastGui, long delta) {
      toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      toastGui.blit(0, 0, 0, 96, 160, 32);
      this.icon.draw(toastGui, 6, 6);
      if (this.subtitle == null) {
         toastGui.getMinecraft().fontRenderer.drawString(this.title, 30.0F, 12.0F, -11534256);
      } else {
         toastGui.getMinecraft().fontRenderer.drawString(this.title, 30.0F, 7.0F, -11534256);
         toastGui.getMinecraft().fontRenderer.drawString(this.subtitle, 30.0F, 18.0F, -16777216);
      }

      if (this.hasProgressBar) {
         AbstractGui.fill(3, 28, 157, 29, -1);
         float f = (float)MathHelper.clampedLerp((double)this.displayedProgress, (double)this.currentProgress, (double)((float)(delta - this.lastDelta) / 100.0F));
         int i;
         if (this.currentProgress >= this.displayedProgress) {
            i = -16755456;
         } else {
            i = -11206656;
         }

         AbstractGui.fill(3, 28, (int)(3.0F + 154.0F * f), 29, i);
         this.displayedProgress = f;
         this.lastDelta = delta;
      }

      return this.visibility;
   }

   public void hide() {
      this.visibility = IToast.Visibility.HIDE;
   }

   public void setProgress(float progress) {
      this.currentProgress = progress;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Icons {
      MOVEMENT_KEYS(0, 0),
      MOUSE(1, 0),
      TREE(2, 0),
      RECIPE_BOOK(0, 1),
      WOODEN_PLANKS(1, 1);

      private final int column;
      private final int row;

      private Icons(int columnIn, int rowIn) {
         this.column = columnIn;
         this.row = rowIn;
      }

      /**
       * Draws the icon at the specified position in the specified Gui
       */
      public void draw(AbstractGui guiIn, int x, int y) {
         RenderSystem.enableBlend();
         guiIn.blit(x, y, 176 + this.column * 20, this.row * 20, 20, 20);
         RenderSystem.enableBlend();
      }
   }
}