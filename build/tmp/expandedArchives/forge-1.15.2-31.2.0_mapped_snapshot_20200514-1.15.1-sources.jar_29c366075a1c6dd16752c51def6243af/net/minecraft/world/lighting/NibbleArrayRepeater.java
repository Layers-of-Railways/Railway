package net.minecraft.world.lighting;

import net.minecraft.world.chunk.NibbleArray;

public class NibbleArrayRepeater extends NibbleArray {
   public NibbleArrayRepeater() {
      super(128);
   }

   public NibbleArrayRepeater(NibbleArray p_i51297_1_, int p_i51297_2_) {
      super(128);
      System.arraycopy(p_i51297_1_.getData(), p_i51297_2_ * 128, this.data, 0, 128);
   }

   protected int getCoordinateIndex(int x, int y, int z) {
      return z << 4 | x;
   }

   public byte[] getData() {
      byte[] abyte = new byte[2048];

      for(int i = 0; i < 16; ++i) {
         System.arraycopy(this.data, 0, abyte, i * 128, 128);
      }

      return abyte;
   }
}