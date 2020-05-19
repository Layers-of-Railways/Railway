package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType DATA_BLOCK_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.data.block.invalid"));
   public static final Function<String, DataCommand.IDataProvider> DATA_PROVIDER = (p_218923_0_) -> {
      return new DataCommand.IDataProvider() {
         /**
          * Creates an accessor based on the command context. This should only refer to arguments registered in {@link
          * createArgument}.
          */
         public IDataAccessor createAccessor(CommandContext<CommandSource> context) throws CommandSyntaxException {
            BlockPos blockpos = BlockPosArgument.getLoadedBlockPos(context, p_218923_0_ + "Pos");
            TileEntity tileentity = ((CommandSource)context.getSource()).getWorld().getTileEntity(blockpos);
            if (tileentity == null) {
               throw BlockDataAccessor.DATA_BLOCK_INVALID_EXCEPTION.create();
            } else {
               return new BlockDataAccessor(tileentity, blockpos);
            }
         }

         /**
          * Creates an argument used for accessing data related to this type of thing, including a literal to
          * distinguish from other types.
          */
         public ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> builder, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> action) {
            return builder.then(Commands.literal("block").then((ArgumentBuilder)action.apply(Commands.argument(p_218923_0_ + "Pos", BlockPosArgument.blockPos()))));
         }
      };
   };
   private final TileEntity tileEntity;
   private final BlockPos pos;

   public BlockDataAccessor(TileEntity tileEntityIn, BlockPos posIn) {
      this.tileEntity = tileEntityIn;
      this.pos = posIn;
   }

   public void mergeData(CompoundNBT other) {
      other.putInt("x", this.pos.getX());
      other.putInt("y", this.pos.getY());
      other.putInt("z", this.pos.getZ());
      this.tileEntity.read(other);
      this.tileEntity.markDirty();
      BlockState blockstate = this.tileEntity.getWorld().getBlockState(this.pos);
      this.tileEntity.getWorld().notifyBlockUpdate(this.pos, blockstate, blockstate, 3);
   }

   public CompoundNBT getData() {
      return this.tileEntity.write(new CompoundNBT());
   }

   public ITextComponent getModifiedMessage() {
      return new TranslationTextComponent("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
   }

   /**
    * Gets the message used as a result of querying the given NBT (both for /data get and /data get path)
    */
   public ITextComponent getQueryMessage(INBT nbt) {
      return new TranslationTextComponent("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), nbt.toFormattedComponent());
   }

   /**
    * Gets the message used as a result of querying the given path with a scale.
    */
   public ITextComponent getGetMessage(NBTPathArgument.NBTPath pathIn, double scale, int value) {
      return new TranslationTextComponent("commands.data.block.get", pathIn, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", scale), value);
   }
}