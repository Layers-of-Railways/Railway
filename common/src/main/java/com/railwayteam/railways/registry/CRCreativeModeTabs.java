package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.ConductorCapItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.block.Block;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class CRCreativeModeTabs {
    @ExpectPlatform
    public static CreativeModeTab getBaseTab() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static CreativeModeTab getCompatTracksTab() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceKey<CreativeModeTab> getBaseTabKey() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceKey<CreativeModeTab> getCompatTracksTabKey() {
        throw new AssertionError();
    }

    public static void register() {
        // just to load class
    }
    
    public static final class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {

        private final boolean mainTab;

        public RegistrateDisplayItemsGenerator(boolean mainTab) {
            this.mainTab = mainTab;
        }
        // TODO later: add SnR-relevant items to these functions
        private static Predicate<Item> makeExclusionPredicate() {
            Set<Item> exclusions = new ReferenceOpenHashSet<>();

            List<ItemProviderEntry<?>> simpleExclusions = List.of(
                AllBlocks.REFINED_RADIANCE_CASING // just as an example
            );

            for (ItemProviderEntry<?> entry : simpleExclusions) {
                exclusions.add(entry.asItem());
            }

            return (item) -> exclusions.contains(item) || item instanceof SequencedAssemblyItem;
        }

        private static List<ItemOrdering> makeOrderings() {
            List<ItemOrdering> orderings = new ReferenceArrayList<>();

            Map<ItemProviderEntry<?>, ItemProviderEntry<?>> simpleBeforeOrderings = Map.of(
                //AllItems.EMPTY_BLAZE_BURNER, AllBlocks.BLAZE_BURNER,
                //AllItems.SCHEDULE, AllBlocks.TRACK_STATION
            );

            Map<ItemProviderEntry<?>, ItemProviderEntry<?>> simpleAfterOrderings = Map.of(
                CRBlocks.CONDUCTOR_WHISTLE_FLAG, CRItems.ITEM_CONDUCTOR_CAP.get(DyeColor.RED),
                CRItems.REMOTE_LENS, CRBlocks.CONDUCTOR_WHISTLE_FLAG,
                CRBlocks.CONDUCTOR_VENT, CRItems.REMOTE_LENS,
                CRBlocks.MANGROVE_TRACK, CRBlocks.SPRUCE_TRACK,
                CRBlocks.CRIMSON_TRACK, CRBlocks.WARPED_TRACK
            );

            simpleBeforeOrderings.forEach((entry, otherEntry) -> {
                orderings.add(ItemOrdering.before(entry.asItem(), otherEntry.asItem()));
            });

            simpleAfterOrderings.forEach((entry, otherEntry) -> {
                orderings.add(ItemOrdering.after(entry.asItem(), otherEntry.asItem()));
            });

            return orderings;
        }

        private static Function<Item, ItemStack> makeStackFunc() {
            Map<Item, Function<Item, ItemStack>> factories = new Reference2ReferenceOpenHashMap<>();

            Map<ItemProviderEntry<?>, Function<Item, ItemStack>> simpleFactories = Map.of(
                AllItems.COPPER_BACKTANK, item -> {
                    ItemStack stack = new ItemStack(item);
                    stack.getOrCreateTag().putInt("Air", BacktankUtil.maxAirWithoutEnchants());
                    return stack;
                },
                AllItems.NETHERITE_BACKTANK, item -> {
                    ItemStack stack = new ItemStack(item);
                    stack.getOrCreateTag().putInt("Air", BacktankUtil.maxAirWithoutEnchants());
                    return stack;
                }
            );

            simpleFactories.forEach((entry, factory) -> {
                factories.put(entry.asItem(), factory);
            });

            return item -> {
                Function<Item, ItemStack> factory = factories.get(item);
                if (factory != null) {
                    return factory.apply(item);
                }
                return new ItemStack(item);
            };
        }

        private static Function<Item, TabVisibility> makeVisibilityFunc() {
            Map<Item, TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();

            Map<ItemProviderEntry<?>, TabVisibility> simpleVisibilities = Map.of(
            AllItems.BLAZE_CAKE_BASE, TabVisibility.SEARCH_TAB_ONLY
            );

            simpleVisibilities.forEach((entry, factory) -> {
                visibilities.put(entry.asItem(), factory);
            });

            for (ItemEntry<ConductorCapItem> entry : CRItems.ITEM_CONDUCTOR_CAP.values()) {
                ConductorCapItem item = entry.get();
                if (item.color != DyeColor.RED) {
                    visibilities.put(item, TabVisibility.SEARCH_TAB_ONLY);
                }
            }

            return item -> {
                TabVisibility visibility = visibilities.get(item);
                if (visibility != null) {
                    return visibility;
                }
                return TabVisibility.PARENT_AND_SEARCH_TABS;
            };
        }

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output output) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            Predicate<Item> exclusionPredicate = makeExclusionPredicate();
            List<ItemOrdering> orderings = makeOrderings();
            Function<Item, ItemStack> stackFunc = makeStackFunc();
            Function<Item, TabVisibility> visibilityFunc = makeVisibilityFunc();
            ResourceKey<CreativeModeTab> tab = mainTab ? getBaseTabKey() : getCompatTracksTabKey();

            List<Item> items = new LinkedList<>();
            items.addAll(collectItems(tab, itemRenderer, true, exclusionPredicate));
            items.addAll(collectBlocks(tab, exclusionPredicate));
            items.addAll(collectItems(tab, itemRenderer, false, exclusionPredicate));

            applyOrderings(items, orderings);
            outputAll(output, items, stackFunc, visibilityFunc);
        }

        private List<Item> collectBlocks(ResourceKey<CreativeModeTab> tab, Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();
            for (RegistryEntry<Block> entry : Railways.registrate().getAll(Registries.BLOCK)) {
                if (!isInCreativeTab(entry, tab))
                    continue;
                Item item = entry.get()
                    .asItem();
                if (item == Items.AIR)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
            return items;
        }

        private List<Item> collectItems(ResourceKey<CreativeModeTab> tab, ItemRenderer itemRenderer, boolean special,
                                        Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();

            if (!mainTab)
                return items;

            for (RegistryEntry<Item> entry : Railways.registrate().getAll(Registries.ITEM)) {
                if (!isInCreativeTab(entry, tab))
                    continue;
                Item item = entry.get();
                if (item instanceof BlockItem)
                    continue;
                BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
                if (model.isGui3d() != special)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            return items;
        }

        @ExpectPlatform
        private static boolean isInCreativeTab(RegistryEntry<?> entry, ResourceKey<CreativeModeTab> tab) {
            throw new AssertionError();
        }

        private static void applyOrderings(List<Item> items, List<ItemOrdering> orderings) {
            for (ItemOrdering ordering : orderings) {
                int anchorIndex = items.indexOf(ordering.anchor());
                if (anchorIndex != -1) {
                    Item item = ordering.item();
                    int itemIndex = items.indexOf(item);
                    if (itemIndex != -1) {
                        items.remove(itemIndex);
                        if (itemIndex < anchorIndex) {
                            anchorIndex--;
                        }
                    }
                    if (ordering.type() == ItemOrdering.Type.AFTER) {
                        items.add(anchorIndex + 1, item);
                    } else {
                        items.add(anchorIndex, item);
                    }
                }
            }
        }

        private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, ItemStack> stackFunc, Function<Item, TabVisibility> visibilityFunc) {
            for (Item item : items) {
                output.accept(stackFunc.apply(item), visibilityFunc.apply(item));
            }
        }

        private record ItemOrdering(Item item, Item anchor, ItemOrdering.Type type) {
            public static ItemOrdering before(Item item, Item anchor) {
                return new ItemOrdering(item, anchor, ItemOrdering.Type.BEFORE);
            }

            public static ItemOrdering after(Item item, Item anchor) {
                return new ItemOrdering(item, anchor, ItemOrdering.Type.AFTER);
            }

            public enum Type {
                BEFORE,
                AFTER;
            }
        }
    }

    public record TabInfo(ResourceKey<CreativeModeTab> key, CreativeModeTab tab) {
    }
}
