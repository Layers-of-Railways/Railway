package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class KilledByCrossbowTrigger extends AbstractCriterionTrigger<KilledByCrossbowTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public KilledByCrossbowTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      EntityPredicate[] aentitypredicate = EntityPredicate.deserializeArray(json.get("victims"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("unique_entity_types"));
      return new KilledByCrossbowTrigger.Instance(aentitypredicate, minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity p_215105_1_, Collection<Entity> p_215105_2_, int p_215105_3_) {
      this.func_227070_a_(p_215105_1_.getAdvancements(), (p_226842_3_) -> {
         return p_226842_3_.test(p_215105_1_, p_215105_2_, p_215105_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate[] field_215118_a;
      private final MinMaxBounds.IntBound field_215119_b;

      public Instance(EntityPredicate[] p_i50580_1_, MinMaxBounds.IntBound p_i50580_2_) {
         super(KilledByCrossbowTrigger.ID);
         this.field_215118_a = p_i50580_1_;
         this.field_215119_b = p_i50580_2_;
      }

      public static KilledByCrossbowTrigger.Instance func_215116_a(EntityPredicate.Builder... p_215116_0_) {
         EntityPredicate[] aentitypredicate = new EntityPredicate[p_215116_0_.length];

         for(int i = 0; i < p_215116_0_.length; ++i) {
            EntityPredicate.Builder entitypredicate$builder = p_215116_0_[i];
            aentitypredicate[i] = entitypredicate$builder.build();
         }

         return new KilledByCrossbowTrigger.Instance(aentitypredicate, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public static KilledByCrossbowTrigger.Instance func_215117_a(MinMaxBounds.IntBound p_215117_0_) {
         EntityPredicate[] aentitypredicate = new EntityPredicate[0];
         return new KilledByCrossbowTrigger.Instance(aentitypredicate, p_215117_0_);
      }

      public boolean test(ServerPlayerEntity p_215115_1_, Collection<Entity> p_215115_2_, int p_215115_3_) {
         if (this.field_215118_a.length > 0) {
            List<Entity> list = Lists.newArrayList(p_215115_2_);

            for(EntityPredicate entitypredicate : this.field_215118_a) {
               boolean flag = false;
               Iterator<Entity> iterator = list.iterator();

               while(iterator.hasNext()) {
                  Entity entity = iterator.next();
                  if (entitypredicate.test(p_215115_1_, entity)) {
                     iterator.remove();
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }
         }

         if (this.field_215119_b == MinMaxBounds.IntBound.UNBOUNDED) {
            return true;
         } else {
            Set<EntityType<?>> set = Sets.newHashSet();

            for(Entity entity1 : p_215115_2_) {
               set.add(entity1.getType());
            }

            return this.field_215119_b.test(set.size()) && this.field_215119_b.test(p_215115_3_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("victims", EntityPredicate.serializeArray(this.field_215118_a));
         jsonobject.add("unique_entity_types", this.field_215119_b.serialize());
         return jsonobject;
      }
   }
}