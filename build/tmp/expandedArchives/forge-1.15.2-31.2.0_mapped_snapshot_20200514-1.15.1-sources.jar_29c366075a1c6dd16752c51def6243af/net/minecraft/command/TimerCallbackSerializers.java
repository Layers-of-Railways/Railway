package net.minecraft.command;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerCallbackSerializers<C> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final TimerCallbackSerializers<MinecraftServer> field_216342_a = (new TimerCallbackSerializers<MinecraftServer>()).func_216340_a(new TimedFunction.Serializer()).func_216340_a(new TimedFunctionTag.Serializer());
   private final Map<ResourceLocation, ITimerCallback.Serializer<C, ?>> field_216344_c = Maps.newHashMap();
   private final Map<Class<?>, ITimerCallback.Serializer<C, ?>> field_216345_d = Maps.newHashMap();

   public TimerCallbackSerializers<C> func_216340_a(ITimerCallback.Serializer<C, ?> p_216340_1_) {
      this.field_216344_c.put(p_216340_1_.func_216310_a(), p_216340_1_);
      this.field_216345_d.put(p_216340_1_.func_216311_b(), p_216340_1_);
      return this;
   }

   private <T extends ITimerCallback<C>> ITimerCallback.Serializer<C, T> func_216338_a(Class<?> p_216338_1_) {
      return (ITimerCallback.Serializer<C, T>) this.field_216345_d.get(p_216338_1_);
   }

   public <T extends ITimerCallback<C>> CompoundNBT func_216339_a(T p_216339_1_) {
      ITimerCallback.Serializer<C, T> serializer = this.func_216338_a(p_216339_1_.getClass());
      CompoundNBT compoundnbt = new CompoundNBT();
      serializer.write(compoundnbt, p_216339_1_);
      compoundnbt.putString("Type", serializer.func_216310_a().toString());
      return compoundnbt;
   }

   @Nullable
   public ITimerCallback<C> func_216341_a(CompoundNBT p_216341_1_) {
      ResourceLocation resourcelocation = ResourceLocation.tryCreate(p_216341_1_.getString("Type"));
      ITimerCallback.Serializer<C, ?> serializer = this.field_216344_c.get(resourcelocation);
      if (serializer == null) {
         LOGGER.error("Failed to deserialize timer callback: " + p_216341_1_);
         return null;
      } else {
         try {
            return serializer.read(p_216341_1_);
         } catch (Exception exception) {
            LOGGER.error("Failed to deserialize timer callback: " + p_216341_1_, (Throwable)exception);
            return null;
         }
      }
   }
}