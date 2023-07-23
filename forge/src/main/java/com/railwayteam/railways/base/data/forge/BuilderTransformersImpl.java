package com.railwayteam.railways.base.data.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.vent.VentBlock;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagBlock;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.semaphore.SemaphoreBlock;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.SmokeStackBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;

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
                .getExistingFile(p.modLoc("block/bogey/monorail/top" + (s.getValue(MonoBogeyBlock.UPSIDE_DOWN) ? "_upside_down" : "")))))
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

    public static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> standardBogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                .getExistingFile(p.modLoc("block/bogey/top"))))
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
}
