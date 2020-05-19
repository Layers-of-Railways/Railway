package net.minecraft.command;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface ITimerCallback<T> {
   void run(T obj, TimerCallbackManager<T> manager, long gameTime);

   public abstract static class Serializer<T, C extends ITimerCallback<T>> {
      private final ResourceLocation typeId;
      private final Class<?> clazz;

      public Serializer(ResourceLocation p_i51270_1_, Class<?> p_i51270_2_) {
         this.typeId = p_i51270_1_;
         this.clazz = p_i51270_2_;
      }

      public ResourceLocation func_216310_a() {
         return this.typeId;
      }

      public Class<?> func_216311_b() {
         return this.clazz;
      }

      public abstract void write(CompoundNBT p_212847_1_, C p_212847_2_);

      public abstract C read(CompoundNBT p_212846_1_);
   }
}