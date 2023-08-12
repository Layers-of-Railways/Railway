package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.multiloader.S2CPacket;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.foundation.utility.Components;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;

public record ModVersionPacket(String version) implements S2CPacket {
  public ModVersionPacket(FriendlyByteBuf buf) {
    this(buf.readUtf());
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    buffer.writeUtf(this.version);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void handle(Minecraft mc) {
    LocalPlayer player = mc.player;
    if (!Railways.VERSION.equals(version) && player != null) {
      String msg = "Steam 'n' Rails version mismatch: Server is using version "+version+", you are using version "+Railways.VERSION+". This may cause problems.";
      Railways.LOGGER.warn(msg);
      player.displayClientMessage(
              Components.literal(msg).withStyle(ChatFormatting.DARK_RED),
              false
      );
    }
    CRPackets.PACKETS.send(new JourneymapConfigurePacket(Mods.JOURNEYMAP.isLoaded));
  }
}
