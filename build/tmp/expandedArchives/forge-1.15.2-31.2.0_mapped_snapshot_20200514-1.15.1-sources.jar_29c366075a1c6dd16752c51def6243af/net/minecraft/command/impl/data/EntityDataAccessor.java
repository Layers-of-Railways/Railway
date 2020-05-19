package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType DATA_ENTITY_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.entity.invalid"));
   public static final Function<String, DataCommand.IDataProvider> DATA_PROVIDER = (p_218922_0_) -> {
      return new DataCommand.IDataProvider() {
         /**
          * Creates an accessor based on the command context. This should only refer to arguments registered in {@link
          * createArgument}.
          */
         public IDataAccessor createAccessor(CommandContext<CommandSource> context) throws CommandSyntaxException {
            return new EntityDataAccessor(EntityArgument.getEntity(context, p_218922_0_));
         }

         /**
          * Creates an argument used for accessing data related to this type of thing, including a literal to
          * distinguish from other types.
          */
         public ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> builder, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> action) {
            return builder.then(Commands.literal("entity").then((ArgumentBuilder)action.apply(Commands.argument(p_218922_0_, EntityArgument.entity()))));
         }
      };
   };
   private final Entity entity;

   public EntityDataAccessor(Entity entityIn) {
      this.entity = entityIn;
   }

   public void mergeData(CompoundNBT other) throws CommandSyntaxException {
      if (this.entity instanceof PlayerEntity) {
         throw DATA_ENTITY_INVALID.create();
      } else {
         UUID uuid = this.entity.getUniqueID();
         this.entity.read(other);
         this.entity.setUniqueId(uuid);
      }
   }

   public CompoundNBT getData() {
      return NBTPredicate.writeToNBTWithSelectedItem(this.entity);
   }

   public ITextComponent getModifiedMessage() {
      return new TranslationTextComponent("commands.data.entity.modified", this.entity.getDisplayName());
   }

   /**
    * Gets the message used as a result of querying the given NBT (both for /data get and /data get path)
    */
   public ITextComponent getQueryMessage(INBT nbt) {
      return new TranslationTextComponent("commands.data.entity.query", this.entity.getDisplayName(), nbt.toFormattedComponent());
   }

   /**
    * Gets the message used as a result of querying the given path with a scale.
    */
   public ITextComponent getGetMessage(NBTPathArgument.NBTPath pathIn, double scale, int value) {
      return new TranslationTextComponent("commands.data.entity.get", pathIn, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", scale), value);
   }
}