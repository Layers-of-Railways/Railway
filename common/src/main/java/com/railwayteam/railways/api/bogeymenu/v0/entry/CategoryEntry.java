package com.railwayteam.railways.api.bogeymenu.v0.entry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.bogey_menu.handler.BogeyMenuHandlerClient;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CategoryEntry {
    private final @NotNull Component name;
    private final @NotNull ResourceLocation id;
    private final @NotNull List<BogeyEntry> bogeyEntryList = new ArrayList<>();

    public CategoryEntry(@NotNull Component name, @NotNull ResourceLocation id) {
        this.name = name;
        this.id = id;
    }

    public @NotNull Component getName() {
        return name;
    }

    public @NotNull ResourceLocation getId() {
        return id;
    }

    public @NotNull List<BogeyEntry> getBogeyEntryList() {
        return bogeyEntryList;
    }

    @ApiStatus.Internal
    void addToBogeyEntryList(BogeyEntry entry) {
        bogeyEntryList.add(entry);
    }

    public static class FavoritesCategory extends CategoryEntry {

        public static final CategoryEntry INSTANCE = new FavoritesCategory();

        @Nullable
        private List<BogeyEntry> cachedEntryList = null;
        private int cachedVersion = -1;

        private FavoritesCategory() {
            super(Components.translatable("railways.gui.bogey_menu.category.favorites"), Railways.asResource("favorites"));
        }

        @Override
        public @NotNull List<BogeyEntry> getBogeyEntryList() {
            int version = BogeyMenuHandlerClient.getFavorites().hashCode();
            if (cachedEntryList == null || cachedVersion != version) {
                cachedEntryList = new ArrayList<>();
                for (BogeyStyle style : BogeyMenuHandlerClient.getFavorites()) {
                    if (BogeyEntry.STYLE_TO_ENTRY.containsKey(style)) {
                        cachedEntryList.add(BogeyEntry.STYLE_TO_ENTRY.get(style));
                    } else {
                        // uh oh, we're going to have to guess values here...
                        cachedEntryList.add(BogeyEntry.getOrCreate(style, null, 24));
                    }
                }
                cachedVersion = version;
            }
            return cachedEntryList;
        }

        @Override
        void addToBogeyEntryList(BogeyEntry entry) {
            // no-op
        }
    }
}
