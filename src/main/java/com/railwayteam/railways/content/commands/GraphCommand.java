package com.railwayteam.railways.content.commands;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.api.RailLineSegmentManager;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Railways.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GraphCommand {
    public static RailLineSegmentManager SEGMENT_MANAGER = RailLineSegmentManager.getInstance();

    @SubscribeEvent
    public static void reg(RegisterCommandsEvent rce) {
        rce.getDispatcher().register(Commands.literal("graph")
                .then(Commands.literal("set")
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> {
                                    return RailLineSegmentManager.addTrack(BlockPosArgument.getBlockPos(context, "position"));
                                })))
                .then(Commands.literal("get")
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(context -> {
                                    boolean found = RailLineSegmentManager.containsTrack(BlockPosArgument.getBlockPos(context, "position"));
                                    if (context.getSource().getEntity() != null) {
                                        context.getSource().sendErrorMessage(new StringTextComponent("track was " + (found ? "" : "not ") + "found in Graph"));
                                    }
                                    return 1;
                                })))
                .executes(context -> {
                    context.getSource().sendErrorMessage(new StringTextComponent("usage: graph set|get <BlockPos>"));
                    return 1;
                })
        );
    }
}
