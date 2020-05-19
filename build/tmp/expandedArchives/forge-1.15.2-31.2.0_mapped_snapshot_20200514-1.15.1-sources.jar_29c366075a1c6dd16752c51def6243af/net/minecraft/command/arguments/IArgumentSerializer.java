package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.PacketBuffer;

public interface IArgumentSerializer<T extends ArgumentType<?>> {
   void write(T argument, PacketBuffer buffer);

   T read(PacketBuffer buffer);

   void write(T p_212244_1_, JsonObject p_212244_2_);
}