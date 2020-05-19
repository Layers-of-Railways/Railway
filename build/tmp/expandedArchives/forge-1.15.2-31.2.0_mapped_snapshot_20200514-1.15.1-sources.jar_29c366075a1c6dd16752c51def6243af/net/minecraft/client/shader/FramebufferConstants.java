package net.minecraft.client.shader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FramebufferConstants {
   public static int GL_FRAMEBUFFER;
   public static int GL_RENDERBUFFER;
   public static int GL_COLOR_ATTACHMENT0;
   public static int GL_DEPTH_ATTACHMENT;
   public static int GL_FRAMEBUFFER_COMPLETE;
   public static int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
   public static int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
   public static int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
   public static int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
}