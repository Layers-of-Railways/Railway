package com.railwayteam.railways.registry;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.track_api.TrackMaterial;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static com.railwayteam.railways.track_api.TrackMaterial.make;

public class CRTrackMaterials {
    public static final TrackMaterial
        ANDESITE = make(Create.asResource("andesite"))
        .lang("Andesite")
        .block(() -> AllBlocks.TRACK)
        .particle(Create.asResource("block/palettes/stone_types/polished/andesite_cut_polished"))
        .setBuiltin()
        .build(),
        ACACIA = make(Railways.asResource("acacia"))
            .lang("Acacia")
            .block(() -> CRBlocks.ACACIA_TRACK)
            .particle(new ResourceLocation("block/acacia_planks"))
            .sleeper(Blocks.ACACIA_SLAB)
            .defaultModels()
            .build(),
        BIRCH = make(Railways.asResource("birch"))
            .lang("Birch")
            .block(() -> CRBlocks.BIRCH_TRACK)
            .particle(new ResourceLocation("block/birch_planks"))
            .sleeper(Blocks.BIRCH_SLAB)
            .defaultModels()
            .build(),
        CRIMSON = make(Railways.asResource("crimson"))
            .lang("Crimson")
            .block(() -> CRBlocks.CRIMSON_TRACK)
            .particle(new ResourceLocation("block/crimson_planks"))
            .sleeper(Blocks.CRIMSON_SLAB)
            .rails(Items.GOLD_NUGGET)
            .defaultModels()
            .build(),
        DARK_OAK = make(Railways.asResource("dark_oak"))
            .lang("Dark Oak")
            .block(() -> CRBlocks.DARK_OAK_TRACK)
            .particle(new ResourceLocation("block/dark_oak_planks"))
            .sleeper(Blocks.DARK_OAK_SLAB)
            .defaultModels()
            .build(),
        JUNGLE = make(Railways.asResource("jungle"))
            .lang("Jungle")
            .block(() -> CRBlocks.JUNGLE_TRACK)
            .particle(new ResourceLocation("block/jungle_planks"))
            .sleeper(Blocks.JUNGLE_SLAB)
            .defaultModels()
            .build(),
        OAK = make(Railways.asResource("oak"))
            .lang("Oak")
            .block(() -> CRBlocks.OAK_TRACK)
            .particle(new ResourceLocation("block/oak_planks"))
            .sleeper(Blocks.OAK_SLAB)
            .defaultModels()
            .build(),
        SPRUCE = make(Railways.asResource("spruce"))
            .lang("Spruce")
            .block(() -> CRBlocks.SPRUCE_TRACK)
            .particle(new ResourceLocation("block/spruce_planks"))
            .sleeper(Blocks.SPRUCE_SLAB)
            .defaultModels()
            .build(),
        WARPED = make(Railways.asResource("warped"))
            .lang("Warped")
            .block(() -> CRBlocks.WARPED_TRACK)
            .particle(new ResourceLocation("block/warped_planks"))
            .sleeper(Blocks.WARPED_SLAB)
            .rails(Items.GOLD_NUGGET)
            .defaultModels()
            .build(),
        BLACKSTONE = make(Railways.asResource("blackstone"))
            .lang("Blackstone")
            .block(() -> CRBlocks.BLACKSTONE_TRACK)
            .particle(new ResourceLocation("block/blackstone"))
            .sleeper(Blocks.BLACKSTONE_SLAB)
            .rails(Items.GOLD_NUGGET)
            .defaultModels()
            .build(),
        MANGROVE = make(Railways.asResource("mangrove"))
            .lang("Mangrove")
            .block(() -> CRBlocks.MANGROVE_TRACK)
            .particle(new ResourceLocation("block/mangrove_planks"))
            .sleeper(Blocks.MANGROVE_SLAB)
            .defaultModels()
            .build(),
        MONORAIL = make(Railways.asResource("monorail"))
            .lang("Monorail")
            .block(() -> CRBlocks.MONORAIL_TRACK)
            .particle(Railways.asResource("block/monorail/monorail"))
            .trackType(TrackMaterial.TrackType.MONORAIL)
            .noRecipeGen()
            .customModels(
                () -> () -> new PartialModel(Railways.asResource("block/monorail/monorail/monorail_half")),
                () -> () -> new PartialModel(Railways.asResource("block/empty")),
                () -> () -> new PartialModel(Railways.asResource("block/empty"))
            )
            .build();

    public static void register() {}
}
