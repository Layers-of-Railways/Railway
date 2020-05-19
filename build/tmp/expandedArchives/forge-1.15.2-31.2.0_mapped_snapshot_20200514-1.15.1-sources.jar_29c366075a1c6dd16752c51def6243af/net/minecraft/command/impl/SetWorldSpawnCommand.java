package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class SetWorldSpawnCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("setworldspawn").requires((p_198704_0_) -> {
         return p_198704_0_.hasPermissionLevel(2);
      }).executes((p_198700_0_) -> {
         return setSpawn(p_198700_0_.getSource(), new BlockPos(p_198700_0_.getSource().getPos()));
      }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_198703_0_) -> {
         return setSpawn(p_198703_0_.getSource(), BlockPosArgument.getBlockPos(p_198703_0_, "pos"));
      })));
   }

   private static int setSpawn(CommandSource source, BlockPos pos) {
      source.getWorld().setSpawnPoint(pos);
      source.getServer().getPlayerList().sendPacketToAllPlayers(new SSpawnPositionPacket(pos));
      source.sendFeedback(new TranslationTextComponent("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ()), true);
      return 1;
   }
}