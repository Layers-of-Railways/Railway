package net.minecraft.server.management;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DemoPlayerInteractionManager extends PlayerInteractionManager {
   private boolean displayedIntro;
   private boolean demoTimeExpired;
   private int demoEndedReminder;
   private int gameModeTicks;

   public DemoPlayerInteractionManager(ServerWorld p_i50709_1_) {
      super(p_i50709_1_);
   }

   public void tick() {
      super.tick();
      ++this.gameModeTicks;
      long i = this.world.getGameTime();
      long j = i / 24000L + 1L;
      if (!this.displayedIntro && this.gameModeTicks > 20) {
         this.displayedIntro = true;
         this.player.connection.sendPacket(new SChangeGameStatePacket(5, 0.0F));
      }

      this.demoTimeExpired = i > 120500L;
      if (this.demoTimeExpired) {
         ++this.demoEndedReminder;
      }

      if (i % 24000L == 500L) {
         if (j <= 6L) {
            if (j == 6L) {
               this.player.connection.sendPacket(new SChangeGameStatePacket(5, 104.0F));
            } else {
               this.player.sendMessage(new TranslationTextComponent("demo.day." + j));
            }
         }
      } else if (j == 1L) {
         if (i == 100L) {
            this.player.connection.sendPacket(new SChangeGameStatePacket(5, 101.0F));
         } else if (i == 175L) {
            this.player.connection.sendPacket(new SChangeGameStatePacket(5, 102.0F));
         } else if (i == 250L) {
            this.player.connection.sendPacket(new SChangeGameStatePacket(5, 103.0F));
         }
      } else if (j == 5L && i % 24000L == 22000L) {
         this.player.sendMessage(new TranslationTextComponent("demo.day.warning"));
      }

   }

   /**
    * Sends a message to the player reminding them that this is the demo version
    */
   private void sendDemoReminder() {
      if (this.demoEndedReminder > 100) {
         this.player.sendMessage(new TranslationTextComponent("demo.reminder"));
         this.demoEndedReminder = 0;
      }

   }

   public void func_225416_a(BlockPos p_225416_1_, CPlayerDiggingPacket.Action p_225416_2_, Direction p_225416_3_, int p_225416_4_) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
      } else {
         super.func_225416_a(p_225416_1_, p_225416_2_, p_225416_3_, p_225416_4_);
      }
   }

   public ActionResultType processRightClick(PlayerEntity player, World worldIn, ItemStack stack, Hand hand) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
         return ActionResultType.PASS;
      } else {
         return super.processRightClick(player, worldIn, stack, hand);
      }
   }

   public ActionResultType func_219441_a(PlayerEntity playerIn, World worldIn, ItemStack stackIn, Hand handIn, BlockRayTraceResult blockRaytraceResultIn) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
         return ActionResultType.PASS;
      } else {
         return super.func_219441_a(playerIn, worldIn, stackIn, handIn, blockRaytraceResultIn);
      }
   }
}