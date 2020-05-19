package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementToast implements IToast {
   private final Advancement advancement;
   private boolean hasPlayedSound;

   public AdvancementToast(Advancement advancementIn) {
      this.advancement = advancementIn;
   }

   public IToast.Visibility draw(ToastGui toastGui, long delta) {
      toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      DisplayInfo displayinfo = this.advancement.getDisplay();
      toastGui.blit(0, 0, 0, 0, 160, 32);
      if (displayinfo != null) {
         List<String> list = toastGui.getMinecraft().fontRenderer.listFormattedStringToWidth(displayinfo.getTitle().getFormattedText(), 125);
         int i = displayinfo.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;
         if (list.size() == 1) {
            toastGui.getMinecraft().fontRenderer.drawString(I18n.format("advancements.toast." + displayinfo.getFrame().getName()), 30.0F, 7.0F, i | -16777216);
            toastGui.getMinecraft().fontRenderer.drawString(displayinfo.getTitle().getFormattedText(), 30.0F, 18.0F, -1);
         } else {
            int j = 1500;
            float f = 300.0F;
            if (delta < 1500L) {
               int k = MathHelper.floor(MathHelper.clamp((float)(1500L - delta) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
               toastGui.getMinecraft().fontRenderer.drawString(I18n.format("advancements.toast." + displayinfo.getFrame().getName()), 30.0F, 11.0F, i | k);
            } else {
               int i1 = MathHelper.floor(MathHelper.clamp((float)(delta - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
               int l = 16 - list.size() * 9 / 2;

               for(String s : list) {
                  toastGui.getMinecraft().fontRenderer.drawString(s, 30.0F, (float)l, 16777215 | i1);
                  l += 9;
               }
            }
         }

         if (!this.hasPlayedSound && delta > 0L) {
            this.hasPlayedSound = true;
            if (displayinfo.getFrame() == FrameType.CHALLENGE) {
               toastGui.getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            }
         }

         toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI((LivingEntity)null, displayinfo.getIcon(), 8, 8);
         return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
      } else {
         return IToast.Visibility.HIDE;
      }
   }
}