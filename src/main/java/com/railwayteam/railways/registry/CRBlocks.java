package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.data.BuilderTransformers;
import com.railwayteam.railways.content.coupling.TrackCouplerDisplaySource;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlockItem;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlockStateGenerator;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailBlockStateGenerator;
import com.railwayteam.railways.content.semaphore.SemaphoreBlock;
import com.railwayteam.railways.content.semaphore.SemaphoreItem;
import com.railwayteam.railways.content.tender.TenderBlock;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.logistics.trains.track.TrackBlockItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import static com.simibubi.create.content.logistics.block.display.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class CRBlocks {

    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    private static BlockEntry<CustomTrackBlock> makeTrack(TrackMaterial material) {
        return makeTrack(material, new CustomTrackBlockStateGenerator()::generate);
    }

    private static BlockEntry<CustomTrackBlock> makeTrack(TrackMaterial material,
                                                          NonNullBiConsumer<DataGenContext<Block, CustomTrackBlock>, RegistrateBlockstateProvider> blockstateGen) {
        return REGISTRATE.block("track_" + material.resName(), material::create)
            .initialProperties(Material.STONE)
            .properties(p -> p.color(MaterialColor.METAL)
                .strength(0.8F)
                .sound(SoundType.METAL)
                .noOcclusion())
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .blockstate(blockstateGen)
            .tag(AllTags.AllBlockTags.RELOCATION_NOT_SUPPORTED.tag)
            .tag(CRTags.AllBlockTags.TRACKS.tag)
            .lang(material.langName + " Train Track")
            .item(TrackBlockItem::new)
            .model((c, p) -> p.generated(c, Railways.asResource("item/track/" + c.getName())))
            .build()
            .register();
    }

    public static final BlockEntry<TenderBlock> BLOCK_TENDER = null;
    static {
        REGISTRATE.startSection(AllSections.LOGISTICS);
    }
    public static final BlockEntry<SemaphoreBlock> SEMAPHORE = REGISTRATE.block("semaphore", SemaphoreBlock::new)
        .initialProperties(SharedProperties::softMetal)
        //.blockstate((ctx,prov)->prov.horizontalBlock(ctx.get(), blockState -> prov.models()
        //.getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block"))))
        .blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry())
            .forAllStates(state -> ConfiguredModel.builder()
                .modelFile(prov.models().getExistingFile(prov.modLoc(
                    "block/semaphore/block" +
                        (state.getValue(SemaphoreBlock.FULL) ?"_full":"") +
                        (state.getValue(SemaphoreBlock.FLIPPED) ?"_flipped":"") +
                        (state.getValue(SemaphoreBlock.UPSIDE_DOWN) ?"_down":""))))
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build()
            )
        )
        .properties(p -> p.color(MaterialColor.COLOR_GRAY))
        .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
        .item(SemaphoreItem::new).transform(customItemModel())
        .transform(axeOnly())
        .addLayer(() -> RenderType::translucent)
        .register();

    public static final BlockEntry<TrackCouplerBlock> TRACK_COUPLER =
        REGISTRATE.block("track_coupler", TrackCouplerBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .properties(p -> p.noOcclusion())
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .blockstate((c, p) -> {
                p.getVariantBuilder(c.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(AssetLookup.partialBaseModel(c, p, state.getValue(TrackCouplerBlock.MODE).getSerializedName()))
                    .build(), TrackCouplerBlock.POWERED);
            })
            .transform(pickaxeOnly())
            .onRegister(assignDataBehaviour(new TrackCouplerDisplaySource(), "track_coupler_info"))
            .lang("Train Coupler")
            .item(TrackCouplerBlockItem.ofType(CREdgePointTypes.COUPLER))
            .transform(customItemModel("_", "block_both"))
            .register();

    public static final BlockEntry<CustomTrackBlock> ACACIA_TRACK = makeTrack(TrackMaterial.ACACIA);
    public static final BlockEntry<CustomTrackBlock> BIRCH_TRACK = makeTrack(TrackMaterial.BIRCH);
    public static final BlockEntry<CustomTrackBlock> CRIMSON_TRACK = makeTrack(TrackMaterial.CRIMSON);
    public static final BlockEntry<CustomTrackBlock> DARK_OAK_TRACK = makeTrack(TrackMaterial.DARK_OAK);
    public static final BlockEntry<CustomTrackBlock> JUNGLE_TRACK = makeTrack(TrackMaterial.JUNGLE);
    public static final BlockEntry<CustomTrackBlock> OAK_TRACK = makeTrack(TrackMaterial.OAK);
    public static final BlockEntry<CustomTrackBlock> SPRUCE_TRACK = makeTrack(TrackMaterial.SPRUCE);
    public static final BlockEntry<CustomTrackBlock> WARPED_TRACK = makeTrack(TrackMaterial.WARPED);
    public static final BlockEntry<CustomTrackBlock> BLACKSTONE_TRACK = makeTrack(TrackMaterial.BLACKSTONE);
    public static final BlockEntry<CustomTrackBlock> MANGROVE_TRACK = makeTrack(TrackMaterial.MANGROVE);
    public static final BlockEntry<CustomTrackBlock> MONORAIL_TRACK = makeTrack(TrackMaterial.MONORAIL, new MonorailBlockStateGenerator()::generate);

    public static final BlockEntry<MonoBogeyBlock> MONO_BOGEY =
        REGISTRATE.block("mono_bogey", p -> new MonoBogeyBlock(p, false))
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.monobogey(false))
            .lang("Monorail Bogey")
            .register();

    public static final BlockEntry<MonoBogeyBlock> MONO_BOGEY_UPSIDE_DOWN =
        REGISTRATE.block("mono_bogey_upside_down", p -> new MonoBogeyBlock(p, true))
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BuilderTransformers.monobogey(true))
            .lang("Upside Down Monorail Bogey")
            .register();

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

    @SuppressWarnings("EmptyMethod")
    public static void register() {}
}
