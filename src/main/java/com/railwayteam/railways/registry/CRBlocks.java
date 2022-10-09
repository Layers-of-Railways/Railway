package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.content.tender.TenderBlock;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.logistics.trains.track.TrackBlockItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.AllTags.pickaxeOnly;

public class CRBlocks {

  private static final CreateRegistrate REGISTRATE = Railways.registrate();

  private static BlockEntry<CustomTrackBlock> makeTrack(TrackMaterial material) {
    return REGISTRATE.block("track_" + material.resName(), material::create)
        .initialProperties(Material.STONE)
        .properties(p -> p.color(MaterialColor.METAL)
            .strength(0.8F)
            .sound(SoundType.METAL)
            .noOcclusion())
        .addLayer(() -> RenderType::cutoutMipped)
        .transform(pickaxeOnly())
        .blockstate(new CustomTrackBlockStateGenerator()::generate)
        .tag(AllTags.AllBlockTags.RELOCATION_NOT_SUPPORTED.tag)
        .tag(CRTags.AllBlockTags.TRACKS.tag)
        .lang(material.langName + " Train Track")
        .item(TrackBlockItem::new)
        .model((c, p) -> p.generated(c, Railways.asResource("item/track/" + c.getName())))
        .build()
        .register();
  }

  public static final BlockEntry<TenderBlock> BLOCK_TENDER = null;

  public static final BlockEntry<CustomTrackBlock> ACACIA_TRACK = makeTrack(TrackMaterial.ACACIA);
  public static final BlockEntry<CustomTrackBlock> BIRCH_TRACK = makeTrack(TrackMaterial.BIRCH);

  static {
    Railways.LOGGER.info("Acacia track: "+ACACIA_TRACK);
  }

/*
    BLOCK_TENDER = reg.block("tender", TenderBlock::new)
    .blockstate((ctx,prov)->
      prov.getVariantBuilder(ctx.get()).forAllStates(state -> {
        ResourceLocation loc = prov.modLoc("block/tender/" + state.getValue(TenderBlock.SHAPE).getSerializedName());
        return ConfiguredModel.builder().modelFile(prov.models().getExistingFile(loc))
        .rotationY(switch(state.getValue(TenderBlock.FACING)) {
          case SOUTH -> 180;
          case EAST  ->  90;
          case WEST  -> -90;
          default    ->   0;
        })
        .build();
      }))
    .item()
      .model((ctx,prov)-> prov.getExistingFile(prov.modLoc("tender")))
      .build()
    .lang("Tender")
    .addLayer(()-> RenderType::cutoutMipped)
    .register();*/

  public static void register() {}
}
