package net.minecraft.command;

import net.minecraft.advancements.FunctionManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class TimedFunctionTag implements ITimerCallback<MinecraftServer> {
   private final ResourceLocation tagName;

   public TimedFunctionTag(ResourceLocation p_i51189_1_) {
      this.tagName = p_i51189_1_;
   }

   public void run(MinecraftServer obj, TimerCallbackManager<MinecraftServer> manager, long gameTime) {
      FunctionManager functionmanager = obj.getFunctionManager();
      Tag<FunctionObject> tag = functionmanager.getTagCollection().getOrCreate(this.tagName);

      for(FunctionObject functionobject : tag.getAllElements()) {
         functionmanager.execute(functionobject, functionmanager.getCommandSource());
      }

   }

   public static class Serializer extends ITimerCallback.Serializer<MinecraftServer, TimedFunctionTag> {
      public Serializer() {
         super(new ResourceLocation("function_tag"), TimedFunctionTag.class);
      }

      public void write(CompoundNBT p_212847_1_, TimedFunctionTag p_212847_2_) {
         p_212847_1_.putString("Name", p_212847_2_.tagName.toString());
      }

      public TimedFunctionTag read(CompoundNBT p_212846_1_) {
         ResourceLocation resourcelocation = new ResourceLocation(p_212846_1_.getString("Name"));
         return new TimedFunctionTag(resourcelocation);
      }
   }
}