package com.railwayteam.railways.compat.tracks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Represents a special ingredient for datagen - it references an item that does not necessarily exist
 */
public class SoftIngredient extends Ingredient {
    public final ResourceLocation item;
    public SoftIngredient(ResourceLocation item) {
        super(Stream.empty());
        this.item = item;
    }

    public static SoftIngredient of(ResourceLocation item) {
        return new SoftIngredient(item);
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", item.toString());
        return jsonobject;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
