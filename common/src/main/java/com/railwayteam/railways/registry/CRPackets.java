package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxDisposeAllPacket;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxEquipPacket;
import com.railwayteam.railways.content.custom_tracks.casing.SlabUseOnCurvePacket;
import com.railwayteam.railways.multiloader.PacketSet;
import com.railwayteam.railways.util.packet.*;

public class CRPackets {
    public static final PacketSet PACKETS = PacketSet.builder(Railways.MODID, 8) // increment version on changes

            .c2s(MountedToolboxDisposeAllPacket.class, MountedToolboxDisposeAllPacket::new)
            .c2s(MountedToolboxEquipPacket.class, MountedToolboxEquipPacket::new)
            .c2s(SlabUseOnCurvePacket.class, SlabUseOnCurvePacket::new)
            .c2s(BogeyStyleSelectionPacket.class, BogeyStyleSelectionPacket::new)
            .c2s(DismountCameraPacket.class, DismountCameraPacket::new)
            .c2s(CameraMovePacket.class, CameraMovePacket::new)
            .c2s(SpyConductorInteractPacket.class, SpyConductorInteractPacket::new)

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

            .build();
}
