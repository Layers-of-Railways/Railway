package com.railwayteam.railways.content.custom_bogeys.blocks.standard_gauge;

import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.world.phys.Vec3;

public class TripleAxleBogeyBlock extends CRBogeyBlock {
    public TripleAxleBogeyBlock(Properties props) {
        super(props, CRBogeyStyles.HEAVYWEIGHT, BogeySizes.SMALL);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 48 / 32f);
    }

    @Override
    public double getWheelPointSpacing() {
        return 3;
    }
}
