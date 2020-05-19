package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapedRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List<String> pattern = Lists.newArrayList();
   private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   private String group;

   public ShapedRecipeBuilder(IItemProvider resultIn, int countIn) {
      this.result = resultIn.asItem();
      this.count = countIn;
   }

   /**
    * Creates a new builder for a shaped recipe.
    */
   public static ShapedRecipeBuilder shapedRecipe(IItemProvider resultIn) {
      return shapedRecipe(resultIn, 1);
   }

   /**
    * Creates a new builder for a shaped recipe.
    */
   public static ShapedRecipeBuilder shapedRecipe(IItemProvider resultIn, int countIn) {
      return new ShapedRecipeBuilder(resultIn, countIn);
   }

   /**
    * Adds a key to the recipe pattern.
    */
   public ShapedRecipeBuilder key(Character symbol, Tag<Item> tagIn) {
      return this.key(symbol, Ingredient.fromTag(tagIn));
   }

   /**
    * Adds a key to the recipe pattern.
    */
   public ShapedRecipeBuilder key(Character symbol, IItemProvider itemIn) {
      return this.key(symbol, Ingredient.fromItems(itemIn));
   }

   /**
    * Adds a key to the recipe pattern.
    */
   public ShapedRecipeBuilder key(Character symbol, Ingredient ingredientIn) {
      if (this.key.containsKey(symbol)) {
         throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
      } else if (symbol == ' ') {
         throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
      } else {
         this.key.put(symbol, ingredientIn);
         return this;
      }
   }

   /**
    * Adds a new entry to the patterns for this recipe.
    */
   public ShapedRecipeBuilder patternLine(String patternIn) {
      if (!this.pattern.isEmpty() && patternIn.length() != this.pattern.get(0).length()) {
         throw new IllegalArgumentException("Pattern must be the same width on every line!");
      } else {
         this.pattern.add(patternIn);
         return this;
      }
   }

   /**
    * Adds a criterion needed to unlock the recipe.
    */
   public ShapedRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
      this.advancementBuilder.withCriterion(name, criterionIn);
      return this;
   }

   public ShapedRecipeBuilder setGroup(String groupIn) {
      this.group = groupIn;
      return this;
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn) {
      this.build(consumerIn, Registry.ITEM.getKey(this.result));
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
    * the result.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(save)).equals(resourcelocation)) {
         throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
      } else {
         this.build(consumerIn, new ResourceLocation(save));
      }
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
      this.validate(id);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
      consumerIn.accept(new ShapedRecipeBuilder.Result(id, this.result, this.count, this.group == null ? "" : this.group, this.pattern, this.key, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath())));
   }

   /**
    * Makes sure that this recipe is valid and obtainable.
    */
   private void validate(ResourceLocation id) {
      if (this.pattern.isEmpty()) {
         throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
      } else {
         Set<Character> set = Sets.newHashSet(this.key.keySet());
         set.remove(' ');

         for(String s : this.pattern) {
            for(int i = 0; i < s.length(); ++i) {
               char c0 = s.charAt(i);
               if (!this.key.containsKey(c0) && c0 != ' ') {
                  throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
               }

               set.remove(c0);
            }
         }

         if (!set.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
         } else if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
         } else if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
         }
      }
   }

   public class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<String> pattern;
      private final Map<Character, Ingredient> key;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation idIn, Item resultIn, int countIn, String groupIn, List<String> patternIn, Map<Character, Ingredient> keyIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn) {
         this.id = idIn;
         this.result = resultIn;
         this.count = countIn;
         this.group = groupIn;
         this.pattern = patternIn;
         this.key = keyIn;
         this.advancementBuilder = advancementBuilderIn;
         this.advancementId = advancementIdIn;
      }

      public void serialize(JsonObject json) {
         if (!this.group.isEmpty()) {
            json.addProperty("group", this.group);
         }

         JsonArray jsonarray = new JsonArray();

         for(String s : this.pattern) {
            jsonarray.add(s);
         }

         json.add("pattern", jsonarray);
         JsonObject jsonobject = new JsonObject();

         for(Entry<Character, Ingredient> entry : this.key.entrySet()) {
            jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().serialize());
         }

         json.add("key", jsonobject);
         JsonObject jsonobject1 = new JsonObject();
         jsonobject1.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            jsonobject1.addProperty("count", this.count);
         }

         json.add("result", jsonobject1);
      }

      public IRecipeSerializer<?> getSerializer() {
         return IRecipeSerializer.CRAFTING_SHAPED;
      }

      /**
       * Gets the ID for the recipe.
       */
      public ResourceLocation getID() {
         return this.id;
      }

      /**
       * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
       */
      @Nullable
      public JsonObject getAdvancementJson() {
         return this.advancementBuilder.serialize();
      }

      /**
       * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
       * is non-null.
       */
      @Nullable
      public ResourceLocation getAdvancementID() {
         return this.advancementId;
      }
   }
}