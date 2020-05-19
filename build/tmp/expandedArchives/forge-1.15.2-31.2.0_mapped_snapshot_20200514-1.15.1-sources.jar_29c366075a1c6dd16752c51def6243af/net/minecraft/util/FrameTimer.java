package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FrameTimer {
   /** An array with the last 240 frames */
   private final long[] frames = new long[240];
   /** The last index used when 240 frames have been set */
   private int lastIndex;
   /** A counter */
   private int counter;
   /** The next index to use in the array */
   private int index;

   /**
    * Add a frame at the next index in the array frames
    */
   public void addFrame(long runningTime) {
      this.frames[this.index] = runningTime;
      ++this.index;
      if (this.index == 240) {
         this.index = 0;
      }

      if (this.counter < 240) {
         this.lastIndex = 0;
         ++this.counter;
      } else {
         this.lastIndex = this.parseIndex(this.index + 1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getLineHeight(long valueIn, int p_219792_3_, int p_219792_4_) {
      double d0 = (double)valueIn / (double)(1000000000L / (long)p_219792_4_);
      return (int)(d0 * (double)p_219792_3_);
   }

   /**
    * Return the last index used when 240 frames have been set
    */
   @OnlyIn(Dist.CLIENT)
   public int getLastIndex() {
      return this.lastIndex;
   }

   /**
    * Return the index of the next frame in the array
    */
   @OnlyIn(Dist.CLIENT)
   public int getIndex() {
      return this.index;
   }

   /**
    * Change 240 to 0
    */
   public int parseIndex(int rawIndex) {
      return rawIndex % 240;
   }

   /**
    * Return the array of frames
    */
   @OnlyIn(Dist.CLIENT)
   public long[] getFrames() {
      return this.frames;
   }
}