package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class DoubleArgumentSerializer implements IArgumentSerializer<DoubleArgumentType> {
   public void write(DoubleArgumentType argument, PacketBuffer buffer) {
      boolean flag = argument.getMinimum() != -Double.MAX_VALUE;
      boolean flag1 = argument.getMaximum() != Double.MAX_VALUE;
      buffer.writeByte(BrigadierSerializers.minMaxFlags(flag, flag1));
      if (flag) {
         buffer.writeDouble(argument.getMinimum());
      }

      if (flag1) {
         buffer.writeDouble(argument.getMaximum());
      }

   }

   public DoubleArgumentType read(PacketBuffer buffer) {
      byte b0 = buffer.readByte();
      double d0 = BrigadierSerializers.hasMin(b0) ? buffer.readDouble() : -Double.MAX_VALUE;
      double d1 = BrigadierSerializers.hasMax(b0) ? buffer.readDouble() : Double.MAX_VALUE;
      return DoubleArgumentType.doubleArg(d0, d1);
   }

   public void write(DoubleArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != -Double.MAX_VALUE) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Double.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }
}