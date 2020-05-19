package net.minecraft.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ITaskExecutor<Msg> extends AutoCloseable {
   String getName();

   void enqueue(Msg taskIn);

   default void close() {
   }

   default <Source> CompletableFuture<Source> func_213141_a(Function<? super ITaskExecutor<Source>, ? extends Msg> p_213141_1_) {
      CompletableFuture<Source> completablefuture = new CompletableFuture<>();
      Msg msg = p_213141_1_.apply(inline("ask future procesor handle", completablefuture::complete));
      this.enqueue(msg);
      return completablefuture;
   }

   static <Msg> ITaskExecutor<Msg> inline(final String name, final Consumer<Msg> p_213140_1_) {
      return new ITaskExecutor<Msg>() {
         public String getName() {
            return name;
         }

         public void enqueue(Msg taskIn) {
            p_213140_1_.accept(taskIn);
         }

         public String toString() {
            return name;
         }
      };
   }
}