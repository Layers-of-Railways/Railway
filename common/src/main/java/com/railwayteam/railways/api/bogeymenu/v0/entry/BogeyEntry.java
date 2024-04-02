package com.railwayteam.railways.api.bogeymenu.v0.entry;

import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public record BogeyEntry(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
    public static final HashMap<BogeyStyle, BogeyEntry> STYLE_TO_ENTRY = new HashMap<>();

    public BogeyEntry(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale) {
        this.categoryEntry = categoryEntry;
        this.bogeyStyle = bogeyStyle;
        this.iconLocation = iconLocation;
        this.scale = scale;
        categoryEntry.addToBogeyEntryList(this);
        STYLE_TO_ENTRY.put(bogeyStyle, this);
    }
}
