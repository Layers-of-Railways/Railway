package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.Conductor.ConductorCapItem;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.content.contraptions.itemAssembly.SequencedAssemblyItem;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.LazySpawnEggItem;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

public class CRItems {
  public static final CreativeModeTab itemGroup = new CreativeModeTab(Railways.MODID) {
    @Override
    @Nonnull
    public ItemStack makeIcon() { return ITEM_CONDUCTOR_CAP.get(DyeColor.BLUE).asStack(); }
  };

  public static TagKey<Item> CONDUCTOR_CAPS = makeItemTag(Railways.MODID, "conductor_caps");

  public static TagKey<Item> makeForgeItemTag (String path) {
    return makeItemTag("forge", path);
  }

  public static TagKey<Item> makeItemTag (String mod, String path) {
    return ForgeRegistries.ITEMS.tags().createOptionalTagKey(new ResourceLocation(mod, path), Collections.emptySet());
  }

  private static ItemBuilder<? extends Item, ?> makeMinecart (Registrate reg, String name, Supplier<EntityEntry<?>> entity, Color primary) {
    return reg.item(name, (props)-> new LazySpawnEggItem<>(entity.get(), primary.getRGB(), Color.BLACK.getRGB(), props))
    .model((ctx,prov)-> prov.withExistingParent(name, prov.mcLoc("item/minecart")).texture("layer0", prov.modLoc("item/" + name)));
  }

  public static Item woolByColor (DyeColor color) {
    return switch (color) {
      case WHITE -> Items.WHITE_WOOL;
      case ORANGE -> Items.ORANGE_WOOL;
      case MAGENTA -> Items.MAGENTA_WOOL;
      case LIGHT_BLUE -> Items.LIGHT_BLUE_WOOL;
      case YELLOW -> Items.YELLOW_WOOL;
      case LIME -> Items.LIME_WOOL;
      case PINK -> Items.PINK_WOOL;
      case GRAY -> Items.GRAY_WOOL;
      case LIGHT_GRAY -> Items.LIGHT_GRAY_WOOL;
      case CYAN -> Items.CYAN_WOOL;
      case PURPLE -> Items.PURPLE_WOOL;
      case BLUE -> Items.BLUE_WOOL;
      case BROWN -> Items.BROWN_WOOL;
      case GREEN -> Items.GREEN_WOOL;
      case RED -> Items.RED_WOOL;
      case BLACK -> Items.BLACK_WOOL;
    };
  }

  public static ItemEntry<? extends Item> ITEM_BENCHCART;
  public static ItemEntry<? extends Item> ITEM_JUKEBOXCART;
  public static ItemEntry<? extends Item> ITEM_STEAMCART;

  public static HashMap<DyeColor, ItemEntry<ConductorCapItem>> ITEM_CONDUCTOR_CAP;
  public static HashMap<DyeColor, ItemEntry<SequencedAssemblyItem>> ITEM_INCOMPLETE_CONDUCTOR_CAP;

  public static void register(Registrate reg) {
    reg.creativeModeTab(() -> itemGroup, "Create Railways");

//    ITEM_BENCHCART = makeMinecart(reg, "benchcart", ()->CREntities.CART_BLOCK, Color.YELLOW)
//      .recipe((ctx,prov)-> ShapelessRecipeBuilder.shapeless(ctx.get()).requires(Items.MINECART).requires(Items.CRAFTING_TABLE)
//        .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov))
//      .lang("Minecart with Workbench")
//      .register();

//    ITEM_JUKEBOXCART = makeMinecart(reg, "jukeboxcart", ()->CREntities.CART_JUKEBOX, Color.RED)
//      .recipe((ctx,prov)-> ShapelessRecipeBuilder.shapeless(ctx.get()).requires(Items.MINECART).requires(Items.JUKEBOX)
//        .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov))
//      .lang("Minecart with Jukebox")
//      .register();

//    ITEM_STEAMCART = makeMinecart(reg, "steamcart", ()->CREntities.CART_STEAM, Color.ORANGE)
//      .recipe((ctx,prov)-> ShapedRecipeBuilder.shaped(ctx.get())
//        .pattern("ctp")
//        .pattern(" u ")
//        .define('c', AllBlocks.COGWHEEL.get())
//        .define('t', AllBlocks.FLUID_TANK.get())
//        .define('p', AllItems.COPPER_SHEET.get())
//        .define('u', Items.MINECART)
//        .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov)
//      )
//      .lang("Steam-Powered Minecart").register();

    ITEM_CONDUCTOR_CAP = new HashMap<>();
    ITEM_INCOMPLETE_CONDUCTOR_CAP = new HashMap<>();
    for (DyeColor color : DyeColor.values()) {
      String colorName = TextUtils.titleCaseConversion(color.getName().replace("_", " "));
      String colorReg  = color.getName().toLowerCase(Locale.ROOT);
      ITEM_INCOMPLETE_CONDUCTOR_CAP.put(color, reg.item(colorReg + "_incomplete_conductor_cap", SequencedAssemblyItem::new)
          .model(((dataGenContext, itemModelProvider) -> {
            itemModelProvider.withExistingParent(colorReg + "_incomplete_conductor_cap", itemModelProvider.modLoc("item/incomplete_conductor_cap"))
                .texture("cap", itemModelProvider.modLoc("entity/caps/" + colorReg + "_conductor_cap"));
          }))
              .lang("Incomplete " + colorName + " Conductor's Cap")
          .register());
      ITEM_CONDUCTOR_CAP.put(color, reg.item(colorReg + "_conductor_cap", p-> new ConductorCapItem(p, color))
        .model(((dataGenContext, itemModelProvider) -> {
          itemModelProvider.withExistingParent(colorReg + "_conductor_cap", itemModelProvider.modLoc("item/conductor_cap"))
              .texture("cap", itemModelProvider.modLoc("entity/caps/" + colorReg + "_conductor_cap"));
        }))
        .lang(colorName + " Conductor's Cap")
        .tag(CONDUCTOR_CAPS)
        .properties(p -> p.stacksTo(1))
        .recipe((ctx, prov)-> {
            /*ShapedRecipeBuilder.shaped(ctx.get())
              .pattern("www")
              .pattern("w w")
              .define('w', woolByColor(color))
              .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(woolByColor(color)))
              .save(prov);*/
            ShapelessRecipeBuilder.shapeless(ctx.get())
              .requires(CONDUCTOR_CAPS)
              .requires(color.getTag())
              .unlockedBy("hasitem", RegistrateRecipeProvider.has(CONDUCTOR_CAPS))
              .save(prov, new ResourceLocation(Railways.MODID, "dying_existing_cap_" + colorReg));
        })
        .register());
    }
  }

  private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(Registrate reg, String name) {
    return reg.item(name, SequencedAssemblyItem::new)
        .register();
  }
}
