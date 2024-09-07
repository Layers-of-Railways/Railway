/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.RailwaysBuildInfo;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.events.ClientEvents;
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
    if (!RailwaysBuildInfo.VERSION.equals(version) && player != null) {
      String msg = "Steam 'n' Rails version mismatch: Server is using version " + version + ", you are using version " + RailwaysBuildInfo.VERSION + ". This may cause problems.";
      Railways.LOGGER.warn(msg);
      player.displayClientMessage(
              Components.literal(msg).withStyle(ChatFormatting.DARK_RED),
              false
      );
    }
    CRPackets.PACKETS.send(new JourneymapConfigurePacket(Mods.JOURNEYMAP.isLoaded));
    boolean useDevCape = CRConfigs.client().useDevCape.get();
    CRPackets.PACKETS.send(new ConfigureDevCapeC2SPacket(useDevCape));
    ClientEvents.previousDevCapeSetting = useDevCape;
  }
}
