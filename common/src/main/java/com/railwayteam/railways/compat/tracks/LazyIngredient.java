package com.railwayteam.railways.compat.tracks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LazyIngredient extends Ingredient {

    private static final LazyIngredient EMPTY = new LazyIngredient(Stream::empty);

    private final Supplier<Supplier<Value>[]> valueSupplier;
    private Value[] internalValues;

    public LazyIngredient(Supplier<Stream<Supplier<? extends Value>>> values) {
        super(Stream.empty());
        valueSupplier = () -> (Supplier<Value>[]) values.get().toArray(Supplier[]::new);
    }

    @Nullable
    private ItemStack[] itemStacks;
    @Nullable
    private IntList stackingIds;

    private Ingredient.Value[] getValues() {
        if (internalValues == null) {
            internalValues = Arrays.stream(valueSupplier.get()).map(Supplier::get).toArray(Value[]::new);
        }
        return internalValues;
    }

    @Override
    public ItemStack[] getItems() {
        this.dissolve();
        return this.itemStacks;
    }

    private void dissolve() {
        if (this.itemStacks == null) {
            this.itemStacks = (ItemStack[]) Arrays.stream(getValues()).flatMap(entry -> entry.getItems().stream()).distinct().toArray(ItemStack[]::new);
        }
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) {
            return false;
        }
        this.dissolve();
        if (this.itemStacks.length == 0) {
            return stack.isEmpty();
        }
        for (ItemStack itemStack : this.itemStacks) {
            if (!itemStack.is(stack.getItem())) continue;
            return true;
        }
        return false;
    }

    @Override
    public IntList getStackingIds() {
        if (this.stackingIds == null) {
            this.dissolve();
            this.stackingIds = new IntArrayList(this.itemStacks.length);
            for (ItemStack itemStack : this.itemStacks) {
                this.stackingIds.add(StackedContents.getStackingIndex(itemStack));
            }
            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return this.stackingIds;
    }

    @Override
    public JsonElement toJson() {
        if (this.getValues().length == 1) {
            return this.getValues()[0].serialize();
        }
        JsonArray jsonArray = new JsonArray();
        for (Ingredient.Value value : this.getValues()) {
            jsonArray.add(value.serialize());
        }
        return jsonArray;
    }

    @Override
    public boolean isEmpty() {
        return !(this.getValues().length != 0 || this.itemStacks != null && this.itemStacks.length != 0 || this.stackingIds != null && !this.stackingIds.isEmpty());
    }

    public static LazyIngredient fromValuesLazy(Supplier<Stream<Supplier<? extends Ingredient.Value>>> stream) {
        LazyIngredient ingredient = new LazyIngredient(stream);
        return ingredient;
    }

    public static LazyIngredient lazyOf() {
        return EMPTY;
    }

    @SafeVarargs
    public static LazyIngredient lazyOf(Supplier<ItemLike>... items) {
        return LazyIngredient.lazyOf(Arrays.stream(items).map((e) -> () -> new ItemStack(e.get())));
    }

    /*public static LazyIngredient of(ItemStack ... stacks) {
        return LazyIngredient.of(Arrays.stream(stacks));
    }*/

    public static LazyIngredient lazyOf(Stream<Supplier<ItemStack>> stacks) {
        return LazyIngredient.fromValuesLazy(() -> stacks.filter(stack -> !stack.get().isEmpty())
            .map((e) -> (Supplier<ItemValue>) () -> new ItemValue(e.get())));
    }
}
