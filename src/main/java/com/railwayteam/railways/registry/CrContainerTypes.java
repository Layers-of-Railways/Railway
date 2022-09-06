package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.Conductor.toolbox.MountedToolboxContainer;
import com.railwayteam.railways.content.Conductor.toolbox.MountedToolboxScreen;
import com.simibubi.create.Create;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.builders.MenuBuilder;
import com.simibubi.create.repack.registrate.util.entry.MenuEntry;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CrContainerTypes {
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
