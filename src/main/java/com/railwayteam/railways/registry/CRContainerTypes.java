package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxContainer;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxScreen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CRContainerTypes {
  private static final CreateRegistrate REGISTRATE = Railways.registrate();

  public static final MenuEntry<MountedToolboxContainer> MOUNTED_TOOLBOX = register("mounted_toolbox", MountedToolboxContainer::new,
      () -> MountedToolboxScreen::new);

  private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
      String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
    return REGISTRATE
        .menu(name, factory, screenFactory)
        .register();
  }

  public static void register() {}
}
