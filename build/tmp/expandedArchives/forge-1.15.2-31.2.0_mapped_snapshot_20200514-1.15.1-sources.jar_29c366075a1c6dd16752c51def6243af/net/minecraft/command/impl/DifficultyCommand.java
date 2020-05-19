package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208823_0_) -> {
      return new TranslationTextComponent("commands.difficulty.failure", p_208823_0_);
   });

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("difficulty");

      for(Difficulty difficulty : Difficulty.values()) {
         literalargumentbuilder.then(Commands.literal(difficulty.getTranslationKey()).executes((p_198347_1_) -> {
            return setDifficulty(p_198347_1_.getSource(), difficulty);
         }));
      }

      dispatcher.register(literalargumentbuilder.requires((p_198348_0_) -> {
         return p_198348_0_.hasPermissionLevel(2);
      }).executes((p_198346_0_) -> {
         Difficulty difficulty1 = p_198346_0_.getSource().getWorld().getDifficulty();
         p_198346_0_.getSource().sendFeedback(new TranslationTextComponent("commands.difficulty.query", difficulty1.getDisplayName()), false);
         return difficulty1.getId();
      }));
   }

   public static int setDifficulty(CommandSource source, Difficulty difficulty) throws CommandSyntaxException {
      MinecraftServer minecraftserver = source.getServer();
      if (minecraftserver.getWorld(DimensionType.OVERWORLD).getDifficulty() == difficulty) {
         throw FAILED_EXCEPTION.create(difficulty.getTranslationKey());
      } else {
         minecraftserver.setDifficultyForAllWorlds(difficulty, true);
         source.sendFeedback(new TranslationTextComponent("commands.difficulty.success", difficulty.getDisplayName()), true);
         return 0;
      }
   }
}