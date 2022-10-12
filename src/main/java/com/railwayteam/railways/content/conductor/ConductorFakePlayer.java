package com.railwayteam.railways.content.conductor;

import com.mojang.authlib.GameProfile;
import com.railwayteam.railways.Railways;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.UUID;

public class ConductorFakePlayer extends FakePlayer {
  public static final GameProfile CONDUCTOR_PROFILE =
    new GameProfile(UUID.fromString("B0FADEE5-4411-3475-ADD0-C4EA7E30D050"), "[Conductor]");

  private static final Connection NETWORK_MANAGER = new Connection(PacketFlow.CLIENTBOUND);

  public ConductorFakePlayer (ServerLevel level) {
    super (level, CONDUCTOR_PROFILE);
    connection = new ConductorNetHandler(level.getServer(), this);
  }

  @Override
  public OptionalInt openMenu (MenuProvider container) { return OptionalInt.empty(); }

  @Override
  public Component getDisplayName () {
    return new TranslatableComponent(Railways.MODID + "." + "conductor_name");
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public float getEyeHeight (Pose pose) { return 0; }

  @Override
  public Vec3 position () { return new Vec3(getX(), getY(), getZ()); }

  @Override
  public float getCurrentItemAttackStrengthDelay () { return 1 / 64f; }

  @Override
  public boolean canEat (boolean ignoreHunger) { return false; }

  @Override
  public ItemStack eat (Level world, ItemStack stack) {
    stack.shrink(1);
    return stack;
  }

  private static class ConductorNetHandler extends ServerGamePacketListenerImpl {
    public ConductorNetHandler (MinecraftServer server, ServerPlayer player) { super(server, NETWORK_MANAGER, player); }

    @Override
    public void send (Packet<?> packet) {}

    @Override
    public void send(Packet<?> p_9832_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_9833_) {}
  }
}
