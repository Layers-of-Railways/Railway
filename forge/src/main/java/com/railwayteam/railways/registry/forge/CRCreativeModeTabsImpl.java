/*
 * Steam 'n' Rails
 * Copyright (c) 2024 The Railways Team
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

package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRCreativeModeTabs.RegistrateDisplayItemsGenerator;
import com.railwayteam.railways.registry.CRCreativeModeTabs.Tabs;
import com.railwayteam.railways.registry.CRPalettes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.railwayteam.railways.registry.CRItems.ITEM_CONDUCTOR_CAP;

@EventBusSubscriber(bus = Bus.MOD)
public class CRCreativeModeTabsImpl {

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Railways.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TAB_REGISTER.register("main",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.railways"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> ITEM_CONDUCTOR_CAP.get(DyeColor.BLUE).asStack())
            .displayItems(new RegistrateDisplayItemsGenerator(Tabs.MAIN))
            .build());

    public static final RegistryObject<CreativeModeTab> TRACKS_TAB = TAB_REGISTER.register("tracks",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.railways_tracks"))
            .withTabsBefore(MAIN_TAB.getKey())
            .icon(CRBlocks.DARK_OAK_TRACK::asStack)
            .displayItems(new RegistrateDisplayItemsGenerator(Tabs.TRACK))
            .build());

    public static final RegistryObject<CreativeModeTab> PALETTES_TAB = TAB_REGISTER.register("palettes",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.railways_palettes"))
            .withTabsBefore(TRACKS_TAB.getKey())
            .icon(() -> CRPalettes.Styles.BOILER.get(DyeColor.RED).asStack())
            .displayItems(new RegistrateDisplayItemsGenerator(Tabs.PALETTES))
            .build());

    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }

    public static ResourceKey<CreativeModeTab> getBaseTabKey() {
        return MAIN_TAB.getKey();
    }

    public static ResourceKey<CreativeModeTab> getTracksTabKey() {
        return TRACKS_TAB.getKey();
    }

    public static ResourceKey<CreativeModeTab> getPalettesTabKey() {
        return PALETTES_TAB.getKey();
    }
}
