package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

import static com.railwayteam.railways.registry.CRTags.NameSpace.MOD;

public class CRTags {
  public enum NameSpace {

    MOD(Railways.MODID, false, true), FORGE("forge")

    ;

    public final String id;
    public final boolean optionalDefault;
    public final boolean alwaysDatagenDefault;

    NameSpace(String id) {
      this(id, true, false);
    }

    NameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
      this.id = id;
      this.optionalDefault = optionalDefault;
      this.alwaysDatagenDefault = alwaysDatagenDefault;
    }

  }

  public enum AllBlockTags {
    SEMAPHORE_POLES,
    TRACK_CASING_BLACKLIST(MOD, MOD.optionalDefault,false),
    CONDUCTOR_SPY_USABLE(MOD, MOD.optionalDefault,false), // so other mods / datapacks can make more blocks usable for conductor spies
    LOCOMETAL,
    LOCOMETAL_BOILERS
    ;

    public final TagKey<Block> tag;
    public final boolean alwaysDatagen;


    AllBlockTags() {
      this(MOD);
    }

    AllBlockTags(NameSpace namespace) {
      this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    }

    AllBlockTags(NameSpace namespace, String path) {
      this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    }

    AllBlockTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
      this(namespace, null, optional, alwaysDatagen);
    }

    AllBlockTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
      ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
      if (optional) {
        tag = optionalTag(Registry.BLOCK, id);
      } else {
        tag = TagKey.create(Registry.BLOCK_REGISTRY, id);
      }
      this.alwaysDatagen = alwaysDatagen;
    }

    @SuppressWarnings("deprecation")
    public boolean matches(Block block) {
      return block.builtInRegistryHolder()
          .is(tag);
    }

    public boolean matches(ItemStack stack) {
      return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
    }

    public boolean matches(BlockState state) {
      return state.is(tag);
    }

    public static void register() { }
  }

  public enum AllItemTags {
    CONDUCTOR_CAPS,
    PHANTOM_TRACK_REVEALING,
    DECO_COUPLERS,
    WOODEN_HEADSTOCKS,
    COPYCAT_HEADSTOCKS,

    CABOOSESTYLE_STACK,
    LONG_STACK,
    COALBURNER_STACK,
    OILBURNER_STACK,
    STREAMLINED_STACK,
    WOODBURNER_STACK,

    NOT_TRAIN_FUEL
    ;

    public final TagKey<Item> tag;
    public final boolean alwaysDatagen;

    AllItemTags() {
      this(NameSpace.MOD);
    }

    AllItemTags(NameSpace namespace) {
      this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    }

    AllItemTags(NameSpace namespace, String path) {
      this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
    }

    AllItemTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
      this(namespace, null, optional, alwaysDatagen);
    }

    AllItemTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
      ResourceLocation id = new ResourceLocation(namespace.id, path == null ? Lang.asId(name()) : path);
      if (optional) {
        tag = optionalTag(Registry.ITEM, id);
      } else {
        tag = TagKey.create(Registry.ITEM_REGISTRY, id);
      }
      this.alwaysDatagen = alwaysDatagen;
    }

    @SuppressWarnings("deprecation")
    public boolean matches(Item item) {
      return item.builtInRegistryHolder()
          .is(tag);
    }

    public boolean matches(ItemStack stack) {
      return stack.is(tag);
    }

    public static void register() { }
  }

  public static <T> TagKey<T> optionalTag(Registry<T> registry, ResourceLocation id) {
    return TagKey.create(registry.key(), id);
  }

  // load all classes
  public static void register() {
    AllBlockTags.register();
    AllItemTags.register();
  }

  public static void provideLangEntries(BiConsumer<String, String> consumer) {
    for (AllBlockTags blockTag : AllBlockTags.values()) {
      ResourceLocation loc = blockTag.tag.location();
      consumer.accept("tag.block." + loc.getNamespace() + "." + loc.getPath().replace('/', '.'),
          TextUtils.titleCaseConversion(blockTag.name()).replace('_', ' '));
    }

    for (AllItemTags itemTag : AllItemTags.values()) {
      ResourceLocation loc = itemTag.tag.location();
      consumer.accept("tag.item." + loc.getNamespace() + "." + loc.getPath().replace('/', '.'),
          TextUtils.titleCaseConversion(itemTag.name().replace('_', ' ')));
    }
    consumer.accept("tag.item.forge.string", "String");
  }
}
