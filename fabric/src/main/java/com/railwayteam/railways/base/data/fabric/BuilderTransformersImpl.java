/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2026 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.base.data.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.annotation.multiloader.ImplClass;
import com.railwayteam.railways.content.buffer.MonoTrackBufferBlock;
import com.railwayteam.railways.content.buffer.TrackBufferBlock;
import com.railwayteam.railways.content.buffer.fabric.BufferModel;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBarsBlock;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBlock;
import com.railwayteam.railways.content.buffer.headstock.HeadstockBlock;
import com.railwayteam.railways.content.buffer.headstock.fabric.CopycatHeadstockBarsModel;
import com.railwayteam.railways.content.buffer.headstock.fabric.CopycatHeadstockModel;
import com.railwayteam.railways.content.buffer.single_deco.GenericDyeableSingleBufferBlock;
import com.railwayteam.railways.content.buffer.single_deco.LinkPinBlock;
import com.railwayteam.railways.content.conductor.vent.VentBlock;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagBlock;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import com.railwayteam.railways.content.custom_bogeys.blocks.base.CRBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.AbstractMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.InvisibleMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.fabric.GenericCrossingModel;
import com.railwayteam.railways.content.handcar.HandcarBlock;
import com.railwayteam.railways.content.palettes.PalettesColor;
import com.railwayteam.railways.content.palettes.RotatedPillarWindowBlock;
import com.railwayteam.railways.content.palettes.doors.HingedDoorBlock;
import com.railwayteam.railways.content.palettes.hazard_stripes.HazardStripesBlock;
import com.railwayteam.railways.content.palettes.painting.PaintPitcherItem;
import com.railwayteam.railways.content.palettes.smokebox.PalettesSmokeboxBlock;
import com.railwayteam.railways.content.palettes.trapdoors.PalettesTrapDoorBlock;
import com.railwayteam.railways.content.semaphore.SemaphoreBlock;
import com.railwayteam.railways.content.smokestack.RotationType;
import com.railwayteam.railways.content.smokestack.SmokestackStyle;
import com.railwayteam.railways.content.smokestack.block.AbstractSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.SmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.StyledSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.diesel.DieselSmokeStackBlock;
import com.railwayteam.railways.content.smokestack.block.variable.VariableStack;
import com.railwayteam.railways.content.smokestack.block.variable.VariableStackPart;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRPalettes.WindowType;
import com.railwayteam.railways.registry.CRPalettes.Wrapping;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.TextUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.MetalLadderBlock;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import io.github.fabricators_of_create.porting_lib.models.generators.ConfiguredModel;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import io.github.fabricators_of_create.porting_lib.models.generators.block.BlockModelBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.railwayteam.railways.base.data.BuilderTransformers.sharedBogey;
import static com.railwayteam.railways.content.conductor.vent.VentBlock.CONDUCTOR_VISIBLE;
import static com.railwayteam.railways.util.TextUtils.join;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

