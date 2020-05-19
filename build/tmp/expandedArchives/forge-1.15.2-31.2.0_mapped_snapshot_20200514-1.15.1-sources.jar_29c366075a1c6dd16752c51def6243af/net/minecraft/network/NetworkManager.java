package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.util.CryptManager;
import net.minecraft.util.LazyValue;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler<IPacket<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Marker NETWORK_MARKER = MarkerManager.getMarker("NETWORK");
   public static final Marker NETWORK_PACKETS_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", NETWORK_MARKER);
   public static final AttributeKey<ProtocolType> PROTOCOL_ATTRIBUTE_KEY = AttributeKey.valueOf("protocol");
   public static final LazyValue<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyValue<>(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
   });
   public static final LazyValue<EpollEventLoopGroup> CLIENT_EPOLL_EVENTLOOP = new LazyValue<>(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
   });
   public static final LazyValue<DefaultEventLoopGroup> CLIENT_LOCAL_EVENTLOOP = new LazyValue<>(() -> {
      return new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
   });
   private final PacketDirection direction;
   /** The queue for packets that require transmission */
   private final Queue<NetworkManager.QueuedPacket> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
   /** The active channel */
   private Channel channel;
   /** The address of the remote party */
   private SocketAddress socketAddress;
   /** The INetHandler instance responsible for processing received packets */
   private INetHandler packetListener;
   /** A String indicating why the network has shutdown. */
   private ITextComponent terminationReason;
   private boolean isEncrypted;
   private boolean disconnected;
   private int field_211394_q;
   private int field_211395_r;
   private float field_211396_s;
   private float field_211397_t;
   private int ticks;
   private boolean field_211399_v;
   private java.util.function.Consumer<NetworkManager> activationHandler;

   public NetworkManager(PacketDirection packetDirection) {
      this.direction = packetDirection;
   }

   public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
      super.channelActive(p_channelActive_1_);
      this.channel = p_channelActive_1_.channel();
      this.socketAddress = this.channel.remoteAddress();
      if (activationHandler != null) activationHandler.accept(this);

      try {
         this.setConnectionState(ProtocolType.HANDSHAKING);
      } catch (Throwable throwable) {
         LOGGER.fatal(throwable);
      }

   }

   /**
    * Sets the new connection state and registers which packets this channel may send and receive
    */
   public void setConnectionState(ProtocolType newState) {
      this.channel.attr(PROTOCOL_ATTRIBUTE_KEY).set(newState);
      this.channel.config().setAutoRead(true);
      LOGGER.debug("Enabled auto read");
   }

   public void channelInactive(ChannelHandlerContext p_channelInactive_1_) throws Exception {
      this.closeChannel(new TranslationTextComponent("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {
      if (p_exceptionCaught_2_ instanceof SkipableEncoderException) {
         LOGGER.debug("Skipping packet due to errors", p_exceptionCaught_2_.getCause());
      } else {
         boolean flag = !this.field_211399_v;
         this.field_211399_v = true;
         if (this.channel.isOpen()) {
            if (p_exceptionCaught_2_ instanceof TimeoutException) {
               LOGGER.debug("Timeout", p_exceptionCaught_2_);
               this.closeChannel(new TranslationTextComponent("disconnect.timeout"));
            } else {
               ITextComponent itextcomponent = new TranslationTextComponent("disconnect.genericReason", "Internal Exception: " + p_exceptionCaught_2_);
               if (flag) {
                  LOGGER.debug("Failed to sent packet", p_exceptionCaught_2_);
                  this.sendPacket(new SDisconnectPacket(itextcomponent), (p_211391_2_) -> {
                     this.closeChannel(itextcomponent);
                  });
                  this.disableAutoRead();
               } else {
                  LOGGER.debug("Double fault", p_exceptionCaught_2_);
                  this.closeChannel(itextcomponent);
               }
            }

         }
      }
   }

   protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, IPacket<?> p_channelRead0_2_) throws Exception {
      if (this.channel.isOpen()) {
         try {
            processPacket(p_channelRead0_2_, this.packetListener);
         } catch (ThreadQuickExitException var4) {
            ;
         }

         ++this.field_211394_q;
      }

   }

   private static <T extends INetHandler> void processPacket(IPacket<T> p_197664_0_, INetHandler p_197664_1_) {
      p_197664_0_.processPacket((T)p_197664_1_);
   }

   /**
    * Sets the NetHandler for this NetworkManager, no checks are made if this handler is suitable for the particular
    * connection state (protocol)
    */
   public void setNetHandler(INetHandler handler) {
      Validate.notNull(handler, "packetListener");
      LOGGER.debug("Set listener of {} to {}", this, handler);
      this.packetListener = handler;
   }

   public void sendPacket(IPacket<?> packetIn) {
      this.sendPacket(packetIn, (GenericFutureListener<? extends Future<? super Void>>)null);
   }

   public void sendPacket(IPacket<?> packetIn, @Nullable GenericFutureListener<? extends Future<? super Void>> p_201058_2_) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packetIn, p_201058_2_);
      } else {
         this.outboundPacketsQueue.add(new NetworkManager.QueuedPacket(packetIn, p_201058_2_));
      }

   }

   /**
    * Will commit the packet to the channel. If the current thread 'owns' the channel it will write and flush the
    * packet, otherwise it will add a task for the channel eventloop thread to do that.
    */
   private void dispatchPacket(IPacket<?> inPacket, @Nullable GenericFutureListener<? extends Future<? super Void>> futureListeners) {
      ProtocolType protocoltype = ProtocolType.getFromPacket(inPacket);
      ProtocolType protocoltype1 = this.channel.attr(PROTOCOL_ATTRIBUTE_KEY).get();
      ++this.field_211395_r;
      if (protocoltype1 != protocoltype) {
         LOGGER.debug("Disabled auto read");
         this.channel.eventLoop().execute(()->this.channel.config().setAutoRead(false));
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (protocoltype != protocoltype1) {
            this.setConnectionState(protocoltype);
         }

         ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);
         if (futureListeners != null) {
            channelfuture.addListener(futureListeners);
         }

         channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(() -> {
            if (protocoltype != protocoltype1) {
               this.setConnectionState(protocoltype);
            }

            ChannelFuture channelfuture1 = this.channel.writeAndFlush(inPacket);
            if (futureListeners != null) {
               channelfuture1.addListener(futureListeners);
            }

            channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
         });
      }

   }

   /**
    * Will iterate through the outboundPacketQueue and dispatch all Packets
    */
   private void flushOutboundQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         synchronized(this.outboundPacketsQueue) {
            NetworkManager.QueuedPacket networkmanager$queuedpacket;
            while((networkmanager$queuedpacket = this.outboundPacketsQueue.poll()) != null) {
               this.dispatchPacket(networkmanager$queuedpacket.packet, networkmanager$queuedpacket.field_201049_b);
            }

         }
      }
   }

   /**
    * Checks timeouts and processes all packets received
    */
   public void tick() {
      this.flushOutboundQueue();
      if (this.packetListener instanceof ServerLoginNetHandler) {
         ((ServerLoginNetHandler)this.packetListener).tick();
      }

      if (this.packetListener instanceof ServerPlayNetHandler) {
         ((ServerPlayNetHandler)this.packetListener).tick();
      }

      if (this.channel != null) {
         this.channel.flush();
      }

      if (this.ticks++ % 20 == 0) {
         this.field_211397_t = this.field_211397_t * 0.75F + (float)this.field_211395_r * 0.25F;
         this.field_211396_s = this.field_211396_s * 0.75F + (float)this.field_211394_q * 0.25F;
         this.field_211395_r = 0;
         this.field_211394_q = 0;
      }

   }

   /**
    * Returns the socket address of the remote side. Server-only.
    */
   public SocketAddress getRemoteAddress() {
      return this.socketAddress;
   }

   /**
    * Closes the channel, the parameter can be used for an exit message (not certain how it gets sent)
    */
   public void closeChannel(ITextComponent message) {
      if (this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.terminationReason = message;
      }

   }

   /**
    * True if this NetworkManager uses a memory connection (single player game). False may imply both an active TCP
    * connection or simply no active connection at all
    */
   public boolean isLocalChannel() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   /**
    * Create a new NetworkManager from the server host and connect it to the server
    */
   @OnlyIn(Dist.CLIENT)
   public static NetworkManager createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport) {
      if (address instanceof java.net.Inet6Address) System.setProperty("java.net.preferIPv4Stack", "false");
      final NetworkManager networkmanager = new NetworkManager(PacketDirection.CLIENTBOUND);
      networkmanager.activationHandler = net.minecraftforge.fml.network.NetworkHooks::registerClientLoginChannel;
      Class<? extends SocketChannel> oclass;
      LazyValue<? extends EventLoopGroup> lazyvalue;
      if (Epoll.isAvailable() && useNativeTransport) {
         oclass = EpollSocketChannel.class;
         lazyvalue = CLIENT_EPOLL_EVENTLOOP;
      } else {
         oclass = NioSocketChannel.class;
         lazyvalue = CLIENT_NIO_EVENTLOOP;
      }

      (new Bootstrap()).group(lazyvalue.getValue()).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_initChannel_1_) throws Exception {
            try {
               p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3) {
               ;
            }

            p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new NettyVarint21FrameDecoder()).addLast("decoder", new NettyPacketDecoder(PacketDirection.CLIENTBOUND)).addLast("prepender", new NettyVarint21FrameEncoder()).addLast("encoder", new NettyPacketEncoder(PacketDirection.SERVERBOUND)).addLast("packet_handler", networkmanager);
         }
      }).channel(oclass).connect(address, serverPort).syncUninterruptibly();
      return networkmanager;
   }

   /**
    * Prepares a clientside NetworkManager: establishes a connection to the socket supplied and configures the channel
    * pipeline. Returns the newly created instance.
    */
   @OnlyIn(Dist.CLIENT)
   public static NetworkManager provideLocalClient(SocketAddress address) {
      final NetworkManager networkmanager = new NetworkManager(PacketDirection.CLIENTBOUND);
      networkmanager.activationHandler = net.minecraftforge.fml.network.NetworkHooks::registerClientLoginChannel;
      (new Bootstrap()).group(CLIENT_LOCAL_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_initChannel_1_) throws Exception {
            p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
         }
      }).channel(LocalChannel.class).connect(address).syncUninterruptibly();
      return networkmanager;
   }

   /**
    * Adds an encoder+decoder to the channel pipeline. The parameter is the secret key used for encrypted communication
    */
   public void enableEncryption(SecretKey key) {
      this.isEncrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
      this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEncrypted() {
      return this.isEncrypted;
   }

   /**
    * Returns true if this NetworkManager has an active channel, false otherwise
    */
   public boolean isChannelOpen() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean hasNoChannel() {
      return this.channel == null;
   }

   /**
    * Gets the current handler for processing packets
    */
   public INetHandler getNetHandler() {
      return this.packetListener;
   }

   /**
    * If this channel is closed, returns the exit message, null otherwise.
    */
   @Nullable
   public ITextComponent getExitMessage() {
      return this.terminationReason;
   }

   /**
    * Switches the channel to manual reading modus
    */
   public void disableAutoRead() {
      this.channel.config().setAutoRead(false);
   }

   public void setCompressionThreshold(int threshold) {
      if (threshold >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionThreshold(threshold);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(threshold));
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            ((NettyCompressionEncoder)this.channel.pipeline().get("compress")).setCompressionThreshold(threshold);
         } else {
            this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(threshold));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }

   }

   public void handleDisconnection() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (this.disconnected) {
            LOGGER.warn("handleDisconnection() called twice");
         } else {
            this.disconnected = true;
            if (this.getExitMessage() != null) {
               this.getNetHandler().onDisconnect(this.getExitMessage());
            } else if (this.getNetHandler() != null) {
               this.getNetHandler().onDisconnect(new TranslationTextComponent("multiplayer.disconnect.generic"));
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getPacketsReceived() {
      return this.field_211396_s;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPacketsSent() {
      return this.field_211397_t;
   }

   public Channel channel() {
      return channel;
   }

   public PacketDirection getDirection() {
      return this.direction;
   }

   static class QueuedPacket {
      private final IPacket<?> packet;
      @Nullable
      private final GenericFutureListener<? extends Future<? super Void>> field_201049_b;

      public QueuedPacket(IPacket<?> p_i48604_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_i48604_2_) {
         this.packet = p_i48604_1_;
         this.field_201049_b = p_i48604_2_;
      }
   }
}