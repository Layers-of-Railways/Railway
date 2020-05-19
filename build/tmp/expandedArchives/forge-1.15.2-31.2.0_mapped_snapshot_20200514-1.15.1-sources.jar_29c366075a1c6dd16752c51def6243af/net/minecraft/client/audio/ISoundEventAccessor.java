package net.minecraft.client.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISoundEventAccessor<T> {
   int getWeight();

   T cloneEntry();

   void enqueuePreload(SoundEngine engine);
}