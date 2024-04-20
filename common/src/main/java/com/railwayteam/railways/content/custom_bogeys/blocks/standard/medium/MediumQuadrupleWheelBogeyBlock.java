package com.railwayteam.railways.content.custom_bogeys.blocks.standard.medium;

import com.railwayteam.railways.content.custom_bogeys.blocks.base.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.world.phys.Vec3;

public class MediumQuadrupleWheelBogeyBlock extends CRBogeyBlock {
    public MediumQuadrupleWheelBogeyBlock(Properties props) {
        super(props, CRBogeyStyles.MEDIUM_QUADRUPLE_WHEEL, BogeySizes.SMALL);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 72 / 32f);
    }

    @Override
    public double getWheelPointSpacing() {
        return 4;
    }
}
