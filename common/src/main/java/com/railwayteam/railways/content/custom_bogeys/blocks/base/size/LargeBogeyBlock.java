package com.railwayteam.railways.content.custom_bogeys.blocks.base.size;

import com.railwayteam.railways.content.custom_bogeys.blocks.base.CRBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;

public class LargeBogeyBlock extends CRBogeyBlock {
    protected LargeBogeyBlock(Properties props, BogeyStyle defaultStyle, BogeySizes.BogeySize size) {
        super(props, defaultStyle, size);
    }

    @Override
    public double getWheelRadius() {
        return 12.5 / 16d;
    }
}
