package com.railwayteam.railways.impl.bogeymenu.internal;

import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BogeyEntry(CategoryEntry categoryEntry, BogeyStyle bogeyStyle, ResourceLocation iconLocation) {
    public BogeyEntry(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation) {
        this.categoryEntry = categoryEntry;
        this.bogeyStyle = bogeyStyle;
        this.iconLocation = iconLocation;
        categoryEntry.addToBogeyEntryList(this);
    }
}
