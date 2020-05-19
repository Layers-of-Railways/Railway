package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.CommandStorage;

public class StorageAccessor implements IDataAccessor {
   private static final SuggestionProvider<CommandSource> field_229834_b_ = (p_229838_0_, p_229838_1_) -> {
      return ISuggestionProvider.func_212476_a(func_229840_b_(p_229838_0_).func_227484_a_(), p_229838_1_);
   };
   public static final Function<String, DataCommand.IDataProvider> field_229833_a_ = (p_229839_0_) -> {
      return new DataCommand.IDataProvider() {
         /**
          * Creates an accessor based on the command context. This should only refer to arguments registered in {@link
          * createArgument}.
          */
         public IDataAccessor createAccessor(CommandContext<CommandSource> context) {
            return new StorageAccessor(StorageAccessor.func_229840_b_(context), ResourceLocationArgument.getResourceLocation(context, p_229839_0_));
         }

         /**
          * Creates an argument used for accessing data related to this type of thing, including a literal to
          * distinguish from other types.
          */
         public ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> builder, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> action) {
            return builder.then(Commands.literal("storage").then((ArgumentBuilder)action.apply(Commands.argument(p_229839_0_, ResourceLocationArgument.resourceLocation()).suggests(StorageAccessor.field_229834_b_))));
         }
      };
   };
   private final CommandStorage field_229835_c_;
   private final ResourceLocation field_229836_d_;

   private static CommandStorage func_229840_b_(CommandContext<CommandSource> p_229840_0_) {
      return p_229840_0_.getSource().getServer().func_229735_aN_();
   }

   private StorageAccessor(CommandStorage p_i226092_1_, ResourceLocation p_i226092_2_) {
      this.field_229835_c_ = p_i226092_1_;
      this.field_229836_d_ = p_i226092_2_;
   }

   public void mergeData(CompoundNBT other) {
      this.field_229835_c_.func_227489_a_(this.field_229836_d_, other);
   }

   public CompoundNBT getData() {
      return this.field_229835_c_.func_227488_a_(this.field_229836_d_);
   }

   public ITextComponent getModifiedMessage() {
      return new TranslationTextComponent("commands.data.storage.modified", this.field_229836_d_);
   }

   /**
    * Gets the message used as a result of querying the given NBT (both for /data get and /data get path)
    */
   public ITextComponent getQueryMessage(INBT nbt) {
      return new TranslationTextComponent("commands.data.storage.query", this.field_229836_d_, nbt.toFormattedComponent());
   }

   /**
    * Gets the message used as a result of querying the given path with a scale.
    */
   public ITextComponent getGetMessage(NBTPathArgument.NBTPath pathIn, double scale, int value) {
      return new TranslationTextComponent("commands.data.storage.get", pathIn, this.field_229836_d_, String.format(Locale.ROOT, "%.2f", scale), value);
   }
}