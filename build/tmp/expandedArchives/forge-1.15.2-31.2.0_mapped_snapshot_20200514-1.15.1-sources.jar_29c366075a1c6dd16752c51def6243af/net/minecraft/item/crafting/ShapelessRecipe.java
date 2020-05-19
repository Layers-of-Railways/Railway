package net.minecraft.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ShapelessRecipe implements ICraftingRecipe {
   private final ResourceLocation id;
   private final String group;
   private final ItemStack recipeOutput;
   private final NonNullList<Ingredient> recipeItems;
   private final boolean isSimple;

   public ShapelessRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
      this.id = idIn;
      this.group = groupIn;
      this.recipeOutput = recipeOutputIn;
      this.recipeItems = recipeItemsIn;
      this.isSimple = recipeItemsIn.stream().allMatch(Ingredient::isSimple);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SHAPELESS;
   }

   /**
    * Recipes with equal group are combined into one button in the recipe book
    */
   public String getGroup() {
      return this.group;
   }

   /**
    * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
    * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
    */
   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.recipeItems;
   }

   /**
    * Used to check if a recipe matches current crafting inventory
    */
   public boolean matches(CraftingInventory inv, World worldIn) {
      RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
      java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
      int i = 0;

      for(int j = 0; j < inv.getSizeInventory(); ++j) {
         ItemStack itemstack = inv.getStackInSlot(j);
         if (!itemstack.isEmpty()) {
            ++i;
            if (isSimple)
            recipeitemhelper.func_221264_a(itemstack, 1);
            else inputs.add(itemstack);
         }
      }

      return i == this.recipeItems.size() && (isSimple ? recipeitemhelper.canCraft(this, (IntList)null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  this.recipeItems) != null);
   }

   /**
    * Returns an Item that is the result of this recipe
    */
   public ItemStack getCraftingResult(CraftingInventory inv) {
      return this.recipeOutput.copy();
   }

   /**
    * Used to determine if this recipe can fit in a grid of the given width/height
    */
   public boolean canFit(int width, int height) {
      return width * height >= this.recipeItems.size();
   }

   public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessRecipe> {
      private static final ResourceLocation NAME = new ResourceLocation("minecraft", "crafting_shapeless");
      public ShapelessRecipe read(ResourceLocation recipeId, JsonObject json) {
         String s = JSONUtils.getString(json, "group", "");
         NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
         if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (nonnulllist.size() > ShapedRecipe.MAX_WIDTH * ShapedRecipe.MAX_HEIGHT) {
            throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + (ShapedRecipe.MAX_WIDTH * ShapedRecipe.MAX_HEIGHT));
         } else {
            ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            return new ShapelessRecipe(recipeId, s, itemstack, nonnulllist);
         }
      }

      private static NonNullList<Ingredient> readIngredients(JsonArray p_199568_0_) {
         NonNullList<Ingredient> nonnulllist = NonNullList.create();

         for(int i = 0; i < p_199568_0_.size(); ++i) {
            Ingredient ingredient = Ingredient.deserialize(p_199568_0_.get(i));
            if (!ingredient.hasNoMatchingItems()) {
               nonnulllist.add(ingredient);
            }
         }

         return nonnulllist;
      }

      public ShapelessRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
         String s = buffer.readString(32767);
         int i = buffer.readVarInt();
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

         for(int j = 0; j < nonnulllist.size(); ++j) {
            nonnulllist.set(j, Ingredient.read(buffer));
         }

         ItemStack itemstack = buffer.readItemStack();
         return new ShapelessRecipe(recipeId, s, itemstack, nonnulllist);
      }

      public void write(PacketBuffer buffer, ShapelessRecipe recipe) {
         buffer.writeString(recipe.group);
         buffer.writeVarInt(recipe.recipeItems.size());

         for(Ingredient ingredient : recipe.recipeItems) {
            ingredient.write(buffer);
         }

         buffer.writeItemStack(recipe.recipeOutput);
      }
   }
}