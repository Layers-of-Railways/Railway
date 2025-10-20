/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.content.custom_bogeys.renderer.unified;

import com.railwayteam.railways.content.custom_bogeys.renderer.unified.impl.UnifiedBogeyRenderer;
import com.railwayteam.railways.content.custom_bogeys.renderer.unified.impl.UnifiedBogeyVisual;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.bogey.BogeyVisualizer;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface BogeyDisplay {
    void update(CompoundTag bogeyData, float wheelAngle);

    interface EntityAware extends BogeyDisplay {
        void entityUpdate(CarriageContraptionEntity cce);
    }

    @FunctionalInterface
    interface Factory {
        BogeyDisplay create(ElementProvider<?> prov, boolean inContraption);

        default @Nullable BogeyRenderer createCustomRenderer() {
            return null;
        }

        default @Nullable BogeyVisualizer createCustomVisualizer() {
            return null;
        }
    }

    @FunctionalInterface
    interface SimpleFactory extends Factory {
        BogeyDisplay create(ElementProvider<?> prov);

        @Override
        @ApiStatus.NonExtendable
        default BogeyDisplay create(ElementProvider<?> prov, boolean inContraption) {
            return create(prov);
        }
    }

    static BogeyStyle.SizeRenderer createSizeRenderer(Factory factory) {
        return new BogeyStyle.SizeRenderer(new UnifiedBogeyRenderer(factory), UnifiedBogeyVisual.create(factory));
    }

    static BogeyStyle.SizeRenderer createSizeRenderer(SimpleFactory factory) {
        return new BogeyStyle.SizeRenderer(new UnifiedBogeyRenderer(factory), UnifiedBogeyVisual.create(factory));
    }
}
