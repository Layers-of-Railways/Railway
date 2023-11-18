package com.railwayteam.railways.compat.tracks;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.railwayteam.railways.util.TextUtils;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.railwayteam.railways.Railways.registrate;
import static com.railwayteam.railways.compat.tracks.TrackCompatUtils.buildCompatModels;
import static com.railwayteam.railways.compat.tracks.TrackCompatUtils.makeTrack;
import static com.railwayteam.railways.registry.CRItems.ITEM_INCOMPLETE_TRACK;
import static com.simibubi.create.content.trains.track.TrackMaterialFactory.make;

public class GenericTrackCompat {
    public final String modid;

    public GenericTrackCompat(String modid) {
        this.modid = modid;
    }

    protected final Map<String, TrackMaterial> MATERIALS = new HashMap<>();
    protected final Map<String, NonNullSupplier<? extends TrackBlock>> BLOCKS = new HashMap<>();

    public static boolean isDataGen() {
        return Utils.isEnvVarTrue("DATAGEN");
    }

    static {
        Railways.registrate().creativeModeTab(() -> CRItems.tracksCreativeTab, "Create Steam 'n' Rails: Tracks");
    }

    protected static boolean registerTracksAnywayGlobal() {
        return CRConfigs.getRegisterMissingTracks(); // || Utils.isDevEnv();
    }

    protected boolean registerTracksAnyway() {
        return registerTracksAnywayGlobal();
    }

    // If tracks/materials should still be registered if the base block is missing
    protected final boolean shouldRegisterMissing() {
        return isDataGen() || registerTracksAnyway();
    }

    public void register(String... names) {
        for (String name : names) {
            Optional<Block> baseBlock = Registry.BLOCK.getOptional(getSlabLocation(name));
            if (baseBlock.isEmpty()) {
                if (!shouldRegisterMissing()) continue; // skip if we shouldn't register tracks for missing base blocks
                if (isDataGen() || Utils.isDevEnv())
                    Railways.LOGGER.error("Failed to locate base block at "+getSlabLocation(name)+" for "+asResource(name));
            }
            // standard gauge
            TrackMaterial standardMaterial = buildCompatModels(make(asResource(name))
                .lang(langName(name))
                .block(() -> BLOCKS.get(name))
                .particle(asResource("block/track/"+name+"/standard_track_crossing_"+name))
                .sleeper(baseBlock.map(Ingredient::of).orElseGet(() -> SoftIngredient.of(getSlabLocation(name))))
            );
            MATERIALS.put(name, standardMaterial);

            NonNullSupplier<TrackBlock> standardBlock = makeTrack(standardMaterial);
            BLOCKS.put(name, standardBlock);

            ITEM_INCOMPLETE_TRACK.put(standardMaterial, registrate().item("track_incomplete_" + modid + "_" + standardMaterial.resourceName(), SequencedAssemblyItem::new)
                .model((c, p) -> p.generated(c, asResource("item/track_incomplete/track_incomplete_" + standardMaterial.resourceName())))
                .lang("Incomplete " + standardMaterial.langName + " Track")
                .register());

            // wide gauge
            TrackMaterial wideMaterial = wideVariant(standardMaterial);
            MATERIALS.put(name+"_wide", wideMaterial);
            CRTrackMaterials.WIDE_GAUGE.put(standardMaterial, wideMaterial);
            CRTrackMaterials.WIDE_GAUGE_REVERSE.put(wideMaterial, standardMaterial);

            NonNullSupplier<TrackBlock> wideBlock = makeTrack(wideMaterial, new WideGaugeCompatTrackBlockStateGenerator()::generate);
            CRBlocks.WIDE_GAUGE_TRACKS.put(wideMaterial, wideBlock);
            BLOCKS.put(name+"_wide", wideBlock);

            ITEM_INCOMPLETE_TRACK.put(wideMaterial, registrate().item("track_incomplete_" + modid + "_" + wideMaterial.resourceName(), SequencedAssemblyItem::new)
                .model((c, p) -> p.generated(c, asResource("item/track_incomplete/track_incomplete_" + wideMaterial.resourceName())))
                .lang("Incomplete " + wideMaterial.langName + " Track")
                .register());

            // narrow gauge
            TrackMaterial narrowMaterial = narrowVariant(standardMaterial);
            MATERIALS.put(name+"_narrow", narrowMaterial);
            CRTrackMaterials.NARROW_GAUGE.put(standardMaterial, narrowMaterial);
            CRTrackMaterials.NARROW_GAUGE_REVERSE.put(narrowMaterial, standardMaterial);

            NonNullSupplier<TrackBlock> narrowBlock = makeTrack(narrowMaterial, new NarrowGaugeCompatTrackBlockStateGenerator()::generate);
            CRBlocks.NARROW_GAUGE_TRACKS.put(narrowMaterial, narrowBlock);
            BLOCKS.put(name+"_narrow", narrowBlock);

            ITEM_INCOMPLETE_TRACK.put(narrowMaterial, registrate().item("track_incomplete_" + modid + "_" + narrowMaterial.resourceName(), SequencedAssemblyItem::new)
                .model((c, p) -> p.generated(c, asResource("item/track_incomplete/track_incomplete_" + narrowMaterial.resourceName())))
                .lang("Incomplete " + narrowMaterial.langName + " Track")
                .register());
        }
    }

    protected String langName(String name) {
        return TextUtils.titleCaseConversion(name.replace('_', ' '));
    }

    protected ResourceLocation asResource(String path) {
        return new ResourceLocation(modid, path);
    }

    protected ResourceLocation getSlabLocation(String name) {
        return asResource(name+"_slab");
    }

    private TrackMaterial wideVariant(TrackMaterial material) {
        String path = material.id.getPath() + "_wide";
        return buildCompatModels(make(asResource(path))
            .lang("Wide " + material.langName)
            .trackType(CRTrackType.WIDE_GAUGE)
            .block(() -> CRBlocks.WIDE_GAUGE_TRACKS.get(CRTrackMaterials.WIDE_GAUGE.get(material)))
            .particle(material.particle)
            .noRecipeGen());
    }

    private TrackMaterial narrowVariant(TrackMaterial material) {
        String path = material.id.getPath() + "_narrow";
        return buildCompatModels(make(asResource(path))
            .lang("Narrow " + material.langName)
            .trackType(CRTrackType.NARROW_GAUGE)
            .block(() -> CRBlocks.NARROW_GAUGE_TRACKS.get(CRTrackMaterials.NARROW_GAUGE.get(material)))
            .particle(material.particle)
            .noRecipeGen());
    }
}
