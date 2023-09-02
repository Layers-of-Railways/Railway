package com.railwayteam.railways.registry;

import com.railwayteam.railways.ponder.ConductorScenes;
import com.railwayteam.railways.ponder.DoorScenes;
import com.railwayteam.railways.ponder.TrainScenes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.foundation.CustomPonderRegistrationHelper;
import net.minecraft.resources.ResourceLocation;


public class CRPonderIndex {
    static final CustomPonderRegistrationHelper<ItemProviderEntry<?>> HELPER = new CustomPonderRegistrationHelper<>(Create.ID, RegistryEntry::getId);

    public static void register() {
        HELPER.forComponents(CRBlocks.SEMAPHORE)
            .addStoryBoard("semaphore", TrainScenes::signaling);
        HELPER.forComponents(CRBlocks.TRACK_COUPLER)
                .addStoryBoard("coupler", TrainScenes::coupling);
        //fixme
//        HELPER.forComponentsIterable(CRItems.ITEM_CONDUCTOR_CAP.values())
//            .addStoryBoard("conductor", ConductorScenes::constructing)
//            .addStoryBoard("conductor_redstone", ConductorScenes::redstoning)
//            .addStoryBoard("conductor", ConductorScenes::toolboxing);
        HELPER.forComponents(
            AllBlocks.ANDESITE_DOOR,
            AllBlocks.BRASS_DOOR,
            AllBlocks.COPPER_DOOR,
            AllBlocks.TRAIN_DOOR,
            AllBlocks.FRAMED_GLASS_DOOR
        )
            .addStoryBoard("door_modes", DoorScenes::modes);
        HELPER.forComponents(CRBlocks.ANDESITE_SWITCH, CRBlocks.BRASS_SWITCH)
                .addStoryBoard("switch", TrainScenes::trackSwitch);
    }
}
