package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISound {
   ResourceLocation getSoundLocation();

   @Nullable
   SoundEventAccessor createAccessor(SoundHandler handler);

   Sound getSound();

   SoundCategory getCategory();

   boolean canRepeat();

   /**
    * True if the sound is not tied to a particular position in world (e.g. BGM)
    */
   boolean isGlobal();

   int getRepeatDelay();

   float getVolume();

   float getPitch();

   float getX();

   float getY();

   float getZ();

   ISound.AttenuationType getAttenuationType();

   default boolean canBeSilent() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum AttenuationType {
      NONE,
      LINEAR;
   }
}