package net.minecraft.util;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWorldPosCallable {
   IWorldPosCallable DUMMY = new IWorldPosCallable() {
      public <T> Optional<T> apply(BiFunction<World, BlockPos, T> p_221484_1_) {
         return Optional.empty();
      }
   };

   static IWorldPosCallable of(final World p_221488_0_, final BlockPos p_221488_1_) {
      return new IWorldPosCallable() {
         public <T> Optional<T> apply(BiFunction<World, BlockPos, T> p_221484_1_) {
            return Optional.of(p_221484_1_.apply(p_221488_0_, p_221488_1_));
         }
      };
   }

   <T> Optional<T> apply(BiFunction<World, BlockPos, T> p_221484_1_);

   default <T> T applyOrElse(BiFunction<World, BlockPos, T> p_221485_1_, T p_221485_2_) {
      return this.apply(p_221485_1_).orElse(p_221485_2_);
   }

   default void consume(BiConsumer<World, BlockPos> p_221486_1_) {
      this.apply((p_221487_1_, p_221487_2_) -> {
         p_221486_1_.accept(p_221487_1_, p_221487_2_);
         return Optional.empty();
      });
   }
}