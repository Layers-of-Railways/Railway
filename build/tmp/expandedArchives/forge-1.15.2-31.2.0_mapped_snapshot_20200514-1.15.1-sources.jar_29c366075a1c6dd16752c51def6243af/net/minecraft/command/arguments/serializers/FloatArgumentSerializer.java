package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class FloatArgumentSerializer implements IArgumentSerializer<FloatArgumentType> {
   public void write(FloatArgumentType argument, PacketBuffer buffer) {
      boolean flag = argument.getMinimum() != -Float.MAX_VALUE;
      boolean flag1 = argument.getMaximum() != Float.MAX_VALUE;
      buffer.writeByte(BrigadierSerializers.minMaxFlags(flag, flag1));
      if (flag) {
         buffer.writeFloat(argument.getMinimum());
      }

      if (flag1) {
         buffer.writeFloat(argument.getMaximum());
      }

   }

   public FloatArgumentType read(PacketBuffer buffer) {
      byte b0 = buffer.readByte();
      float f = BrigadierSerializers.hasMin(b0) ? buffer.readFloat() : -Float.MAX_VALUE;
      float f1 = BrigadierSerializers.hasMax(b0) ? buffer.readFloat() : Float.MAX_VALUE;
      return FloatArgumentType.floatArg(f, f1);
   }

   public void write(FloatArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != -Float.MAX_VALUE) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Float.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }
}