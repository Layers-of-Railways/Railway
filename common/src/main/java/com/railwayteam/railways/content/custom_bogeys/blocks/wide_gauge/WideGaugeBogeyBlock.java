package com.railwayteam.railways.content.custom_bogeys.blocks.wide_gauge;

import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.phys.Vec3;

public class WideGaugeBogeyBlock extends CRBogeyBlock {
    public static NonNullFunction<Properties, WideGaugeBogeyBlock> create(boolean large) {
        return (props) -> new WideGaugeBogeyBlock(props, large ? BogeySizes.LARGE : BogeySizes.SMALL);
    }

    protected WideGaugeBogeyBlock(Properties props, BogeySize size) {
        this(props, CRBogeyStyles.WIDE_DEFAULT, size);
    }

    protected WideGaugeBogeyBlock(Properties props, BogeyStyle style, BogeySize size) {
        super(props, style, size);
    }

    @Override
    public TrackType getTrackType(BogeyStyle style) {
        return CRTrackType.WIDE_GAUGE;
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return new Vec3(0, 7 / 32f, size == BogeySizes.SMALL ? 48 / 32f : 36 / 32f);
    }

    @Override
    public double getWheelRadius() {
        return (size == BogeySizes.LARGE ? 12.5 : 6.5) / 16d;
    }
}
