package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Sound implements ISoundEventAccessor<Sound> {
   private final ResourceLocation name;
   private final float volume;
   private final float pitch;
   private final int weight;
   private final Sound.Type type;
   private final boolean streaming;
   private final boolean preload;
   private final int attenuationDistance;

   public Sound(String nameIn, float volumeIn, float pitchIn, int weightIn, Sound.Type typeIn, boolean streamingIn, boolean preloadIn, int attenuationDistanceIn) {
      this.name = new ResourceLocation(nameIn);
      this.volume = volumeIn;
      this.pitch = pitchIn;
      this.weight = weightIn;
      this.type = typeIn;
      this.streaming = streamingIn;
      this.preload = preloadIn;
      this.attenuationDistance = attenuationDistanceIn;
   }

   public ResourceLocation getSoundLocation() {
      return this.name;
   }

   public ResourceLocation getSoundAsOggLocation() {
      return new ResourceLocation(this.name.getNamespace(), "sounds/" + this.name.getPath() + ".ogg");
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public int getWeight() {
      return this.weight;
   }

   public Sound cloneEntry() {
      return this;
   }

   public void enqueuePreload(SoundEngine engine) {
      if (this.preload) {
         engine.enqueuePreload(this);
      }

   }

   public Sound.Type getType() {
      return this.type;
   }

   public boolean isStreaming() {
      return this.streaming;
   }

   public boolean shouldPreload() {
      return this.preload;
   }

   public int getAttenuationDistance() {
      return this.attenuationDistance;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      private Type(String nameIn) {
         this.name = nameIn;
      }

      public static Sound.Type getByName(String nameIn) {
         for(Sound.Type sound$type : values()) {
            if (sound$type.name.equals(nameIn)) {
               return sound$type;
            }
         }

         return null;
      }
   }
}