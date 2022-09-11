package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.Conductor.toolbox.MountedToolboxContainer;
import com.railwayteam.railways.content.Conductor.toolbox.MountedToolboxScreen;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CRContainerTypes {
  public static MenuEntry<MountedToolboxContainer> MOUNTED_TOOLBOX;

  public static void register(Registrate reg) {
    MOUNTED_TOOLBOX = register(reg, "mounted_toolbox", MountedToolboxContainer::new, () -> MountedToolboxScreen::new);
  }

  private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
      Registrate reg, String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
    return reg
        .menu(name, factory, screenFactory)
        .register();
  }
}
