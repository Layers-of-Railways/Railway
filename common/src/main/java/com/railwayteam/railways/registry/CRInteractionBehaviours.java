package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.moving_bes.GuiBlockMovingInteractionBehaviour;
import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CRInteractionBehaviours {
    public static void register() {
        add(Blocks.CARTOGRAPHY_TABLE, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.CRAFTING_TABLE, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.GRINDSTONE, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.LOOM, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.SMITHING_TABLE, new GuiBlockMovingInteractionBehaviour());
        add(Blocks.STONECUTTER, new GuiBlockMovingInteractionBehaviour());
    }

    private static void add(Block block, MovingInteractionBehaviour behaviour) {
        AllInteractionBehaviours.registerBehaviour(block, behaviour);
    }

    private static void add(ResourceLocation block, MovingInteractionBehaviour behaviour) {
        AllInteractionBehaviours.registerBehaviour(block, behaviour);
    }
}
