package com.railwayteam.railways.content.custom_bogeys.blocks.standard.medium;

import com.railwayteam.railways.content.custom_bogeys.blocks.base.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.world.phys.Vec3;

public class MediumQuintupleWheelBogeyBlock extends CRBogeyBlock {
    public MediumQuintupleWheelBogeyBlock(Properties props) {
        super(props, CRBogeyStyles.MEDIUM_QUINTUPLE_WHEEL, BogeySizes.SMALL);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 96 / 32f);
    }

    @Override
    public double getWheelPointSpacing() {
        return 4; // needs to be even, otherwise station alignment is bad (was 5)
    }
}
