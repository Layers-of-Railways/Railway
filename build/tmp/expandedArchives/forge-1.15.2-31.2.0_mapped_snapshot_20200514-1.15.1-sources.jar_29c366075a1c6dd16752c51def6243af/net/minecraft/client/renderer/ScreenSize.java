package net.minecraft.client.renderer;

import java.util.OptionalInt;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenSize {
   public final int width;
   public final int height;
   public final OptionalInt fullscreenWidth;
   public final OptionalInt fullscreenHeight;
   public final boolean fullscreen;

   public ScreenSize(int widthIn, int heightIn, OptionalInt fullscreenWidthIn, OptionalInt fullscreenHeightIn, boolean fullscreenIn) {
      this.width = widthIn;
      this.height = heightIn;
      this.fullscreenWidth = fullscreenWidthIn;
      this.fullscreenHeight = fullscreenHeightIn;
      this.fullscreen = fullscreenIn;
   }
}