/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.api.bogeymenu.v0;

import com.railwayteam.railways.api.bogeymenu.v0.entry.BogeyEntry;
import com.railwayteam.railways.api.bogeymenu.v0.entry.CategoryEntry;
import com.railwayteam.railways.impl.bogeymenu.v0.BogeyMenuManagerImpl;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles adding bogies and categories to the Bogey UI
 * <p>
 * <b> IMPORTANT: </b> If you are depending on or using categories provided by Steam 'n' Rails you need to
 * use the events we provide to load your class otherwise you risk unpredictable mod loading causing issues. {@link BogeyMenuEvents}
 * An alternative to using this event on forge is setting a dependency on Steam 'n' Rails and settings ordering to AFTER so your mod loads after Steam 'n' Rails
 */
@SuppressWarnings("JavadocReference")
public interface BogeyMenuManager {
    BogeyMenuManager INSTANCE = new BogeyMenuManagerImpl();

    /**
     * Register a Bogey Category
     * <p>
     * Note: You'll want to hold onto this object as it will be used when adding a bogey to a category later.
     * <p>
     * Example: <pre>
     * {@code registerCategory(Component.translatable("create.bogeymenu.category.standard_bogies", Create.asResource("standard_bogies"))}
     * </pre>
     *
     * @param name The {@link Component#translatable(String)} name of the category
     * @param id The categories id, Must be unique and must-use your modid `create:standard_bogies`
     *
     * @return The {@link CategoryEntry} that has been added.
     */
    CategoryEntry registerCategory(@NotNull Component name, @NotNull ResourceLocation id);

    /**
     * Grab a category with the provided ID
     *
     * @param id The id of the category you are trying to get
     * @return {@link CategoryEntry} or null if the specified category doesn't exist
     */
    @Nullable CategoryEntry getCategoryById(@NotNull ResourceLocation id);

    /**
     * Add a bogey to a category
     *
     * @param categoryEntry The categories entry
     * @param bogeyStyle The Bogey Style instance you are registering
     * @param iconLocation The {@link ResourceLocation} of the bogie icon
     */
    BogeyEntry addToCategory(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation);

    /**
     * Add a bogey to a category
     *
     * @param categoryEntry The categories entry
     * @param bogeyStyle The Bogey Style instance you are registering
     * @param iconLocation The {@link ResourceLocation} of the bogie icon
     * @param scale The scale to render the bogey at in the bogey menu
     */
    BogeyEntry addToCategory(@NotNull CategoryEntry categoryEntry, @NotNull BogeyStyle bogeyStyle, @Nullable ResourceLocation iconLocation, float scale);

    /**
     * Set a custom scale for when rendering this BogeySize in the Bogey Menu
     *
     * @param style The bogey style you want to set this custom scale for
     * @param size The bogey size you want to set this custom scale for
     * @param scale The scale you want this bogey size to render at in the bogey menu
     */
    void setScalesForBogeySizes(BogeyStyle style, BogeySizes.BogeySize size, float scale);
}
