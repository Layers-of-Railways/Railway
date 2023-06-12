package com.railwayteam.railways.compat.tracks;

import com.google.common.collect.ImmutableSet;
import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin.AccessorTrackMaterialFactory;
import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackMaterialFactory;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Set;
import java.util.function.Function;

import static com.railwayteam.railways.base.data.CRTagGen.addOptionalTag;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public abstract class TrackCompatUtils {

    public static final Set<String> TRACK_COMPAT_MODS = ImmutableSet.of(
        "hexcasting",
        "byg" // Oh The Biomes You'll Go
    );

    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material) {
        return makeTrack(material, new CompatTrackBlockStateGenerator()::generate);
    }

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, boolean hideInCreativeTabs) {
        return makeTrack(material, new CompatTrackBlockStateGenerator()::generate, (t) -> {}, (p) -> p, hideInCreativeTabs);
    }

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen) {
        return makeTrack(material, blockstateGen, (t) -> {});
    }

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen, NonNullConsumer<? super TrackBlock> onRegister) {
        return makeTrack(material, blockstateGen, onRegister, (p) -> p);
    }

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> collectProperties) {
        return makeTrack(material, blockstateGen, (t) -> {}, collectProperties);
    }

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen, NonNullConsumer<? super TrackBlock> onRegister, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> collectProperties) {
        return makeTrack(material, blockstateGen, onRegister, collectProperties, false);
    }

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, NonNullBiConsumer<DataGenContext<Block, TrackBlock>, RegistrateBlockstateProvider> blockstateGen, NonNullConsumer<? super TrackBlock> onRegister, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> collectProperties, boolean hideInCreativeTabs) {
        String owningMod = material.id.getNamespace();
        String name = "track_" + owningMod + "_" + material.resourceName();

        addOptionalTag(Railways.asResource(name), AllTags.AllBlockTags.TRACKS.tag,
            CommonTags.RELOCATION_NOT_SUPPORTED.forge, CommonTags.RELOCATION_NOT_SUPPORTED.fabric);
        if (material.trackType != CRTrackMaterials.CRTrackType.MONORAIL)
            addOptionalTag(Railways.asResource(name), AllTags.AllBlockTags.GIRDABLE_TRACKS.tag);

        return REGISTRATE.block(name, material::createBlock)
            .initialProperties(Material.STONE)
            .properties(p -> collectProperties.apply(p)
                .color(MaterialColor.METAL)
                .strength(0.8F)
                .sound(SoundType.METAL)
                .noOcclusion())
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .blockstate(blockstateGen)
            .lang(material.langName + " Train Track")
            .onRegister(onRegister)
            .item(TrackBlockItem::new)
            .properties(p -> {
                if (hideInCreativeTabs) //noinspection DataFlowIssue
                    p.tab(null);
                return p;
            })
            .model((c, p) -> p.generated(c, new ResourceLocation(owningMod, "item/track/track_"+material.resourceName())))
            .build()
            .register();
    }

    public static TrackMaterial buildCompatModels(TrackMaterialFactory factory) {
        String namespace = ((AccessorTrackMaterialFactory)factory).getId().getNamespace();
        String path = ((AccessorTrackMaterialFactory)factory).getId().getPath();
        String prefix = "block/track/compat/" + namespace + "/" + path + "/";
        return factory.customModels(
            () -> () -> new PartialModel(Railways.asResource(prefix + "tie")),
            () -> () -> new PartialModel(Railways.asResource(prefix + "segment_left")),
            () -> () -> new PartialModel(Railways.asResource(prefix + "segment_right"))
        ).build();
    }
}
