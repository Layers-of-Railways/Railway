package com.railwayteam.railways.content.custom_bogeys.wide_gauge;

import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.world.phys.Vec3;

public class WideGaugeComicallyLargeBogeyBlock extends WideGaugeBogeyBlock {
    public WideGaugeComicallyLargeBogeyBlock(Properties props) {
        this(props, CRBogeyStyles.WIDE_COMICALLY_LARGE, BogeySizes.LARGE);
    }

    protected WideGaugeComicallyLargeBogeyBlock(Properties props, BogeyStyle style, BogeySize size) {
        super(props, style, size);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 36 / 32f);
    }

    @Override
    public double getWheelRadius() {
        return 22.5 / 16d;
    }
}
