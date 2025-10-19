package com.railwayteam.railways.registry.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRCreativeModeTabs.RegistrateDisplayItemsGenerator;
import com.railwayteam.railways.registry.CRCreativeModeTabs.TabInfo;
import com.railwayteam.railways.registry.CRCreativeModeTabs.Tabs;
import com.railwayteam.railways.registry.CRPalettes;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;

import java.util.function.Supplier;

import static com.railwayteam.railways.registry.CRItems.ITEM_CONDUCTOR_CAP;

public class CRCreativeModeTabsImpl {

    private static final TabInfo MAIN_TAB = register("main",
        () -> FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.railways"))
            .icon(() -> ITEM_CONDUCTOR_CAP.get(DyeColor.BLUE).asStack())
            .displayItems(new RegistrateDisplayItemsGenerator(Tabs.MAIN))
            .build());

    @SuppressWarnings("Convert2MethodRef")
    private static final TabInfo TRACKS_TAB = register("tracks",
        () -> FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.railways_tracks"))
            .icon(() -> CRBlocks.DARK_OAK_TRACK.asStack())
            .displayItems(new RegistrateDisplayItemsGenerator(Tabs.TRACK))
            .build());

    private static final TabInfo PALETTES_TAB = register("palettes",
            () -> FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.railways_palettes"))
                    .icon(() -> CRPalettes.Styles.BOILER.get(DyeColor.RED).asStack())
                    .displayItems(new RegistrateDisplayItemsGenerator(Tabs.PALETTES))
                    .build());

    public static ResourceKey<CreativeModeTab> getBaseTabKey() {
        return MAIN_TAB.key();
    }

    public static ResourceKey<CreativeModeTab> getTracksTabKey() {
        return TRACKS_TAB.key();
    }

    private static TabInfo register(String name, Supplier<CreativeModeTab> supplier) {
        ResourceLocation id = Railways.asResource(name);
        ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id);
        CreativeModeTab tab = supplier.get();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
        return new TabInfo(key, tab);
    }

    public static ResourceKey<CreativeModeTab> getPalettesTabKey() {
        return PALETTES_TAB.key();
    }
}
