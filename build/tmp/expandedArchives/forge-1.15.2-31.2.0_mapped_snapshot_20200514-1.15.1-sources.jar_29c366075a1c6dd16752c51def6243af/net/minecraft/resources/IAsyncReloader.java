package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAsyncReloader {
   CompletableFuture<Unit> onceDone();

   @OnlyIn(Dist.CLIENT)
   float estimateExecutionSpeed();

   @OnlyIn(Dist.CLIENT)
   boolean asyncPartDone();

   @OnlyIn(Dist.CLIENT)
   boolean fullyDone();

   @OnlyIn(Dist.CLIENT)
   void join();
}