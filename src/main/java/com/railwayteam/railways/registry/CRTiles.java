package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.tiles.ters.SpeedSignalTileRenderer;
import com.railwayteam.railways.content.tiles.tiles.SignalTileEntity;
import com.railwayteam.railways.content.tiles.tiles.SpeedSignalTileEntity;
import com.railwayteam.railways.content.tiles.tiles.StationSensorRailTileEntity;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

import static com.railwayteam.railways.registry.CRBlocks.*;

public class CRTiles {
    public static TileEntityEntry<StationSensorRailTileEntity> R_TE_STATION_SENSOR;
    public static TileEntityEntry<SignalTileEntity> R_TE_SIGNAL;
    public static TileEntityEntry<SpeedSignalTileEntity> R_TE_NUMERICAL_SIGNAL;

    public static void register(Registrate reg) {
        R_TE_STATION_SENSOR = reg.tileEntity(StationSensorRailTileEntity.NAME, StationSensorRailTileEntity::new)
                .validBlock(() -> R_BLOCK_STATION_SENSOR.get())
                .register();

        R_TE_SIGNAL = reg.tileEntity(SignalTileEntity.NAME, SignalTileEntity::new)
                .validBlock(() -> R_BLOCK_SIGNAL.get())
                .register();

        R_TE_NUMERICAL_SIGNAL = reg.tileEntity("numerical_signal", SpeedSignalTileEntity::new)
                .validBlock(() -> R_BLOCK_NUMERICAL_SIGNAL.get())
                .renderer(() -> SpeedSignalTileRenderer::new)
                .register();
    }
}
