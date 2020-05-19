package net.minecraft.client.gui.toasts;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToastGui extends AbstractGui {
   private final Minecraft mc;
   private final ToastGui.ToastInstance<?>[] visible = new ToastGui.ToastInstance[5];
   private final Deque<IToast> toastsQueue = Queues.newArrayDeque();

   public ToastGui(Minecraft mcIn) {
      this.mc = mcIn;
   }

   public void render() {
      if (!this.mc.gameSettings.hideGUI) {
         for(int i = 0; i < this.visible.length; ++i) {
            ToastGui.ToastInstance<?> toastinstance = this.visible[i];
            if (toastinstance != null && toastinstance.render(this.mc.getMainWindow().getScaledWidth(), i)) {
               this.visible[i] = null;
            }

            if (this.visible[i] == null && !this.toastsQueue.isEmpty()) {
               this.visible[i] = new ToastGui.ToastInstance(this.toastsQueue.removeFirst());
            }
         }

      }
   }

   @Nullable
   public <T extends IToast> T getToast(Class<? extends T> p_192990_1_, Object p_192990_2_) {
      for(ToastGui.ToastInstance<?> toastinstance : this.visible) {
         if (toastinstance != null && p_192990_1_.isAssignableFrom(toastinstance.getToast().getClass()) && toastinstance.getToast().getType().equals(p_192990_2_)) {
            return (T)toastinstance.getToast();
         }
      }

      for(IToast itoast : this.toastsQueue) {
         if (p_192990_1_.isAssignableFrom(itoast.getClass()) && itoast.getType().equals(p_192990_2_)) {
            return (T)itoast;
         }
      }

      return (T)null;
   }

   public void clear() {
      Arrays.fill(this.visible, (Object)null);
      this.toastsQueue.clear();
   }

   public void add(IToast toastIn) {
      this.toastsQueue.add(toastIn);
   }

   public Minecraft getMinecraft() {
      return this.mc;
   }

   @OnlyIn(Dist.CLIENT)
   class ToastInstance<T extends IToast> {
      private final T toast;
      private long animationTime = -1L;
      private long visibleTime = -1L;
      private IToast.Visibility visibility = IToast.Visibility.SHOW;

      private ToastInstance(T toastIn) {
         this.toast = toastIn;
      }

      public T getToast() {
         return this.toast;
      }

      private float getVisibility(long p_193686_1_) {
         float f = MathHelper.clamp((float)(p_193686_1_ - this.animationTime) / 600.0F, 0.0F, 1.0F);
         f = f * f;
         return this.visibility == IToast.Visibility.HIDE ? 1.0F - f : f;
      }

      public boolean render(int p_193684_1_, int p_193684_2_) {
         long i = Util.milliTime();
         if (this.animationTime == -1L) {
            this.animationTime = i;
            this.visibility.playSound(ToastGui.this.mc.getSoundHandler());
         }

         if (this.visibility == IToast.Visibility.SHOW && i - this.animationTime <= 600L) {
            this.visibleTime = i;
         }

         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)p_193684_1_ - 160.0F * this.getVisibility(i), (float)(p_193684_2_ * 32), (float)(800 + p_193684_2_));
         IToast.Visibility itoast$visibility = this.toast.draw(ToastGui.this, i - this.visibleTime);
         RenderSystem.popMatrix();
         if (itoast$visibility != this.visibility) {
            this.animationTime = i - (long)((int)((1.0F - this.getVisibility(i)) * 600.0F));
            this.visibility = itoast$visibility;
            this.visibility.playSound(ToastGui.this.mc.getSoundHandler());
         }

         return this.visibility == IToast.Visibility.HIDE && i - this.animationTime > 600L;
      }
   }
}