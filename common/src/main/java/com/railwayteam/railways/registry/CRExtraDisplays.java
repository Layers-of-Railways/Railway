package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.distant_signals.SignalDisplaySource;
import com.simibubi.create.Create;
import net.minecraft.core.Registry;

import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;

public class CRExtraDisplays {
    public static void register() {
        Create.REGISTRATE.addRegisterCallback("track_signal", Registry.BLOCK_REGISTRY, (block) -> {
            assignDataBehaviour(new SignalDisplaySource()).accept(block);
        });
    }
}
