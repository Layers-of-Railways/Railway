package com.railwayteam.railways.content.buffer;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.palettes.cycle_menu.TagCycleHandlerClient;
import com.railwayteam.railways.content.palettes.cycle_menu.TagCycleHandlerServer;
import com.railwayteam.railways.multiloader.Env;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BlockStateBlockItemGroup<C, T extends BlockStateBlockItemGroup.IStyle<C> & Comparable<T>> {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    private final C context;
    @NotNull
    private final Property<T> property;
    @NotNull
    private final T[] values;
    @NotNull
    private final BlockEntry<?> blockEntry;
    @NotNull
    private final TagKey<Item> cycleTag;

    @Nullable
    private final T excluded;

    @NotNull
    private final NonNullUnaryOperator<ItemBuilder<BlockStateBlockItem<T>, CreateRegistrate>> itemTransformer;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<T, ItemEntry<BlockStateBlockItem<T>>> items = new HashMap<>();

    public BlockStateBlockItemGroup(C context, @NotNull Property<T> property, @NotNull T[] values, @NotNull BlockEntry<?> blockEntry,
                                    @NotNull NonNullUnaryOperator<ItemBuilder<BlockStateBlockItem<T>, CreateRegistrate>> itemTransformer,
                                    @NotNull TagKey<Item> cycleTag) {
        this(context, property, values, blockEntry, itemTransformer, cycleTag, null);
    }

    public BlockStateBlockItemGroup(C context, @NotNull Property<T> property, @NotNull T[] values,
                                    @NotNull BlockEntry<?> blockEntry,
                                    @NotNull NonNullUnaryOperator<ItemBuilder<BlockStateBlockItem<T>, CreateRegistrate>> itemTransformer,
                                    @NotNull TagKey<Item> cycleTag, @Nullable T excluded) {
        this.context = context;
        this.property = property;
        this.values = values;
        this.blockEntry = blockEntry;
        this.itemTransformer = itemTransformer;
        this.cycleTag = cycleTag;
        this.excluded = excluded;

        this.register();
    }

    public ItemEntry<BlockStateBlockItem<T>> get(T value) {
        return items.get(value);
    }

    private void register() {
        TagCycleHandlerServer.CYCLE_TRACKER.registerCycle(cycleTag);
        TagCycleHandlerServer.CYCLE_TRACKER.scheduleRecompute();

        Env.CLIENT.runIfCurrent(() -> () -> {
            TagCycleHandlerClient.CYCLE_TRACKER.registerCycle(cycleTag);
            TagCycleHandlerClient.CYCLE_TRACKER.scheduleRecompute();
        });

        for (T v : values) {
            if (excluded != null && v == excluded) continue;

            items.put(v, REGISTRATE.item(v.getBlockId(context), BlockStateBlockItem.create(blockEntry::get, property, v))
                .lang(v.getLangName(context))
                .transform(itemTransformer)
                .tag(cycleTag)
                .model((c, p) -> p.withExistingParent("item/" + c.getName(), v.getModel(context)))
                .register());
        }
    }

    public interface IStyle<T> {
        ResourceLocation getModel(T context);

        String getBlockId(T context);

        String getLangName(T context);
    }
}
