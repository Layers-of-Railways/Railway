/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.base.data.recipe.processing;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.compat.emi.EmiRecipeDefaultsGen;
import com.railwayteam.railways.base.data.recipe.EnumRecipeList.DyedOnlyPalettesRecipeList;
import com.railwayteam.railways.base.data.recipe.EnumRecipeList.VanillaDyedOnlyPalettesRecipeList;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.painting.PaintFluid;
import com.railwayteam.railways.content.palettes.painting.PaintPitcherItem;
import com.railwayteam.railways.multiloader.fluid.FluidUnits;
import com.railwayteam.railways.registry.CRFluids;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRPalettes.StyledList;
import com.railwayteam.railways.util.FluidUtils;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class RailwaysMixingRecipeGen extends RailwaysProcessingRecipeGen {
    StyledList<DyedOnlyPalettesRecipeList> LOCOMETAL_DYEING = new StyledList<>(style -> new DyedOnlyPalettesRecipeList(
        color -> createWithDeferredId(
            () -> {
                ResourceLocation loc = Railways.asResource("palettes/dyeing/" + RegisteredObjects.getKeyOrThrow(style.get(color).asItem()).getPath());
                if (style != CRPalettes.Styles.FLYWHEEL) {
                    EmiRecipeDefaultsGen.DEFAULT_RECIPES.add(Railways.asResource(getRecipeType().getId().getPath() + "/" + loc.getPath()));
                }
                return loc;
            },
            b -> b
                .require(style.dyeGroupTag)
                .require(Ingredients.palettesPaint(color, PaintPitcherItem.FLUID_PER_LEVEL))
                .output(style.get(color))
        )
    ));

    // Paint from vanilla dyes
    VanillaDyedOnlyPalettesRecipeList PAINT_VANILLA = new VanillaDyedOnlyPalettesRecipeList((color, dyeColor) -> createWithDeferredId(
        paintLoc(color),
        b -> {
            b.require(Ingredients.dye(dyeColor));
            b.require(Ingredients.bindingAgent());
            b.require(FluidIngredient.fromTag(FluidTags.WATER, FluidUnits.bucket()));
            FluidUtils.addFluidOutput(b, CRFluids.PAINT.get(), FluidUnits.bucket(), PaintFluid.setColor(new CompoundTag(), color));
            return b;
        }
    ));

    // Paint from rocks
    GeneratedRecipe
        PAINT_GRANITE = paintStone(PalettesColor.GRANITE, AllPaletteStoneTypes.GRANITE),
        PAINT_DRIPSTONE = paintStone(PalettesColor.DRIPSTONE, AllPaletteStoneTypes.DRIPSTONE),
        PAINT_OCHRUM = paintStone(PalettesColor.OCHRUM, AllPaletteStoneTypes.OCHRUM),
        PAINT_DIORITE = paintStone(PalettesColor.DIORITE, AllPaletteStoneTypes.DIORITE),
        PAINT_LIMESTONE = paintStone(PalettesColor.LIMESTONE, AllPaletteStoneTypes.LIMESTONE),
        PAINT_TUFF = paintStone(PalettesColor.TUFF, AllPaletteStoneTypes.TUFF),
        PAINT_SCORCHIA = paintStone(PalettesColor.SCORCHIA, AllPaletteStoneTypes.SCORCHIA);

    // Paint primarily from mixing paints
    GeneratedRecipe
        PAINT_MAROON = paintMix(PalettesColor.MAROON, PalettesColor.RED, PalettesColor.BLACK),
        PAINT_VERMILION = paintMix(PalettesColor.VERMILION, PalettesColor.RED, PalettesColor.ORANGE),
        PAINT_CHARTREUSE = paintMix(PalettesColor.CHARTREUSE, PalettesColor.YELLOW, PalettesColor.GREEN),
        PAINT_OLIVE_GREEN = paintMix(PalettesColor.OLIVE_GREEN, PalettesColor.LIME, PalettesColor.GREEN),
        PAINT_PINE_GREEN = paintMix(PalettesColor.PINE_GREEN, PalettesColor.GREEN, PalettesColor.BLACK),
        PAINT_SEA_GREEN = paintMix(PalettesColor.SEA_GREEN, PalettesColor.CYAN, PalettesColor.BLACK),
        PAINT_TURQUOISE = paintMix(PalettesColor.TURQUOISE, PalettesColor.CYAN, PalettesColor.WHITE),
        PAINT_ROYAL_BLUE = paintMix(PalettesColor.ROYAL_BLUE, PalettesColor.BLUE, PalettesColor.BLACK)
    ;

    // Paint secondary from mixing paints
    GeneratedRecipe
        PAINT_ORANGE = paintMix$(PalettesColor.ORANGE, PalettesColor.RED, PalettesColor.YELLOW),
        PAINT_LIME = paintMix$(PalettesColor.LIME, PalettesColor.GREEN, PalettesColor.WHITE),
        PAINT_CYAN = paintMix$(PalettesColor.CYAN, PalettesColor.BLUE, PalettesColor.GREEN),
        PAINT_LIGHT_BLUE = paintMix$(PalettesColor.LIGHT_BLUE, PalettesColor.BLUE, PalettesColor.WHITE),
        PAINT_PURPLE = paintMix$(PalettesColor.PURPLE, PalettesColor.BLUE, PalettesColor.RED),
        PAINT_MAGENTA = paintMix$(PalettesColor.MAGENTA, PalettesColor.PURPLE, PalettesColor.PINK),
        PAINT_PINK = paintMix$(PalettesColor.PINK, PalettesColor.RED, PalettesColor.WHITE),
        PAINT_LIGHT_GRAY = paintMix$(PalettesColor.LIGHT_GRAY, PalettesColor.GRAY, PalettesColor.WHITE),
        PAINT_GRAY = paintMix$(PalettesColor.GRAY, PalettesColor.BLACK, PalettesColor.WHITE)
    ;

    public RailwaysMixingRecipeGen(PackOutput output) {
        super(output);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.MIXING;
    }

    private Supplier<ResourceLocation> paintLoc(PalettesColor color) {
        return () -> {
            ResourceLocation loc = Railways.asResource("palettes/dye/" + color.getSerializedName());
            EmiRecipeDefaultsGen.DEFAULT_RECIPES.add(Railways.asResource(getRecipeType().getId().getPath() + "/" + loc.getPath()));
            return loc;
        };
    }

    GeneratedRecipe paintStone(PalettesColor color, AllPaletteStoneTypes stoneType) {
        return createWithDeferredId(
            paintLoc(color),
            b -> {
                b.require(stoneType.getBaseBlock().get());
                b.require(Ingredients.bindingAgent());
                b.require(FluidIngredient.fromTag(FluidTags.WATER, FluidUnits.bucket()));
                b.requiresHeat(HeatCondition.HEATED);
                FluidUtils.addFluidOutput(b, CRFluids.PAINT.get(), FluidUnits.bucket(), PaintFluid.setColor(new CompoundTag(), color));
                return b;
            }
        );
    }

    private GeneratedRecipe paintMix(PalettesColor result, PalettesColor colorA, PalettesColor colorB, boolean makeDefault) {
        return createWithDeferredId(
            () -> {
                ResourceLocation loc = Railways.asResource(
                    "palettes/dye/"
                        + result.getSerializedName()
                        + "_from_"
                        + colorA.getSerializedName()
                        + "_" + colorB.getSerializedName());
                if (makeDefault) {
                    EmiRecipeDefaultsGen.DEFAULT_RECIPES.add(Railways.asResource(getRecipeType().getId().getPath() + "/" + loc.getPath()));
                }
                return loc;
            },
            b -> {
                b.require(Ingredients.palettesPaint(colorA, PaintPitcherItem.FLUID_PER_LEVEL));
                b.require(Ingredients.palettesPaint(colorB, PaintPitcherItem.FLUID_PER_LEVEL));
                FluidUtils.addFluidOutput(b, CRFluids.PAINT.get(), PaintPitcherItem.FLUID_PER_LEVEL * 2, PaintFluid.setColor(new CompoundTag(), result));
                return b;
            }
        );
    }

    private GeneratedRecipe paintMix(PalettesColor result, PalettesColor colorA, PalettesColor colorB) {
        return paintMix(result, colorA, colorB, true);
    }

    private GeneratedRecipe paintMix$(PalettesColor result, PalettesColor colorA, PalettesColor colorB) {
        return paintMix(result, colorA, colorB, false);
    }
}
