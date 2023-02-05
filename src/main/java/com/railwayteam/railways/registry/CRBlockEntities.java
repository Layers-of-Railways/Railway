package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagRenderer;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagTileEntity;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerRenderer;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerTileEntity;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyTileEntity;
import com.railwayteam.railways.content.semaphore.SemaphoreBlockEntity;
import com.railwayteam.railways.content.semaphore.SemaphoreRenderer;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackRenderer;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackTileEntity;
import com.railwayteam.railways.content.tender.TenderBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.trains.BogeyTileEntityRenderer;
import com.simibubi.create.content.logistics.trains.track.StandardBogeyTileEntity;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.simibubi.create.Create.REGISTRATE;

public class CRBlockEntities {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final BlockEntityEntry<TenderBlockEntity> TENDER_BE = null;
    public static final BlockEntityEntry<SemaphoreBlockEntity> SEMAPHORE = REGISTRATE.tileEntity("semaphore", SemaphoreBlockEntity::new)
        .validBlocks(CRBlocks.SEMAPHORE)
        .renderer(() -> SemaphoreRenderer::new)
        .register();

    public static final BlockEntityEntry<TrackCouplerTileEntity> TRACK_COUPLER = REGISTRATE.tileEntity("track_coupler", TrackCouplerTileEntity::new)
        .validBlocks(CRBlocks.TRACK_COUPLER)
        .renderer(() -> TrackCouplerRenderer::new)
        .register();

    public static final BlockEntityEntry<MonoBogeyTileEntity> MONO_BOGEY = REGISTRATE
        .tileEntity("mono_bogey", MonoBogeyTileEntity::new)
        .renderer(() -> BogeyTileEntityRenderer::new)
        .validBlocks(CRBlocks.MONO_BOGEY, CRBlocks.MONO_BOGEY_UPSIDE_DOWN)
        .register();

    public static final BlockEntityEntry<ConductorWhistleFlagTileEntity> CONDUCTOR_WHISTLE_FLAG = REGISTRATE.tileEntity("conductor_whistle", ConductorWhistleFlagTileEntity::new)
        .validBlocks(CRBlocks.CONDUCTOR_WHISTLE_FLAG)
        .renderer(() -> ConductorWhistleFlagRenderer::new)
        .register();

    public static final BlockEntityEntry<DieselSmokeStackTileEntity> DIESEL_SMOKE_STACK = REGISTRATE.tileEntity("diesel_smokestack", DieselSmokeStackTileEntity::new)
        .validBlocks(CRBlocks.DIESEL_STACK)
        .renderer(() -> DieselSmokeStackRenderer::new)
        .register();


    @SuppressWarnings("EmptyMethod")
    public static void register() {}
}
