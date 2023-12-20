package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.base.data.recipe.RailwaysRecipeProvider.GeneratedRecipe;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class DyedRecipeList implements Iterable<GeneratedRecipe> {
    private static final int COLOR_AMOUNT = DyeColor.values().length;

    protected final GeneratedRecipe[] values = new GeneratedRecipe[getColorCount()];

    public DyedRecipeList(Function<@NotNull DyeColor, GeneratedRecipe> filler) {
        for (DyeColor color : DyeColor.values()) {
            values[color.ordinal()] = filler.apply(color);
        }
    }

    protected int getColorCount() {
        return COLOR_AMOUNT;
    }

    public GeneratedRecipe get(@NotNull DyeColor color) {
        return values[color.ordinal()];
    }

    public GeneratedRecipe[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public Iterator<GeneratedRecipe> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @Override
            public GeneratedRecipe next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return values[index++];
            }
        };
    }

    public static class NullableDyedRecipeList extends DyedRecipeList {
        public NullableDyedRecipeList(Function<@Nullable DyeColor, GeneratedRecipe> filler) {
            super(filler);
            values[values.length - 1] = filler.apply(null);
        }

        @Override
        protected int getColorCount() {
            return COLOR_AMOUNT + 1;
        }

        @Override
        public GeneratedRecipe get(@Nullable DyeColor color) {
            if (color == null)
                return values[values.length - 1];
            return super.get(color);
        }
    }
}
