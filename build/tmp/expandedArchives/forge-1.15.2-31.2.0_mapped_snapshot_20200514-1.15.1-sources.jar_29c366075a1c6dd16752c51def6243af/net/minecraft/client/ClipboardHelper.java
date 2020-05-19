package net.minecraft.client;

import com.google.common.base.Charsets;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class ClipboardHelper {
   private final ByteBuffer buffer = BufferUtils.createByteBuffer(8192);

   public String getClipboardString(long window, GLFWErrorCallbackI errorCallback) {
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback(errorCallback);
      String s = GLFW.glfwGetClipboardString(window);
      s = s != null ? SharedConstants.func_215070_b(s) : "";
      GLFWErrorCallback glfwerrorcallback1 = GLFW.glfwSetErrorCallback(glfwerrorcallback);
      if (glfwerrorcallback1 != null) {
         glfwerrorcallback1.free();
      }

      return s;
   }

   private static void func_230147_a_(long p_230147_0_, ByteBuffer p_230147_2_, byte[] p_230147_3_) {
      ((Buffer)p_230147_2_).clear();
      p_230147_2_.put(p_230147_3_);
      p_230147_2_.put((byte)0);
      ((Buffer)p_230147_2_).flip();
      GLFW.glfwSetClipboardString(p_230147_0_, p_230147_2_);
   }

   public void setClipboardString(long window, String string) {
      byte[] abyte = string.getBytes(Charsets.UTF_8);
      int i = abyte.length + 1;
      if (i < this.buffer.capacity()) {
         func_230147_a_(window, this.buffer, abyte);
      } else {
         ByteBuffer bytebuffer = MemoryUtil.memAlloc(i);

         try {
            func_230147_a_(window, bytebuffer, abyte);
         } finally {
            MemoryUtil.memFree(bytebuffer);
         }
      }

   }
}