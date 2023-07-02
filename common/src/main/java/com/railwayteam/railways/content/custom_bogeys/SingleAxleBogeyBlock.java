package com.railwayteam.railways.content.custom_bogeys;

import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.world.phys.Vec3;

public class SingleAxleBogeyBlock extends CRBogeyBlock {
    public SingleAxleBogeyBlock(Properties props) {
        super(props, CRBogeyStyles.SINGLEAXLE, BogeySizes.SMALL);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 8 / 32f);
    }

    @Override
    public double getWheelPointSpacing() {
        return 1;
    }
}
