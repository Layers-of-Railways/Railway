package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.ResourceLocation;

public class ConstructBeaconTrigger extends AbstractCriterionTrigger<ConstructBeaconTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("construct_beacon");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public ConstructBeaconTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("level"));
      return new ConstructBeaconTrigger.Instance(minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity player, BeaconTileEntity beacon) {
      this.func_227070_a_(player.getAdvancements(), (p_226308_1_) -> {
         return p_226308_1_.test(beacon);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.IntBound level;

      public Instance(MinMaxBounds.IntBound p_i49736_1_) {
         super(ConstructBeaconTrigger.ID);
         this.level = p_i49736_1_;
      }

      public static ConstructBeaconTrigger.Instance forLevel(MinMaxBounds.IntBound p_203912_0_) {
         return new ConstructBeaconTrigger.Instance(p_203912_0_);
      }

      public boolean test(BeaconTileEntity beacon) {
         return this.level.test(beacon.getLevels());
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("level", this.level.serialize());
         return jsonobject;
      }
   }
}