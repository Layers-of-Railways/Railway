package com.railwayteam.railways.content.custom_bogeys.medium;

import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.world.phys.Vec3;

public class MediumSingleWheelBogeyBlock extends CRBogeyBlock {
    public MediumSingleWheelBogeyBlock(Properties props) {
        this(props, CRBogeyStyles.MEDIUM_SINGLE_WHEEL, BogeySizes.SMALL);
    }

    protected MediumSingleWheelBogeyBlock(Properties props, BogeyStyle defaultStyle, BogeySizes.BogeySize size) {
        super(props, defaultStyle, size);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 32 / 32f);
    }
}
