package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class ChanneledLightningTrigger extends AbstractCriterionTrigger<ChanneledLightningTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public ChanneledLightningTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      EntityPredicate[] aentitypredicate = EntityPredicate.deserializeArray(json.get("victims"));
      return new ChanneledLightningTrigger.Instance(aentitypredicate);
   }

   public void trigger(ServerPlayerEntity player, Collection<? extends Entity> entityTriggered) {
      this.func_227070_a_(player.getAdvancements(), (p_226307_2_) -> {
         return p_226307_2_.test(player, entityTriggered);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate[] victims;

      public Instance(EntityPredicate[] victims) {
         super(ChanneledLightningTrigger.ID);
         this.victims = victims;
      }

      public static ChanneledLightningTrigger.Instance channeledLightning(EntityPredicate... p_204824_0_) {
         return new ChanneledLightningTrigger.Instance(p_204824_0_);
      }

      public boolean test(ServerPlayerEntity player, Collection<? extends Entity> p_204823_2_) {
         for(EntityPredicate entitypredicate : this.victims) {
            boolean flag = false;

            for(Entity entity : p_204823_2_) {
               if (entitypredicate.test(player, entity)) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               return false;
            }
         }

         return true;
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("victims", EntityPredicate.serializeArray(this.victims));
         return jsonobject;
      }
   }
}