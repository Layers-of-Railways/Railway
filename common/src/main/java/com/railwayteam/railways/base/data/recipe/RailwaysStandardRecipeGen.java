package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.compat.emi.EmiRecipeDefaultsGen;
import com.railwayteam.railways.base.data.recipe.DyedRecipeList.NullableDyedRecipeList;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRPalettes.CyclingStyleList;
import com.railwayteam.railways.registry.CRPalettes.StyledList;
import com.railwayteam.railways.registry.CRPalettes.Styles;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
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

    GeneratedRecipe REMOTE_LENS = create(CRItems.REMOTE_LENS)
            .unlockedBy(Ingredients::precisionMechanism)
            .viaShapeless(b -> b
                .requires(Ingredients.precisionMechanism())
                .requires(Ingredients.eyeOfEnder())
                .requires(Ingredients.brassSheet()));

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
            .define('*', Ingredients.propeller())
            .pattern("#*#")
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

    GeneratedRecipe LONG_STACK = create(CRBlocks.LONG_STACK)
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('+', Ingredients.campfire())
            .define('.', Ingredients.ironNugget())
            .pattern(".+.")
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

    // FIXME: Recipe unlocking doesn't seem to work properly
    GeneratedRecipe ANDESITE_SWITCH = create(CRBlocks.ANDESITE_SWITCH)
      .unlockedBy(Ingredients::andesiteCasing)
      .viaShaped(b -> b
        .define('L', Ingredients.lever())
        .define('C', Ingredients.andesiteCasing())
        .define('W', Ingredients.cogwheel())
        .pattern("L")
        .pattern("C")
        .pattern("W")
      );

    GeneratedRecipe BRASS_SWITCH = create(CRBlocks.BRASS_SWITCH)
      .unlockedBy(Ingredients::precisionMechanism)
      .viaShaped(b -> b
        .define('L', Ingredients.lever())
        .define('C', Ingredients.brassCasing())
        .define('P', Ingredients.precisionMechanism())
        .pattern("L")
        .pattern("C")
        .pattern("P")
      );

    GeneratedRecipe HANDCAR = create(CRBlocks.HANDCAR)
        .unlockedBy(Ingredients::contraptionControls)
        .viaShaped(b -> b
            .define('/', Ingredients.stick())
            .define('%', Ingredients.andesiteAlloy())
            .define('_', Ingredients.woodenSlab())
            .define('C', Ingredients.contraptionControls())
            .define('#', Ingredients.andesiteCasing())
            .define('*', Ingredients.smallCog())
            .pattern("/%/")
            .pattern("_C_")
            .pattern("#*#")
        );

    GeneratedRecipe LINK_AND_PIN = create(CRBlocks.LINK_AND_PIN)
        .unlockedBy(Ingredients::industrialIron)
        .returns(4)
        .viaShaped(b -> b
            .define('#', Ingredients.industrialIron())
            .define('_', Ingredients.ironSheet())
            .define('I', Ingredients.shaft())
            .define(',', Ingredients.ironNugget())
            .pattern("__ ")
            .pattern("#I,")
            .pattern("__ ")
        );

    GeneratedRecipe HEADSTOCK = create(CRBlocks.HEADSTOCK)
        .unlockedBy(Ingredients::linkPin)
        .viaShaped(b -> b
            .define('#', Ingredients.linkPin())
            .define('_', Ingredients.woodenSlab())
            .pattern(" # ")
            .pattern("___")
        );

    GeneratedRecipe BUFFER = create(CRBlocks.TRACK_BUFFER)
        .unlockedBy(Ingredients::headstock)
        .viaShaped(b -> b
            .define('#', Ingredients.industrialIron())
            .define('_', Ingredients.woodenSlab())
            .define('>', Ingredients.headstock())
            .define('=', Ingredients.girder())
            .pattern(">_>")
            .pattern("===")
            .pattern("# #")
        );

    GeneratedRecipe RIVETED_LOCOMETAL = create(Styles.RIVETED.get(null))
        .returns(8)
        .setEmiDefault()
        .viaStonecutting(Ingredients::ironBlock)
        .create();

    // dye a style
    StyledList<DyedRecipeList> LOCOMETAL_DYEING_8x = new StyledList<>(style -> new DyedRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/dyeing_8x", style.get(color))
            .unlockedByTag(() -> style.dyeGroupTag)
            .returns(8)
            .setEmiDefault()
            .viaShaped(b -> b
                .define('#', style.dyeGroupTag)
                .define('d', Ingredients.dye(color))
                .pattern("###")
                .pattern("#d#")
                .pattern("###")
            )
    ));

    StyledList<DyedRecipeList> LOCOMETAL_DYEING_1x = new StyledList<>(style -> new DyedRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/dyeing_1x", style.get(color))
            .unlockedByTag(() -> style.dyeGroupTag)
            .viaShapeless(b -> b
                .requires(style.dyeGroupTag)
                .requires(Ingredients.dye(color))
            )
    ));

    DyedRecipeList LOCOMETAL_WRAPPING_BRASS = new NullableDyedRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/wrapping", Styles.BRASS_WRAPPED_SLASHED.get(color))
            .unlockedBy(() -> Styles.SLASHED.get(color).get())
            .returns(8)
            .setEmiDefault(color == null)
            .viaShaped(b -> b
                .define('#', Styles.SLASHED.get(color).get())
                .define('d', Ingredients.brassIngot())
                .pattern("###")
                .pattern("#d#")
                .pattern("###")
            )
    );

    // cut a color to other blocks in the cycle
    CyclingStyleList<DyedRecipeList> LOCOMETAL_CYCLING = new CyclingStyleList<>(style -> new NullableDyedRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/cycling", style.get(color))
            .setEmiDefault(color == null && style != Styles.RIVETED)
            .viaStonecuttingTag(() -> CRPalettes.CYCLE_GROUPS.get(color))
            .create()
    ));

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
        return "Steam 'n' Rails Standard Recipes";
    }

    class GeneratedRecipeBuilder {

        private final String path;
        private String suffix;
        private Supplier<? extends ItemLike> result;
        private ResourceLocation compatDatagenOutput;

        private Supplier<ItemPredicate> unlockedBy;
        private int amount;
        private boolean addToEmiDefaults;

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

        GeneratedRecipeBuilder setEmiDefault() {
            return setEmiDefault(true);
        }

        GeneratedRecipeBuilder setEmiDefault(boolean addToEmiDefaults) {
            this.addToEmiDefaults = addToEmiDefaults;
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

        private static ResourceLocation clean(ResourceLocation loc) {
            String path = loc.getPath();
            while (path.contains("//"))
                path = path.replaceAll("//", "/");
            return new ResourceLocation(loc.getNamespace(), path);
        }

        private ResourceLocation createSimpleLocation(String recipeType) {
            ResourceLocation loc = clean(Railways.asResource(recipeType + "/" + getRegistryName().getPath() + suffix));
            if (addToEmiDefaults) {
                EmiRecipeDefaultsGen.DEFAULT_RECIPES.add(loc);
            }
            return loc;
        }

        private ResourceLocation createLocation(String recipeType) {
            ResourceLocation loc = clean(Railways.asResource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix));
            if (addToEmiDefaults) {
                EmiRecipeDefaultsGen.DEFAULT_RECIPES.add(loc);
            }
            return loc;
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

        GeneratedStonecuttingRecipeBuilder viaStonecutting(Supplier<? extends ItemLike> item) {
            return unlockedBy(item).viaStonecuttingIngrdient(() -> Ingredient.of(item.get()));
        }

        GeneratedStonecuttingRecipeBuilder viaStonecuttingTag(Supplier<TagKey<Item>> tag) {
            return unlockedByTag(tag).viaStonecuttingIngrdient(() -> Ingredient.of(tag.get()));
        }

        GeneratedStonecuttingRecipeBuilder viaStonecuttingIngrdient(Supplier<Ingredient> ingredient) {
            return new GeneratedStonecuttingRecipeBuilder(ingredient);
        }

        class GeneratedStonecuttingRecipeBuilder {

            private final Supplier<Ingredient> ingredient;

            GeneratedStonecuttingRecipeBuilder(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
            }

            private GeneratedRecipe create(UnaryOperator<SingleItemRecipeBuilder> builder) {
                return register(consumer -> {
                    SingleItemRecipeBuilder b = builder.apply(SingleItemRecipeBuilder.stonecutting(ingredient.get(), result.get(), amount));
                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                    b.save(consumer, createLocation("stonecutting"));
                });
            }

            private GeneratedRecipe create() {
                return create(b -> b);
            }
        }

        class GeneratedCookingRecipeBuilder {

            private final Supplier<Ingredient> ingredient;
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
