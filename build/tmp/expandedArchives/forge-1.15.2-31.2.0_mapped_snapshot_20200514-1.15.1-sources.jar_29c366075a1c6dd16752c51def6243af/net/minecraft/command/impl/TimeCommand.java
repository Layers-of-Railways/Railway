package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.TimeArgument;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class TimeCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("time").requires((p_198828_0_) -> {
         return p_198828_0_.hasPermissionLevel(2);
      }).then(Commands.literal("set").then(Commands.literal("day").executes((p_198832_0_) -> {
         return setTime(p_198832_0_.getSource(), 1000);
      })).then(Commands.literal("noon").executes((p_198825_0_) -> {
         return setTime(p_198825_0_.getSource(), 6000);
      })).then(Commands.literal("night").executes((p_198822_0_) -> {
         return setTime(p_198822_0_.getSource(), 13000);
      })).then(Commands.literal("midnight").executes((p_200563_0_) -> {
         return setTime(p_200563_0_.getSource(), 18000);
      })).then(Commands.argument("time", TimeArgument.func_218091_a()).executes((p_200564_0_) -> {
         return setTime(p_200564_0_.getSource(), IntegerArgumentType.getInteger(p_200564_0_, "time"));
      }))).then(Commands.literal("add").then(Commands.argument("time", TimeArgument.func_218091_a()).executes((p_198830_0_) -> {
         return addTime(p_198830_0_.getSource(), IntegerArgumentType.getInteger(p_198830_0_, "time"));
      }))).then(Commands.literal("query").then(Commands.literal("daytime").executes((p_198827_0_) -> {
         return sendQueryResults(p_198827_0_.getSource(), getDayTime(p_198827_0_.getSource().getWorld()));
      })).then(Commands.literal("gametime").executes((p_198821_0_) -> {
         return sendQueryResults(p_198821_0_.getSource(), (int)(p_198821_0_.getSource().getWorld().getGameTime() % 2147483647L));
      })).then(Commands.literal("day").executes((p_198831_0_) -> {
         return sendQueryResults(p_198831_0_.getSource(), (int)(p_198831_0_.getSource().getWorld().getDayTime() / 24000L % 2147483647L));
      }))));
   }

   /**
    * Returns the day time (time wrapped within a day)
    */
   private static int getDayTime(ServerWorld worldIn) {
      return (int)(worldIn.getDayTime() % 24000L);
   }

   private static int sendQueryResults(CommandSource source, int time) {
      source.sendFeedback(new TranslationTextComponent("commands.time.query", time), false);
      return time;
   }

   public static int setTime(CommandSource source, int time) {
      for(ServerWorld serverworld : source.getServer().getWorlds()) {
         serverworld.setDayTime((long)time);
      }

      source.sendFeedback(new TranslationTextComponent("commands.time.set", time), true);
      return getDayTime(source.getWorld());
   }

   public static int addTime(CommandSource source, int amount) {
      for(ServerWorld serverworld : source.getServer().getWorlds()) {
         serverworld.setDayTime(serverworld.getDayTime() + (long)amount);
      }

      int i = getDayTime(source.getWorld());
      source.sendFeedback(new TranslationTextComponent("commands.time.set", i), true);
      return i;
   }
}