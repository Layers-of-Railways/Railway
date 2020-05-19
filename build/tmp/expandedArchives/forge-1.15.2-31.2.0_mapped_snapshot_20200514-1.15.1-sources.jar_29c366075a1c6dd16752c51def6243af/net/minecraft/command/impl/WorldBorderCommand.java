package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
   private static final SimpleCommandExceptionType CENTER_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.center.failed"));
   private static final SimpleCommandExceptionType SIZE_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.set.failed.nochange"));
   private static final SimpleCommandExceptionType SIZE_TOO_SMALL = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.set.failed.small."));
   private static final SimpleCommandExceptionType SIZE_TOO_BIG = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.set.failed.big."));
   private static final SimpleCommandExceptionType WARNING_TIME_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.warning.time.failed"));
   private static final SimpleCommandExceptionType WARNING_DISTANCE_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.warning.distance.failed"));
   private static final SimpleCommandExceptionType DAMAGE_BUFFER_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.damage.buffer.failed"));
   private static final SimpleCommandExceptionType DAMAGE_AMOUNT_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.damage.amount.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("worldborder").requires((p_198903_0_) -> {
         return p_198903_0_.hasPermissionLevel(2);
      }).then(Commands.literal("add").then(Commands.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((p_198908_0_) -> {
         return setSize(p_198908_0_.getSource(), p_198908_0_.getSource().getWorld().getWorldBorder().getDiameter() + (double)FloatArgumentType.getFloat(p_198908_0_, "distance"), 0L);
      }).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198901_0_) -> {
         return setSize(p_198901_0_.getSource(), p_198901_0_.getSource().getWorld().getWorldBorder().getDiameter() + (double)FloatArgumentType.getFloat(p_198901_0_, "distance"), p_198901_0_.getSource().getWorld().getWorldBorder().getTimeUntilTarget() + (long)IntegerArgumentType.getInteger(p_198901_0_, "time") * 1000L);
      })))).then(Commands.literal("set").then(Commands.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((p_198906_0_) -> {
         return setSize(p_198906_0_.getSource(), (double)FloatArgumentType.getFloat(p_198906_0_, "distance"), 0L);
      }).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198909_0_) -> {
         return setSize(p_198909_0_.getSource(), (double)FloatArgumentType.getFloat(p_198909_0_, "distance"), (long)IntegerArgumentType.getInteger(p_198909_0_, "time") * 1000L);
      })))).then(Commands.literal("center").then(Commands.argument("pos", Vec2Argument.vec2()).executes((p_198893_0_) -> {
         return setCenter(p_198893_0_.getSource(), Vec2Argument.getVec2f(p_198893_0_, "pos"));
      }))).then(Commands.literal("damage").then(Commands.literal("amount").then(Commands.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F)).executes((p_198897_0_) -> {
         return setDamageAmount(p_198897_0_.getSource(), FloatArgumentType.getFloat(p_198897_0_, "damagePerBlock"));
      }))).then(Commands.literal("buffer").then(Commands.argument("distance", FloatArgumentType.floatArg(0.0F)).executes((p_198905_0_) -> {
         return setDamageBuffer(p_198905_0_.getSource(), FloatArgumentType.getFloat(p_198905_0_, "distance"));
      })))).then(Commands.literal("get").executes((p_198900_0_) -> {
         return getSize(p_198900_0_.getSource());
      })).then(Commands.literal("warning").then(Commands.literal("distance").then(Commands.argument("distance", IntegerArgumentType.integer(0)).executes((p_198892_0_) -> {
         return setWarningDistance(p_198892_0_.getSource(), IntegerArgumentType.getInteger(p_198892_0_, "distance"));
      }))).then(Commands.literal("time").then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198907_0_) -> {
         return setWarningTime(p_198907_0_.getSource(), IntegerArgumentType.getInteger(p_198907_0_, "time"));
      })))));
   }

   private static int setDamageBuffer(CommandSource source, float distance) throws CommandSyntaxException {
      WorldBorder worldborder = source.getWorld().getWorldBorder();
      if (worldborder.getDamageBuffer() == (double)distance) {
         throw DAMAGE_BUFFER_NO_CHANGE.create();
      } else {
         worldborder.setDamageBuffer((double)distance);
         source.sendFeedback(new TranslationTextComponent("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", distance)), true);
         return (int)distance;
      }
   }

   private static int setDamageAmount(CommandSource source, float damagePerBlock) throws CommandSyntaxException {
      WorldBorder worldborder = source.getWorld().getWorldBorder();
      if (worldborder.getDamagePerBlock() == (double)damagePerBlock) {
         throw DAMAGE_AMOUNT_NO_CHANGE.create();
      } else {
         worldborder.setDamagePerBlock((double)damagePerBlock);
         source.sendFeedback(new TranslationTextComponent("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", damagePerBlock)), true);
         return (int)damagePerBlock;
      }
   }

   private static int setWarningTime(CommandSource source, int time) throws CommandSyntaxException {
      WorldBorder worldborder = source.getWorld().getWorldBorder();
      if (worldborder.getWarningTime() == time) {
         throw WARNING_TIME_NO_CHANGE.create();
      } else {
         worldborder.setWarningTime(time);
         source.sendFeedback(new TranslationTextComponent("commands.worldborder.warning.time.success", time), true);
         return time;
      }
   }

   private static int setWarningDistance(CommandSource source, int distance) throws CommandSyntaxException {
      WorldBorder worldborder = source.getWorld().getWorldBorder();
      if (worldborder.getWarningDistance() == distance) {
         throw WARNING_DISTANCE_NO_CHANGE.create();
      } else {
         worldborder.setWarningDistance(distance);
         source.sendFeedback(new TranslationTextComponent("commands.worldborder.warning.distance.success", distance), true);
         return distance;
      }
   }

   private static int getSize(CommandSource source) {
      double d0 = source.getWorld().getWorldBorder().getDiameter();
      source.sendFeedback(new TranslationTextComponent("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d0)), false);
      return MathHelper.floor(d0 + 0.5D);
   }

   private static int setCenter(CommandSource source, Vec2f pos) throws CommandSyntaxException {
      WorldBorder worldborder = source.getWorld().getWorldBorder();
      if (worldborder.getCenterX() == (double)pos.x && worldborder.getCenterZ() == (double)pos.y) {
         throw CENTER_NO_CHANGE.create();
      } else {
         worldborder.setCenter((double)pos.x, (double)pos.y);
         source.sendFeedback(new TranslationTextComponent("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", pos.x), String.format("%.2f", pos.y)), true);
         return 0;
      }
   }

   private static int setSize(CommandSource source, double newSize, long time) throws CommandSyntaxException {
      WorldBorder worldborder = source.getWorld().getWorldBorder();
      double d0 = worldborder.getDiameter();
      if (d0 == newSize) {
         throw SIZE_NO_CHANGE.create();
      } else if (newSize < 1.0D) {
         throw SIZE_TOO_SMALL.create();
      } else if (newSize > 6.0E7D) {
         throw SIZE_TOO_BIG.create();
      } else {
         if (time > 0L) {
            worldborder.setTransition(d0, newSize, time);
            if (newSize > d0) {
               source.sendFeedback(new TranslationTextComponent("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", newSize), Long.toString(time / 1000L)), true);
            } else {
               source.sendFeedback(new TranslationTextComponent("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", newSize), Long.toString(time / 1000L)), true);
            }
         } else {
            worldborder.setTransition(newSize);
            source.sendFeedback(new TranslationTextComponent("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", newSize)), true);
         }

         return (int)(newSize - d0);
      }
   }
}