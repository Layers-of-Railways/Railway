package net.minecraft.client.settings;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Monitor;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FullscreenResolutionOption extends SliderPercentageOption {
   public FullscreenResolutionOption(MainWindow mainWindowIn) {
      this(mainWindowIn, mainWindowIn.getMonitor());
   }

   private FullscreenResolutionOption(MainWindow mainWindowIn, @Nullable Monitor monitorIn) {
      super("options.fullscreen.resolution", -1.0D, monitorIn != null ? (double)(monitorIn.getVideoModeCount() - 1) : -1.0D, 1.0F, (p_225306_2_) -> {
         if (monitorIn == null) {
            return -1.0D;
         } else {
            Optional<VideoMode> optional = mainWindowIn.getVideoMode();
            return optional.map((p_225304_1_) -> {
               return (double)monitorIn.getVideoModeIndex(p_225304_1_);
            }).orElse(-1.0D);
         }
      }, (p_225303_2_, p_225303_3_) -> {
         if (monitorIn != null) {
            if (p_225303_3_ == -1.0D) {
               mainWindowIn.setVideoMode(Optional.empty());
            } else {
               mainWindowIn.setVideoMode(Optional.of(monitorIn.getVideoModeFromIndex(p_225303_3_.intValue())));
            }

         }
      }, (p_225305_1_, p_225305_2_) -> {
         if (monitorIn == null) {
            return I18n.format("options.fullscreen.unavailable");
         } else {
            double d0 = p_225305_2_.get(p_225305_1_);
            String s = p_225305_2_.getDisplayString();
            return d0 == -1.0D ? s + I18n.format("options.fullscreen.current") : monitorIn.getVideoModeFromIndex((int)d0).toString();
         }
      });
   }
}