package com.railwayteam.railways.registry;

import com.google.common.collect.ImmutableSet;
import com.railwayteam.railways.content.distant_signals.SignalDisplaySource;
import com.railwayteam.railways.mixin.AccessorBlockEntityType;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.content.redstone.displayLink.DisplayBehaviour;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class CRExtraRegistration {
    public static boolean registeredSignalSource = false;
    public static boolean registeredVentAsCopycat = false;

    // register the source, working independently of mod loading order
    public static void register() {
        platformSpecificRegistration();
        addSignalSource();
    }

    public static void addVentAsCopycat(BlockEntityType<?> object) {
        if (registeredVentAsCopycat) return;
        Block ventBlock;
        try {
            ventBlock = CRBlocks.CONDUCTOR_VENT.get();
        } catch (NullPointerException ignored) {
            return;
        }
        Set<Block> validBlocks = ((AccessorBlockEntityType) object).getValidBlocks();
        validBlocks = new ImmutableSet.Builder<Block>()
                .add(validBlocks.toArray(Block[]::new))
                .add(ventBlock)
                .build();
        ((AccessorBlockEntityType) object).setValidBlocks(validBlocks);
        registeredVentAsCopycat = true;
    }

    public static void addSignalSource() {
        if (registeredSignalSource) return;
        DisplayBehaviour signalDisplaySource = AllDisplayBehaviours.register(Create.asResource("track_signal_source"), new SignalDisplaySource());
        AllDisplayBehaviours.assignBlock(signalDisplaySource, Create.asResource("track_signal"));
        registeredSignalSource = true;
    }

    @ExpectPlatform
    public static void platformSpecificRegistration() {
        throw new AssertionError();
    }
}
