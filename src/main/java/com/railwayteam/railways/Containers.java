package com.railwayteam.railways;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.network.IContainerFactory;

public enum Containers {

  SCHEDULE(StationListContainer::new);

  public ContainerType<? extends Container> type;
  private net.minecraft.inventory.container.ContainerType.IFactory<?> factory;

  private <C extends Container> Containers (IContainerFactory<C> factory) {
    this.factory = factory;
  }

  public static void register (RegistryEvent.Register<ContainerType<?>> event) {
    for (Containers container : values()) {
      container.type = new ContainerType<>(container.factory)
        .setRegistryName(Railways.MODID, container.name().toLowerCase());
      event.getRegistry().register(container.type);
    }
  }

  @OnlyIn(Dist.CLIENT)
  public static void registerScreenFactories () {
    bind(SCHEDULE, StationListScreen::new);
  }

  @OnlyIn(Dist.CLIENT)
  @SuppressWarnings("unchecked")
  private static <C extends Container, S extends Screen & IHasContainer<C>> void bind (Containers c, ScreenManager.IScreenFactory<C,S> factory) {
    ScreenManager.registerFactory((ContainerType<C>)c.type, factory);
  }
}
