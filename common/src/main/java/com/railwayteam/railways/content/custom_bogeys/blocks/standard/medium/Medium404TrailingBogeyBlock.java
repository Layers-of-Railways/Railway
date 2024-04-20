package com.railwayteam.railways.content.custom_bogeys.blocks.standard.medium;

import com.railwayteam.railways.content.custom_bogeys.blocks.base.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.world.phys.Vec3;

public class Medium404TrailingBogeyBlock extends CRBogeyBlock {
    public Medium404TrailingBogeyBlock(Properties props) {
        super(props, CRBogeyStyles.MEDIUM_4_0_4_TRAILING, BogeySizes.SMALL);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 24 / 32f);
    }
}
