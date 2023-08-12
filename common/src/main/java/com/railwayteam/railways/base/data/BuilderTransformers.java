package com.railwayteam.railways.base.data;

import com.railwayteam.railways.content.conductor.vent.VentBlock;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagBlock;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.InvisibleMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionBlock;
import com.railwayteam.railways.content.semaphore.SemaphoreBlock;
import com.simibubi.create.AllTags;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.SmokeStackBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BuilderTransformers {
    @ExpectPlatform
    public static <B extends MonoBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> monobogey() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends InvisibleBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> invisibleBogey() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends InvisibleMonoBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> invisibleMonoBogey() {
        throw new AssertionError();
    }

    private static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> sharedBogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(p -> p.noOcclusion())
            .transform(pickaxeOnly())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()));
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

    @ExpectPlatform
    public static <B extends SmokeStackBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> smokestack(boolean rotates, ResourceLocation modelLoc) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends SemaphoreBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> semaphore() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends TrackCouplerBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> trackCoupler() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends TrackSwitchBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> trackSwitch(boolean andesite) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends ConductorWhistleFlagBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> conductorWhistleFlag() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends DieselSmokeStackBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> dieselSmokeStack() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends VentBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> conductorVent() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static NonNullBiConsumer<DataGenContext<Block, SmokeStackBlock>, RegistrateBlockstateProvider> defaultSmokeStack(ResourceLocation modelLoc, boolean rotates) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static NonNullBiConsumer<DataGenContext<Block, SmokeStackBlock>, RegistrateBlockstateProvider> oilburnerSmokeStack() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends CasingCollisionBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> casingCollision() {
        throw new AssertionError();
    }
}
