package com.railwayteam.railways.compat.tracks;

import com.google.common.collect.ImmutableSet;
import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Config;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.mixin.AccessorTrackMaterialFactory;
import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import static com.railwayteam.railways.base.data.CRTagGen.addOptionalTag;

public abstract class TrackCompatUtils {

    public static final Set<String> TRACK_COMPAT_MODS = ImmutableSet.of(
        "hexcasting",
        "byg", // Oh The Biomes You'll Go,
        "blue_skies",
        "twilightforest",
        "biomesoplenty"
    );

    public static boolean anyLoaded() {
        if (GenericTrackCompat.isDataGen() || Config.REGISTER_MISSING_TRACKS.get())
            return true;
        for (String mod : TRACK_COMPAT_MODS) {
            if (Mods.valueOf(mod.toUpperCase(Locale.ROOT)).isLoaded)
                return true;
        }
        return false;
    }

    @ApiStatus.Internal
    public static boolean mixinIgnoreErrorForMissingItem(ResourceLocation resourceLocation) {
        if (resourceLocation.getNamespace().equals(Railways.MODID)) {
            for (String compatMod : TRACK_COMPAT_MODS) {
                if (resourceLocation.getPath().contains(compatMod)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material) {
        return makeTrack(material, CompatTrackBlockStateGenerator.create()::generate);
    }

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material, boolean hideInCreativeTabs) {
        return makeTrack(material, CompatTrackBlockStateGenerator.create()::generate, (t) -> {}, (p) -> p, hideInCreativeTabs);
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
                CommonTags.RELOCATION_NOT_SUPPORTED.forge, CommonTags.RELOCATION_NOT_SUPPORTED.fabric,
                BlockTags.MINEABLE_WITH_PICKAXE); // pickaxe-mineable tag is moved here as Registrate cannot add optional tag in BlockBuilder
        if (material.trackType != CRTrackMaterials.CRTrackType.MONORAIL)
            addOptionalTag(Railways.asResource(name), AllTags.AllBlockTags.GIRDABLE_TRACKS.tag);

        return REGISTRATE.block(name, material::createBlock)
            .initialProperties(SharedProperties::stone)
            .properties(p -> collectProperties.apply(p)
                .mapColor(MapColor.METAL)
                .strength(0.8F)
                .sound(SoundType.METAL)
                .noOcclusion())
            .addLayer(() -> RenderType::cutoutMipped)
            .blockstate(blockstateGen)
            .lang(material.langName + " Train Track")
            .onRegister(onRegister)
            .onRegister(CreateRegistrate.blockModel(() -> TrackModel::new))
            .onRegister(CRTrackMaterials::addToBlockEntityType)
            .item(TrackBlockItem::new)
            .removeTab(hideInCreativeTabs ? null : CreativeModeTabs.SEARCH)
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
