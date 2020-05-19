package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.util.ResourceLocation;

public class FunctionObject {
   private final FunctionObject.IEntry[] entries;
   private final ResourceLocation id;

   public FunctionObject(ResourceLocation p_i47973_1_, FunctionObject.IEntry[] p_i47973_2_) {
      this.id = p_i47973_1_;
      this.entries = p_i47973_2_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public FunctionObject.IEntry[] getEntries() {
      return this.entries;
   }

   public static FunctionObject create(ResourceLocation p_197000_0_, FunctionManager p_197000_1_, List<String> p_197000_2_) {
      List<FunctionObject.IEntry> list = Lists.newArrayListWithCapacity(p_197000_2_.size());

      for(int i = 0; i < p_197000_2_.size(); ++i) {
         int j = i + 1;
         String s = p_197000_2_.get(i).trim();
         StringReader stringreader = new StringReader(s);
         if (stringreader.canRead() && stringreader.peek() != '#') {
            if (stringreader.peek() == '/') {
               stringreader.skip();
               if (stringreader.peek() == '/') {
                  throw new IllegalArgumentException("Unknown or invalid command '" + s + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
               }

               String s1 = stringreader.readUnquotedString();
               throw new IllegalArgumentException("Unknown or invalid command '" + s + "' on line " + j + " (did you mean '" + s1 + "'? Do not use a preceding forwards slash.)");
            }

            try {
               ParseResults<CommandSource> parseresults = p_197000_1_.getServer().getCommandManager().getDispatcher().parse(stringreader, p_197000_1_.func_223402_g());
               if (parseresults.getReader().canRead()) {
                  throw Commands.func_227481_a_(parseresults);
               }

               list.add(new FunctionObject.CommandEntry(parseresults));
            } catch (CommandSyntaxException commandsyntaxexception) {
               throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + commandsyntaxexception.getMessage());
            }
         }
      }

      return new FunctionObject(p_197000_0_, list.toArray(new FunctionObject.IEntry[0]));
   }

   public static class CacheableFunction {
      public static final FunctionObject.CacheableFunction EMPTY = new FunctionObject.CacheableFunction((ResourceLocation)null);
      @Nullable
      private final ResourceLocation id;
      private boolean isValid;
      private Optional<FunctionObject> function = Optional.empty();

      public CacheableFunction(@Nullable ResourceLocation idIn) {
         this.id = idIn;
      }

      public CacheableFunction(FunctionObject functionIn) {
         this.isValid = true;
         this.id = null;
         this.function = Optional.of(functionIn);
      }

      public Optional<FunctionObject> func_218039_a(FunctionManager p_218039_1_) {
         if (!this.isValid) {
            if (this.id != null) {
               this.function = p_218039_1_.get(this.id);
            }

            this.isValid = true;
         }

         return this.function;
      }

      @Nullable
      public ResourceLocation getId() {
         return this.function.map((p_218040_0_) -> {
            return p_218040_0_.id;
         }).orElse(this.id);
      }
   }

   public static class CommandEntry implements FunctionObject.IEntry {
      private final ParseResults<CommandSource> field_196999_a;

      public CommandEntry(ParseResults<CommandSource> p_i47816_1_) {
         this.field_196999_a = p_i47816_1_;
      }

      public void execute(FunctionManager p_196998_1_, CommandSource p_196998_2_, ArrayDeque<FunctionManager.QueuedCommand> p_196998_3_, int p_196998_4_) throws CommandSyntaxException {
         p_196998_1_.getCommandDispatcher().execute(new ParseResults<>(this.field_196999_a.getContext().withSource(p_196998_2_), this.field_196999_a.getReader(), this.field_196999_a.getExceptions()));
      }

      public String toString() {
         return this.field_196999_a.getReader().getString();
      }
   }

   public static class FunctionEntry implements FunctionObject.IEntry {
      private final FunctionObject.CacheableFunction function;

      public FunctionEntry(FunctionObject functionIn) {
         this.function = new FunctionObject.CacheableFunction(functionIn);
      }

      public void execute(FunctionManager p_196998_1_, CommandSource p_196998_2_, ArrayDeque<FunctionManager.QueuedCommand> p_196998_3_, int p_196998_4_) {
         this.function.func_218039_a(p_196998_1_).ifPresent((p_218041_4_) -> {
            FunctionObject.IEntry[] afunctionobject$ientry = p_218041_4_.getEntries();
            int i = p_196998_4_ - p_196998_3_.size();
            int j = Math.min(afunctionobject$ientry.length, i);

            for(int k = j - 1; k >= 0; --k) {
               p_196998_3_.addFirst(new FunctionManager.QueuedCommand(p_196998_1_, p_196998_2_, afunctionobject$ientry[k]));
            }

         });
      }

      public String toString() {
         return "function " + this.function.getId();
      }
   }

   public interface IEntry {
      void execute(FunctionManager p_196998_1_, CommandSource p_196998_2_, ArrayDeque<FunctionManager.QueuedCommand> p_196998_3_, int p_196998_4_) throws CommandSyntaxException;
   }
}