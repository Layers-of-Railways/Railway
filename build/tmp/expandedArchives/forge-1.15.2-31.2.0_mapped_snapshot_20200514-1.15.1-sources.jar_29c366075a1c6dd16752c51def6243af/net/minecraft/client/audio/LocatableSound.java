package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LocatableSound implements ISound {
   protected Sound sound;
   @Nullable
   private SoundEventAccessor soundEvent;
   protected final SoundCategory category;
   protected final ResourceLocation positionedSoundLocation;
   protected float volume = 1.0F;
   protected float pitch = 1.0F;
   protected float x;
   protected float y;
   protected float z;
   protected boolean repeat;
   /** The number of ticks between repeating the sound */
   protected int repeatDelay;
   protected ISound.AttenuationType attenuationType = ISound.AttenuationType.LINEAR;
   protected boolean priority;
   protected boolean global;

   protected LocatableSound(SoundEvent soundIn, SoundCategory categoryIn) {
      this(soundIn.getName(), categoryIn);
   }

   protected LocatableSound(ResourceLocation soundId, SoundCategory categoryIn) {
      this.positionedSoundLocation = soundId;
      this.category = categoryIn;
   }

   public ResourceLocation getSoundLocation() {
      return this.positionedSoundLocation;
   }

   public SoundEventAccessor createAccessor(SoundHandler handler) {
      this.soundEvent = handler.getAccessor(this.positionedSoundLocation);
      if (this.soundEvent == null) {
         this.sound = SoundHandler.MISSING_SOUND;
      } else {
         this.sound = this.soundEvent.cloneEntry();
      }

      return this.soundEvent;
   }

   public Sound getSound() {
      return this.sound;
   }

   public SoundCategory getCategory() {
      return this.category;
   }

   public boolean canRepeat() {
      return this.repeat;
   }

   public int getRepeatDelay() {
      return this.repeatDelay;
   }

   public float getVolume() {
      return this.volume * this.sound.getVolume();
   }

   public float getPitch() {
      return this.pitch * this.sound.getPitch();
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   public ISound.AttenuationType getAttenuationType() {
      return this.attenuationType;
   }

   /**
    * True if the sound is not tied to a particular position in world (e.g. BGM)
    */
   public boolean isGlobal() {
      return this.global;
   }
}