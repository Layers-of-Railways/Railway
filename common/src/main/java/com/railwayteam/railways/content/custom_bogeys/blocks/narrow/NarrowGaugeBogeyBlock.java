/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.custom_bogeys.blocks.narrow;

import com.railwayteam.railways.content.custom_bogeys.blocks.base.CRBogeyBlock;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class NarrowGaugeBogeyBlock extends CRBogeyBlock {

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
        };
    }

    @Override
    public double getWheelRadius() {
        return (size == BogeySizes.LARGE ? 8.75 : 6.5) / 16d;
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
