package com.railwayteam.railways;

import com.railwayteam.railways.blocks.*;
import com.railwayteam.railways.entities.SteadyMinecartEntity;
import com.railwayteam.railways.entities.SteadyMinecartRenderer;
import com.railwayteam.railways.items.StationEditorItem;
import com.railwayteam.railways.items.WayPointToolItem;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.entry.TileEntityEntry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import com.tterrag.registrate.Registrate;

import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.ConfiguredModel;
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
  public static BlockEntry<StationSensorRailBlock> R_BLOCK_STATION_SENSOR;
  public static BlockEntry<LargeTrackBlock> R_BLOCK_LARGE_STRAIGHT;
  public static BlockEntry<LargeSwitchTrackBlock> R_BLOCK_LARGE_DIAGONAL;

  public static TileEntityEntry<StationSensorRailTileEntity> R_TE_STATION_SENSOR;

  public static ItemEntry<WayPointToolItem> R_ITEM_WAYPOINT_TOOL;
  public static ItemEntry<StationEditorItem> R_ITEM_STATION_EDITOR_TOOL;

  public static RegistryEntry<EntityType<SteadyMinecartEntity>> R_ENTITY_STEADYCART;

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

    R_BLOCK_LARGE_STRAIGHT = reg.block(LargeTrackBlock.name, LargeTrackBlock::new)
      .properties(p->p.hardnessAndResistance(10.0f, 10.0f).notSolid().doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
        return ConfiguredModel.builder().modelFile(LargeTrackBlock.partialModel(ctx,prov,state.get(LargeTrackBlock.TRACK_SIDE).getName())).build();
      }))
      .item().model((ctx,prov) -> prov.getExistingFile(prov.modLoc("block/wide_gauge/" + ctx.getName() + "_n_s"))).build()
      .lang("Wide Gauge Track")
      .register();

    R_BLOCK_LARGE_DIAGONAL = reg.block(LargeSwitchTrackBlock.name, LargeSwitchTrackBlock::new)
      .properties(p->p.hardnessAndResistance(10.0f, 10.0f).notSolid().doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getExistingVariantBuilder(ctx.getEntry()))
      //.blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStatesExcept(IProperty<I> ignored))
      //.blockstate((ctx,prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
      //  return ConfiguredModel.builder().modelFile(LargeSwitchTrackBlock.partialModel(ctx,prov,state.get(LargeTrackBlock.TRACK_SIDE).getName())).build();
      //}))
      .item().model((ctx,prov) -> prov.getExistingFile(prov.modLoc("block/wide_gauge/" + ctx.getName() + "_n_l"))).build()
      .lang("Wide Gauge Switch")
      .register();

    R_BLOCK_STATION_SENSOR = reg.block(StationSensorRailBlock.name, StationSensorRailBlock::new)
      .initialProperties(()->Blocks.DETECTOR_RAIL)
      .properties(p->p.notSolid().doesNotBlockMovement())
      .blockstate((ctx,prov) -> prov.getExistingVariantBuilder(ctx.getEntry()))
      .item().model((ctx,prov)-> prov.getExistingFile(prov.modLoc("block/" + ctx.getName()))).build()
      .tag(BlockTags.RAILS)
      .lang("Station Sensor")
      .register();

    R_TE_STATION_SENSOR = reg.tileEntity(StationSensorRailTileEntity.NAME, StationSensorRailTileEntity::new)
      .validBlock(()->R_BLOCK_STATION_SENSOR.get())
      .register();

    R_ITEM_WAYPOINT_TOOL = reg.item(WayPointToolItem.name, WayPointToolItem::new)
      .lang("Waypoint Tool")
      .register();

    R_ITEM_STATION_EDITOR_TOOL = reg.item(StationEditorItem.NAME, StationEditorItem::new)
      .lang("Station Editor")
      .register();

    R_ENTITY_STEADYCART = reg.<SteadyMinecartEntity>entity(SteadyMinecartEntity.name, SteadyMinecartEntity::new, EntityClassification.MISC)
      .lang("Steady Minecart")
      .register();
  }

  @OnlyIn(value=Dist.CLIENT)
  public static void registerRenderers () {
    RenderingRegistry.registerEntityRenderingHandler(R_ENTITY_STEADYCART.get(), SteadyMinecartRenderer::new);
  }
}
