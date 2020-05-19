package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.IResult> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
   private static final DynamicCommandExceptionType UNKNOWN_TAG = new DynamicCommandExceptionType((p_208699_0_) -> {
      return new TranslationTextComponent("arguments.item.tag.unknown", p_208699_0_);
   });

   public static ItemPredicateArgument itemPredicate() {
      return new ItemPredicateArgument();
   }

   public ItemPredicateArgument.IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ItemParser itemparser = (new ItemParser(p_parse_1_, true)).parse();
      if (itemparser.getItem() != null) {
         ItemPredicateArgument.ItemPredicate itempredicateargument$itempredicate = new ItemPredicateArgument.ItemPredicate(itemparser.getItem(), itemparser.getNbt());
         return (p_199848_1_) -> {
            return itempredicateargument$itempredicate;
         };
      } else {
         ResourceLocation resourcelocation = itemparser.getTag();
         return (p_199845_2_) -> {
            Tag<Item> tag = ((CommandSource)p_199845_2_.getSource()).getServer().getNetworkTagManager().getItems().get(resourcelocation);
            if (tag == null) {
               throw UNKNOWN_TAG.create(resourcelocation.toString());
            } else {
               return new ItemPredicateArgument.TagPredicate(tag, itemparser.getNbt());
            }
         };
      }
   }

   public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      return context.getArgument(name, ItemPredicateArgument.IResult.class).create(context);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
      stringreader.setCursor(p_listSuggestions_2_.getStart());
      ItemParser itemparser = new ItemParser(stringreader, true);

      try {
         itemparser.parse();
      } catch (CommandSyntaxException var6) {
         ;
      }

      return itemparser.fillSuggestions(p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public interface IResult {
      Predicate<ItemStack> create(CommandContext<CommandSource> p_create_1_) throws CommandSyntaxException;
   }

   static class ItemPredicate implements Predicate<ItemStack> {
      private final Item item;
      @Nullable
      private final CompoundNBT nbt;

      public ItemPredicate(Item itemIn, @Nullable CompoundNBT nbtIn) {
         this.item = itemIn;
         this.nbt = nbtIn;
      }

      public boolean test(ItemStack p_test_1_) {
         return p_test_1_.getItem() == this.item && NBTUtil.areNBTEquals(this.nbt, p_test_1_.getTag(), true);
      }
   }

   static class TagPredicate implements Predicate<ItemStack> {
      private final Tag<Item> tag;
      @Nullable
      private final CompoundNBT nbt;

      public TagPredicate(Tag<Item> tagIn, @Nullable CompoundNBT nbtIn) {
         this.tag = tagIn;
         this.nbt = nbtIn;
      }

      public boolean test(ItemStack p_test_1_) {
         return this.tag.contains(p_test_1_.getItem()) && NBTUtil.areNBTEquals(this.nbt, p_test_1_.getTag(), true);
      }
   }
}