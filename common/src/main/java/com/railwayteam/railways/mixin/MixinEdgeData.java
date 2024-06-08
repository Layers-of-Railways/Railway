/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.switches.TrackSwitch;
import com.railwayteam.railways.mixin_interfaces.ISwitchDisabledEdge;
import com.simibubi.create.content.trains.graph.EdgeData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = EdgeData.class, remap = false)
public class MixinEdgeData implements ISwitchDisabledEdge {
    private boolean enabled = true;
    private boolean automatic = false;
    private int selectPriority = -1;

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    @Override
    public boolean isAutomatic() {
        return automatic;
    }

    @Override
    public void setAutomaticallySelected() {
        selectPriority = TrackSwitch.getSelectionPriority();
        enabled = true;
    }

    @Override
    public int getAutomaticallySelectedPriority() {
        return selectPriority;
    }

    @Override
    public boolean isAutomaticallySelected() {
        return selectPriority != -1;
    }

    @Override
    public void ackAutomaticSelection() {
        selectPriority = -1;
    }
}
