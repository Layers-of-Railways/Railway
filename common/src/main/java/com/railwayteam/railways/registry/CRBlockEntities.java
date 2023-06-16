package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagBlockEntity;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagRenderer;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlockEntity;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerRenderer;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlockEntity;
import com.railwayteam.railways.content.semaphore.SemaphoreBlockEntity;
import com.railwayteam.railways.content.semaphore.SemaphoreRenderer;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackBlockEntity;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackRenderer;
import com.railwayteam.railways.content.switches.TrackSwitchRenderer;
import com.railwayteam.railways.content.switches.TrackSwitchTileEntity;
import com.railwayteam.railways.content.tender.TenderBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeyBlockEntityRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CRBlockEntities {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final BlockEntityEntry<TenderBlockEntity> TENDER_BE = null;
    public static final BlockEntityEntry<SemaphoreBlockEntity> SEMAPHORE = REGISTRATE.blockEntity("semaphore", SemaphoreBlockEntity::new)
        .validBlocks(CRBlocks.SEMAPHORE)
        .renderer(() -> SemaphoreRenderer::new)
        .register();

    public static final BlockEntityEntry<TrackCouplerBlockEntity> TRACK_COUPLER = REGISTRATE.blockEntity("track_coupler", TrackCouplerBlockEntity::new)
        .validBlocks(CRBlocks.TRACK_COUPLER)
        .renderer(() -> TrackCouplerRenderer::new)
        .register();

    public static final BlockEntityEntry<TrackSwitchTileEntity> ANDESITE_SWITCH = REGISTRATE.blockEntity("track_switch_andesite", TrackSwitchTileEntity::new)
      .validBlocks(CRBlocks.ANDESITE_SWITCH)
      .renderer(() -> TrackSwitchRenderer::new)
      .register();

    public static final BlockEntityEntry<TrackSwitchTileEntity> BRASS_SWITCH = REGISTRATE.blockEntity("track_switch_brass", TrackSwitchTileEntity::new)
      .validBlocks(CRBlocks.BRASS_SWITCH)
      .renderer(() -> TrackSwitchRenderer::new)
      .register();

    public static final BlockEntityEntry<MonoBogeyBlockEntity> MONO_BOGEY = REGISTRATE
        .blockEntity("mono_bogey", MonoBogeyBlockEntity::new)
        .renderer(() -> BogeyBlockEntityRenderer::new)
        .validBlocks(CRBlocks.MONO_BOGEY)
        .register();

    public static final BlockEntityEntry<ConductorWhistleFlagBlockEntity> CONDUCTOR_WHISTLE_FLAG = REGISTRATE.blockEntity("conductor_whistle", ConductorWhistleFlagBlockEntity::new)
        .validBlocks(CRBlocks.CONDUCTOR_WHISTLE_FLAG)
        .renderer(() -> ConductorWhistleFlagRenderer::new)
        .register();

    public static final BlockEntityEntry<DieselSmokeStackBlockEntity> DIESEL_SMOKE_STACK = REGISTRATE.blockEntity("diesel_smokestack", DieselSmokeStackBlockEntity::new)
        .validBlocks(CRBlocks.DIESEL_STACK)
        .renderer(() -> DieselSmokeStackRenderer::new)
        .register();


    @SuppressWarnings("EmptyMethod")
    public static void register() {}
}
