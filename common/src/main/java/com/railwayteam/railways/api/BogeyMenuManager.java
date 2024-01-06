package com.railwayteam.railways.api;

import com.railwayteam.railways.impl.BogeyMenuManagerImpl;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.resources.ResourceLocation;

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
     * @param id The categories id, Must be unique and must use your modid `create:standard_bogies`
     * @param iconLocation The <code>{@link ResourceLocation}</code> of the categories icon
     */
    void registerCategory(ResourceLocation id, ResourceLocation iconLocation);

    /**
     *
     * @param categoryId The id of the category you want to register this bogey style to
     * @param bogeyStyle The Bogey Style instance you are registering
     */
    void addToCategory(ResourceLocation categoryId, BogeyStyle bogeyStyle);
}
