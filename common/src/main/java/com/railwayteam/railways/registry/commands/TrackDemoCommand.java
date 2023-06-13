package com.railwayteam.railways.registry.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.railwayteam.railways.compat.tracks.SoftIngredient;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Optional;

public class TrackDemoCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("track_demo")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(ctx -> {
                    BlockPos origin = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
                    BlockPos.MutableBlockPos pos = origin.mutable();
                    for (TrackMaterial material : TrackMaterial.ALL.values()) {
                        ServerLevel level = ctx.getSource().getLevel();

                        BlockState trackState = material.getBlock().defaultBlockState();
                        level.setBlockAndUpdate(pos, trackState);

                        if (material.sleeperIngredient != null && !material.sleeperIngredient.isEmpty()) {
                            if (material.sleeperIngredient.values.length >= 1
                                && material.sleeperIngredient.values[0] instanceof Ingredient.ItemValue itemValue
                                && itemValue.getItems().stream().findFirst().orElseGet(() -> new ItemStack(Blocks.AIR)).getItem() instanceof BlockItem blockItem) {

                                BlockState baseState = blockItem.getBlock().defaultBlockState();
                                if (baseState.hasProperty(SlabBlock.TYPE))
                                    baseState = baseState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
                                level.setBlockAndUpdate(pos.east(3), baseState);
                                level.setBlockAndUpdate(pos.east(3).above(), baseState);
                            } else if (material.sleeperIngredient instanceof SoftIngredient softIngredient) {
                                Optional<Block> baseBlock = Registry.BLOCK.getOptional(softIngredient.item);
                                if (baseBlock.isPresent()) {
                                    BlockState baseState = baseBlock.get().defaultBlockState();
                                    if (baseState.hasProperty(SlabBlock.TYPE))
                                        baseState = baseState.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
                                    level.setBlockAndUpdate(pos.east(3), baseState);
                                    level.setBlockAndUpdate(pos.east(3).above(), baseState);
                                }
                            }
                        }

                        pos.move(0, 0, 1);
                    }

                    ctx.getSource().sendSuccess(Components.literal("Placed tracks"), true);
                    return 1;
                }));
    }
}
