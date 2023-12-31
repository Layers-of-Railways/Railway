package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.moving_bes.CraftingTableMovementBehaviour;
import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CRInteractionBehaviours {
    public static void register() {
        add(Blocks.CRAFTING_TABLE, new CraftingTableMovementBehaviour());
    }

    private static void add(Block block, MovementBehaviour behaviour) {
        AllMovementBehaviours.registerBehaviour(block, behaviour);
    }

    private static void add(ResourceLocation block, MovementBehaviour behaviour) {
        AllMovementBehaviours.registerBehaviour(block, behaviour);
    }

    private static void add(Block block, MovingInteractionBehaviour behaviour) {
        AllInteractionBehaviours.registerBehaviour(block, behaviour);
    }

    private static void add(ResourceLocation block, MovingInteractionBehaviour behaviour) {
        AllInteractionBehaviours.registerBehaviour(block, behaviour);
    }
}
