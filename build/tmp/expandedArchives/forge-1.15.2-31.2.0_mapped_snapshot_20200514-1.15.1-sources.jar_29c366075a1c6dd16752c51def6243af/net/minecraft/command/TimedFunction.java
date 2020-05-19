package net.minecraft.command;

import net.minecraft.advancements.FunctionManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class TimedFunction implements ITimerCallback<MinecraftServer> {
   private final ResourceLocation field_216318_a;

   public TimedFunction(ResourceLocation p_i51190_1_) {
      this.field_216318_a = p_i51190_1_;
   }

   public void run(MinecraftServer obj, TimerCallbackManager<MinecraftServer> manager, long gameTime) {
      FunctionManager functionmanager = obj.getFunctionManager();
      functionmanager.get(this.field_216318_a).ifPresent((p_216316_1_) -> {
         functionmanager.execute(p_216316_1_, functionmanager.getCommandSource());
      });
   }

   public static class Serializer extends ITimerCallback.Serializer<MinecraftServer, TimedFunction> {
      public Serializer() {
         super(new ResourceLocation("function"), TimedFunction.class);
      }

      public void write(CompoundNBT p_212847_1_, TimedFunction p_212847_2_) {
         p_212847_1_.putString("Name", p_212847_2_.field_216318_a.toString());
      }

      public TimedFunction read(CompoundNBT p_212846_1_) {
         ResourceLocation resourcelocation = new ResourceLocation(p_212846_1_.getString("Name"));
         return new TimedFunction(resourcelocation);
      }
   }
}