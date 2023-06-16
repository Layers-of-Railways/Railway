package com.railwayteam.railways.registry;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.NoCollisionCustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static com.simibubi.create.content.trains.track.TrackMaterialFactory.make;

public class CRTrackMaterials {
    public static final TrackMaterial
        ACACIA = make(Railways.asResource("acacia"))
            .lang("Acacia")
            .block(() -> CRBlocks.ACACIA_TRACK)
            .particle(new ResourceLocation("block/acacia_planks"))
            .sleeper(Blocks.ACACIA_SLAB)
            .standardModels()
            .build(),
        BIRCH = make(Railways.asResource("birch"))
            .lang("Birch")
            .block(() -> CRBlocks.BIRCH_TRACK)
            .particle(new ResourceLocation("block/birch_planks"))
            .sleeper(Blocks.BIRCH_SLAB)
            .standardModels()
            .build(),
        CRIMSON = make(Railways.asResource("crimson"))
            .lang("Crimson")
            .block(() -> CRBlocks.CRIMSON_TRACK)
            .particle(new ResourceLocation("block/crimson_planks"))
            .sleeper(Blocks.CRIMSON_SLAB)
            .rails(Items.GOLD_NUGGET)
            .standardModels()
            .build(),
        DARK_OAK = make(Railways.asResource("dark_oak"))
            .lang("Dark Oak")
            .block(() -> CRBlocks.DARK_OAK_TRACK)
            .particle(new ResourceLocation("block/dark_oak_planks"))
            .sleeper(Blocks.DARK_OAK_SLAB)
            .standardModels()
            .build(),
        JUNGLE = make(Railways.asResource("jungle"))
            .lang("Jungle")
            .block(() -> CRBlocks.JUNGLE_TRACK)
            .particle(new ResourceLocation("block/jungle_planks"))
            .sleeper(Blocks.JUNGLE_SLAB)
            .standardModels()
            .build(),
        OAK = make(Railways.asResource("oak"))
            .lang("Oak")
            .block(() -> CRBlocks.OAK_TRACK)
            .particle(new ResourceLocation("block/oak_planks"))
            .sleeper(Blocks.OAK_SLAB)
            .standardModels()
            .build(),
        SPRUCE = make(Railways.asResource("spruce"))
            .lang("Spruce")
            .block(() -> CRBlocks.SPRUCE_TRACK)
            .particle(new ResourceLocation("block/spruce_planks"))
            .sleeper(Blocks.SPRUCE_SLAB)
            .standardModels()
            .build(),
        WARPED = make(Railways.asResource("warped"))
            .lang("Warped")
            .block(() -> CRBlocks.WARPED_TRACK)
            .particle(new ResourceLocation("block/warped_planks"))
            .sleeper(Blocks.WARPED_SLAB)
            .rails(Items.GOLD_NUGGET)
            .standardModels()
            .build(),
        BLACKSTONE = make(Railways.asResource("blackstone"))
            .lang("Blackstone")
            .block(() -> CRBlocks.BLACKSTONE_TRACK)
            .particle(new ResourceLocation("block/blackstone"))
            .sleeper(Blocks.BLACKSTONE_SLAB)
            .rails(Items.GOLD_NUGGET)
            .standardModels()
            .build(),
        MANGROVE = make(Railways.asResource("mangrove"))
            .lang("Mangrove")
            .block(() -> CRBlocks.MANGROVE_TRACK)
            .particle(new ResourceLocation("block/mangrove_planks"))
            .sleeper(Blocks.MANGROVE_SLAB)
            .standardModels()
            .build(),
        MONORAIL = make(Railways.asResource("monorail"))
            .lang("Monorail")
            .block(() -> CRBlocks.MONORAIL_TRACK)
            .particle(Railways.asResource("block/monorail/monorail"))
            .trackType(CRTrackMaterials.CRTrackType.MONORAIL)
            .noRecipeGen()
            .customModels(
                () -> () -> new PartialModel(Railways.asResource("block/monorail/monorail/monorail_half")),
                () -> () -> new PartialModel(Railways.asResource("block/empty")),
                () -> () -> new PartialModel(Railways.asResource("block/empty"))
            )
            .build(),
        ENDER = make(Railways.asResource("ender"))
            .lang("Ender")
            .block(() -> CRBlocks.ENDER_TRACK)
            .particle(new ResourceLocation("block/end_stone"))
            .sleeper(Blocks.END_STONE_BRICK_SLAB)
            .standardModels()
            .build(),
        TIELESS = make(Railways.asResource("tieless"))
            .lang("Tieless")
            .block(() -> CRBlocks.TIELESS_TRACK)
            .particle(new ResourceLocation("block/glass"))
            .sleeper(Blocks.GLASS_PANE)
            .customBlockFactory(NoCollisionCustomTrackBlock::new)
            .standardModels()
            .build(),
        PHANTOM = make(Railways.asResource("phantom"))
            .lang("Phantom")
            .block(() -> CRBlocks.PHANTOM_TRACK)
            .particle(new ResourceLocation("block/glass"))
            .noRecipeGen()
            .customBlockFactory(NoCollisionCustomTrackBlock::new)
            .standardModels()
            .build()
        ;

    public static class CRTrackType extends TrackMaterial.TrackType {
        public static final TrackMaterial.TrackType MONORAIL = new CRTrackType(Railways.asResource("monorail"), MonorailTrackBlock::new);

        public CRTrackType(ResourceLocation id, TrackBlockFactory factory) {
            super(id, factory);
        }
    }

    public static void register() {}
}
