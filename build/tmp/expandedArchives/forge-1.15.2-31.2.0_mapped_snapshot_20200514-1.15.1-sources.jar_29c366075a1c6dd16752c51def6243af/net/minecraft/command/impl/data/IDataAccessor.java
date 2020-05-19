package net.minecraft.command.impl.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;

public interface IDataAccessor {
   void mergeData(CompoundNBT other) throws CommandSyntaxException;

   CompoundNBT getData() throws CommandSyntaxException;

   ITextComponent getModifiedMessage();

   /**
    * Gets the message used as a result of querying the given NBT (both for /data get and /data get path)
    */
   ITextComponent getQueryMessage(INBT nbt);

   /**
    * Gets the message used as a result of querying the given path with a scale.
    */
   ITextComponent getGetMessage(NBTPathArgument.NBTPath pathIn, double scale, int value);
}