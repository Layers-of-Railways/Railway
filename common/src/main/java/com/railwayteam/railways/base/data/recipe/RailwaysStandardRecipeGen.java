/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.compat.emi.EmiRecipeDefaultsGen;
import com.railwayteam.railways.base.data.recipe.EnumRecipeList.PalettesRecipeList;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRPalettes.CycleCategoryList;
import com.railwayteam.railways.registry.CRPalettes.CycleGroupCategory;
import com.railwayteam.railways.registry.CRPalettes.CyclingStyleList;
import com.railwayteam.railways.registry.CRPalettes.Styles;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.AbstractionUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class RailwaysStandardRecipeGen extends RailwaysRecipeProvider {

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

    GeneratedRecipe COALBURNER_STACK = create(CRBlocks.COALBURNER_STACKS.getFirst())
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

    GeneratedRecipe OILBURNER_STACK = create(CRBlocks.OILBURNER_STACKS.getFirst())
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('#', Ingredients.ironSheet())
            .define('+', Ingredients.campfire())
            .pattern("# #")
            .pattern("#+#")
        );

    GeneratedRecipe STREAMLINED_STACK = create(CRBlocks.STREAMLINED_STACKS.getFirst())
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('#', Ingredients.ironSheet())
            .define('+', Ingredients.campfire())
            .pattern("#+#")
        );

    GeneratedRecipe WOODBURNER_STACK = create(CRBlocks.WOODBURNER_STACKS.getFirst())
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('#', Ingredients.ironSheet())
            .define('+', Ingredients.campfire())
            .define('.', Ingredients.ironNugget())
            .pattern(".#.")
            .pattern("#+#")
        );

    GeneratedRecipe LONG_STACK = create(CRBlocks.LONG_STACKS.getFirst())
        .unlockedBy(Ingredients::campfire)
        .viaShaped(b -> b.define('+', Ingredients.campfire())
            .define('.', Ingredients.ironNugget())
            .pattern(".+.")
        );

    GeneratedRecipe SEMAPHORE = create(CRBlocks.SEMAPHORE)
        .unlockedBy(AllItems.ELECTRON_TUBE)
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

    GeneratedRecipe SMALL_BUFFER = create(CRBlocks.SMALL_BUFFER)
        .unlockedBy(Ingredients::industrialIron)
        .returns(4)
        .viaShaped(b -> b
            .define('#', Ingredients.industrialIron())
            .define('_', Ingredients.ironSheet())
            .define('I', Ingredients.shaft())
            .pattern("#I_")
        );

    GeneratedRecipe BIG_BUFFER = create(CRBlocks.BIG_BUFFER)
        .unlockedBy(Ingredients::smallBuffer)
        .returns(4)
        .viaShaped(b -> b
            .define('#', Ingredients.industrialIron())
            .define('_', Ingredients.ironSheet())
            .define('I', Ingredients.smallBuffer())
            .pattern("#I_")
        );

    GeneratedRecipe LINK_AND_PIN = create(Ingredients::linkPin)
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

    GeneratedRecipe HEADSTOCK = create(Ingredients::headstock)
        .unlockedBy(Ingredients::linkPin)
        .viaShaped(b -> b
            .define('#', Ingredients.linkPinTag())
            .define('_', Ingredients.woodenSlab())
            .pattern(" # ")
            .pattern("___")
        );

    GeneratedRecipe COPYCAT_HEADSTOCK = create(Ingredients::copycatHeadstock)
        .unlockedBy(Ingredients::linkPin)
        .viaShaped(b -> b
            .define('#', Ingredients.linkPinTag())
            .define('_', Ingredients.copycatPanel())
            .pattern(" # ")
            .pattern("___")
        );

    GeneratedRecipe BUFFER = create(CRBlocks.TRACK_BUFFER)
        .unlockedBy(Ingredients::headstock)
        .viaShaped(b -> b
            .define('#', Ingredients.industrialIron())
            .define('>', Ingredients.headstockTag())
            .define('=', Ingredients.girder())
            .pattern(">>>")
            .pattern("===")
            .pattern("# #")
        );

    GeneratedRecipe RIVETED_LOCOMETAL = create(Styles.RIVETED.get(PalettesColor.NETHERITE))
        .returns(8)
        .setEmiDefault()
        .viaStonecutting(Ingredients::ironBlock)
        .create();

    CyclingStyleList<PalettesRecipeList> LOCOMETAL_LADDERS = new CyclingStyleList<>(CycleGroupCategory.LADDERS, style ->
        new PalettesRecipeList(color ->
            new GeneratedRecipeBuilder("palettes/ladders", style.get(color))
                .returns(2)
                .setEmiDefault(color.isNetherite())
                .viaStonecuttingTag(() -> CycleGroupCategory.BASE.getTag(color))
                .create()
        )
    );

    PalettesRecipeList LOCOMETAL_DOORS = new PalettesRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/doors", Styles.HINGED_DOOR.get(color))
            .setEmiDefault(color.isNetherite())
            .unlockedByTag(Ingredients::woodenDoors)
            .viaShapeless(b -> b
                .requires(Ingredients.woodenDoors())
                .requires(Styles.RIVETED.get(color))
            )
    );

    PalettesRecipeList LOCOMETAL_TRAPDOORS = new PalettesRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/trapdoors", Styles.TRAPDOOR.get(color))
            .setEmiDefault(color.isNetherite())
            .unlockedByTag(Ingredients::woodenTrapdoors)
            .viaShapeless(b -> b
                .requires(Ingredients.woodenTrapdoors())
                .requires(Styles.RIVETED.get(color))
            )
    );

    PalettesRecipeList LOCOMETAL_WINDOWS = new PalettesRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/windows", Styles.SINGLE_PANE_WINDOW.get(color))
            .setEmiDefault(color.isNetherite())
            .unlockedByTag(Ingredients::colorlessGlass)
            .viaShapeless(b -> b
                .requires(Ingredients.colorlessGlass())
                .requires(Styles.RIVETED.get(color))
            )
    );

    PalettesRecipeList
        HAZARD_STRIPES_DIAGONAL_BLACK_A = hazardStripesDiagonal(PalettesColor.BLACK, Styles.HAZARD_STRIPES_DIAGONAL_BLACK, false),
        HAZARD_STRIPES_DIAGONAL_BLACK_B = hazardStripesDiagonal(PalettesColor.BLACK, Styles.HAZARD_STRIPES_DIAGONAL_BLACK, true),
        HAZARD_STRIPES_DIAGONAL_WHITE_A = hazardStripesDiagonal(PalettesColor.WHITE, Styles.HAZARD_STRIPES_DIAGONAL_WHITE, false),
        HAZARD_STRIPES_DIAGONAL_WHITE_B = hazardStripesDiagonal(PalettesColor.WHITE, Styles.HAZARD_STRIPES_DIAGONAL_WHITE, true)
    ;

    private PalettesRecipeList hazardStripesDiagonal(PalettesColor baseColor, Styles hazardStyle, boolean flipped) {
        char c1 = flipped ? '.' : '#';
        char c2 = flipped ? '#' : '.';

        return new PalettesRecipeList(color ->
            new GeneratedRecipeBuilder("palettes/hazard_stripes_" + (flipped ? "b" : "a") + "/", hazardStyle.get(color))
                .setEmiDefault()
                .unlockedBy(() -> Styles.SLASHED.get(baseColor).get())
                .returns(4)
                .viaShaped(b -> b
                    .define(c1, Styles.SLASHED.get(color).get())
                    .define(c2, Styles.SLASHED.get(baseColor).get())
                    .pattern(".#")
                    .pattern("#.")
                )
        );
    }

    PalettesRecipeList LOCOMETAL_WRAPPING_BRASS = new PalettesRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/wrapping", Styles.BRASS_WRAPPED_SLASHED.get(color))
            .unlockedBy(() -> Styles.SLASHED.get(color).get())
            .returns(8)
            .setEmiDefault(color.isNetherite())
            .viaShaped(b -> b
                .define('#', Styles.SLASHED.get(color).get())
                .define('d', Ingredients.brassIngot())
                .pattern("###")
                .pattern("#d#")
                .pattern("###")
            )
    );

    PalettesRecipeList LOCOMETAL_WRAPPING_COPPER = new PalettesRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/wrapping", Styles.COPPER_WRAPPED_SLASHED.get(color))
            .unlockedBy(() -> Styles.SLASHED.get(color).get())
            .returns(8)
            .setEmiDefault(color.isNetherite())
            .viaShaped(b -> b
                .define('#', Styles.SLASHED.get(color).get())
                .define('d', Ingredients.copperIngot())
                .pattern("###")
                .pattern("#d#")
                .pattern("###")
            )
    );

    PalettesRecipeList LOCOMETAL_WRAPPING_IRON = new PalettesRecipeList(color ->
        new GeneratedRecipeBuilder("palettes/wrapping", Styles.IRON_WRAPPED_SLASHED.get(color))
            .unlockedBy(() -> Styles.SLASHED.get(color).get())
            .returns(8)
            .setEmiDefault(color.isNetherite())
            .viaShaped(b -> b
                .define('#', Styles.SLASHED.get(color).get())
                .define('d', Ingredients.ironIngot())
                .pattern("###")
                .pattern("#d#")
                .pattern("###")
            )
    );

    // cut a color to other blocks in the cycle
    CycleCategoryList<CyclingStyleList<PalettesRecipeList>> LOCOMETAL_CYCLING = new CycleCategoryList<>(category ->
        new CyclingStyleList<>(category, style ->
            new PalettesRecipeList(color ->
                new GeneratedRecipeBuilder("palettes/cycling", style.get(color))
                    .setEmiDefault(color.isNetherite() && style != category.baseStyle.get())
                    .viaStonecuttingTag(() -> CRPalettes.CYCLE_GROUPS.get(Pair.of(color, style.cycleGroupCategory)))
                    .create()
            )
        )
    );

    GeneratedRecipe FUEL_TANK = create(AbstractionUtils.getFluidTankBlockEntry())
        .unlockedBy(AllBlocks.FLUID_TANK)
        .viaShaped(b -> b
            .define('S', Ingredients.sturdySheet())
            .define('F', AllBlocks.FLUID_TANK.get())
            .pattern("S")
            .pattern("F")
            .pattern("S")
        );

    GeneratedRecipe PORTABLE_FUEL_INTERFACE = create(AbstractionUtils.getPortableFuelInterfaceBlockEntry())
        .unlockedBy(AllBlocks.PORTABLE_FLUID_INTERFACE)
        .viaShapeless(b -> b
            .requires(Ingredients.railwayCasing())
            .requires(Ingredients.chute())
        );

    GeneratedRecipe EMPTY_PAINT_PITCHER = create(Ingredients::emptyPaintPitcher)
        .unlockedByTag(Ingredients::colorlessGlass)
        .returns(5)
        .viaShaped(b -> b
            .define('G', Ingredients.colorlessGlass())
            .pattern("G G")
            .pattern("G G")
            .pattern(" G ")
        );

    GeneratedRecipe PAINT_BRUSH = create(Ingredients::paintBrush)
        .unlockedBy(Ingredients::feather)
        .viaShaped(b -> b
            .define('F', Ingredients.feather())
            .define('_', Ingredients.ironIngot())
            .define('|', Ingredients.stick())
            .pattern("F")
            .pattern("_")
            .pattern("|")
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

    public RailwaysStandardRecipeGen(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    public @NotNull String getName() {
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
                ShapedRecipeBuilder b = builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(consumer -> {
                ShapelessRecipeBuilder b = builder.apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), amount));
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
                    SingleItemRecipeBuilder b = builder.apply(SingleItemRecipeBuilder.stonecutting(ingredient.get(), RecipeCategory.MISC, result.get(), amount));
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

            private final SimpleCookingSerializer<?>
                FURNACE = (SimpleCookingSerializer<?>) RecipeSerializer.SMELTING_RECIPE,
                SMOKER = (SimpleCookingSerializer<?>) RecipeSerializer.SMOKING_RECIPE,
                BLAST = (SimpleCookingSerializer<?>) RecipeSerializer.BLASTING_RECIPE,
                CAMPFIRE = (SimpleCookingSerializer<?>) RecipeSerializer.CAMPFIRE_COOKING_RECIPE;

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

                    // fix|me removed serializer from the cooking time + refactored with whatever intellij said
                    // can't really test this 'cause we don't have any cooking recipes yet, just assume it's fine for now
                    SimpleCookingRecipeBuilder b = builder.apply(
                        SimpleCookingRecipeBuilder.campfireCooking(ingredient.get(), RecipeCategory.MISC, isOtherMod ? Items.DIRT : result.get(),
                            exp, (int) (cookingTime * cookingTimeModifier)));
                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                    b.save(consumer, createSimpleLocation(RegisteredObjects.getKeyOrThrow(serializer)
                        .getPath()));
                });
            }
        }
    }
}
