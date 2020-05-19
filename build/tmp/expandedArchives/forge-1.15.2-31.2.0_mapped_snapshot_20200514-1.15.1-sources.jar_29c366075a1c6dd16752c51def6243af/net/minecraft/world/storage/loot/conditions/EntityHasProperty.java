package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class EntityHasProperty implements ILootCondition {
   private final EntityPredicate predicate;
   private final LootContext.EntityTarget target;

   private EntityHasProperty(EntityPredicate predicateIn, LootContext.EntityTarget targetIn) {
      this.predicate = predicateIn;
      this.target = targetIn;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.POSITION, this.target.getParameter());
   }

   public boolean test(LootContext p_test_1_) {
      Entity entity = p_test_1_.get(this.target.getParameter());
      BlockPos blockpos = p_test_1_.get(LootParameters.POSITION);
      return this.predicate.func_217993_a(p_test_1_.getWorld(), blockpos != null ? new Vec3d(blockpos) : null, entity);
   }

   public static ILootCondition.IBuilder builder(LootContext.EntityTarget targetIn) {
      return builder(targetIn, EntityPredicate.Builder.create());
   }

   public static ILootCondition.IBuilder builder(LootContext.EntityTarget targetIn, EntityPredicate.Builder predicateBuilderIn) {
      return () -> {
         return new EntityHasProperty(predicateBuilderIn.build(), targetIn);
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<EntityHasProperty> {
      protected Serializer() {
         super(new ResourceLocation("entity_properties"), EntityHasProperty.class);
      }

      public void serialize(JsonObject json, EntityHasProperty value, JsonSerializationContext context) {
         json.add("predicate", value.predicate.serialize());
         json.add("entity", context.serialize(value.target));
      }

      public EntityHasProperty deserialize(JsonObject json, JsonDeserializationContext context) {
         EntityPredicate entitypredicate = EntityPredicate.deserialize(json.get("predicate"));
         return new EntityHasProperty(entitypredicate, JSONUtils.deserializeClass(json, "entity", context, LootContext.EntityTarget.class));
      }
   }
}