package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.RandomValueRange;

public class EntityHasScore implements ILootCondition {
   private final Map<String, RandomValueRange> scores;
   private final LootContext.EntityTarget target;

   private EntityHasScore(Map<String, RandomValueRange> scoreIn, LootContext.EntityTarget targetIn) {
      this.scores = ImmutableMap.copyOf(scoreIn);
      this.target = targetIn;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(this.target.getParameter());
   }

   public boolean test(LootContext p_test_1_) {
      Entity entity = p_test_1_.get(this.target.getParameter());
      if (entity == null) {
         return false;
      } else {
         Scoreboard scoreboard = entity.world.getScoreboard();

         for(Entry<String, RandomValueRange> entry : this.scores.entrySet()) {
            if (!this.entityScoreMatch(entity, scoreboard, entry.getKey(), entry.getValue())) {
               return false;
            }
         }

         return true;
      }
   }

   protected boolean entityScoreMatch(Entity entityIn, Scoreboard scoreboardIn, String objectiveStr, RandomValueRange rand) {
      ScoreObjective scoreobjective = scoreboardIn.getObjective(objectiveStr);
      if (scoreobjective == null) {
         return false;
      } else {
         String s = entityIn.getScoreboardName();
         return !scoreboardIn.entityHasObjective(s, scoreobjective) ? false : rand.isInRange(scoreboardIn.getOrCreateScore(s, scoreobjective).getScorePoints());
      }
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<EntityHasScore> {
      protected Serializer() {
         super(new ResourceLocation("entity_scores"), EntityHasScore.class);
      }

      public void serialize(JsonObject json, EntityHasScore value, JsonSerializationContext context) {
         JsonObject jsonobject = new JsonObject();

         for(Entry<String, RandomValueRange> entry : value.scores.entrySet()) {
            jsonobject.add(entry.getKey(), context.serialize(entry.getValue()));
         }

         json.add("scores", jsonobject);
         json.add("entity", context.serialize(value.target));
      }

      public EntityHasScore deserialize(JsonObject json, JsonDeserializationContext context) {
         Set<Entry<String, JsonElement>> set = JSONUtils.getJsonObject(json, "scores").entrySet();
         Map<String, RandomValueRange> map = Maps.newLinkedHashMap();

         for(Entry<String, JsonElement> entry : set) {
            map.put(entry.getKey(), JSONUtils.deserializeClass(entry.getValue(), "score", context, RandomValueRange.class));
         }

         return new EntityHasScore(map, JSONUtils.deserializeClass(json, "entity", context, LootContext.EntityTarget.class));
      }
   }
}