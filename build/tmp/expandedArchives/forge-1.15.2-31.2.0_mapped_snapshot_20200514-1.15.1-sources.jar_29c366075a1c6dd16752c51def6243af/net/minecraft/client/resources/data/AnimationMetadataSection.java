package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
   public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection(Lists.newArrayList(), -1, -1, 1, false) {
      public Pair<Integer, Integer> getSpriteSize(int widthIn, int heightIn) {
         return Pair.of(widthIn, heightIn);
      }
   };
   private final List<AnimationFrame> animationFrames;
   private final int frameWidth;
   private final int frameHeight;
   private final int frameTime;
   private final boolean interpolate;

   public AnimationMetadataSection(List<AnimationFrame> animationFramesIn, int frameWidthIn, int frameHeightIn, int frameTimeIn, boolean interpolateIn) {
      this.animationFrames = animationFramesIn;
      this.frameWidth = frameWidthIn;
      this.frameHeight = frameHeightIn;
      this.frameTime = frameTimeIn;
      this.interpolate = interpolateIn;
   }

   private static boolean isMultipleOf(int valMul, int val) {
      return valMul / val * val == valMul;
   }

   public Pair<Integer, Integer> getSpriteSize(int widthIn, int heightIn) {
      Pair<Integer, Integer> pair = this.getFrameSize(widthIn, heightIn);
      int i = pair.getFirst();
      int j = pair.getSecond();
      if (isMultipleOf(widthIn, i) && isMultipleOf(heightIn, j)) {
         return pair;
      } else {
         throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", widthIn, heightIn, i, j));
      }
   }

   private Pair<Integer, Integer> getFrameSize(int defWidthIn, int defHeightIn) {
      if (this.frameWidth != -1) {
         return this.frameHeight != -1 ? Pair.of(this.frameWidth, this.frameHeight) : Pair.of(this.frameWidth, defHeightIn);
      } else if (this.frameHeight != -1) {
         return Pair.of(defWidthIn, this.frameHeight);
      } else {
         int i = Math.min(defWidthIn, defHeightIn);
         return Pair.of(i, i);
      }
   }

   public int getFrameHeight(int defHeightIn) {
      return this.frameHeight == -1 ? defHeightIn : this.frameHeight;
   }

   public int getFrameWidth(int defWidthIn) {
      return this.frameWidth == -1 ? defWidthIn : this.frameWidth;
   }

   public int getFrameCount() {
      return this.animationFrames.size();
   }

   public int getFrameTime() {
      return this.frameTime;
   }

   public boolean isInterpolate() {
      return this.interpolate;
   }

   private AnimationFrame getAnimationFrame(int frame) {
      return this.animationFrames.get(frame);
   }

   public int getFrameTimeSingle(int frame) {
      AnimationFrame animationframe = this.getAnimationFrame(frame);
      return animationframe.hasNoTime() ? this.frameTime : animationframe.getFrameTime();
   }

   public int getFrameIndex(int frame) {
      return this.animationFrames.get(frame).getFrameIndex();
   }

   public Set<Integer> getFrameIndexSet() {
      Set<Integer> set = Sets.newHashSet();

      for(AnimationFrame animationframe : this.animationFrames) {
         set.add(animationframe.getFrameIndex());
      }

      return set;
   }
}