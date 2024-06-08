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

package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.cycle_menu.TagCycleHandlerClient;
import com.railwayteam.railways.content.cycle_menu.TagCycleHandlerServer;
import com.railwayteam.railways.multiloader.Env;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BlockStateBlockItemGroup<C, T extends BlockStateBlockItemGroup.IStyle<C> & Comparable<T>> {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();
    private static final HashMap<ResourceLocation, BlockStateBlockItemGroup<?, ?>> ALL = new HashMap<>();

    private final C context;
    @NotNull private final Property<T> property;
    @NotNull private final T[] values;
    @NotNull private final BlockEntry<?> blockEntry;
    @NotNull private final TagKey<Item> cycleTag;
    @Nullable private final T excluded;
    @Nullable private final String tooltipTranslationKey;

    @NotNull
    private final NonNullUnaryOperator<ItemBuilder<BlockStateBlockItem<T>, CreateRegistrate>> itemTransformer;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<T, ItemEntry<BlockStateBlockItem<T>>> items = new HashMap<>();

    public BlockStateBlockItemGroup(C context, @NotNull Property<T> property, @NotNull T[] values, @NotNull BlockEntry<?> blockEntry,
                                    @NotNull NonNullUnaryOperator<ItemBuilder<BlockStateBlockItem<T>, CreateRegistrate>> itemTransformer,
                                    @NotNull TagKey<Item> cycleTag) {
        this(context, property, values, blockEntry, itemTransformer, cycleTag, null, null);
    }

    public BlockStateBlockItemGroup(C context, @NotNull Property<T> property, @NotNull T[] values, @NotNull BlockEntry<?> blockEntry,
                                    @NotNull NonNullUnaryOperator<ItemBuilder<BlockStateBlockItem<T>, CreateRegistrate>> itemTransformer,
                                    @NotNull TagKey<Item> cycleTag, @Nullable String tooltipTranslationKey) {
        this(context, property, values, blockEntry, itemTransformer, cycleTag, null, tooltipTranslationKey);
    }

    public BlockStateBlockItemGroup(C context, @NotNull Property<T> property, @NotNull T[] values,
                                    @NotNull BlockEntry<?> blockEntry,
                                    @NotNull NonNullUnaryOperator<ItemBuilder<BlockStateBlockItem<T>, CreateRegistrate>> itemTransformer,
                                    @NotNull TagKey<Item> cycleTag, @Nullable T excluded, @Nullable String tooltipTranslationKey) {
        this.context = context;
        this.property = property;
        this.values = values;
        this.blockEntry = blockEntry;
        this.itemTransformer = itemTransformer;
        this.cycleTag = cycleTag;
        this.excluded = excluded;
        this.tooltipTranslationKey = tooltipTranslationKey;

        this.register();

        ALL.put(blockEntry.getId(), this);
    }

    public static BlockStateBlockItemGroup<?, ?> get(ResourceLocation id) {
        return ALL.get(id);
    }

    public ItemEntry<BlockStateBlockItem<T>> get(T value) {
        return items.get(value);
    }

    public Iterable<ItemEntry<BlockStateBlockItem<T>>> getItems() {
        return Arrays.stream(values).map(this::get).toList();
    }

    public void registerDefaultEntry(T value, ItemEntry<BlockStateBlockItem<T>> entry) {
        items.put(value, entry);
    }

    private void register() {
        TagCycleHandlerServer.CYCLE_TRACKER.registerCycle(cycleTag);
        TagCycleHandlerServer.CYCLE_TRACKER.scheduleRecompute();

        Env.CLIENT.runIfCurrent(() -> () -> {
            TagCycleHandlerClient.CYCLE_TRACKER.registerCycle(cycleTag);
            TagCycleHandlerClient.CYCLE_TRACKER.scheduleRecompute();
        });

        String tooltipKey = tooltipTranslationKey == null ? "block.railways.generic_radial" : tooltipTranslationKey;

        boolean primary = true;
        for (T v : values) {
            if (excluded != null && v == excluded) {
                primary = false;
                continue;
            }

            items.put(v, REGISTRATE.item(v.getBlockId(context), BlockStateBlockItem.create(blockEntry::get, property, v, primary))
                .lang(v.getLangName(context))
                .onRegisterAfter(Registries.ITEM, i -> ItemDescription.useKey(i, tooltipKey))
                .transform(itemTransformer)
                .tag(cycleTag)
                .model((c, p) -> p.withExistingParent("item/" + c.getName(), v.getModel(context)))
                .register());
            primary = false;
        }
    }

    public interface IStyle<T> {
        ResourceLocation getModel(T context);

        String getBlockId(T context);

        String getLangName(T context);
    }

    /** Marker interface */
    public interface GroupedBlock {}
}
