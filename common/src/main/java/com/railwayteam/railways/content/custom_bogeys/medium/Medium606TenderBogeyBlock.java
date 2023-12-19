package com.railwayteam.railways.content.custom_bogeys.medium;

import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import net.minecraft.world.phys.Vec3;

public class Medium606TenderBogeyBlock extends CRBogeyBlock {
    public Medium606TenderBogeyBlock(Properties props) {
        this(props, CRBogeyStyles.MEDIUM_6_0_6_TENDER, BogeySizes.SMALL);
    }

    protected Medium606TenderBogeyBlock(Properties props, BogeyStyle defaultStyle, BogeySizes.BogeySize size) {
        super(props, defaultStyle, size);
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 48 / 32f);
    }
}
