package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class CelebrateRaidVictoryTask extends Task<VillagerEntity> {
   @Nullable
   private Raid raid;

   public CelebrateRaidVictoryTask(int p_i50370_1_, int p_i50370_2_) {
      super(ImmutableMap.of(), p_i50370_1_, p_i50370_2_);
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      BlockPos blockpos = new BlockPos(owner);
      this.raid = worldIn.findRaid(blockpos);
      return this.raid != null && this.raid.isVictory() && MoveToSkylightTask.func_226306_a_(worldIn, owner, blockpos);
   }

   protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      return this.raid != null && !this.raid.isStopped();
   }

   protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      this.raid = null;
      entityIn.getBrain().updateActivity(worldIn.getDayTime(), worldIn.getGameTime());
   }

   protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      Random random = owner.getRNG();
      if (random.nextInt(100) == 0) {
         owner.playCelebrateSound();
      }

      if (random.nextInt(200) == 0 && MoveToSkylightTask.func_226306_a_(worldIn, owner, new BlockPos(owner))) {
         DyeColor dyecolor = DyeColor.values()[random.nextInt(DyeColor.values().length)];
         int i = random.nextInt(3);
         ItemStack itemstack = this.makeFirework(dyecolor, i);
         FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(owner.world, owner.getPosX(), owner.getPosYEye(), owner.getPosZ(), itemstack);
         owner.world.addEntity(fireworkrocketentity);
      }

   }

   private ItemStack makeFirework(DyeColor color, int flightTime) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 1);
      ItemStack itemstack1 = new ItemStack(Items.FIREWORK_STAR);
      CompoundNBT compoundnbt = itemstack1.getOrCreateChildTag("Explosion");
      List<Integer> list = Lists.newArrayList();
      list.add(color.getFireworkColor());
      compoundnbt.putIntArray("Colors", list);
      compoundnbt.putByte("Type", (byte)FireworkRocketItem.Shape.BURST.getIndex());
      CompoundNBT compoundnbt1 = itemstack.getOrCreateChildTag("Fireworks");
      ListNBT listnbt = new ListNBT();
      CompoundNBT compoundnbt2 = itemstack1.getChildTag("Explosion");
      if (compoundnbt2 != null) {
         listnbt.add(compoundnbt2);
      }

      compoundnbt1.putByte("Flight", (byte)flightTime);
      if (!listnbt.isEmpty()) {
         compoundnbt1.put("Explosions", listnbt);
      }

      return itemstack;
   }
}