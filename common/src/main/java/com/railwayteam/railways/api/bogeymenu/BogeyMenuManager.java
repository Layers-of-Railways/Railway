package com.railwayteam.railways.api.bogeymenu;

import com.railwayteam.railways.api.bogeymenu.entry.BogeyEntry;
import com.railwayteam.railways.api.bogeymenu.entry.CategoryEntry;
import com.railwayteam.railways.impl.bogeymenu.BogeyMenuManagerImpl;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles adding bogies and categories to the Bogey UI
 */
public interface BogeyMenuManager {
    BogeyMenuManagerImpl INSTANCE = new BogeyMenuManagerImpl();

    /**
     * Register a Bogey Category
     *
     * <p>
     * Example: <pre>
     * {@code registerCategory(Create.asResource("standard_bogies"), Create.asResource("textures/gui/category")}
     * </pre>
     *
     * @param name The {@link Component#translatable(String)} name of the category
     * @param id The categories id, Must be unique and must-use your modid `create:standard_bogies`
     *
     * @return the {@link CategoryEntry} that has been added.
     */
    CategoryEntry registerCategory(@NotNull Component name, @NotNull ResourceLocation id);

    /**
     * Add a bogey to a category
     *
     * @param categoryEntry The categories entry
     * @param bogeyStyle The Bogey Style instance you are registering
     * @param iconLocation The {@link ResourceLocation} of the bogie icon
     */
    BogeyEntry addToCategory(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation);
}
