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

package com.railwayteam.railways.mixin_interfaces;

import com.simibubi.create.content.trains.graph.TrackEdge;

public interface ISwitchDisabledEdge {
    void setEnabled(boolean enabled);
    boolean isEnabled();

    void setAutomatic(boolean automatic);
    boolean isAutomatic();

    void setAutomaticallySelected();
    int getAutomaticallySelectedPriority();
    boolean isAutomaticallySelected();
    void ackAutomaticSelection();

    static boolean isEnabled(TrackEdge edge) {
        return ((ISwitchDisabledEdge) edge.getEdgeData()).isEnabled();
    }

    static boolean isDisabled(TrackEdge edge) {
        return !isEnabled(edge);
    }

    static boolean isAutomatic(TrackEdge edge) {
        return ((ISwitchDisabledEdge) edge.getEdgeData()).isAutomatic();
    }

    static void automaticallySelect(TrackEdge edge) {
        ((ISwitchDisabledEdge) edge.getEdgeData()).setAutomaticallySelected();
    }
}
