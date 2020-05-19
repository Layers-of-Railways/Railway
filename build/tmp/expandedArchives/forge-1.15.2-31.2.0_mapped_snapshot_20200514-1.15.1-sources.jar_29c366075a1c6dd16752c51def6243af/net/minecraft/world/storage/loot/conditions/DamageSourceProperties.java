package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class DamageSourceProperties implements ILootCondition {
   private final DamageSourcePredicate predicate;

   private DamageSourceProperties(DamageSourcePredicate p_i51205_1_) {
      this.predicate = p_i51205_1_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.POSITION, LootParameters.DAMAGE_SOURCE);
   }

   public boolean test(LootContext p_test_1_) {
      DamageSource damagesource = p_test_1_.get(LootParameters.DAMAGE_SOURCE);
      BlockPos blockpos = p_test_1_.get(LootParameters.POSITION);
      return blockpos != null && damagesource != null && this.predicate.func_217952_a(p_test_1_.getWorld(), new Vec3d(blockpos), damagesource);
   }

   public static ILootCondition.IBuilder builder(DamageSourcePredicate.Builder p_215966_0_) {
      return () -> {
         return new DamageSourceProperties(p_215966_0_.build());
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<DamageSourceProperties> {
      protected Serializer() {
         super(new ResourceLocation("damage_source_properties"), DamageSourceProperties.class);
      }

      public void serialize(JsonObject json, DamageSourceProperties value, JsonSerializationContext context) {
         json.add("predicate", value.predicate.serialize());
      }

      public DamageSourceProperties deserialize(JsonObject json, JsonDeserializationContext context) {
         DamageSourcePredicate damagesourcepredicate = DamageSourcePredicate.deserialize(json.get("predicate"));
         return new DamageSourceProperties(damagesourcepredicate);
      }
   }
}