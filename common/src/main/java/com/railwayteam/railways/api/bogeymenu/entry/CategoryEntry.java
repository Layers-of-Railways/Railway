package com.railwayteam.railways.api.bogeymenu.entry;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CategoryEntry {
    private final Component name;
    private final ResourceLocation id;
    private final ResourceLocation iconLocation;
    private final List<BogeyEntry> bogeyEntryList = new ArrayList<>();

    public CategoryEntry(@NotNull Component name, @NotNull ResourceLocation id, @Nullable ResourceLocation iconLocation) {
        this.name = name;
        this.id = id;
        this.iconLocation = iconLocation;
    }

    public Component getName() {
        return name;
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceLocation getIconLocation() {
        return iconLocation;
    }

    public List<BogeyEntry> getBogeyEntryList() {
        return bogeyEntryList;
    }

    public void addToBogeyEntryList(BogeyEntry entry) {
        bogeyEntryList.add(entry);
    }
}
