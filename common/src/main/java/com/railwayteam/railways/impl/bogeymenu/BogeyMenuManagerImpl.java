package com.railwayteam.railways.impl.bogeymenu;

import com.railwayteam.railways.api.bogeymenu.BogeyMenuManager;
import com.railwayteam.railways.impl.bogeymenu.internal.BogeyEntry;
import com.railwayteam.railways.impl.bogeymenu.internal.CategoryEntry;
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
    public CategoryEntry registerCategory(@NotNull Component name, @NotNull ResourceLocation id, @Nullable ResourceLocation iconLocation) {
        CategoryEntry entry = new CategoryEntry(name, id, iconLocation);
        CATEGORIES.add(entry);
        return entry;
    }

    @Override
    public BogeyEntry addToCategory(@NotNull  CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation) {
        BogeyEntry entry = new BogeyEntry(categoryEntry, bogeyStyle, iconLocation);
        BOGIES.add(entry);
        return entry;
    }
}
