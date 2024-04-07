package com.railwayteam.railways.content.custom_bogeys.blocks.gauge.standard.medium;

import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.world.phys.Vec3;

public class Medium202TrailingBogeyBlock extends CRBogeyBlock {
    public Medium202TrailingBogeyBlock(Properties props) {
        super(props, CRBogeyStyles.MEDIUM_2_0_2_TRAILING, BogeySizes.SMALL);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 30 / 32f);
    }

    @Override
    public double getWheelPointSpacing() {
        return 2; // needs to be even, otherwise station alignment is bad (was 1)
    }
}
