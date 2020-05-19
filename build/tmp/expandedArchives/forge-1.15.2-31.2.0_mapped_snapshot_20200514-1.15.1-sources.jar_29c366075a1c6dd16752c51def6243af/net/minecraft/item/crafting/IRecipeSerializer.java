package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface IRecipeSerializer<T extends IRecipe<?>> extends net.minecraftforge.registries.IForgeRegistryEntry<IRecipeSerializer<?>> {
   IRecipeSerializer<ShapedRecipe> CRAFTING_SHAPED = register("crafting_shaped", new ShapedRecipe.Serializer());
   IRecipeSerializer<ShapelessRecipe> CRAFTING_SHAPELESS = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   SpecialRecipeSerializer<ArmorDyeRecipe> CRAFTING_SPECIAL_ARMORDYE = register("crafting_special_armordye", new SpecialRecipeSerializer<>(ArmorDyeRecipe::new));
   SpecialRecipeSerializer<BookCloningRecipe> CRAFTING_SPECIAL_BOOKCLONING = register("crafting_special_bookcloning", new SpecialRecipeSerializer<>(BookCloningRecipe::new));
   SpecialRecipeSerializer<MapCloningRecipe> CRAFTING_SPECIAL_MAPCLONING = register("crafting_special_mapcloning", new SpecialRecipeSerializer<>(MapCloningRecipe::new));
   SpecialRecipeSerializer<MapExtendingRecipe> CRAFTING_SPECIAL_MAPEXTENDING = register("crafting_special_mapextending", new SpecialRecipeSerializer<>(MapExtendingRecipe::new));
   SpecialRecipeSerializer<FireworkRocketRecipe> CRAFTING_SPECIAL_FIREWORK_ROCKET = register("crafting_special_firework_rocket", new SpecialRecipeSerializer<>(FireworkRocketRecipe::new));
   SpecialRecipeSerializer<FireworkStarRecipe> CRAFTING_SPECIAL_FIREWORK_STAR = register("crafting_special_firework_star", new SpecialRecipeSerializer<>(FireworkStarRecipe::new));
   SpecialRecipeSerializer<FireworkStarFadeRecipe> CRAFTING_SPECIAL_FIREWORK_STAR_FADE = register("crafting_special_firework_star_fade", new SpecialRecipeSerializer<>(FireworkStarFadeRecipe::new));
   SpecialRecipeSerializer<TippedArrowRecipe> CRAFTING_SPECIAL_TIPPEDARROW = register("crafting_special_tippedarrow", new SpecialRecipeSerializer<>(TippedArrowRecipe::new));
   SpecialRecipeSerializer<BannerDuplicateRecipe> CRAFTING_SPECIAL_BANNERDUPLICATE = register("crafting_special_bannerduplicate", new SpecialRecipeSerializer<>(BannerDuplicateRecipe::new));
   SpecialRecipeSerializer<ShieldRecipes> CRAFTING_SPECIAL_SHIELD = register("crafting_special_shielddecoration", new SpecialRecipeSerializer<>(ShieldRecipes::new));
   SpecialRecipeSerializer<ShulkerBoxColoringRecipe> CRAFTING_SPECIAL_SHULKERBOXCOLORING = register("crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer<>(ShulkerBoxColoringRecipe::new));
   SpecialRecipeSerializer<SuspiciousStewRecipe> CRAFTING_SPECIAL_SUSPICIOUSSTEW = register("crafting_special_suspiciousstew", new SpecialRecipeSerializer<>(SuspiciousStewRecipe::new));
   SpecialRecipeSerializer<RepairItemRecipe> CRAFTING_SPECIAL_REPAIRITEM = register("crafting_special_repairitem", new SpecialRecipeSerializer<>(RepairItemRecipe::new));
   CookingRecipeSerializer<FurnaceRecipe> SMELTING = register("smelting", new CookingRecipeSerializer<>(FurnaceRecipe::new, 200));
   CookingRecipeSerializer<BlastingRecipe> BLASTING = register("blasting", new CookingRecipeSerializer<>(BlastingRecipe::new, 100));
   CookingRecipeSerializer<SmokingRecipe> SMOKING = register("smoking", new CookingRecipeSerializer<>(SmokingRecipe::new, 100));
   CookingRecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking", new CookingRecipeSerializer<>(CampfireCookingRecipe::new, 100));
   IRecipeSerializer<StonecuttingRecipe> STONECUTTING = register("stonecutting", new SingleItemRecipe.Serializer<>(StonecuttingRecipe::new));

   T read(ResourceLocation recipeId, JsonObject json);

   @javax.annotation.Nullable
   T read(ResourceLocation recipeId, PacketBuffer buffer);

   void write(PacketBuffer buffer, T recipe);

   static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String key, S recipeSerializer) {
      return (S)(Registry.<IRecipeSerializer<?>>register(Registry.RECIPE_SERIALIZER, key, recipeSerializer));
   }
}