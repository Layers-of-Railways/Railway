package com.railwayteam.railways;

import com.railwayteam.railways.packets.CustomPacketCartSchedule;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RailwaysPacketHandler {
  public static final String PROTOCOL_VERSION       = new ResourceLocation(Railways.MODID, "1").toString();
  public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Railways.MODID, "cart-update");
  public static SimpleChannel channel;

  private static final CustomPacketRegistrar<CustomPacketCartSchedule> cprCartSchedule =
    new CustomPacketRegistrar<>(CustomPacketCartSchedule.class, CustomPacketCartSchedule::new);

  public static void register () {
    channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
      .serverAcceptedVersions(s->true)
      .clientAcceptedVersions(s->true)
      .networkProtocolVersion( ()->PROTOCOL_VERSION).simpleChannel();

    cprCartSchedule.register();
  }

  public static abstract class CustomPacketBase {
    public abstract void write (PacketBuffer buf);
    public abstract void handle (Supplier<Context> context);
  }

  private static class CustomPacketRegistrar <T extends CustomPacketBase> {
    private static int index = 0;
    BiConsumer<T, PacketBuffer> encoder;
    Function<PacketBuffer, T> decoder;
    BiConsumer<T, Supplier<Context>> handler;
    Class<T> type;

    public CustomPacketRegistrar (Class<T> type, Function<PacketBuffer,T> factory) {
      encoder = T::write;
      decoder = factory;
      handler = T::handle;
      this.type = type;
    }

    public void register () {
      channel.messageBuilder(type, index++).encoder(encoder).decoder(decoder).consumer(handler).add();
    }
  }
}
