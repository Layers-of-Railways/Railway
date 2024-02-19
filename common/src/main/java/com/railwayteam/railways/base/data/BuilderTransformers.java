package com.railwayteam.railways.base.data;

import com.railwayteam.railways.content.buffer.MonoTrackBufferBlock;
import com.railwayteam.railways.content.buffer.TrackBufferBlock;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBlock;
import com.railwayteam.railways.content.buffer.headstock.HeadstockBlock;
import com.railwayteam.railways.content.buffer.single_deco.GenericDyeableSingleBufferBlock;
import com.railwayteam.railways.content.buffer.single_deco.LinkPinBlock;
import com.railwayteam.railways.content.conductor.vent.VentBlock;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagBlock;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.InvisibleMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.handcar.HandcarBlock;
import com.railwayteam.railways.content.palettes.boiler.BoilerBlock;
import com.railwayteam.railways.content.palettes.boiler.BoilerGenerator;
import com.railwayteam.railways.content.semaphore.SemaphoreBlock;
import com.railwayteam.railways.content.smokestack.block.DieselSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.SmokeStackBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.registry.CRPalettes.Wrapping;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.ColorUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

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

    @ApiStatus.Internal
    public static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> sharedBogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()));
    }

    @ExpectPlatform
    public static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> standardBogey() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> wideBogey() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> narrowBogey() {
        throw new AssertionError();
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
    public static NonNullBiConsumer<DataGenContext<Block, SmokeStackBlock>, RegistrateBlockstateProvider> defaultSmokeStack(ResourceLocation modelLoc, String variant, boolean rotates) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends CasingCollisionBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> casingCollision() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends HandcarBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> handcar() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends GenericCrossingBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> genericCrossing() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalBase(@Nullable DyeColor color, @Nullable String type) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends RotatedPillarBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalPillar(@Nullable DyeColor color) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends RotatedPillarBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalSmokeBox(@Nullable DyeColor color) {
        throw new AssertionError();
    }

    public static <B extends BoilerBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalBoiler(@Nullable DyeColor color, @Nullable Wrapping wrapping) {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p
                .mapColor(ColorUtils.mapColorFromDye(color, MapColor.COLOR_BLACK))
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion()
            )
            .tag(CRTags.AllBlockTags.LOCOMETAL.tag)
            .tag(CRTags.AllBlockTags.LOCOMETAL_BOILERS.tag)
            .tag(AllTags.AllBlockTags.COPYCAT_DENY.tag)
            .transform(pickaxeOnly())
            .onRegisterAfter(Registry.ITEM_REGISTRY, v -> ItemDescription.useKey(v, "block.railways.boiler"))
            .blockstate(BoilerGenerator.create(color, wrapping)::generate);
    }

    @ExpectPlatform
    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> variantBuffer() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <I extends Item, P> NonNullUnaryOperator<ItemBuilder<I, P>> variantBufferItem() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends TrackBufferBlock<?>, P> NonNullUnaryOperator<BlockBuilder<B, P>> bufferBlockState(Function<BlockState, ResourceLocation> modelFunc, Function<BlockState, Direction> facingFunc) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends MonoTrackBufferBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> monoBuffer() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends LinkPinBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> linkAndPin() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends HeadstockBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> headstock() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> invisibleBlockState() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends CopycatHeadstockBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> copycatHeadstock() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <I extends Item, P> NonNullUnaryOperator<ItemBuilder<I, P>> copycatHeadstockItem() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends GenericDyeableSingleBufferBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> bigBuffer() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <B extends GenericDyeableSingleBufferBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> smallBuffer() {
        throw new AssertionError();
    }
}
