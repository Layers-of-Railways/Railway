package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.client.gui.screen.GrindstoneScreen;
import net.minecraft.client.gui.screen.HopperScreen;
import net.minecraft.client.gui.screen.LecternScreen;
import net.minecraft.client.gui.screen.LoomScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.BeaconScreen;
import net.minecraft.client.gui.screen.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screen.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.DispenserScreen;
import net.minecraft.client.gui.screen.inventory.FurnaceScreen;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.inventory.SmokerScreen;
import net.minecraft.client.gui.screen.inventory.StonecutterScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenManager {
   private static final Logger LOG = LogManager.getLogger();
   private static final Map<ContainerType<?>, ScreenManager.IScreenFactory<?, ?>> FACTORIES = Maps.newHashMap();

   public static <T extends Container> void openScreen(@Nullable ContainerType<T> type, Minecraft mc, int windowId, ITextComponent title) {
      getScreenFactory(type, mc, windowId, title).ifPresent(f -> f.createScreen(title, type, mc, windowId));
   }

   public static <T extends Container> java.util.Optional<IScreenFactory<T, ?>> getScreenFactory(@Nullable ContainerType<T> type, Minecraft mc, int windowId, ITextComponent title) {
      if (type == null) {
         LOG.warn("Trying to open invalid screen with name: {}", (Object)title.getString());
      } else {
         ScreenManager.IScreenFactory<T, ?> iscreenfactory = getFactory(type);
         if (iscreenfactory == null) {
            LOG.warn("Failed to create screen for menu type: {}", (Object)Registry.MENU.getKey(type));
         } else {
            return java.util.Optional.of(iscreenfactory);
         }
      }
      return java.util.Optional.empty();
   }

   @Nullable
   private static <T extends Container> ScreenManager.IScreenFactory<T, ?> getFactory(ContainerType<T> type) {
      return (ScreenManager.IScreenFactory<T, ?>)FACTORIES.get(type);
   }

   public static <M extends Container, U extends Screen & IHasContainer<M>> void registerFactory(ContainerType<? extends M> type, ScreenManager.IScreenFactory<M, U> factory) {
      ScreenManager.IScreenFactory<?, ?> iscreenfactory = FACTORIES.put(type, factory);
      if (iscreenfactory != null) {
         throw new IllegalStateException("Duplicate registration for " + Registry.MENU.getKey(type));
      }
   }

   public static boolean isMissingScreen() {
      boolean flag = false;

      for(ContainerType<?> containertype : Registry.MENU) {
         if (!FACTORIES.containsKey(containertype)) {
            LOG.debug("Menu {} has no matching screen", (Object)Registry.MENU.getKey(containertype));
            flag = true;
         }
      }

      return flag;
   }

   static {
      registerFactory(ContainerType.GENERIC_9X1, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X2, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X3, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X4, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X5, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_9X6, ChestScreen::new);
      registerFactory(ContainerType.GENERIC_3X3, DispenserScreen::new);
      registerFactory(ContainerType.ANVIL, AnvilScreen::new);
      registerFactory(ContainerType.BEACON, BeaconScreen::new);
      registerFactory(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
      registerFactory(ContainerType.BREWING_STAND, BrewingStandScreen::new);
      registerFactory(ContainerType.CRAFTING, CraftingScreen::new);
      registerFactory(ContainerType.ENCHANTMENT, EnchantmentScreen::new);
      registerFactory(ContainerType.FURNACE, FurnaceScreen::new);
      registerFactory(ContainerType.GRINDSTONE, GrindstoneScreen::new);
      registerFactory(ContainerType.HOPPER, HopperScreen::new);
      registerFactory(ContainerType.LECTERN, LecternScreen::new);
      registerFactory(ContainerType.LOOM, LoomScreen::new);
      registerFactory(ContainerType.MERCHANT, MerchantScreen::new);
      registerFactory(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
      registerFactory(ContainerType.SMOKER, SmokerScreen::new);
      registerFactory(ContainerType.field_226625_v_, CartographyTableScreen::new);
      registerFactory(ContainerType.STONECUTTER, StonecutterScreen::new);
   }

   @OnlyIn(Dist.CLIENT)
   public interface IScreenFactory<T extends Container, U extends Screen & IHasContainer<T>> {
      default void createScreen(ITextComponent title, ContainerType<T> type, Minecraft mc, int windowId) {
         U u = this.create(type.create(windowId, mc.player.inventory), mc.player.inventory, title);
         mc.player.openContainer = ((IHasContainer)u).getContainer();
         mc.displayGuiScreen(u);
      }

      U create(T p_create_1_, PlayerInventory p_create_2_, ITextComponent p_create_3_);
   }
}