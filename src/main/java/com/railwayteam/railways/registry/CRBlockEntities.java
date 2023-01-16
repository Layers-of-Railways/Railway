package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerRenderer;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerTileEntity;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyTileEntity;
import com.railwayteam.railways.content.semaphore.SemaphoreBlockEntity;
import com.railwayteam.railways.content.semaphore.SemaphoreRenderer;
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
        .validBlocks(CRBlocks.MONO_BOGEY)
        .register();


    @SuppressWarnings("EmptyMethod")
    public static void register() {}
}
