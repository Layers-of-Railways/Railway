package net.minecraft.network;

import io.netty.buffer.Unpooled;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.pathfinding.Path;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugPacketSender {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void func_229752_a_(ServerWorld p_229752_0_, BlockPos p_229752_1_, String p_229752_2_, int p_229752_3_, int p_229752_4_) {
      PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
      packetbuffer.writeBlockPos(p_229752_1_);
      packetbuffer.writeInt(p_229752_3_);
      packetbuffer.writeString(p_229752_2_);
      packetbuffer.writeInt(p_229752_4_);
      func_229753_a_(p_229752_0_, packetbuffer, SCustomPayloadPlayPacket.field_229729_o_);
   }

   public static void func_229751_a_(ServerWorld p_229751_0_) {
      PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
      func_229753_a_(p_229751_0_, packetbuffer, SCustomPayloadPlayPacket.field_229730_p_);
   }

   public static void sendChuckPos(ServerWorld worldIn, ChunkPos p_218802_1_) {
   }

   public static void func_218799_a(ServerWorld worldIn, BlockPos p_218799_1_) {
   }

   public static void func_218805_b(ServerWorld worldIn, BlockPos p_218805_1_) {
   }

   public static void func_218801_c(ServerWorld worldIn, BlockPos p_218801_1_) {
   }

   public static void sendPath(World worldIn, MobEntity p_218803_1_, @Nullable Path p_218803_2_, float p_218803_3_) {
   }

   public static void func_218806_a(World worldIn, BlockPos p_218806_1_) {
   }

   public static void sendStructureStart(IWorld worldIn, StructureStart p_218804_1_) {
   }

   public static void sendGoal(World worldIn, MobEntity p_218800_1_, GoalSelector p_218800_2_) {
   }

   public static void sendRaids(ServerWorld worldIn, Collection<Raid> p_222946_1_) {
   }

   public static void sendLivingEntity(LivingEntity p_218798_0_) {
   }

   public static void func_229749_a_(BeeEntity p_229749_0_) {
   }

   public static void sendBeehiveDebugData(BeehiveTileEntity p_229750_0_) {
   }

   private static void func_229753_a_(ServerWorld p_229753_0_, PacketBuffer p_229753_1_, ResourceLocation p_229753_2_) {
      IPacket<?> ipacket = new SCustomPayloadPlayPacket(p_229753_2_, p_229753_1_);

      for(PlayerEntity playerentity : p_229753_0_.getWorld().getPlayers()) {
         ((ServerPlayerEntity)playerentity).connection.sendPacket(ipacket);
      }

   }
}