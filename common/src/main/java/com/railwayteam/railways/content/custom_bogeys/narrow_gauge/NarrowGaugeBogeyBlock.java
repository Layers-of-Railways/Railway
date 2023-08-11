package com.railwayteam.railways.content.custom_bogeys.narrow_gauge;

import com.railwayteam.railways.content.custom_bogeys.DoubleAxleBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class NarrowGaugeBogeyBlock extends DoubleAxleBogeyBlock {

    public final NarrowGaugeStandardStyle style;

    public static NonNullFunction<Properties, NarrowGaugeBogeyBlock> create(NarrowGaugeStandardStyle style) {
        return (props) -> new NarrowGaugeBogeyBlock(props, style);
    }

    public NarrowGaugeBogeyBlock(Properties props, NarrowGaugeStandardStyle style) {
        super(props, style.style.get(), style.size.get());
        this.style = style;
    }

    @Override
    public TrackType getTrackType(BogeyStyle style) {
        return CRTrackType.NARROW_GAUGE;
    }

    @Override
    public Vec3 getConnectorAnchorOffset() {
        return switch (style) {
            case SMALL, SCOTCH_YOKE -> new Vec3(0, 7 / 32f, 32 / 32f);
            case DOUBLE_SCOTCH_YOKE -> new Vec3(0, 7 / 32f, 38 / 32f);
            default -> new Vec3(0, 7 / 32f, 32 / 32f);
        };
    }

    @Override
    public double getWheelRadius() {
        return (size == BogeySizes.LARGE ? 12.5 : 6.5) / 16d;
    }

    public enum NarrowGaugeStandardStyle {
        SMALL(() -> CRBogeyStyles.NARROW_DEFAULT, () -> BogeySizes.SMALL),
        SCOTCH_YOKE(() -> CRBogeyStyles.NARROW_DEFAULT, () -> BogeySizes.LARGE),
        DOUBLE_SCOTCH_YOKE(() -> CRBogeyStyles.NARROW_DOUBLE_SCOTCH, () -> BogeySizes.LARGE)
        ;
        public final Supplier<BogeyStyle> style;
        public final Supplier<BogeySize> size;

        NarrowGaugeStandardStyle(Supplier<BogeyStyle> style, Supplier<BogeySize> size) {
            this.style = style;
            this.size = size;
        }
    }
}
