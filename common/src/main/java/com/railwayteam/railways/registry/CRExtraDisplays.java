package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.distant_signals.SignalDisplaySource;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;

public class CRExtraDisplays {
    public static boolean registered = false;

    // register the source, working independently of mod loading order
    public static void register() {
        Block maybeRegistered;
        try {
            maybeRegistered = AllBlocks.TRACK_SIGNAL.get();
        } catch (NullPointerException ignored) {
            maybeRegistered = null;
        }
        Create.REGISTRATE.addRegisterCallback("track_signal", Registry.BLOCK_REGISTRY, CRExtraDisplays::addSignalSource);
        if (maybeRegistered != null) {
            addSignalSource(maybeRegistered);
        }
    }

    public static void addSignalSource(Block block) {
        if (registered) return;
        assignDataBehaviour(new SignalDisplaySource()).accept(block);
        registered = true;
    }
}
