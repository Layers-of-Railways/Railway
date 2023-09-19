package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.ConductorCapItem;
import com.railwayteam.railways.content.conductor.remote_lens.RemoteLensItem;
import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.util.ItemUtils;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.*;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CRItems {
  private static final CreateRegistrate REGISTRATE = Railways.registrate();
  public static final CreativeModeTab mainCreativeTab = new CreativeModeTab(ItemUtils.nextTabId(), Railways.MODID) {
    @Override
    @Nonnull
    public ItemStack makeIcon() { return ITEM_CONDUCTOR_CAP.get(DyeColor.BLUE).asStack(); }
  };

  public static final CreativeModeTab tracksCreativeTab = new CreativeModeTab(ItemUtils.nextTabId(), Railways.MODID+"_tracks") {
    @Override
    @Nonnull
    public ItemStack makeIcon() { return CRBlocks.DARK_OAK_TRACK.asStack(); }
  };

  public static final TagKey<Item> CONDUCTOR_CAPS = CRTags.AllItemTags.CONDUCTOR_CAPS.tag;//makeItemTag(Railways.MODID, "conductor_caps");

  public static TagKey<Item> makeForgeItemTag(String path) {
    return makeItemTag("forge", path);
  }

  public static TagKey<Item> makeItemTag(String mod, String path) {
    return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(mod, path));
  }

  private static ItemBuilder<? extends Item, ?> makeMinecart(String name, AbstractMinecart.Type type) {
    return REGISTRATE.item(name, (props) -> new MinecartItem(type, props))
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

  public static final ItemEntry<? extends Item> ITEM_BENCHCART = makeMinecart("benchcart", MinecartWorkbench.TYPE)
      .recipe((ctx,prov)-> ShapelessRecipeBuilder.shapeless(ctx.get()).requires(Items.MINECART).requires(Items.CRAFTING_TABLE)
        .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov))
      .lang("Minecart with Workbench")
      .register();
  public static final ItemEntry<? extends Item> ITEM_JUKEBOXCART = makeMinecart("jukeboxcart", MinecartJukebox.TYPE)
      .recipe((ctx,prov)-> ShapelessRecipeBuilder.shapeless(ctx.get()).requires(Items.MINECART).requires(Items.JUKEBOX)
          .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov))
      .lang("Minecart with Jukebox")
      .register();

  public static final ItemEntry<? extends RemoteLensItem> REMOTE_LENS = REGISTRATE.item("remote_lens", RemoteLensItem::new)
      .lang("Remote Lens")
      .register();

  public static final EnumMap<DyeColor, ItemEntry<ConductorCapItem>> ITEM_CONDUCTOR_CAP = new EnumMap<>(DyeColor.class);
  public static final EnumMap<DyeColor, ItemEntry<SequencedAssemblyItem>> ITEM_INCOMPLETE_CONDUCTOR_CAP = new EnumMap<>(DyeColor.class);

  public static final Map<TrackMaterial, ItemEntry<SequencedAssemblyItem>> ITEM_INCOMPLETE_TRACK = new HashMap<>();

  static {
    for (DyeColor color : DyeColor.values()) {
      String colorName = TextUtils.titleCaseConversion(color.getName().replace("_", " "));
      String colorReg  = color.getName().toLowerCase(Locale.ROOT);
      ITEM_INCOMPLETE_CONDUCTOR_CAP.put(color, REGISTRATE.item(colorReg + "_incomplete_conductor_cap", SequencedAssemblyItem::new)
          .model(((dataGenContext, itemModelProvider) -> itemModelProvider.withExistingParent(colorReg + "_incomplete_conductor_cap", itemModelProvider.modLoc("item/incomplete_conductor_cap"))
              .texture("cap", itemModelProvider.modLoc("entity/caps/" + colorReg + "_conductor_cap"))))
              .lang("Incomplete " + colorName + " Conductor's Cap")
          .register());
      ITEM_CONDUCTOR_CAP.put(color, REGISTRATE.item(colorReg + "_conductor_cap", p-> ConductorCapItem.create(p, color))
        .model(((dataGenContext, itemModelProvider) -> itemModelProvider.withExistingParent(colorReg + "_conductor_cap", itemModelProvider.modLoc("item/conductor_cap"))
            .texture("cap", itemModelProvider.modLoc("entity/caps/" + colorReg + "_conductor_cap"))))
        .lang(colorName + " Conductor's Cap")
        .tag(CONDUCTOR_CAPS)
        .properties(p -> p.stacksTo(1))
        .recipe((ctx, prov)-> ShapelessRecipeBuilder.shapeless(ctx.get())
            .requires(CONDUCTOR_CAPS)
            .requires(CommonTags.DYES.get(color).tag)
            .unlockedBy("hasitem", RegistrateRecipeProvider.has(CONDUCTOR_CAPS))
            .save(prov, new ResourceLocation(Railways.MODID, "dying_existing_cap_" + colorReg)))
        .register());
    }

    for (TrackMaterial material : TrackMaterial.allFromMod(Railways.MODID)) {
      ITEM_INCOMPLETE_TRACK.put(material, REGISTRATE.item("track_incomplete_" + material.resourceName(), SequencedAssemblyItem::new)
          .model((c, p) -> p.generated(c, Railways.asResource("item/track_incomplete/" + c.getName())))
          .lang("Incomplete " + material.langName + " Track")
          .register());
    }
  }

  private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
    return REGISTRATE.item(name, SequencedAssemblyItem::new)
        .register();
  }

  @SuppressWarnings("EmptyMethod")
  public static void register() {}
}
