package com.railwayteam.railways.content.custom_bogeys.blocks.gauge.standard.medium;

import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.world.phys.Vec3;

public class MediumQuadrupleWheelBogeyBlock extends CRBogeyBlock {
    public MediumQuadrupleWheelBogeyBlock(Properties props) {
        this(props, CRBogeyStyles.MEDIUM_QUADRUPLE_WHEEL, BogeySizes.SMALL);
    }

    protected MediumQuadrupleWheelBogeyBlock(Properties props, BogeyStyle defaultStyle, BogeySizes.BogeySize size) {
        super(props, defaultStyle, size);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 72 / 32f);
    }
}
