package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class SlotArgument implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "12", "weapon");
   private static final DynamicCommandExceptionType SLOT_UNKNOWN = new DynamicCommandExceptionType((p_208679_0_) -> {
      return new TranslationTextComponent("slot.unknown", p_208679_0_);
   });
   private static final Map<String, Integer> KNOWN_SLOTS = Util.make(Maps.newHashMap(), (p_209386_0_) -> {
      for(int i = 0; i < 54; ++i) {
         p_209386_0_.put("container." + i, i);
      }

      for(int j = 0; j < 9; ++j) {
         p_209386_0_.put("hotbar." + j, j);
      }

      for(int k = 0; k < 27; ++k) {
         p_209386_0_.put("inventory." + k, 9 + k);
      }

      for(int l = 0; l < 27; ++l) {
         p_209386_0_.put("enderchest." + l, 200 + l);
      }

      for(int i1 = 0; i1 < 8; ++i1) {
         p_209386_0_.put("villager." + i1, 300 + i1);
      }

      for(int j1 = 0; j1 < 15; ++j1) {
         p_209386_0_.put("horse." + j1, 500 + j1);
      }

      p_209386_0_.put("weapon", 98);
      p_209386_0_.put("weapon.mainhand", 98);
      p_209386_0_.put("weapon.offhand", 99);
      p_209386_0_.put("armor.head", 100 + EquipmentSlotType.HEAD.getIndex());
      p_209386_0_.put("armor.chest", 100 + EquipmentSlotType.CHEST.getIndex());
      p_209386_0_.put("armor.legs", 100 + EquipmentSlotType.LEGS.getIndex());
      p_209386_0_.put("armor.feet", 100 + EquipmentSlotType.FEET.getIndex());
      p_209386_0_.put("horse.saddle", 400);
      p_209386_0_.put("horse.armor", 401);
      p_209386_0_.put("horse.chest", 499);
   });

   public static SlotArgument slot() {
      return new SlotArgument();
   }

   public static int getSlot(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, Integer.class);
   }

   public Integer parse(StringReader p_parse_1_) throws CommandSyntaxException {
      String s = p_parse_1_.readUnquotedString();
      if (!KNOWN_SLOTS.containsKey(s)) {
         throw SLOT_UNKNOWN.create(s);
      } else {
         return KNOWN_SLOTS.get(s);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggest(KNOWN_SLOTS.keySet(), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}