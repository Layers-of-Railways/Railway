package com.railwayteam.railways.base.data.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.buffer.fabric.BufferModel;
import com.railwayteam.railways.content.conductor.vent.VentBlock;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagBlock;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.AbstractMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.InvisibleMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.fabric.GenericCrossingModel;
import com.railwayteam.railways.content.handcar.HandcarBlock;
import com.railwayteam.railways.content.semaphore.SemaphoreBlock;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.OilburnerSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.SmokeStackBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.ColorUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import io.github.fabricators_of_create.porting_lib.models.generators.ConfiguredModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static com.railwayteam.railways.base.data.BuilderTransformers.sharedBogey;
import static com.railwayteam.railways.content.conductor.vent.VentBlock.CONDUCTOR_VISIBLE;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class BuilderTransformersImpl {
    /*
    these blockstate transformers should be IDENTICAL on forge and fabric, just with a different inport for ConfiguredModel
     */


    public static <B extends MonoBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> monobogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
                .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                        .getExistingFile(p.modLoc("block/bogey/monorail/top" + (s.getValue(AbstractMonoBogeyBlock.UPSIDE_DOWN) ? "_upside_down" : "")))))
            .loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()));
    }

    public static <B extends InvisibleBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> invisibleBogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                .getExistingFile(p.modLoc("block/bogey/invisible/top"))))
            .loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()));
    }

    public static <B extends SmokeStackBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> smokestack(boolean rotates, ResourceLocation modelLoc) {
        return a -> a.blockstate((c, p) -> {
//                rotates ? p.axisBlock(c.get(), p.models().getExistingFile(modelLoc)) : null
            if (rotates) {
                p.getVariantBuilder(c.get())
                    .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(p.models().getExistingFile(modelLoc))
                        .rotationY((state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0))
                        .build());
            } else {
                p.getVariantBuilder(c.get())
                    .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(p.models().getExistingFile(modelLoc))
                        .build());
            }
        });
    }

    public static <B extends SemaphoreBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> semaphore() {
        return a -> a.blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.getEntry())
            .forAllStates(state -> ConfiguredModel.builder()
                .modelFile(prov.models().getExistingFile(prov.modLoc(
                    "block/semaphore/block" +
                        (state.getValue(SemaphoreBlock.FULL) ? "_full" : "") +
                        (state.getValue(SemaphoreBlock.FLIPPED) ? "_flipped" : "") +
                        (state.getValue(SemaphoreBlock.UPSIDE_DOWN) ? "_down" : ""))))
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build()
            )
        );
    }

    public static <B extends TrackCouplerBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> trackCoupler() {
        return a -> a.blockstate((c, p) -> {
            p.getVariantBuilder(c.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(AssetLookup.partialBaseModel(c, p, state.getValue(TrackCouplerBlock.MODE).getSerializedName()))
                .build(), TrackCouplerBlock.POWERED);
        });
    }

    public static <B extends TrackSwitchBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> trackSwitch(boolean andesite) {
        return a -> a.blockstate((c, p) -> p.getVariantBuilder(c.get())
            .forAllStatesExcept(
                state -> ConfiguredModel.builder()
                    .modelFile(p.models().getExistingFile(Railways.asResource("block/track_switch_"+(andesite ? "andesite" : "brass")+"/block")))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90) % 360)
                    .build(),
                TrackSwitchBlock.LOCKED//, TrackSwitchBlock.STATE
            ));
    }

    public static <B extends ConductorWhistleFlagBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> conductorWhistleFlag() {
        return a -> a.blockstate((c, p) -> p.getVariantBuilder(c.get())
            .forAllStates(state -> ConfiguredModel.builder()
                .modelFile(AssetLookup.partialBaseModel(c, p, "pole"))
                .build()));
    }

    public static <B extends DieselSmokeStackBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> dieselSmokeStack() {
        return a -> a.blockstate((c, p) -> p.getVariantBuilder(c.get())
            .forAllStates(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(Railways.asResource("block/smokestack/block_diesel_case")))
                .build()));
    }

    public static <B extends VentBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> conductorVent() {
        return a -> a.blockstate((c, p) -> p.getVariantBuilder(c.get())
            .forAllStates(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(state.getValue(CONDUCTOR_VISIBLE) ?
                    Railways.asResource("block/copycat_vent_visible") :
                    new ResourceLocation("block/air")))
                .build()));
    }

    public static <B extends InvisibleMonoBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> invisibleMonoBogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(p -> p.noOcclusion())
            .transform(pickaxeOnly())
            .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                .getExistingFile(p.modLoc("block/bogey/invisible_monorail/top" + (s.getValue(AbstractMonoBogeyBlock.UPSIDE_DOWN) ? "_upside_down" : "")))))
            .loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()));
    }

    public static NonNullBiConsumer<DataGenContext<Block, SmokeStackBlock>, RegistrateBlockstateProvider> defaultSmokeStack(ResourceLocation modelLoc, boolean rotates) {
        return (c, p) -> {
//                rotates ? p.axisBlock(c.get(), p.models().getExistingFile(modelLoc)) : null
            if (rotates) {
                p.getVariantBuilder(c.get())
                    .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(p.models().getExistingFile(modelLoc))
                        .rotationY((state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0))
                        .build());
            } else {
                p.getVariantBuilder(c.get())
                    .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(p.models().getExistingFile(modelLoc))
                        .build());
            }
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, SmokeStackBlock>, RegistrateBlockstateProvider> oilburnerSmokeStack() {
        return (c, p) -> {
            p.getVariantBuilder(c.get())
                .forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(p.models().getExistingFile(Railways.asResource("block/smokestack/block_oilburner"+(state.getValue(OilburnerSmokeStackBlock.ENCASED) ? "_encased" : ""))))
                    .build());
        };
    }

    public static <B extends CasingCollisionBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> casingCollision() {
        return a -> a.blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
            .withExistingParent(c.getName(), p.mcLoc("block/air"))));
    }

    public static <B extends HandcarBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> handcar() {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion())
            .transform(pickaxeOnly())
            .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                .getExistingFile(p.mcLoc("air"))))
            .loot((p, l) -> p.dropOther(l, CRBlocks.HANDCAR.get()));
    }

    public static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> standardBogey() {
        return b -> b.transform(sharedBogey())
            .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                .getExistingFile(p.modLoc("block/bogey/top"))));
    }

    public static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> wideBogey() {
        return b -> b.transform(sharedBogey())
            .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                .getExistingFile(p.modLoc("block/bogey/wide/top"))));
    }

    public static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> narrowBogey() {
        return b -> b.transform(sharedBogey())
            .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                .getExistingFile(p.modLoc("block/bogey/narrow/top"))));
    }

    public static <B extends GenericCrossingBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> genericCrossing() {
        return b -> b.onRegister(CreateRegistrate.blockModel(() -> GenericCrossingModel::new));
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalBase(@Nullable DyeColor color, @Nullable String type) {
        return b -> {
            BlockBuilder<B, P> out = b.initialProperties(SharedProperties::softMetal)
                .properties(p -> p
                    .mapColor(ColorUtils.mapColorFromDye(color, MapColor.COLOR_BLACK))
                    .sound(SoundType.NETHERITE_BLOCK)
                )
                .transform(pickaxeOnly())
                .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
                .tag(CRTags.AllBlockTags.LOCOMETAL.tag);
            if (type != null)
                out = out.blockstate((c, p) -> p.simpleBlock(c.get(), p.models().cubeAll(
                    c.getName(), p.modLoc("block/palettes/" + colorName(color) + "/" + type)
                )));
            return out;
        };
    }

    public static <B extends RotatedPillarBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalPillar(@Nullable DyeColor color) {
        return b -> b.transform(locoMetalBase(color, null))
            .blockstate((c, p) -> p.axisBlock(c.get(),
                p.modLoc("block/palettes/" + colorName(color) + "/riveted_pillar_side"),
                p.modLoc("block/palettes/" + colorName(color) + "/riveted_pillar_top")
            ));
    }

    public static <B extends RotatedPillarBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalSmokeBox(@Nullable DyeColor color) {
        return b -> b.transform(locoMetalBase(color, null))
            .blockstate((c, p) -> p.axisBlock(c.get(),
                p.modLoc("block/palettes/" + colorName(color) + "/tank_side"),
                p.modLoc("block/palettes/" + colorName(color) + "/smokebox_tank_top")
            ));
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> variantBuffer() {
        return b -> b.onRegister(CreateRegistrate.blockModel(() -> BufferModel::new));
    }

    public static <I extends Item, P> NonNullUnaryOperator<ItemBuilder<I, P>> variantBufferItem() {
        return i -> i.onRegister(CreateRegistrate.itemModel(() -> BufferModel::new));
    }

    private static String colorName(@Nullable DyeColor color) {
        return color == null ? "netherite" : color.name().toLowerCase(Locale.ROOT);
    }
}
