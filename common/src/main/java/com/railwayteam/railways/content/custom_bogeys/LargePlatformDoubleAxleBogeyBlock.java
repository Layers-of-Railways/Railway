package com.railwayteam.railways.content.custom_bogeys;

import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import net.minecraft.world.phys.Vec3;

public class LargePlatformDoubleAxleBogeyBlock extends DoubleAxleBogeyBlock {
    public LargePlatformDoubleAxleBogeyBlock(Properties props) {
        super(props, CRBogeyStyles.FREIGHT, BogeySizes.SMALL);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 32 / 32f);
    }
}
