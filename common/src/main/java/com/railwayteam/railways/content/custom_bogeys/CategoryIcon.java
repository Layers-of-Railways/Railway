package com.railwayteam.railways.content.custom_bogeys;

import com.railwayteam.railways.Railways;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class CategoryIcon implements ItemLike {

    public final ResourceLocation location;

    public CategoryIcon(ResourceLocation location) {
        this.location = location;
    }

    public static CategoryIcon standard(String name) {
        return new CategoryIcon(Railways.asResource("textures/gui/bogey_icons/" + name + ".png"));
    }

    public static NonNullSupplier<CategoryIcon> standardSupplier(String name) {
        return () -> standard(name);
    }

    @Override
    public @NotNull Item asItem() {
        return Items.AIR;
    }
}