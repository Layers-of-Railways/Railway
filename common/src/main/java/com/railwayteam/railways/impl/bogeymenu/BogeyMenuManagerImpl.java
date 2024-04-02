package com.railwayteam.railways.impl.bogeymenu;

import com.railwayteam.railways.api.bogeymenu.v0.BogeyMenuManager;
import com.railwayteam.railways.api.bogeymenu.v0.entry.BogeyEntry;
import com.railwayteam.railways.api.bogeymenu.v0.entry.CategoryEntry;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class BogeyMenuManagerImpl implements BogeyMenuManager {
    // fixme should these even be public? Probably should add methods to the interface instead...
    public static final List<CategoryEntry> CATEGORIES = new ArrayList<>();
    public static final List<BogeyEntry> BOGIES = new ArrayList<>();

    public static final Map<Pair<BogeyStyle, BogeySizes.BogeySize>, Float> SIZES_TO_SCALE = new HashMap<>();

    public static final float defaultScale = 24;

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
        return addToCategory(categoryEntry, bogeyStyle, iconLocation, defaultScale);
    }

    @Override
    public BogeyEntry addToCategory(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
        BogeyEntry entry = new BogeyEntry(categoryEntry, bogeyStyle, iconLocation, scale);
        BOGIES.add(entry);
        return entry;
    }

    @Override
    public void setScaleForBogeySize(BogeyStyle style, BogeySizes.BogeySize size, float scale) {
        SIZES_TO_SCALE.put(Pair.of(style, size), scale);
    }
}
