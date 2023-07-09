package com.railwayteam.railways;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class Config {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_SEMAPHORE = "semaphore";
    public static final String CATEGORY_OPTIMIZATION = "optimization";

    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final ForgeConfigSpec CLIENT_CONFIG;

//    public static ForgeConfigSpec.BooleanValue HIBYE;
    public static ForgeConfigSpec.IntValue FAR_TRAIN_SYNC_TICKS;
    public static ForgeConfigSpec.IntValue NEAR_TRAIN_SYNC_TICKS;
    public static ForgeConfigSpec.IntValue JOURNEYMAP_UPDATE_TICKS;
    public static ForgeConfigSpec.IntValue JOURNEYMAP_REMOVE_OBSOLETE_TICKS;
    public static ForgeConfigSpec.BooleanValue EXTENDED_COUPLER_DEBUG;
    public static ForgeConfigSpec.BooleanValue SKIP_CLIENT_DERAILING;
    public static ForgeConfigSpec.BooleanValue CONDUCTOR_SPY_SHADER;

    public static ForgeConfigSpec.BooleanValue SIMPLIFIED_SEMAPHORE_PLACEMENT;
    public static ForgeConfigSpec.BooleanValue SEMAPHORES_FLIP_YELLOW_ORDER;
    public static ForgeConfigSpec.BooleanValue CONDUCTOR_WHISTLE_REQUIRES_OWNING;
    public static ForgeConfigSpec.BooleanValue STRICT_COUPLER;
    public static ForgeConfigSpec.BooleanValue REGISTER_MISSING_TRACKS;
    public static ForgeConfigSpec.BooleanValue FLIP_DISTANT_SWITCHES;
    public static ForgeConfigSpec.IntValue SWITCH_PLACEMENT_RANGE;
    public static ForgeConfigSpec.BooleanValue DISABLE_DATAFIXER;
    public static ForgeConfigSpec.IntValue MAX_CONDUCTOR_VENT_LENGTH;
    public static ForgeConfigSpec.BooleanValue DISABLE_TRAIN_COLLISION;
    public static ForgeConfigSpec.BooleanValue OPTIMIZE_FUNNEL_BELT_INTERACTION;


    static {

        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        JOURNEYMAP_UPDATE_TICKS = CLIENT_BUILDER.comment("Journeymap train overlay update time (in ticks)").defineInRange("journeymapUpdateTicks", 1, 1, 600);
        JOURNEYMAP_REMOVE_OBSOLETE_TICKS = CLIENT_BUILDER.comment("Journeymap train overlay old marker removal check time (in ticks)").defineInRange("journeymapObsolescenceCheckTicks", 200, 10, 1200);
        EXTENDED_COUPLER_DEBUG = CLIENT_BUILDER.comment("Show extended debug info in coupler goggle overlay").define("extendedCouplerDebug", false);
        SKIP_CLIENT_DERAILING = CLIENT_BUILDER.comment("Skip clientside train derailing. This prevents stuttering when a train places tracks, but trains will not appear derailed when they crash").define("skipClientsideDerailing", false);
        CONDUCTOR_SPY_SHADER = CLIENT_BUILDER.comment("Use a scanline shader when spying through a conductor").define("conductorSpyShader", true);

        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        setupGeneralCategory(SERVER_BUILDER);
        SERVER_BUILDER.pop().comment("Semaphore settings").push(CATEGORY_SEMAPHORE);
        setupSemaphoreCategory(SERVER_BUILDER);
        SERVER_BUILDER.pop().comment("Optimization settings").push(CATEGORY_OPTIMIZATION);
        setupOptimizationCategory(SERVER_BUILDER);
        SERVER_BUILDER.pop();


        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupGeneralCategory(ForgeConfigSpec.Builder builder) {
        //ROLLER_SU = COMMON_BUILDER.comment("Base SU for the Rolling Machine").defineInRange("rollingMachineSU", 4, 0, Integer.MAX_VALUE);
//        HIBYE = COMMON_BUILDER.comment("sorceror").define("hibye", false);
        FAR_TRAIN_SYNC_TICKS = builder.comment("Outside-of-render-distance train sync time (in ticks)").defineInRange("farTrainUpdateTicks", 200, 10, 600);
        NEAR_TRAIN_SYNC_TICKS = builder.comment("In-render-distance train sync time (in ticks)").defineInRange("nearTrainUpdateTicks", 1, 1, 600);
        CONDUCTOR_WHISTLE_REQUIRES_OWNING = builder.comment("Conductor whistle is limited to the owner of a train").define("mustOwnBoundTrain", false);
        STRICT_COUPLER = builder.comment("Coupler will require points to be on the same or adjacent track edge, this will prevent the coupler from working if there is any form of junction in between the two points.").define("strictCoupler", false);
        REGISTER_MISSING_TRACKS = builder.comment("Register integration tracks for mods that are not present").define("registerMissingTracks", false);
        FLIP_DISTANT_SWITCHES = builder.comment("Allow controlling Brass Switches remotely when approaching them on a train").define("flipDistantSwitches", true);
        SWITCH_PLACEMENT_RANGE = builder.comment("Placement range for switches").defineInRange("switchPlacementRange", 64, 16, 128);
        DISABLE_DATAFIXER = builder.comment("Disable Steam 'n Rails datafixers. Do not enable this config if you world contains pre-Create 0.5.1 monobogeys, because then they will be destroyed").define("disableDatafixer", false);
        MAX_CONDUCTOR_VENT_LENGTH = builder.comment("Maximum length of conductor vents").defineInRange("maxConductorVentLength", 64, 1, Integer.MAX_VALUE);
    }

    private static void setupSemaphoreCategory(ForgeConfigSpec.Builder builder) {
        SIMPLIFIED_SEMAPHORE_PLACEMENT = builder.comment("Simplified semaphore placement").define("simplifiedSemaphorePlacement", true);
        //Whether yellow is above red when semaphores are flipped upside-down
        SEMAPHORES_FLIP_YELLOW_ORDER = builder.comment("Whether semaphore color order is reversed when the semaphores are oriented upside-down")
            .define("semaphoresFlipYellowOrder", false);
    }

    private static void setupOptimizationCategory(ForgeConfigSpec.Builder builder) {
        DISABLE_TRAIN_COLLISION = builder.comment("Disable collisions between trains. May have significant performance impact if playing with many trains").define("disableTrainCollision", false);
        OPTIMIZE_FUNNEL_BELT_INTERACTION = builder.comment("Optimizes belts placing items onto belts by skipping the calculation of an unused, but expensive, variable").define("optimizeFunnelBeltInteraction", false);
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }
}