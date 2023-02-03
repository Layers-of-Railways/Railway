package com.railwayteam.railways;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_SEMAPHORE = "semaphore";

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec CLIENT_CONFIG;

//    public static ForgeConfigSpec.BooleanValue HIBYE;
    public static ForgeConfigSpec.BooleanValue SIMPLIFIED_SEMAPHORE_PLACEMENT;
    public static ForgeConfigSpec.BooleanValue SEMAPHORES_FLIP_YELLOW_ORDER;
    public static ForgeConfigSpec.BooleanValue CONDUCTOR_WHISTLE_REQUIRES_OWNING;


    static {

        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        setupGeneralCategory(COMMON_BUILDER);
        COMMON_BUILDER.pop().comment("Semaphore settings").push(CATEGORY_SEMAPHORE);
        setupSemaphoreCategory(COMMON_BUILDER);
        COMMON_BUILDER.pop();


        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupGeneralCategory(ForgeConfigSpec.Builder builder) {
        //ROLLER_SU = COMMON_BUILDER.comment("Base SU for the Rolling Machine").defineInRange("rollingMachineSU", 4, 0, Integer.MAX_VALUE);
//        HIBYE = COMMON_BUILDER.comment("sorceror").define("hibye", false);
        CONDUCTOR_WHISTLE_REQUIRES_OWNING = builder.comment("Conductor whistle is limited to the owner of a train").define("mustOwnBoundTrain", false);
    }

    private static void setupSemaphoreCategory(ForgeConfigSpec.Builder builder) {
        SIMPLIFIED_SEMAPHORE_PLACEMENT = builder.comment("Simplified semaphore placement").define("simplifiedSemaphorePlacement", true);
        //Whether yellow is above red when semaphores are flipped upside-down
        SEMAPHORES_FLIP_YELLOW_ORDER = builder.comment("Whether semaphore color order is reversed when the semaphores are oriented upside-down")
            .define("semaphoresFlipYellowOrder", false);
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    @SuppressWarnings({"unused", "EmptyMethod"})
    public static void onLoad(final ModConfigEvent.Loading configEvent) {

    }
}