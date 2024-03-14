package com.railwayteam.railways.impl.bogeymenu;

import com.railwayteam.railways.api.bogeymenu.BogeyMenuManager;
import com.railwayteam.railways.api.bogeymenu.entry.BogeyEntry;
import com.railwayteam.railways.api.bogeymenu.entry.CategoryEntry;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class BogeyMenuManagerImpl implements BogeyMenuManager {
    public static final List<CategoryEntry> CATEGORIES = new ArrayList<>();
    public static final List<BogeyEntry> BOGIES = new ArrayList<>();

    @Override
    public CategoryEntry registerCategory(@NotNull Component name, @NotNull ResourceLocation id) {
        CategoryEntry entry = new CategoryEntry(name, id);
        CATEGORIES.add(entry);
        return entry;
    }

    @Override
    public @Nullable CategoryEntry getCategoryById(@NotNull ResourceLocation id) {
        for (CategoryEntry categoryEntry : CATEGORIES) {
            if (categoryEntry.getId().equals(id))
                return categoryEntry;
        }
        return null;
    }

    @Override
    public BogeyEntry addToCategory(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation) {
        return addToCategory(categoryEntry, bogeyStyle, iconLocation, 25);
    }

    @Override
    public BogeyEntry addToCategory(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
        BogeyEntry entry = new BogeyEntry(categoryEntry, bogeyStyle, iconLocation, scale);
        BOGIES.add(entry);
        return entry;
    }
}
