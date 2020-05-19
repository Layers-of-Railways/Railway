package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.FunctionArgument;
import net.minecraft.util.text.TranslationTextComponent;

public class FunctionCommand {
   public static final SuggestionProvider<CommandSource> FUNCTION_SUGGESTER = (p_198477_0_, p_198477_1_) -> {
      FunctionManager functionmanager = p_198477_0_.getSource().getServer().getFunctionManager();
      ISuggestionProvider.suggestIterable(functionmanager.getTagCollection().getRegisteredTags(), p_198477_1_, "#");
      return ISuggestionProvider.suggestIterable(functionmanager.getFunctions().keySet(), p_198477_1_);
   };

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("function").requires((p_198480_0_) -> {
         return p_198480_0_.hasPermissionLevel(2);
      }).then(Commands.argument("name", FunctionArgument.function()).suggests(FUNCTION_SUGGESTER).executes((p_198479_0_) -> {
         return executeFunctions(p_198479_0_.getSource(), FunctionArgument.getFunctions(p_198479_0_, "name"));
      })));
   }

   private static int executeFunctions(CommandSource source, Collection<FunctionObject> functions) {
      int i = 0;

      for(FunctionObject functionobject : functions) {
         i += source.getServer().getFunctionManager().execute(functionobject, source.withFeedbackDisabled().withMinPermissionLevel(2));
      }

      if (functions.size() == 1) {
         source.sendFeedback(new TranslationTextComponent("commands.function.success.single", i, functions.iterator().next().getId()), true);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.function.success.multiple", i, functions.size()), true);
      }

      return i;
   }
}