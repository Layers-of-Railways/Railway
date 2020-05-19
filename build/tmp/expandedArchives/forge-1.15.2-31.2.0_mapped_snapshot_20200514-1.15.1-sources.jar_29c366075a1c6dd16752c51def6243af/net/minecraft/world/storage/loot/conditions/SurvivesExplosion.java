package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class SurvivesExplosion implements ILootCondition {
   private static final SurvivesExplosion INSTANCE = new SurvivesExplosion();

   private SurvivesExplosion() {
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.EXPLOSION_RADIUS);
   }

   public boolean test(LootContext p_test_1_) {
      Float f = p_test_1_.get(LootParameters.EXPLOSION_RADIUS);
      if (f != null) {
         Random random = p_test_1_.getRandom();
         float f1 = 1.0F / f;
         return random.nextFloat() <= f1;
      } else {
         return true;
      }
   }

   public static ILootCondition.IBuilder builder() {
      return () -> {
         return INSTANCE;
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<SurvivesExplosion> {
      protected Serializer() {
         super(new ResourceLocation("survives_explosion"), SurvivesExplosion.class);
      }

      public void serialize(JsonObject json, SurvivesExplosion value, JsonSerializationContext context) {
      }

      public SurvivesExplosion deserialize(JsonObject json, JsonDeserializationContext context) {
         return SurvivesExplosion.INSTANCE;
      }
   }
}