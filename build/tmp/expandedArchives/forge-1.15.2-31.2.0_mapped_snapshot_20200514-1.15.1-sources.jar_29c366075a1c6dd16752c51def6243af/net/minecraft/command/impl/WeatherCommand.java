package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class WeatherCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("weather").requires((p_198868_0_) -> {
         return p_198868_0_.hasPermissionLevel(2);
      }).then(Commands.literal("clear").executes((p_198861_0_) -> {
         return setClear(p_198861_0_.getSource(), 6000);
      }).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_198864_0_) -> {
         return setClear(p_198864_0_.getSource(), IntegerArgumentType.getInteger(p_198864_0_, "duration") * 20);
      }))).then(Commands.literal("rain").executes((p_198860_0_) -> {
         return setRain(p_198860_0_.getSource(), 6000);
      }).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_198866_0_) -> {
         return setRain(p_198866_0_.getSource(), IntegerArgumentType.getInteger(p_198866_0_, "duration") * 20);
      }))).then(Commands.literal("thunder").executes((p_198859_0_) -> {
         return setThunder(p_198859_0_.getSource(), 6000);
      }).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_198867_0_) -> {
         return setThunder(p_198867_0_.getSource(), IntegerArgumentType.getInteger(p_198867_0_, "duration") * 20);
      }))));
   }

   private static int setClear(CommandSource source, int time) {
      source.getWorld().getWorldInfo().setClearWeatherTime(time);
      source.getWorld().getWorldInfo().setRainTime(0);
      source.getWorld().getWorldInfo().setThunderTime(0);
      source.getWorld().getWorldInfo().setRaining(false);
      source.getWorld().getWorldInfo().setThundering(false);
      source.sendFeedback(new TranslationTextComponent("commands.weather.set.clear"), true);
      return time;
   }

   private static int setRain(CommandSource source, int time) {
      source.getWorld().getWorldInfo().setClearWeatherTime(0);
      source.getWorld().getWorldInfo().setRainTime(time);
      source.getWorld().getWorldInfo().setThunderTime(time);
      source.getWorld().getWorldInfo().setRaining(true);
      source.getWorld().getWorldInfo().setThundering(false);
      source.sendFeedback(new TranslationTextComponent("commands.weather.set.rain"), true);
      return time;
   }

   private static int setThunder(CommandSource source, int time) {
      source.getWorld().getWorldInfo().setClearWeatherTime(0);
      source.getWorld().getWorldInfo().setRainTime(time);
      source.getWorld().getWorldInfo().setThunderTime(time);
      source.getWorld().getWorldInfo().setRaining(true);
      source.getWorld().getWorldInfo().setThundering(true);
      source.sendFeedback(new TranslationTextComponent("commands.weather.set.thunder"), true);
      return time;
   }
}