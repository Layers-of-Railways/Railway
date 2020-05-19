package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoeItem extends TieredItem {
   private final float speed;
   protected static final Map<Block, BlockState> HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));

   public HoeItem(IItemTier tier, float attackSpeedIn, Item.Properties builder) {
      super(tier, builder);
      this.speed = attackSpeedIn;
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(context);
      if (hook != 0) return hook > 0 ? ActionResultType.SUCCESS : ActionResultType.FAIL;
      if (context.getFace() != Direction.DOWN && world.isAirBlock(blockpos.up())) {
         BlockState blockstate = HOE_LOOKUP.get(world.getBlockState(blockpos).getBlock());
         if (blockstate != null) {
            PlayerEntity playerentity = context.getPlayer();
            world.playSound(playerentity, blockpos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isRemote) {
               world.setBlockState(blockpos, blockstate, 11);
               if (playerentity != null) {
                  context.getItem().damageItem(1, playerentity, (p_220043_1_) -> {
                     p_220043_1_.sendBreakAnimation(context.getHand());
                  });
               }
            }

            return ActionResultType.SUCCESS;
         }
      }

      return ActionResultType.PASS;
   }

   /**
    * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
    * the damage on the stack.
    */
   public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.damageItem(1, attacker, (p_220042_0_) -> {
         p_220042_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   /**
    * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
    */
   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
      if (equipmentSlot == EquipmentSlotType.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 0.0D, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.speed, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }
}