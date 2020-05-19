package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class IntArgumentSerializer implements IArgumentSerializer<IntegerArgumentType> {
   public void write(IntegerArgumentType argument, PacketBuffer buffer) {
      boolean flag = argument.getMinimum() != Integer.MIN_VALUE;
      boolean flag1 = argument.getMaximum() != Integer.MAX_VALUE;
      buffer.writeByte(BrigadierSerializers.minMaxFlags(flag, flag1));
      if (flag) {
         buffer.writeInt(argument.getMinimum());
      }

      if (flag1) {
         buffer.writeInt(argument.getMaximum());
      }

   }

   public IntegerArgumentType read(PacketBuffer buffer) {
      byte b0 = buffer.readByte();
      int i = BrigadierSerializers.hasMin(b0) ? buffer.readInt() : Integer.MIN_VALUE;
      int j = BrigadierSerializers.hasMax(b0) ? buffer.readInt() : Integer.MAX_VALUE;
      return IntegerArgumentType.integer(i, j);
   }

   public void write(IntegerArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != Integer.MIN_VALUE) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Integer.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }
}