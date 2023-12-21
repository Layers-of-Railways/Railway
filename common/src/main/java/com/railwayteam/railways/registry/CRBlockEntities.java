package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.buffer.DyeableBlockEntity;
import com.railwayteam.railways.content.buffer.TrackBufferBlockEntity;
import com.railwayteam.railways.content.buffer.WoodVariantTrackBufferBlockEntity;
import com.railwayteam.railways.content.buffer.headstock.HeadstockBlockEntity;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagBlockEntity;
import com.railwayteam.railways.content.conductor.whistle.ConductorWhistleFlagRenderer;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlockEntity;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerRenderer;
import com.railwayteam.railways.content.custom_bogeys.CRBogeyBlockEntity;
import com.railwayteam.railways.content.custom_bogeys.invisible.InvisibleBogeyBlockEntity;
import com.railwayteam.railways.content.custom_bogeys.monobogey.InvisibleMonoBogeyBlockEntity;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlockEntity;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionBlockEntity;
import com.railwayteam.railways.content.custom_tracks.generic_crossing.GenericCrossingBlockEntity;
import com.railwayteam.railways.content.semaphore.SemaphoreBlockEntity;
import com.railwayteam.railways.content.semaphore.SemaphoreRenderer;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackBlockEntity;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackRenderer;
import com.railwayteam.railways.content.smokestack.SmokeStackBlockEntity;
import com.railwayteam.railways.content.switches.TrackSwitchRenderer;
import com.railwayteam.railways.content.switches.TrackSwitchBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeyBlockEntityRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CRBlockEntities {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final BlockEntityEntry<SemaphoreBlockEntity> SEMAPHORE = REGISTRATE.blockEntity("semaphore", SemaphoreBlockEntity::new)
        .validBlocks(CRBlocks.SEMAPHORE)
        .renderer(() -> SemaphoreRenderer::new)
        .register();

    public static final BlockEntityEntry<TrackCouplerBlockEntity> TRACK_COUPLER = REGISTRATE.blockEntity("track_coupler", TrackCouplerBlockEntity::new)
        .validBlocks(CRBlocks.TRACK_COUPLER)
        .renderer(() -> TrackCouplerRenderer::new)
        .register();

    public static final BlockEntityEntry<TrackBufferBlockEntity> TRACK_BUFFER = REGISTRATE.blockEntity("track_buffer", TrackBufferBlockEntity::new)
        .validBlocks(CRBlocks.TRACK_BUFFER_WIDE)
        .register();

    public static final BlockEntityEntry<WoodVariantTrackBufferBlockEntity> TRACK_BUFFER_WOOD_VARIANT = REGISTRATE.blockEntity("track_buffer_wood_variant", WoodVariantTrackBufferBlockEntity::new)
        .validBlocks(CRBlocks.TRACK_BUFFER, CRBlocks.TRACK_BUFFER_NARROW, CRBlocks.TRACK_BUFFER_MONO)
        .register();

    public static final BlockEntityEntry<TrackSwitchBlockEntity> ANDESITE_SWITCH = REGISTRATE.blockEntity("track_switch_andesite", TrackSwitchBlockEntity::new)
      .validBlocks(CRBlocks.ANDESITE_SWITCH)
      .renderer(() -> TrackSwitchRenderer::new)
      .register();

    public static final BlockEntityEntry<TrackSwitchBlockEntity> BRASS_SWITCH = REGISTRATE.blockEntity("track_switch_brass", TrackSwitchBlockEntity::new)
      .validBlocks(CRBlocks.BRASS_SWITCH)
      .renderer(() -> TrackSwitchRenderer::new)
      .register();

    public static final BlockEntityEntry<MonoBogeyBlockEntity> MONO_BOGEY = REGISTRATE
            .blockEntity("mono_bogey", MonoBogeyBlockEntity::new)
            .renderer(() -> BogeyBlockEntityRenderer::new)
            .validBlocks(CRBlocks.MONO_BOGEY)
            .register();

    public static final BlockEntityEntry<InvisibleBogeyBlockEntity> INVISIBLE_BOGEY = REGISTRATE
            .blockEntity("invisible_bogey", InvisibleBogeyBlockEntity::new)
            .renderer(() -> BogeyBlockEntityRenderer::new)
            .validBlocks(CRBlocks.INVISIBLE_BOGEY)
            .register();

    public static final BlockEntityEntry<InvisibleMonoBogeyBlockEntity> INVISIBLE_MONO_BOGEY = REGISTRATE
        .blockEntity("invisible_mono_bogey", InvisibleMonoBogeyBlockEntity::new)
        .renderer(() -> BogeyBlockEntityRenderer::new)
        .validBlocks(CRBlocks.INVISIBLE_MONO_BOGEY)
        .register();

    public static final BlockEntityEntry<CRBogeyBlockEntity> BOGEY = REGISTRATE
            .blockEntity("bogey", CRBogeyBlockEntity::new)
            .renderer(() -> BogeyBlockEntityRenderer::new)
            .validBlocks(CRBlocks.SINGLEAXLE_BOGEY, CRBlocks.DOUBLEAXLE_BOGEY, CRBlocks.LARGE_PLATFORM_DOUBLEAXLE_BOGEY,
                CRBlocks.TRIPLEAXLE_BOGEY, CRBlocks.WIDE_DOUBLEAXLE_BOGEY, CRBlocks.WIDE_SCOTCH_BOGEY,
                CRBlocks.WIDE_COMICALLY_LARGE_BOGEY, CRBlocks.NARROW_SMALL_BOGEY, CRBlocks.NARROW_SCOTCH_BOGEY,
                CRBlocks.NARROW_DOUBLE_SCOTCH_BOGEY, CRBlocks.HANDCAR, CRBlocks.MEDIUM_BOGEY,
                CRBlocks.MEDIUM_TRIPLE_WHEEL, CRBlocks.MEDIUM_QUADRUPLE_WHEEL, CRBlocks.MEDIUM_2_0_2_TRAILING,
                CRBlocks.MEDIUM_4_0_4_TRAILING)
            .register();

    public static final BlockEntityEntry<ConductorWhistleFlagBlockEntity> CONDUCTOR_WHISTLE_FLAG = REGISTRATE.blockEntity("conductor_whistle", ConductorWhistleFlagBlockEntity::new)
        .validBlocks(CRBlocks.CONDUCTOR_WHISTLE_FLAG)
        .renderer(() -> ConductorWhistleFlagRenderer::new)
        .register();

    public static final BlockEntityEntry<SmokeStackBlockEntity> SMOKE_STACK = REGISTRATE.blockEntity("smokestack", SmokeStackBlockEntity::new)
        .validBlocks(CRBlocks.CABOOSESTYLE_STACK, CRBlocks.LONG_STACK, CRBlocks.COALBURNER_STACK, CRBlocks.OILBURNER_STACK, CRBlocks.STREAMLINED_STACK, CRBlocks.WOODBURNER_STACK)
        .register();

    public static final BlockEntityEntry<DieselSmokeStackBlockEntity> DIESEL_SMOKE_STACK = REGISTRATE.blockEntity("diesel_smokestack", DieselSmokeStackBlockEntity::new)
        .validBlocks(CRBlocks.DIESEL_STACK)
        .renderer(() -> DieselSmokeStackRenderer::new)
        .register();

    public static final BlockEntityEntry<CasingCollisionBlockEntity> CASING_COLLISION = REGISTRATE
        .blockEntity("casing_collision", CasingCollisionBlockEntity::new)
        .validBlocks(CRBlocks.CASING_COLLISION)
        .register();

    public static final BlockEntityEntry<GenericCrossingBlockEntity> GENERIC_CROSSING = REGISTRATE
        .blockEntity("generic_crossing", GenericCrossingBlockEntity::new)
        .validBlocks(CRBlocks.GENERIC_CROSSING)
        .register();

    public static final BlockEntityEntry<DyeableBlockEntity> DYEABLE = REGISTRATE
        .blockEntity("dyeable", DyeableBlockEntity::new)
        .validBlocks(CRBlocks.LINK_AND_PIN)
        .register();

    public static final BlockEntityEntry<HeadstockBlockEntity> HEADSTOCK = REGISTRATE
        .blockEntity("headstock", HeadstockBlockEntity::new)
        .validBlocks(CRBlocks.HEADSTOCK)
        .register();


    @SuppressWarnings("EmptyMethod")
    public static void register() {}
}
