package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class SpawnPointCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("spawnpoint").requires((p_198699_0_) -> {
         return p_198699_0_.hasPermissionLevel(2);
      }).executes((p_198697_0_) -> {
         return setSpawnPoint(p_198697_0_.getSource(), Collections.singleton(p_198697_0_.getSource().asPlayer()), new BlockPos(p_198697_0_.getSource().getPos()));
      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_198694_0_) -> {
         return setSpawnPoint(p_198694_0_.getSource(), EntityArgument.getPlayers(p_198694_0_, "targets"), new BlockPos(p_198694_0_.getSource().getPos()));
      }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_198698_0_) -> {
         return setSpawnPoint(p_198698_0_.getSource(), EntityArgument.getPlayers(p_198698_0_, "targets"), BlockPosArgument.getBlockPos(p_198698_0_, "pos"));
      }))));
   }

   private static int setSpawnPoint(CommandSource source, Collection<ServerPlayerEntity> targets, BlockPos pos) {
      for(ServerPlayerEntity serverplayerentity : targets) {
         serverplayerentity.setRespawnPosition(pos, true, false);
      }

      if (targets.size() == 1) {
         source.sendFeedback(new TranslationTextComponent("commands.spawnpoint.success.single", pos.getX(), pos.getY(), pos.getZ(), targets.iterator().next().getDisplayName()), true);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.spawnpoint.success.multiple", pos.getX(), pos.getY(), pos.getZ(), targets.size()), true);
      }

      return targets.size();
   }
}