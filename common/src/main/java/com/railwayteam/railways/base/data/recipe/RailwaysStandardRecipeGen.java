package com.railwayteam.railways.base.data.recipe;

import com.google.common.base.Supplier;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public abstract class RailwaysStandardRecipeGen extends RailwaysRecipeProvider {

    GeneratedRecipe TRACK_COUPLER = create(CRBlocks.TRACK_COUPLER)
        .unlockedBy(Ingredients::railwayCasing)
        .viaShaped(b -> b.define('=', Ingredients.ironSheet())
            .define('#', Ingredients.redstone())
            .define('T', Ingredients.railwayCasing())
            .pattern("=")
            .pattern("#")
            .pattern("T"));

    GeneratedRecipe CONDUCTOR_WHISTLE = create(CRBlocks.CONDUCTOR_WHISTLE_FLAG)
        .unlockedByTag(() -> CRTags.AllItemTags.CONDUCTOR_CAPS.tag)
        .viaShapeless(b -> b
            .requires(Ingredients.copperIngot())
            .requires(Ingredients.brassNugget()));

    GeneratedRecipe COALBURNER_STACK = create(CRBlocks.COALBURNER_STACK)
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('#', Ingredients.ironSheet())
            .define('+', Ingredients.campfire())
            .pattern("# #")
            .pattern("# #")
            .pattern("#+#")
        );

    GeneratedRecipe DIESEL_STACK = create(CRBlocks.DIESEL_STACK)
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('#', Ingredients.ironSheet())
            .define('+', Ingredients.campfire())
            .define('*', Ingredients.propeller())
            .pattern("#*#")
            .pattern(" + ")
        );

    GeneratedRecipe CABOOSE_STACK = create(CRBlocks.CABOOSESTYLE_STACK)
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('#', Ingredients.ironSheet())
            .define('+', Ingredients.campfire())
            .define('.', Ingredients.ironNugget())
            .pattern(".#.")
            .pattern(" + ")
        );

    GeneratedRecipe OILBURNER_STACK = create(CRBlocks.OILBURNER_STACK)
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('#', Ingredients.ironSheet())
            .define('+', Ingredients.campfire())
            .pattern("# #")
            .pattern("#+#")
        );

    GeneratedRecipe STREAMLINED_STACK = create(CRBlocks.STREAMLINED_STACK)
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('#', Ingredients.ironSheet())
            .define('+', Ingredients.campfire())
            .pattern("#+#")
        );

    GeneratedRecipe WOODBURNER_STACK = create(CRBlocks.WOODBURNER_STACK)
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('#', Ingredients.ironSheet())
            .define('+', Ingredients.campfire())
            .define('.', Ingredients.ironNugget())
            .pattern(".#.")
            .pattern("#+#")
        );

    GeneratedRecipe SEMAPHORE = create(CRBlocks.SEMAPHORE)
        .unlockedBy(AllItems.ELECTRON_TUBE::get)
        .returns(4)
        .viaShaped(b -> b
            .define('C', Ingredients.andesiteCasing())
            .define('T', Ingredients.electronTube())
            .define('F', Ingredients.fence())
            .define('S', Ingredients.ironSheet())
            .pattern(" S ")
            .pattern("FCT")
            .pattern(" S ")
        );

    GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilder("/", result);
    }

    GeneratedRecipeBuilder create(ResourceLocation result) {
        return new GeneratedRecipeBuilder("/", result);
    }

    GeneratedRecipeBuilder create(ItemProviderEntry<? extends ItemLike> result) {
        return create(result::get);
    }

    protected RailwaysStandardRecipeGen(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @ExpectPlatform
    public static RecipeProvider create(DataGenerator gen) {
        throw new AssertionError();
    }

    @Override
    public String getName() {
        return "Steam 'n Rails Standard Recipes";
    }

    class GeneratedRecipeBuilder {

        private String path;
        private String suffix;
        private Supplier<? extends ItemLike> result;
        private ResourceLocation compatDatagenOutput;

        private Supplier<ItemPredicate> unlockedBy;
        private int amount;

        private GeneratedRecipeBuilder(String path) {
            this.path = path;
            this.suffix = "";
            this.amount = 1;
        }

        public GeneratedRecipeBuilder(String path, Supplier<? extends ItemLike> result) {
            this(path);
            this.result = result;
        }

        public GeneratedRecipeBuilder(String path, ResourceLocation result) {
            this(path);
            this.compatDatagenOutput = result;
        }

        GeneratedRecipeBuilder returns(int amount) {
            this.amount = amount;
            return this;
        }

        GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                .of(item.get())
                .build();
            return this;
        }

        GeneratedRecipeBuilder unlockedByTag(Supplier<TagKey<Item>> tag) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                .of(tag.get())
                .build();
            return this;
        }

        GeneratedRecipeBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return register(consumer -> {
                ShapedRecipeBuilder b = builder.apply(ShapedRecipeBuilder.shaped(result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(consumer -> {
                ShapelessRecipeBuilder b = builder.apply(ShapelessRecipeBuilder.shapeless(result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        private ResourceLocation createSimpleLocation(String recipeType) {
            return Railways.asResource(recipeType + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation createLocation(String recipeType) {
            return Railways.asResource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation getRegistryName() {
            return compatDatagenOutput == null ? RegisteredObjects.getKeyOrThrow(result.get()
                .asItem()) : compatDatagenOutput;
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends ItemLike> item) {
            return unlockedBy(item).viaCookingIngredient(() -> Ingredient.of(item.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingTag(Supplier<TagKey<Item>> tag) {
            return unlockedByTag(tag).viaCookingIngredient(() -> Ingredient.of(tag.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
            return new GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder(ingredient);
        }

        class GeneratedCookingRecipeBuilder {

            private Supplier<Ingredient> ingredient;
            private float exp;
            private int cookingTime;

            private final SimpleCookingSerializer<?> FURNACE = RecipeSerializer.SMELTING_RECIPE,
                SMOKER = RecipeSerializer.SMOKING_RECIPE, BLAST = RecipeSerializer.BLASTING_RECIPE,
                CAMPFIRE = RecipeSerializer.CAMPFIRE_COOKING_RECIPE;

            GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
                cookingTime = 200;
                exp = 0;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder forDuration(int duration) {
                cookingTime = duration;
                return this;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder rewardXP(float xp) {
                exp = xp;
                return this;
            }

            GeneratedRecipe inFurnace() {
                return inFurnace(b -> b);
            }

            GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                return create(FURNACE, builder, 1);
            }

            GeneratedRecipe inSmoker() {
                return inSmoker(b -> b);
            }

            GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(FURNACE, builder, 1);
                create(CAMPFIRE, builder, 3);
                return create(SMOKER, builder, .5f);
            }

            GeneratedRecipe inBlastFurnace() {
                return inBlastFurnace(b -> b);
            }

            GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(FURNACE, builder, 1);
                return create(BLAST, builder, .5f);
            }

            private GeneratedRecipe create(SimpleCookingSerializer<?> serializer,
                                           UnaryOperator<SimpleCookingRecipeBuilder> builder, float cookingTimeModifier) {
                return register(consumer -> {
                    boolean isOtherMod = compatDatagenOutput != null;

                    SimpleCookingRecipeBuilder b = builder.apply(
                        SimpleCookingRecipeBuilder.cooking(ingredient.get(), isOtherMod ? Items.DIRT : result.get(),
                            exp, (int) (cookingTime * cookingTimeModifier), serializer));
                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                    b.save(result -> {
                        consumer.accept(result);
                    }, createSimpleLocation(RegisteredObjects.getKeyOrThrow(serializer)
                        .getPath()));
                });
            }
        }
    }
}
