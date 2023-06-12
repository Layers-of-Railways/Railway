package com.railwayteam.railways.compat.tracks;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.multiloader.CommonTags;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public abstract class TrackCompatUtils {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static BlockEntry<TrackBlock> makeTrack(TrackMaterial material) {
        return makeTrack(material, new CompatTrackBlockStateGenerator()::generate);
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
        List<TagKey<Block>> trackTags = new ArrayList<>();
        trackTags.add(AllTags.AllBlockTags.TRACKS.tag);
        if (material.trackType != CRTrackMaterials.CRTrackType.MONORAIL)
            trackTags.add(AllTags.AllBlockTags.GIRDABLE_TRACKS.tag);
        String owningMod = material.id.getNamespace();
        //noinspection unchecked
        return REGISTRATE.block("track_" + owningMod + "_" + material.resourceName(), material::createBlock)
            .initialProperties(Material.STONE)
            .properties(p -> collectProperties.apply(p)
                .color(MaterialColor.METAL)
                .strength(0.8F)
                .sound(SoundType.METAL)
                .noOcclusion())
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(pickaxeOnly())
            .blockstate(blockstateGen)
            .tag(CommonTags.RELOCATION_NOT_SUPPORTED.forge, CommonTags.RELOCATION_NOT_SUPPORTED.fabric)
            .tag((TagKey<Block>[]) trackTags.toArray(new TagKey[0])) // keep the cast, or stuff breaks
            .lang(material.langName + " Train Track")
            .onRegister(onRegister)
            .item(TrackBlockItem::new)
            .model((c, p) -> p.generated(c, new ResourceLocation(owningMod, "item/track/" + c.getName())))
            .build()
            .register();
    }
}
