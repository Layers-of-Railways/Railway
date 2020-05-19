package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SilverfishBlock extends Block {
   private final Block mimickedBlock;
   private static final Map<Block, Block> field_196470_b = Maps.newIdentityHashMap();

   public SilverfishBlock(Block blockIn, Block.Properties properties) {
      super(properties);
      this.mimickedBlock = blockIn;
      field_196470_b.put(blockIn, this);
   }

   public Block getMimickedBlock() {
      return this.mimickedBlock;
   }

   public static boolean canContainSilverfish(BlockState state) {
      return field_196470_b.containsKey(state.getBlock());
   }

   /**
    * Perform side-effects from block dropping, such as creating silverfish
    */
   public void spawnAdditionalDrops(BlockState state, World worldIn, BlockPos pos, ItemStack stack) {
      super.spawnAdditionalDrops(state, worldIn, pos, stack);
      if (!worldIn.isRemote && worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
         SilverfishEntity silverfishentity = EntityType.SILVERFISH.create(worldIn);
         silverfishentity.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
         worldIn.addEntity(silverfishentity);
         silverfishentity.spawnExplosionParticle();
      }

   }

   public static BlockState infest(Block blockIn) {
      return field_196470_b.get(blockIn).getDefaultState();
   }
}