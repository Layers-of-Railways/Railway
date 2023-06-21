package com.railwayteam.railways.base.data;

import com.railwayteam.railways.content.custom_bogeys.invisible.InvisibleBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.coilspring.CoilspringBogeyBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.world.level.block.SoundType;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class BuilderTransformers {
    public static <B extends MonoBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> monobogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
                .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                .properties(p -> p.noOcclusion())
                .transform(pickaxeOnly())
                .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                        .getExistingFile(p.modLoc("block/bogey/monorail/top" + (s.getValue(MonoBogeyBlock.UPSIDE_DOWN) ? "_upside_down" : "")))))
                .loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()));
    }

    public static <B extends InvisibleBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> invisibleBogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
                .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                .properties(p -> p.noOcclusion())
                .transform(pickaxeOnly())
                .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                        .getExistingFile(p.modLoc("block/bogey/invisible/top"))))
                .loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()));
    }

    public static <B extends CoilspringBogeyBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> singleaxleBogey() {
        return b -> b.initialProperties(SharedProperties::softMetal)
                .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
                .properties(p -> p.noOcclusion())
                .transform(pickaxeOnly())
                .blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models()
                        .getExistingFile(p.modLoc("block/bogey/top"))))
                .loot((p, l) -> p.dropOther(l, AllBlocks.RAILWAY_CASING.get()));
    }
}
