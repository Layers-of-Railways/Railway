package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IParameterized;
import net.minecraft.world.storage.loot.LootContext;

@FunctionalInterface
public interface ILootCondition extends IParameterized, Predicate<LootContext> {
   public abstract static class AbstractSerializer<T extends ILootCondition> {
      private final ResourceLocation lootTableLocation;
      private final Class<T> conditionClass;

      protected AbstractSerializer(ResourceLocation location, Class<T> clazz) {
         this.lootTableLocation = location;
         this.conditionClass = clazz;
      }

      public ResourceLocation getLootTableLocation() {
         return this.lootTableLocation;
      }

      public Class<T> getConditionClass() {
         return this.conditionClass;
      }

      public abstract void serialize(JsonObject json, T value, JsonSerializationContext context);

      public abstract T deserialize(JsonObject json, JsonDeserializationContext context);
   }

   @FunctionalInterface
   public interface IBuilder {
      ILootCondition build();

      default ILootCondition.IBuilder inverted() {
         return Inverted.builder(this);
      }

      default Alternative.Builder alternative(ILootCondition.IBuilder builderIn) {
         return Alternative.builder(this, builderIn);
      }
   }
}