package com.railwayteam.railways.track_api;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.CustomTrackBlock;
import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackBlock;
import com.railwayteam.railways.mixin.AccessorBlockEntityType;
import com.railwayteam.railways.multiloader.Env;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class TrackMaterial {

    public static final List<TrackMaterial> ALL = new ArrayList<>();

    public static final TrackMaterial
        ANDESITE = make(Create.asResource("andesite"))
            .lang("Andesite")
            .block(() -> AllBlocks.TRACK)
            .particle(Create.asResource("block/palettes/stone_types/polished/andesite_cut_polished"))
            .setBuiltin()
            .build();

    public final ResourceLocation id;
    public final String langName;
    public final Supplier<BlockEntry<? extends TrackBlock>> trackBlock;
    public final Ingredient sleeperIngredient;
    public final Ingredient railsIngredient;
    public final ResourceLocation particle;
    public final TrackType trackType;

    @Nullable
    private final TrackType.CustomTrackBlockFactory customFactory; // todo track api

    @Environment(EnvType.CLIENT)
    protected TrackModelHolder modelHolder;

    @Environment(EnvType.CLIENT)
    public TrackModelHolder getModelHolder() {
        return modelHolder;
    }

    public static TrackMaterialFactory make(ResourceLocation id) {
        return new TrackMaterialFactory(id);
    }

    /*public TrackMaterial(String langName, Supplier<BlockEntry<? extends TrackBlock>> trackBlock, ResourceLocation particle, ItemLike... items) {
        this(langName, trackBlock, particle, Ingredient.of(items));
    }

    public TrackMaterial(String langName, Supplier<BlockEntry<? extends TrackBlock>> trackBlock, ResourceLocation particle, Ingredient sleeperIngredient) {
        this(langName, trackBlock, particle, sleeperIngredient, Ingredient.fromValues(
            Stream.of(new Ingredient.TagValue(Ingredients.ironNugget()), new Ingredient.TagValue(Ingredients.zincNugget()))), false);
    }

    public TrackMaterial(String langName, Supplier<BlockEntry<? extends TrackBlock>> trackBlock, ResourceLocation particle, Ingredient sleeperIngredient, Ingredient railsIngredient) {
        this(langName, trackBlock, particle, sleeperIngredient, railsIngredient, false);
    }

    public TrackMaterial(String langName, Supplier<BlockEntry<? extends TrackBlock>> trackBlock, ResourceLocation particle, Ingredient sleeperIngredient, Ingredient railsIngredient, boolean createBuiltin) {
        this(langName, trackBlock, particle, sleeperIngredient, railsIngredient, createBuiltin, TrackType.STANDARD);
    }*/

    public TrackMaterial(ResourceLocation id, String langName, Supplier<BlockEntry<? extends TrackBlock>> trackBlock,
                         ResourceLocation particle, Ingredient sleeperIngredient, Ingredient railsIngredient,
                         TrackType trackType, Supplier<Supplier<TrackModelHolder>> modelHolder, @Nullable TrackType.CustomTrackBlockFactory customFactory) {
        this.id = id;
        this.langName = langName;
        this.trackBlock = trackBlock;
//    Railways.LOGGER.info("Building track_material: "+this.langName+", trackBlock:"+this.trackBlock);
        this.sleeperIngredient = sleeperIngredient;
        this.railsIngredient = railsIngredient;
        this.particle = particle;
        this.trackType = trackType;
        this.customFactory = customFactory;
        Env.CLIENT.runIfCurrent(() -> () -> {
            this.modelHolder = modelHolder.get().get();
        });
        ALL.add(this);
    }

    public BlockEntry<? extends TrackBlock> getTrackBlock() {
        return this.trackBlock.get();
    }

    public CustomTrackBlock create(BlockBehaviour.Properties properties) {
        return customFactory != null ? customFactory.create(properties, this) : this.trackType.create(properties, this);
    }

    public boolean isCustom(String modId) {
        return this.id.getNamespace().equals(modId);
    }

    public static TrackMaterial[] allCustom(String modid) {
        return ALL.stream().filter(tm -> tm.isCustom(modid)).toArray(TrackMaterial[]::new);
    }

    public static List<BlockEntry<?>> allCustomBlocks(String modid) {
        List<BlockEntry<?>> list = new ArrayList<>();
        for (TrackMaterial material : allCustom(modid)) {
            list.add(material.getTrackBlock());
        }
        return list;
    }

    public static List<BlockEntry<?>> allBlocks() {
        List<BlockEntry<?>> list = new ArrayList<>();
        for (TrackMaterial material : ALL) {
            list.add(material.getTrackBlock());
        }
        return list;
    }

    public static void addCustomValidTracks(BlockEntityType<?> type) {
        AccessorBlockEntityType access = (AccessorBlockEntityType) type;
        Set<Block> blocks = new HashSet<>(access.getValidBlocks());
        allBlocks().forEach(entry -> blocks.add(entry.get()));
        access.setValidBlocks(blocks);
    }

    public String resName() {
        return this.id.getPath();
    }

    public static TrackMaterial deserialize(String serializedName) {
        if (!serializedName.contains(":")) {
            String mod = serializedName.equals("andesite") ? Create.ID : Railways.MODID;
            String oldName = serializedName;
            serializedName = mod + ":" + serializedName;
            Railways.LOGGER.warn("Legacy track material detected (no namespace): " + oldName + " -> " + serializedName);
        }
        ResourceLocation id = new ResourceLocation(serializedName);
        for (TrackMaterial material : ALL) {
            if (material.id.equals(id))
                return material;
        }
        return ANDESITE;
    }

    public static class TrackType {

        @FunctionalInterface
        public interface CustomTrackBlockFactory {
            CustomTrackBlock create(BlockBehaviour.Properties properties, TrackMaterial material);
        }

        public static final TrackType STANDARD = new TrackType(Create.asResource("standard"), CustomTrackBlock::new);
        public static final TrackType MONORAIL = new TrackType(Railways.asResource("monorail"), MonorailTrackBlock::new);

        public final ResourceLocation id;
        private final CustomTrackBlockFactory factory;

        public TrackType(ResourceLocation id, CustomTrackBlockFactory factory) {
            this.id = id;
            this.factory = factory;
        }

        protected CustomTrackBlock create(BlockBehaviour.Properties properties, TrackMaterial material) {
            return factory.create(properties, material);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class TrackModelHolder {
        static final TrackModelHolder DEFAULT = new TrackModelHolder(AllPartialModels.TRACK_TIE, AllPartialModels.TRACK_SEGMENT_LEFT, AllPartialModels.TRACK_SEGMENT_RIGHT);

        public final PartialModel tie;
        public final PartialModel segment_left;
        public final PartialModel segment_right;


        protected TrackModelHolder(PartialModel tie, PartialModel segment_left, PartialModel segment_right) {
            this.tie = tie;
            this.segment_left = segment_left;
            this.segment_right = segment_right;
        }
    }
}
