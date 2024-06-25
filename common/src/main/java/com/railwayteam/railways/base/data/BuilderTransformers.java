/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.base.data;

import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBarsBlock;
import com.railwayteam.railways.content.buffer.headstock.CopycatHeadstockBlock;
import com.railwayteam.railways.content.custom_bogeys.blocks.base.CRBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.AbstractMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.InvisibleMonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.special.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlock;
import com.railwayteam.railways.content.handcar.HandcarBlock;
import com.railwayteam.railways.content.palettes.boiler.BoilerBlock;
import com.railwayteam.railways.content.palettes.boiler.BoilerGenerator;
import com.railwayteam.railways.content.palettes.smokebox.PalettesSmokeboxBlock;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRPalettes.Wrapping;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.ColorUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class BuilderTransformers {
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

    public static <B extends InvisibleMonoBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> invisibleMonoBogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                .getExistingFile(p.modLoc("block/bogey/invisible_monorail/top" + (s.getValue(AbstractMonoBogeyBlock.UPSIDE_DOWN) ? "_upside_down" : "")))))
            .loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()));
    }

    private static <B extends CRBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> sharedBogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::noOcclusion)
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

    @ExpectPlatform
    public static <B extends GenericCrossingBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> genericCrossing() {
        throw new AssertionError();
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalBase(@Nullable DyeColor color, @Nullable String type) {
        return b -> {
            BlockBuilder<B, P> out = b.initialProperties(SharedProperties::softMetal)
                .properties(p -> p
                    .color(ColorUtils.materialColorFromDye(color, MaterialColor.COLOR_BLACK))
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

    public static <B extends PalettesSmokeboxBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalSmokeBox(@Nullable DyeColor color) {
        return b -> b.transform(locoMetalBase(color, null))
                .blockstate((c, p) -> p.getVariantBuilder(c.get()).forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    String name = dir.getAxis().isVertical() ? "smokebox" : "smokebox_horizontal";

                    return ConfiguredModel.builder()
                            .modelFile(
                                    p.models().withExistingParent(colorNameUnderscore(color) + "locometal_" + name, p.modLoc("block/palettes/smokebox/" + name))
                                            .texture("side", p.modLoc("block/palettes/" + colorName(color) + "/tank_side"))
                                            .texture("top", p.modLoc("block/palettes/" + colorName(color) + "/smokebox_tank_top"))
                            )
                            .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                            .build();
                }));
    }

    public static <B extends BoilerBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> locoMetalBoiler(@Nullable DyeColor color, @Nullable Wrapping wrapping) {
        return b -> b.initialProperties(SharedProperties::softMetal)
            .properties(p -> p
                .color(ColorUtils.materialColorFromDye(color, MaterialColor.COLOR_BLACK))
                .sound(SoundType.NETHERITE_BLOCK)
                .noOcclusion()
            )
            .tag(CRTags.AllBlockTags.LOCOMETAL.tag)
            .tag(CRTags.AllBlockTags.LOCOMETAL_BOILERS.tag)
            .tag(AllTags.AllBlockTags.COPYCAT_DENY.tag)
            .transform(pickaxeOnly())
            .onRegisterAfter(Registry.ITEM_REGISTRY, v -> ItemDescription.useKey(v, "block.railways.boiler"))
            .blockstate(new BoilerGenerator(color, wrapping)::generate);
    }

    private static String colorName(@Nullable DyeColor color) {
        return color == null ? "netherite" : color.name().toLowerCase(Locale.ROOT);
    }

    private static String colorNameUnderscore(@Nullable DyeColor color) {
        return color == null ? "" : color.name().toLowerCase(Locale.ROOT) + "_";
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
    public static <B extends CopycatHeadstockBarsBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> copycatHeadstockBars() {
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
}
