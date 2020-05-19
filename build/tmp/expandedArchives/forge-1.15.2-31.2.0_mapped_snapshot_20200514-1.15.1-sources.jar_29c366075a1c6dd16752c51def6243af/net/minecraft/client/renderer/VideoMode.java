package net.minecraft.client.renderer;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@OnlyIn(Dist.CLIENT)
public final class VideoMode {
   private final int width;
   private final int height;
   private final int redBits;
   private final int greenBits;
   private final int blueBits;
   private final int refreshRate;
   private static final Pattern PATTERN = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

   public VideoMode(int widthIn, int heightIn, int redBitsIn, int greenBitsIn, int blueBitsIn, int refreshRateIn) {
      this.width = widthIn;
      this.height = heightIn;
      this.redBits = redBitsIn;
      this.greenBits = greenBitsIn;
      this.blueBits = blueBitsIn;
      this.refreshRate = refreshRateIn;
   }

   public VideoMode(Buffer buffer) {
      this.width = buffer.width();
      this.height = buffer.height();
      this.redBits = buffer.redBits();
      this.greenBits = buffer.greenBits();
      this.blueBits = buffer.blueBits();
      this.refreshRate = buffer.refreshRate();
   }

   public VideoMode(GLFWVidMode glfwVidMode) {
      this.width = glfwVidMode.width();
      this.height = glfwVidMode.height();
      this.redBits = glfwVidMode.redBits();
      this.greenBits = glfwVidMode.greenBits();
      this.blueBits = glfwVidMode.blueBits();
      this.refreshRate = glfwVidMode.refreshRate();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getRedBits() {
      return this.redBits;
   }

   public int getGreenBits() {
      return this.greenBits;
   }

   public int getBlueBits() {
      return this.blueBits;
   }

   public int getRefreshRate() {
      return this.refreshRate;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VideoMode videomode = (VideoMode)p_equals_1_;
         return this.width == videomode.width && this.height == videomode.height && this.redBits == videomode.redBits && this.greenBits == videomode.greenBits && this.blueBits == videomode.blueBits && this.refreshRate == videomode.refreshRate;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.width, this.height, this.redBits, this.greenBits, this.blueBits, this.refreshRate);
   }

   public String toString() {
      return String.format("%sx%s@%s (%sbit)", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }

   public static Optional<VideoMode> parseFromSettings(@Nullable String videoModeIn) {
      if (videoModeIn == null) {
         return Optional.empty();
      } else {
         try {
            Matcher matcher = PATTERN.matcher(videoModeIn);
            if (matcher.matches()) {
               int i = Integer.parseInt(matcher.group(1));
               int j = Integer.parseInt(matcher.group(2));
               String s = matcher.group(3);
               int k;
               if (s == null) {
                  k = 60;
               } else {
                  k = Integer.parseInt(s);
               }

               String s1 = matcher.group(4);
               int l;
               if (s1 == null) {
                  l = 24;
               } else {
                  l = Integer.parseInt(s1);
               }

               int i1 = l / 3;
               return Optional.of(new VideoMode(i, j, i1, i1, i1, k));
            }
         } catch (Exception var9) {
            ;
         }

         return Optional.empty();
      }
   }

   public String getSettingsString() {
      return String.format("%sx%s@%s:%s", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }
}