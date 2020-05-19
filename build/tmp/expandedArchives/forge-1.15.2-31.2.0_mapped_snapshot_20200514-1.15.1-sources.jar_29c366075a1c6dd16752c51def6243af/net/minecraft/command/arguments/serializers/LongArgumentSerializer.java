package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class LongArgumentSerializer implements IArgumentSerializer<LongArgumentType> {
   public void write(LongArgumentType argument, PacketBuffer buffer) {
      boolean flag = argument.getMinimum() != Long.MIN_VALUE;
      boolean flag1 = argument.getMaximum() != Long.MAX_VALUE;
      buffer.writeByte(BrigadierSerializers.minMaxFlags(flag, flag1));
      if (flag) {
         buffer.writeLong(argument.getMinimum());
      }

      if (flag1) {
         buffer.writeLong(argument.getMaximum());
      }

   }

   public LongArgumentType read(PacketBuffer buffer) {
      byte b0 = buffer.readByte();
      long i = BrigadierSerializers.hasMin(b0) ? buffer.readLong() : Long.MIN_VALUE;
      long j = BrigadierSerializers.hasMax(b0) ? buffer.readLong() : Long.MAX_VALUE;
      return LongArgumentType.longArg(i, j);
   }

   public void write(LongArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != Long.MIN_VALUE) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Long.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }
}