@ImplClass
public class BuilderTransformersImpl {
    /*
    these blockstate transformers should be IDENTICAL on forge and fabric, just with a different import for ConfiguredModel
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
                    .modelFile(p.models().getExistingFile(Railways.asResource("block/track_switch_" + (andesite ? "andesite" : "brass") + "/block")))
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
        return a -> a
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                .forAllStatesExcept(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                        .modelFile(p.models().getExistingFile(Railways.asResource("block/smokestack/block_diesel_case")))
                        .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                        .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                        .build();
                }, DieselSmokeStackBlock.WATERLOGGED, DieselSmokeStackBlock.ENABLED, DieselSmokeStackBlock.POWERED));
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

    public static NonNullBiConsumer<DataGenContext<Block, SmokeStackBlock>, RegistrateBlockstateProvider> defaultSmokeStack(String variant, RotationType rotType) {
        return (c, p) -> p.getVariantBuilder(c.get())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(!state.hasProperty(StyledSmokeStackBlock.STYLE)
                            ? p.models().getExistingFile(p.modLoc("block/smokestack/block_" + variant))
                            :p.models().withExistingParent(
                                c.getName() + "_" + state.getValue(StyledSmokeStackBlock.STYLE).getBlockId(),
                                p.modLoc("block/smokestack/block_" + variant)
                            )
                            .texture("0", state.getValue(StyledSmokeStackBlock.STYLE).getTexture(variant))
                            .texture("particle", "#0")
                    )
                    .rotationY(rotType .getModelYRot(state))
                    .build(),
                AbstractSmokeStackBlock.ENABLED,
                AbstractSmokeStackBlock.POWERED,
                AbstractSmokeStackBlock.WATERLOGGED
            );
    }

    public static <B extends Block & VariableStack> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> variableSmokeStack(String variant, RotationType rotType) {
        return (c, p) -> p.getVariantBuilder(c.get())
            .forAllStatesExcept(state -> {
                    VariableStackPart part = state.getValue(c.get().partProperty());
                    SmokestackStyle style = state.getValue(StyledSmokeStackBlock.STYLE);

                    BlockModelBuilder model = p.models().withExistingParent(
                        c.getName() + "_" + style.getBlockId() + part.generatedModelName(),
                        p.modLoc("block/smokestack/" + variant + "/" + part)
                    );

                    model.texture("0", part.isSegment() ? style.getSegmentTexture(variant) : style.getTexture(variant));

                    return ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationY(rotType.getModelYRot(state))
                    .build();
                },
                AbstractSmokeStackBlock.ENABLED,
                AbstractSmokeStackBlock.POWERED,
                AbstractSmokeStackBlock.WATERLOGGED
            );
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

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalBase(PalettesColor color, @Nullable String type) {
        return b -> {
            BlockBuilder<B, P> out = b.initialProperties(SharedProperties::softMetal)
                .properties(p -> p
                    .mapColor(color.getMapColor())
                    .sound(SoundType.NETHERITE_BLOCK)
                )
                .transform(pickaxeOnly())
                .tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
                .tag(CRTags.AllBlockTags.LOCOMETAL.tag);
            if (type != null)
                out = out.blockstate((c, p) -> p.simpleBlock(c.get(), p.models().cubeAll(
                    "block/palettes/"+TextUtils.prefixToFolder(c.getName(), color.getSerializedName()), p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type)
                )));
            return out;
        };
    }

    public static <B extends RotatedPillarBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalPillar(PalettesColor color) {
        return b -> b.transform(locoMetalBase(color, null))
            .blockstate((c, p) -> {
                String modelName = "block/palettes/"+TextUtils.prefixToFolder(c.getName(), color.getSerializedName());
                ResourceLocation side = p.modLoc("block/palettes/" + color.getSerializedName() + "/riveted_pillar_side");
                ResourceLocation end = p.modLoc("block/palettes/" + color.getSerializedName() + "/riveted_pillar_top");
                p.axisBlock(c.get(),
                    p.models().cubeColumn(modelName, side, end),
                    p.models().cubeColumnHorizontal(modelName + "_horizontal", side, end)
                );
            });
    }

    public static <B extends PalettesSmokeboxBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalSmokeBox(PalettesColor color, @Nullable Wrapping wrapping) {
        return b -> b.transform(locoMetalBase(color, null))
            .blockstate((c, p) -> p.getVariantBuilder(c.get()).forAllStates(state -> {
                Direction dir = state.getValue(BlockStateProperties.FACING);
                String name = dir.getAxis().isVertical() ? "smokebox" : "smokebox_horizontal";
                String wrappingName = wrapping == null ? "" : wrapping.prefix("wrapped_");

                return ConfiguredModel.builder()
                    .modelFile(
                        p.models().withExistingParent("block/palettes/" + color.getSerializedName() + "/" + wrappingName + "locometal_" + name, p.modLoc("block/palettes/smokebox/" + name))
                            .texture("side", p.modLoc("block/palettes/" + color.getSerializedName() + "/" + wrappingName + "tank_side"))
                            .texture("top", p.modLoc("block/palettes/" + color.getSerializedName() + "/smokebox_tank_top"))
                    )
                    .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
            }));
    }

    @SafeVarargs
    public static <B extends MetalLadderBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalLadder(PalettesColor color, String ladderType, TagKey<Item>... tags) {
        return b -> b.transform(locoMetalBase(color, null))
            .initialProperties(() -> Blocks.LADDER)
            .addLayer(() -> RenderType::cutout)
            .properties(p -> p.sound(SoundType.COPPER))
            .blockstate((c, p) -> {
                String main = join("/", "block", "palettes", color.getSerializedName(), ladderType+"_ladder");
                String hoop = main + "_hoop";
                p.horizontalBlock(c.get(), p.models()
                    .withExistingParent(main, Create.asResource("block/ladder"))
                    .texture("0", p.modLoc(hoop))
                    .texture("1", p.modLoc(main))
                    .texture("particle", p.modLoc(main))
                );
            })
            .tag(BlockTags.CLIMBABLE)
            .item()
            .model((c, p) -> p.blockSprite(
                c::get,
                p.modLoc(join("/", "block", "palettes", color.getSerializedName(), ladderType+"_ladder"))
            ))
            .tag(tags)
            .build();
    }

    @SafeVarargs
    public static <B extends FlywheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalFlywheel(PalettesColor color, TagKey<Item>... tags) {
        return b -> b.transform(locoMetalBase(color, null))
            .properties(p -> p.noOcclusion())
            .transform(axeOrPickaxe())
            .transform(BlockStressDefaults.setNoImpact())
            .blockstate((c, p) -> {
                String modelName = join("/", "block", "palettes", "flywheel", color.getSerializedName(), "block");
                ResourceLocation flywheelTex = p.modLoc(join("/", "block", "palettes", color.getSerializedName(), "flywheel"));
                BlockStateGen.axisBlock(
                    c, p,
                    $ -> p.models().withExistingParent(modelName, Create.asResource("block/flywheel/block"))
                        .texture("0", flywheelTex)
                        .texture("particle", flywheelTex)
                );
            })
            .item()
            .tag(tags)
            .model((c, p) -> {
                ResourceLocation flywheelTex = p.modLoc(join("/", "block", "palettes", color.getSerializedName(), "flywheel"));
                p.withExistingParent(c.getName(), Create.asResource("block/flywheel/item"))
                    .texture("0", flywheelTex)
                    .texture("particle", flywheelTex);
            })
            .build();
    }

    public static <B extends DoorBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locometalHingedDoorBlockState(PalettesColor color, String type) {
        return b -> b.blockstate((c, p) -> {
            Couple<Couple<Couple<BlockModelBuilder>>> pieces = Couple.createWithContext((right) -> Couple.createWithContext((bottom) -> {
                String texName = bottom ? "bottom" : "top";
                String modelSuffix = texName + "_" + (right ? "right" : "left");
                String modelName = "block/palettes/" + color.getSerializedName() + "/" + type + "_door/block_" + modelSuffix;
                return Couple.createWithContext((windowed) -> {
                    String windowedName = windowed ? "_windowed" : "";
                    return p.models().withExistingParent(
                            modelName + windowedName,
                            p.modLoc("block/palettes/doors/block_" + modelSuffix)
                        )
                        .texture("side", p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type + windowedName + "_door_side"))
                        .texture(texName, p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type + windowedName + "_door_" + texName))
                        .texture("block_particle", p.modLoc("block/palettes/" + color.getSerializedName() + "/annexed_slashed"));
                });
            }));
            p.getVariantBuilder(c.get()).forAllStatesExcept((state) -> {
                int yRot = (int) state.getValue(DoorBlock.FACING).toYRot() + 90;
                boolean right = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
                boolean open = state.getValue(DoorBlock.OPEN);
                boolean lower = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
                boolean windowed = state.getValue(HingedDoorBlock.WINDOWED);

                if (open) {
                    yRot += 90;
                }
                if (right && open) {
                    yRot += 180;
                }
                yRot %= 360;

                ModelFile model = pieces.get(right ^ open).get(lower).get(windowed);

                return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .build();
            }, DoorBlock.POWERED);
        });
    }

    public static <B extends DoorBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locometalSlidingDoorBlockState(PalettesColor color, String type) {
        return locometalSlidingDoorBlockState(color, type, (c, p) -> {});
    }

    private static <B extends DoorBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locometalSlidingDoorBlockState(PalettesColor color, String type, BiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> extraRegistration) {
        return b -> b.blockstate((c, p) -> {
            extraRegistration.accept(c, p);
            Couple<Couple<BlockModelBuilder>> pieces = Couple.createWithContext((bottom) -> {
                String texName = bottom ? "bottom" : "top";
                String modelName = "block/palettes/" + color.getSerializedName() + "/" + type + "_door/block_" + texName;
                return Couple.createWithContext((windowed) -> {
                    String windowedName = windowed ? "_windowed" : "";
                    return p.models().withExistingParent(
                            modelName + windowedName,
                            p.modLoc("block/palettes/doors/block_" + texName + "_left")
                        )
                        .texture("side", p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type + windowedName + "_door_side"))
                        .texture(texName, p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type + windowedName + "_door_" + texName))
                        .texture("block_particle", p.modLoc("block/palettes/" + color.getSerializedName() + "/annexed_slashed"));
                });
            });
            p.getVariantBuilder(c.get()).forAllStatesExcept((state) -> {
                int yRot = (int) state.getValue(DoorBlock.FACING).toYRot() + 90;
                boolean right = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
                boolean open = state.getValue(DoorBlock.OPEN);
                boolean lower = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
                boolean windowed = state.getValue(HingedDoorBlock.WINDOWED);

                if (open) {
                    yRot += 90;
                }
                if (right && open) {
                    yRot += 180;
                }
                yRot %= 360;

                ModelFile model = pieces.get(lower).get(windowed);

                return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .build();
            }, DoorBlock.POWERED);
        });
    }

    public static <B extends DoorBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locometalFoldingDoorBlockState(PalettesColor color, String type) {
        return b -> b.transform(locometalSlidingDoorBlockState(color, type, (c, p) -> {
            // just generate the needed fold models. Amazingly, this still generates even though it isn't used in any blockstate
            for (boolean right : Iterate.trueAndFalse) {
                for (boolean windowed : Iterate.trueAndFalse) {
                    String name = right ? "right" : "left";
                    String windowStr = windowed ? "_windowed" : "";
                    String modelName = "block/palettes/" + color.getSerializedName() + "/" + type + "_door/fold_" + name + windowStr;
                    p.models().withExistingParent(
                            modelName,
                            p.modLoc("block/palettes/doors/fold_" + name)
                        )
                        .texture("side", p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type + windowStr + "_door_side"))
                        .texture("bottom", p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type + windowStr + "_door_bottom"))
                        .texture("top", p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type + windowStr + "_door_top"))
                        .texture("block_particle", p.modLoc("block/palettes/" + color.getSerializedName() + "/annexed_slashed"));
                }
            }
        }));
    }

    private static void axisBlock(RegistrateBlockstateProvider p, RotatedPillarWindowBlock block, ModelFile vertical, ModelFile horizontal) {
        p.getVariantBuilder(block)
            .partialState().with(RotatedPillarWindowBlock.AXIS, Direction.Axis.Y)
            .modelForState().modelFile(vertical).addModel()
            .partialState().with(RotatedPillarWindowBlock.AXIS, Direction.Axis.Z)
            .modelForState().modelFile(horizontal).rotationX(90).addModel()
            .partialState().with(RotatedPillarWindowBlock.AXIS, Direction.Axis.X)
            .modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
    }

    public static <B extends RotatedPillarWindowBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locometalWindow(PalettesColor color, WindowType type) {
        return b -> b.transform(locoMetalBase(color, null))
            .blockstate((c, p) -> {
                String modelName = "block/palettes/"+TextUtils.prefixToFolder(c.getName(), color.getSerializedName());
                ResourceLocation side = p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type.getTextureName());
                ResourceLocation end = p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type.getTextureName());
                axisBlock(p, c.get(),
                    p.models().cubeColumn(modelName, side, end),
                    p.models().cubeColumnHorizontal(modelName + "_horizontal", side, end)
                );
            });
    }

    public static <B extends PalettesTrapDoorBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locometalTrapdoor(PalettesColor color) {
        return b -> b.transform(locoMetalBase(color, null))
            .blockstate((c, p) -> {
                String[] qualifiers = {"bottom", "top", "open"};
                Couple<ModelFile[]> models = Couple.createWithContext((windowed) -> {
                    String windowStr = windowed ? "windowed_" : "";
                    ModelFile[] out = new ModelFile[3];
                    for (int i = 0; i < 3; i++) {
                        String modelName = "block/palettes/" + color.getSerializedName() + "/trapdoors/" + windowStr + "trapdoor_" + qualifiers[i];
                        out[i] = p.models().withExistingParent(
                                modelName,
                                p.modLoc("block/palettes/trapdoors/template_orientable_trapdoor_" + qualifiers[i])
                            )
                            .texture("texture", p.modLoc("block/palettes/" + color.getSerializedName() + "/" + windowStr + "trapdoor"))
                            .texture("side", p.modLoc("block/palettes/" + color.getSerializedName() + "/hinged_door_side"));
                    }
                    return out;
                });

                Couple<ModelFile> bottom = Couple.createWithContext((windowed) -> models.get(windowed)[0]);
                Couple<ModelFile> top = Couple.createWithContext((windowed) -> models.get(windowed)[1]);
                Couple<ModelFile> open = Couple.createWithContext((windowed) -> models.get(windowed)[2]);

                // create block model alias
                p.models().withExistingParent(
                    "block/palettes/" + TextUtils.prefixToFolder(c.getName(), color.getSerializedName()),
                    bottom.get(false).getLocation()
                );

                p.getVariantBuilder(c.get()).forAllStatesExcept(state -> {
                    Direction facing = state.getValue(PalettesTrapDoorBlock.FACING);
                    boolean isOpen = state.getValue(PalettesTrapDoorBlock.OPEN);
                    Half half = state.getValue(PalettesTrapDoorBlock.HALF);
                    boolean windowed = state.getValue(PalettesTrapDoorBlock.WINDOWED);

                    int xRot = 0;
                    int yRot = ((int) facing.toYRot()) + 180;
                    if (isOpen && half == Half.TOP) {
                        xRot += 180;
                        yRot += 180;
                    }
                    yRot %= 360;
                    return ConfiguredModel.builder()
                        .modelFile((isOpen ? open : half == Half.TOP ? top : bottom).get(windowed))
                        .rotationX(xRot)
                        .rotationY(yRot)
                        .build();
                }, PalettesTrapDoorBlock.POWERED, PalettesTrapDoorBlock.WATERLOGGED);
            });
    }

    public static <B extends HazardStripesBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> hazardStripes(boolean chevron) {
        return b -> b.blockstate((c, p) -> {
            PalettesColor color = c.get().getMainColor();
            PalettesColor baseColor = c.get().getBaseColor();

            String shape = chevron ? "chevron" : "diagonal";
            String baseModel = "block/palettes/hazard_stripes/" + shape;
            var model = p.models().withExistingParent(
                "block/palettes/" + TextUtils.prefixToFolder(c.getName(), color.getSerializedName()),
                p.modLoc(baseModel)
            ).texture("texture", p.modLoc("block/palettes/" + color.getSerializedName() +
                "/hazard_stripes_" + shape + "_a_on_" + baseColor.getSerializedName()));

            p.getVariantBuilder(c.get()).forAllStates(state -> {
                int yRot = c.get().getYRot(state) % 360;
                return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .build();
            });
        });
    }

    public static <I extends BlockItem, P> NonNullUnaryOperator<ItemBuilder<I, P>> locometalDoorItemModel(PalettesColor color, String type) {
        return i -> i.model((c, p) -> p.blockSprite(c, p.modLoc("block/palettes/" + color.getSerializedName() + "/" + type + "_door")));
    }

    public static <I extends Item, P> NonNullUnaryOperator<ItemBuilder<I, P>> locoMetalItem(PalettesColor color) {
        return i -> i.model((c, p) -> p.withExistingParent(
            c.getName(),
            p.modLoc("block/palettes/" + TextUtils.prefixToFolder(c.getName(), color.getSerializedName()))
        ));
    }

    public static <I extends PaintPitcherItem, P> NonNullUnaryOperator<ItemBuilder<I, P>> paintPitcher() {
        return i -> i.model((c, p) -> {
            PalettesColor color = c.get().getColor();
            p.blockSprite(
                c,
                color == null
                    ? p.modLoc("block/palettes/sandy_pitcher")
                    : p.modLoc("block/palettes/" + TextUtils.prefixToFolder(c.getName(), color.getSerializedName()))
            );
        });
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> variantBuffer() {
        return b -> b.onRegister(CreateRegistrate.blockModel(() -> BufferModel::new));
    }

    public static <I extends Item, P> NonNullUnaryOperator<ItemBuilder<I, P>> variantBufferItem() {
        return i -> i.onRegister(CreateRegistrate.itemModel(() -> BufferModel::new));
    }

    public static <B extends CopycatHeadstockBarsBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> copycatHeadstockBars() {
        return b -> b
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                .forAllStates(state -> ConfiguredModel.builder()
                    .modelFile(p.models()
                        .getExistingFile(p.modLoc("block/buffer/headstock/copycat_headstock_bars"+
                            (state.getValue(CopycatHeadstockBarsBlock.UPSIDE_DOWN) ? "_upside_down" : "")
                        ))
                    )
                    .rotationY(((int) state.getValue(CopycatHeadstockBarsBlock.FACING).toYRot() + 180) % 360)
                    .build()
                )
            )
            .onRegister(CreateRegistrate.blockModel(() -> CopycatHeadstockBarsModel::new));
    }

    public static <B extends TrackBufferBlock<?>, P> NonNullUnaryOperator<BlockBuilder<B, P>> bufferBlockState(Function<BlockState, ResourceLocation> modelFunc, Function<BlockState, Direction> facingFunc) {
        return b -> b.blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(modelFunc.apply(state)))
                .rotationY(TrackBufferBlock.getBaseModelYRotationOf(state))
                .build(), BlockStateProperties.WATERLOGGED
            )
        );
    }

    public static <B extends MonoTrackBufferBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> monoBuffer() {
        return b -> b.blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> {
                    boolean hanging = state.getValue(MonoTrackBufferBlock.UPSIDE_DOWN);
                    return ConfiguredModel.builder()
                        .modelFile(p.models().getExistingFile(state.getValue(MonoTrackBufferBlock.STYLE).getModel()))
                        .rotationX(hanging ? 180 : 0)
                        .rotationY(TrackBufferBlock.getBaseModelYRotationOf(state, hanging ? 0 : 180))
                        .build();
                }, MonoTrackBufferBlock.WATERLOGGED
            )
        );
    }

    public static <B extends LinkPinBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> linkAndPin() {
        return b -> b.blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(state.getValue(LinkPinBlock.STYLE).getModel()))
                .rotationY(((int) state.getValue(LinkPinBlock.FACING).toYRot() + 180) % 360)
                .build(), LinkPinBlock.WATERLOGGED
            )
        );
    }

    public static <B extends HeadstockBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> headstock() {
        return b -> b.blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(state.getValue(HeadstockBlock.STYLE).getModel(false, state.getValue(HeadstockBlock.UPSIDE_DOWN))))
                .rotationY(((int) state.getValue(HeadstockBlock.FACING).toYRot() + 180) % 360)
                .build(), HeadstockBlock.WATERLOGGED
            )
        );
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> invisibleBlockState() {
        return b -> b.blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
            .withExistingParent(c.getName(), p.modLoc("block/invisible"))));
    }

    public static <B extends CopycatHeadstockBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> copycatHeadstock() {
        return b -> b
            .blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
                .forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(p.models().getExistingFile(state.getValue(CopycatHeadstockBlock.STYLE).getModel(true, state.getValue(CopycatHeadstockBlock.UPSIDE_DOWN))))
                    .rotationY(((int) state.getValue(HeadstockBlock.FACING).toYRot() + 180) % 360)
                    .build(), HeadstockBlock.WATERLOGGED
                )
            ).properties(p -> p.noOcclusion()
                .mapColor(MapColor.NONE))
            .addLayer(() -> RenderType::solid)
            .addLayer(() -> RenderType::cutout)
            .addLayer(() -> RenderType::cutoutMipped)
            .addLayer(() -> RenderType::translucent)
            .color(() -> CopycatBlock::wrappedColor)
            .onRegister(CreateRegistrate.blockModel(() -> CopycatHeadstockModel::new));
    }

    public static <I extends Item, P> NonNullUnaryOperator<ItemBuilder<I, P>> copycatHeadstockItem() {
        return i -> i
            .color(() -> CopycatHeadstockBlock::wrappedItemColor)
            .onRegister(CreateRegistrate.itemModel(() -> CopycatHeadstockModel::new));
    }

    public static <B extends GenericDyeableSingleBufferBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> bigBuffer() {
        return b -> b.blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(p.modLoc("block/buffer/single_deco/big_buffer")))
                .rotationY(((int) state.getValue(GenericDyeableSingleBufferBlock.FACING).toYRot() + 180) % 360)
                .build(), GenericDyeableSingleBufferBlock.WATERLOGGED
            )
        );
    }

    public static <B extends GenericDyeableSingleBufferBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> smallBuffer() {
        return b -> b.blockstate((c, p) -> p.getVariantBuilder(c.getEntry())
            .forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(p.models().getExistingFile(p.modLoc("block/buffer/single_deco/small_buffer")))
                .rotationY(((int) state.getValue(GenericDyeableSingleBufferBlock.FACING).toYRot() + 180) % 360)
                .build(), GenericDyeableSingleBufferBlock.WATERLOGGED
            )
        );
    }
}
