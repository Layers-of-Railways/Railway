package com.railwayteam.railways.content.custom_bogeys.blocks.standard.medium;

import com.railwayteam.railways.content.custom_bogeys.blocks.base.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.world.phys.Vec3;

public class MediumTripleWheelBogeyBlock extends CRBogeyBlock {
    public MediumTripleWheelBogeyBlock(Properties props) {
        super(props, CRBogeyStyles.MEDIUM_TRIPLE_WHEEL, BogeySizes.SMALL);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 48 / 32f);
    }

    @Override
    public double getWheelPointSpacing() {
        return 2; // needs to be even, otherwise station alignment is bad (was 3)
    }
}
