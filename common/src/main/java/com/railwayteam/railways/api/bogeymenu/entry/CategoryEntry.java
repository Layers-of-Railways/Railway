package com.railwayteam.railways.api.bogeymenu.entry;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CategoryEntry {
    private final Component name;
    private final ResourceLocation id;
    private final List<BogeyEntry> bogeyEntryList = new ArrayList<>();

    public CategoryEntry(@NotNull Component name, @NotNull ResourceLocation id) {
        this.name = name;
        this.id = id;
    }

    public Component getName() {
        return name;
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<BogeyEntry> getBogeyEntryList() {
        return bogeyEntryList;
    }

    @ApiStatus.Internal
    public void addToBogeyEntryList(BogeyEntry entry) {
        bogeyEntryList.add(entry);
    }
}
