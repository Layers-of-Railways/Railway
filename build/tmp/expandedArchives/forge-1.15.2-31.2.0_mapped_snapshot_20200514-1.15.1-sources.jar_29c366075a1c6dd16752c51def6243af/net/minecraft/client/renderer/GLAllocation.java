package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GLAllocation {
   /**
    * Creates and returns a direct byte buffer with the specified capacity. Applies native ordering to speed up access.
    */
   public static synchronized ByteBuffer createDirectByteBuffer(int capacity) {
      return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
   }

   /**
    * Creates and returns a direct float buffer with the specified capacity. Applies native ordering to speed up access.
    */
   public static FloatBuffer createDirectFloatBuffer(int capacity) {
      return createDirectByteBuffer(capacity << 2).asFloatBuffer();
   }
}