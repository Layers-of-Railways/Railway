package com.railwayteam.railways.content.custom_bogeys;

import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import net.minecraft.world.phys.Vec3;

public class WideGaugeDoubleAxleBogeyBlock extends DoubleAxleBogeyBlock {
    public WideGaugeDoubleAxleBogeyBlock(Properties props) {
        this(props, CRBogeyStyles.WIDE_DEFAULT, BogeySizes.SMALL);
    }

    protected WideGaugeDoubleAxleBogeyBlock(Properties props, BogeyStyle style, BogeySize size) {
        super(props, style, size);
    }

    @Override
    public TrackType getTrackType(BogeyStyle style) {
        return CRTrackType.WIDE_GAUGE;
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, 48 / 32f);
    }
}
