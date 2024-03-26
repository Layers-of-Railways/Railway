package com.railwayteam.railways.content.custom_bogeys.blocks.gauge.standard.large;

import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.world.phys.Vec3;

public class LargeCreateStyle060BogeyBlock extends CRBogeyBlock {
    public LargeCreateStyle060BogeyBlock(Properties props) {
        this(props, CRBogeyStyles.LARGE_CREATE_STYLED_0_6_0, BogeySizes.LARGE);
    }

    protected LargeCreateStyle060BogeyBlock(Properties props, BogeyStyle defaultStyle, BogeySizes.BogeySize size) {
        super(props, defaultStyle, size);
    }

    //fixme fix offset
    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 60 / 32f);
    }
}
