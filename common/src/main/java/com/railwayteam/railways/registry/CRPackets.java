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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxDisposeAllPacket;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxEquipPacket;
import com.railwayteam.railways.content.custom_tracks.casing.SlabUseOnCurvePacket;
import com.railwayteam.railways.multiloader.PacketSet;
import com.railwayteam.railways.util.packet.*;

public class CRPackets {
    public static final PacketSet PACKETS = PacketSet.builder(Railways.MODID, 12) // increment version on changes

            .c2s(MountedToolboxDisposeAllPacket.class, MountedToolboxDisposeAllPacket::new)
            .c2s(MountedToolboxEquipPacket.class, MountedToolboxEquipPacket::new)
            .c2s(SlabUseOnCurvePacket.class, SlabUseOnCurvePacket::new)
            .c2s(BogeyStyleSelectionPacket.class, BogeyStyleSelectionPacket::new)
            .c2s(DismountCameraPacket.class, DismountCameraPacket::new)
            .c2s(CameraMovePacket.class, CameraMovePacket::new)
            .c2s(SpyConductorInteractPacket.class, SpyConductorInteractPacket::new)
            .c2s(JourneymapConfigurePacket.class, JourneymapConfigurePacket::new)
            .c2s(ConfigureDevCapeC2SPacket.class, ConfigureDevCapeC2SPacket::new)
            .c2s(TagCycleSelectionPacket.class, TagCycleSelectionPacket::new)
            .c2s(CurvedTrackHandcarPlacementPacket.class, CurvedTrackHandcarPlacementPacket::new)

            .s2c(JukeboxCartPacket.class, JukeboxCartPacket::new)
            .s2c(MountedToolboxSyncPacket.class, MountedToolboxSyncPacket::new)
            .s2c(ModVersionPacket.class, ModVersionPacket::new)
            .s2c(CarriageContraptionEntityUpdatePacket.class, CarriageContraptionEntityUpdatePacket::new)
            .s2c(ChopTrainEndPacket.class, ChopTrainEndPacket::new)
            .s2c(AddTrainEndPacket.class, AddTrainEndPacket::new)
            .s2c(TrackCouplerClientInfoPacket.class, TrackCouplerClientInfoPacket::new)
            .s2c(TrainMarkerDataUpdatePacket.class, TrainMarkerDataUpdatePacket::new)
            .s2c(OverridableSignalPacket.class, OverridableSignalPacket::new)
            .s2c(SwitchDataUpdatePacket.class, SwitchDataUpdatePacket::new)
            .s2c(SetCameraViewPacket.class, SetCameraViewPacket::new)
            .s2c(CameraMovePacket.class, CameraMovePacket::new)
            .s2c(ConfigureDevCapeS2CPacket.class, ConfigureDevCapeS2CPacket::new)

            .build();
}
