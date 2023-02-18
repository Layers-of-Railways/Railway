package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static com.railwayteam.railways.registry.CRTags.NameSpace.MOD;
import static com.railwayteam.railways.util.Utils.builder;

public class CRTags {

  private static final CreateRegistrate REGISTRATE = Railways.registrate();

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
    TRACKS,
    SEMAPHORE_POLES(MOD,MOD.optionalDefault,false),
    TRACK_CASING_BLACKLIST(MOD,MOD.optionalDefault,false),
    ;

    public final TagKey<Block> tag;


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
      tag = optionalTag(Registry.BLOCK, id);
      if (alwaysDatagen) {
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> builder(prov, tag));
      }
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

    public void add(Block... values) {
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> builder(prov, tag)
          .add(values));
    }

    public void addOptional(Mods mod, String... ids) {
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> {
        TagsProvider.TagAppender<Block> builder = builder(prov, tag);
        for (String id : ids)
          builder.addOptional(mod.asResource(id));
      });
    }

    public void addOptional(String namespace, String... ids) {
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> {
        TagsProvider.TagAppender<Block> builder = builder(prov, tag);
        for (String id : ids)
          builder.addOptional(new ResourceLocation(namespace, id));
      });
    }

    public void addOptional(ResourceLocation location) {
      addOptional(location.getNamespace(), location.getPath());
    }

    public void includeIn(TagKey<Block> parent) {
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(parent)
          .addTag(tag));
    }

    public void includeIn(AllTags.AllBlockTags parent) {
      includeIn(parent.tag);
    }

    public void includeAll(TagKey<Block> child) {
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> builder(prov, tag)
          .addTag(child));
    }
  }

  public enum AllItemTags {
    CONDUCTOR_CAPS
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
      tag = optionalTag(Registry.ITEM, id);
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

    public void add(Item... values) {
      REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> builder(prov, tag)
          .add(values));
    }

    public void addOptional(Mods mod, String... ids) {
      REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> {
        TagsProvider.TagAppender<Item> builder = builder(prov, tag);
        for (String id : ids)
          builder.addOptional(mod.asResource(id));
      });
    }

    public void addOptional(String namespace, String... ids) {
      REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> {
        TagsProvider.TagAppender<Item> builder = builder(prov, tag);
        for (String id : ids)
          builder.addOptional(new ResourceLocation(namespace, id));
      });
    }

    public void addOptional(ResourceLocation location) {
      addOptional(location.getNamespace(), location.getPath());
    }

    public void includeIn(TagKey<Item> parent) {
      REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(parent)
          .addTag(tag));
    }

    public void includeIn(AllTags.AllItemTags parent) {
      includeIn(parent.tag);
    }

    public void includeAll(TagKey<Item> child) {
      REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> builder(prov, tag)
          .addTag(child));
    }
  }

  public static <T> TagKey<T> optionalTag(Registry<T> registry, ResourceLocation id) {
    return TagKey.create(registry.key(), id);
  }

  public static void register() {
    AllBlockTags.TRACKS.addOptional(AllBlocks.TRACK.getId());
  }
}
