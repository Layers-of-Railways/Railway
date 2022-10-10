package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import static com.railwayteam.railways.registry.CRTags.NameSpace.MOD;
import static com.simibubi.create.AllTags.optionalTag;

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
    TRACKS

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
      if (optional) {
        tag = optionalTag(ForgeRegistries.BLOCKS, id);
      } else {
        tag = BlockTags.create(id);
      }
      if (alwaysDatagen) {
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(tag));
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
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(tag)
          .add(values));
    }

    public void addOptional(Mods mod, String... ids) {
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> {
        TagsProvider.TagAppender<Block> builder = prov.tag(tag);
        for (String id : ids)
          builder.addOptional(mod.asResource(id));
      });
    }

    public void addOptional(String namespace, String... ids) {
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> {
        TagsProvider.TagAppender<Block> builder = prov.tag(tag);
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
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(tag)
          .addTag(child));
    }
  }

  public static void register() {
    AllBlockTags.TRACKS.addOptional(AllBlocks.TRACK.getId());
  }
}
