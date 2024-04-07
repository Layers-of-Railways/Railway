package com.railwayteam.railways.registry;

import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class CRMovementBehaviours {
    public static void register() { }

    private static void add(Block block, MovementBehaviour behaviour) {
        AllMovementBehaviours.registerBehaviour(block, behaviour);
    }

    private static void add(ResourceLocation block, MovementBehaviour behaviour) {
        AllMovementBehaviours.registerBehaviour(block, behaviour);
    }
}
