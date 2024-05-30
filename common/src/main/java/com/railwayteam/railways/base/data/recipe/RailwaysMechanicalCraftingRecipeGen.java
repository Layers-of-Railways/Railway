/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.compat.emi.EmiRecipeDefaultsGen;
import com.railwayteam.railways.base.data.recipe.DyedRecipeList.NullableDyedRecipeList;
import com.railwayteam.railways.registry.CRPalettes.Styles;
import com.simibubi.create.foundation.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public abstract class RailwaysMechanicalCraftingRecipeGen extends RailwaysRecipeProvider {

    DyedRecipeList BOILERS = new NullableDyedRecipeList(color -> create(() -> Styles.BOILER.get(color).get())
        .returns(4)
        .setEmiDefault(color == null)
        .recipe(b -> b.key('#', Styles.SLASHED.get(color).get())
            .key('u', Items.BUCKET)
            .key('/', Items.BLAZE_ROD)
            .patternLine("  #  ")
            .patternLine(" #u# ")
            .patternLine("#///#")
            .patternLine(" #/# ")
            .patternLine("  #  ")));

    DyedRecipeList BRASS_WRAPPED_BOILERS = new NullableDyedRecipeList(color -> create(() -> Styles.BRASS_WRAPPED_BOILER.get(color).get())
        .returns(4)
        .setEmiDefault(color == null)
        .recipe(b -> b.key('#', Styles.BRASS_WRAPPED_SLASHED.get(color).get())
            .key('u', Items.BUCKET)
            .key('/', Items.BLAZE_ROD)
            .patternLine("  #  ")
            .patternLine(" #u# ")
            .patternLine("#///#")
            .patternLine(" #/# ")
            .patternLine("  #  ")));

    DyedRecipeList COPPER_WRAPPED_BOILERS = new NullableDyedRecipeList(color -> create(() -> Styles.COPPER_WRAPPED_BOILER.get(color).get())
        .returns(4)
        .setEmiDefault(color == null)
        .recipe(b -> b.key('#', Styles.COPPER_WRAPPED_SLASHED.get(color).get())
            .key('u', Items.BUCKET)
            .key('/', Items.BLAZE_ROD)
            .patternLine("  #  ")
            .patternLine(" #u# ")
            .patternLine("#///#")
            .patternLine(" #/# ")
            .patternLine("  #  ")));

    DyedRecipeList IRON_WRAPPED_BOILERS = new NullableDyedRecipeList(color -> create(() -> Styles.IRON_WRAPPED_BOILER.get(color).get())
        .returns(4)
        .setEmiDefault(color == null)
        .recipe(b -> b.key('#', Styles.IRON_WRAPPED_SLASHED.get(color).get())
            .key('u', Items.BUCKET)
            .key('/', Items.BLAZE_ROD)
            .patternLine("  #  ")
            .patternLine(" #u# ")
            .patternLine("#///#")
            .patternLine(" #/# ")
            .patternLine("  #  ")));

    protected RailwaysMechanicalCraftingRecipeGen(DataGenerator pGenerator) {
        super(pGenerator);
    }

    GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilder(result);
    }

    class GeneratedRecipeBuilder {
        private String suffix;
        private Supplier<ItemLike> result;
        private int amount;
        private boolean addToEmiDefaults;

        public GeneratedRecipeBuilder(Supplier<ItemLike> result) {
            this.suffix = "";
            this.result = result;
            this.amount = 1;
        }

        GeneratedRecipeBuilder returns(int amount) {
            this.amount = amount;
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

        private static ResourceLocation clean(ResourceLocation loc) {
            String path = loc.getPath();
            while (path.contains("//"))
                path = path.replaceAll("//", "/");
            return new ResourceLocation(loc.getNamespace(), path);
        }



        GeneratedRecipe recipe(UnaryOperator<MechanicalCraftingRecipeBuilder> builder) {
            return register(consumer -> {
                MechanicalCraftingRecipeBuilder b =
                    builder.apply(MechanicalCraftingRecipeBuilder.shapedRecipe(result.get(), amount));
                ResourceLocation location = clean(Railways.asResource("mechanical_crafting/" + RegisteredObjects.getKeyOrThrow(result.get()
                        .asItem())
                    .getPath() + suffix));
                if (addToEmiDefaults) {
                    EmiRecipeDefaultsGen.DEFAULT_RECIPES.add(location);
                }
                b.build(consumer, location);
            });
        }
    }

    @ExpectPlatform
    public static RecipeProvider create(DataGenerator gen) {
        throw new AssertionError();
    }

    @Override
    public @NotNull String getName() {
        return "Steam 'n' Rails Mechanical Crafting Recipes";
    }
}
