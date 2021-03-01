package com.railwayteam.railways;

import com.railwayteam.railways.blocks.WayPointBlock;
import com.railwayteam.railways.entities.SteadyMinecartEntity;
import com.railwayteam.railways.entities.SteadyMinecartRenderer;
import com.railwayteam.railways.entities.TrackEntity;
import com.railwayteam.railways.entities.TrackRenderer;
import com.railwayteam.railways.items.WayPointToolItem;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import com.tterrag.registrate.Registrate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ModSetup {
  public static ItemGroup itemGroup = new ItemGroup(Railways.MODID) {
    @Override
    public ItemStack createIcon() {
            return new ItemStack(Items.MINECART);
        }
  };

  // we cache Registry entries in case other mod components need a convenient reference (including Registrate itself)
  public static BlockEntry<WayPointBlock> R_BLOCK_WAYPOINT;

  public static ItemEntry<WayPointToolItem> R_ITEM_WAYPOINT_TOOL;

  public static RegistryEntry<EntityType<SteadyMinecartEntity>> R_ENTITY_STEADYCART;
  public static RegistryEntry<EntityType<TrackEntity>> R_ENTITY_TRACK;

  public void init() {
  }

  public static void register (Registrate reg) {
    // set item group for the following registry entries
    reg.itemGroup(()->itemGroup, Railways.MODID);

    // right now we're registering a block and an item.
    // TODO: consider splitting into ::registerBlocks and ::registerItems, or even to dedicated files?
    R_BLOCK_WAYPOINT = reg.block(WayPointBlock.name, WayPointBlock::new)         // tell Registrate how to create it
      .properties(p->p.hardnessAndResistance(5.0f, 6.0f))    // set block properties
      .blockstate((ctx,prov) -> prov.simpleBlock(ctx.getEntry(),                 // block state determines the model
        prov.models().getExistingFile(prov.modLoc("block/"+ctx.getName())) // hence why that's tucked in here
      ))
      .simpleItem()     // nothing special about the item right now
      .lang("Waypoint") // give it a friendly name
      .register();      // pack it up for Registrate

    R_ITEM_WAYPOINT_TOOL = reg.item(WayPointToolItem.name, WayPointToolItem::new)
      .lang("Waypoint Tool")
      .register();

    R_ENTITY_STEADYCART = reg.<SteadyMinecartEntity>entity(SteadyMinecartEntity.name, SteadyMinecartEntity::new, EntityClassification.MISC)
      .lang("Steady Minecart")
      .register();
    R_ENTITY_TRACK      = reg.entity(TrackEntity.name, TrackEntity::new, EntityClassification.MISC)
      .lang("Track Segment")
      .register();
  }

  @OnlyIn(value=Dist.CLIENT)
  public static void registerRenderers () {
    RenderingRegistry.registerEntityRenderingHandler(R_ENTITY_STEADYCART.get(), SteadyMinecartRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(R_ENTITY_TRACK.get(), TrackRenderer::new);
  }
}
