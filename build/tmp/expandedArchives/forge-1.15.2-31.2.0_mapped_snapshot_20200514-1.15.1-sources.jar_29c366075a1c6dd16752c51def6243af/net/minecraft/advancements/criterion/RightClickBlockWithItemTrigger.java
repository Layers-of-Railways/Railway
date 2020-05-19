package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class RightClickBlockWithItemTrigger extends AbstractCriterionTrigger<RightClickBlockWithItemTrigger.Instance> {
   private final ResourceLocation field_226692_a_;

   public RightClickBlockWithItemTrigger(ResourceLocation p_i225742_1_) {
      this.field_226692_a_ = p_i225742_1_;
   }

   public ResourceLocation getId() {
      return this.field_226692_a_;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public RightClickBlockWithItemTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      BlockPredicate blockpredicate = BlockPredicate.func_226237_a_(json.get("block"));
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.deserializeProperties(json.get("state"));
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new RightClickBlockWithItemTrigger.Instance(this.field_226692_a_, blockpredicate, statepropertiespredicate, itempredicate);
   }

   public void test(ServerPlayerEntity p_226695_1_, BlockPos p_226695_2_, ItemStack p_226695_3_) {
      BlockState blockstate = p_226695_1_.getServerWorld().getBlockState(p_226695_2_);
      this.func_227070_a_(p_226695_1_.getAdvancements(), (p_226694_4_) -> {
         return p_226694_4_.func_226700_a_(blockstate, p_226695_1_.getServerWorld(), p_226695_2_, p_226695_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final BlockPredicate field_226696_a_;
      private final StatePropertiesPredicate field_226697_b_;
      private final ItemPredicate field_226698_c_;

      public Instance(ResourceLocation p_i225743_1_, BlockPredicate p_i225743_2_, StatePropertiesPredicate p_i225743_3_, ItemPredicate p_i225743_4_) {
         super(p_i225743_1_);
         this.field_226696_a_ = p_i225743_2_;
         this.field_226697_b_ = p_i225743_3_;
         this.field_226698_c_ = p_i225743_4_;
      }

      public static RightClickBlockWithItemTrigger.Instance func_226699_a_(BlockPredicate.Builder p_226699_0_, ItemPredicate.Builder p_226699_1_) {
         return new RightClickBlockWithItemTrigger.Instance(CriteriaTriggers.SAFELY_HARVEST_HONEY.field_226692_a_, p_226699_0_.func_226245_b_(), StatePropertiesPredicate.EMPTY, p_226699_1_.build());
      }

      public boolean func_226700_a_(BlockState p_226700_1_, ServerWorld p_226700_2_, BlockPos p_226700_3_, ItemStack p_226700_4_) {
         if (!this.field_226696_a_.func_226238_a_(p_226700_2_, p_226700_3_)) {
            return false;
         } else if (!this.field_226697_b_.matches(p_226700_1_)) {
            return false;
         } else {
            return this.field_226698_c_.test(p_226700_4_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("block", this.field_226696_a_.func_226236_a_());
         jsonobject.add("state", this.field_226697_b_.toJsonElement());
         jsonobject.add("item", this.field_226698_c_.serialize());
         return jsonobject;
      }
   }
}