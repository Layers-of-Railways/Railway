package net.minecraft.dispenser;

import java.util.Random;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.WitherSkeletonSkullBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public interface IDispenseItemBehavior {
   IDispenseItemBehavior NOOP = (p_210297_0_, p_210297_1_) -> {
      return p_210297_1_;
   };

   ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_);

   static void init() {
      DispenserBlock.registerDispenseBehavior(Items.ARROW, new ProjectileDispenseBehavior() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            ArrowEntity arrowentity = new ArrowEntity(worldIn, position.getX(), position.getY(), position.getZ());
            arrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
            return arrowentity;
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.TIPPED_ARROW, new ProjectileDispenseBehavior() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            ArrowEntity arrowentity = new ArrowEntity(worldIn, position.getX(), position.getY(), position.getZ());
            arrowentity.setPotionEffect(stackIn);
            arrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
            return arrowentity;
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.SPECTRAL_ARROW, new ProjectileDispenseBehavior() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            AbstractArrowEntity abstractarrowentity = new SpectralArrowEntity(worldIn, position.getX(), position.getY(), position.getZ());
            abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
            return abstractarrowentity;
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.EGG, new ProjectileDispenseBehavior() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            return Util.make(new EggEntity(worldIn, position.getX(), position.getY(), position.getZ()), (p_218408_1_) -> {
               p_218408_1_.setItem(stackIn);
            });
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.SNOWBALL, new ProjectileDispenseBehavior() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            return Util.make(new SnowballEntity(worldIn, position.getX(), position.getY(), position.getZ()), (p_218409_1_) -> {
               p_218409_1_.setItem(stackIn);
            });
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.EXPERIENCE_BOTTLE, new ProjectileDispenseBehavior() {
         /**
          * Return the projectile entity spawned by this dispense behavior.
          */
         protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            return Util.make(new ExperienceBottleEntity(worldIn, position.getX(), position.getY(), position.getZ()), (p_218410_1_) -> {
               p_218410_1_.setItem(stackIn);
            });
         }

         protected float getProjectileInaccuracy() {
            return super.getProjectileInaccuracy() * 0.5F;
         }

         protected float getProjectileVelocity() {
            return super.getProjectileVelocity() * 1.25F;
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.SPLASH_POTION, new IDispenseItemBehavior() {
         public ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
            return (new ProjectileDispenseBehavior() {
               /**
                * Return the projectile entity spawned by this dispense behavior.
                */
               protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                  return Util.make(new PotionEntity(worldIn, position.getX(), position.getY(), position.getZ()), (p_218411_1_) -> {
                     p_218411_1_.setItem(stackIn);
                  });
               }

               protected float getProjectileInaccuracy() {
                  return super.getProjectileInaccuracy() * 0.5F;
               }

               protected float getProjectileVelocity() {
                  return super.getProjectileVelocity() * 1.25F;
               }
            }).dispense(p_dispense_1_, p_dispense_2_);
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.LINGERING_POTION, new IDispenseItemBehavior() {
         public ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
            return (new ProjectileDispenseBehavior() {
               /**
                * Return the projectile entity spawned by this dispense behavior.
                */
               protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                  return Util.make(new PotionEntity(worldIn, position.getX(), position.getY(), position.getZ()), (p_218413_1_) -> {
                     p_218413_1_.setItem(stackIn);
                  });
               }

               protected float getProjectileInaccuracy() {
                  return super.getProjectileInaccuracy() * 0.5F;
               }

               protected float getProjectileVelocity() {
                  return super.getProjectileVelocity() * 1.25F;
               }
            }).dispense(p_dispense_1_, p_dispense_2_);
         }
      });
      DefaultDispenseItemBehavior defaultdispenseitembehavior = new DefaultDispenseItemBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            EntityType<?> entitytype = ((SpawnEggItem)stack.getItem()).getType(stack.getTag());
            entitytype.spawn(source.getWorld(), stack, (PlayerEntity)null, source.getBlockPos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
            stack.shrink(1);
            return stack;
         }
      };

      for(SpawnEggItem spawneggitem : SpawnEggItem.getEggs()) {
         DispenserBlock.registerDispenseBehavior(spawneggitem, defaultdispenseitembehavior);
      }

      DispenserBlock.registerDispenseBehavior(Items.ARMOR_STAND, new DefaultDispenseItemBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            BlockPos blockpos = source.getBlockPos().offset(direction);
            World world = source.getWorld();
            ArmorStandEntity armorstandentity = new ArmorStandEntity(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
            EntityType.applyItemNBT(world, (PlayerEntity)null, armorstandentity, stack.getTag());
            armorstandentity.rotationYaw = direction.getHorizontalAngle();
            world.addEntity(armorstandentity);
            stack.shrink(1);
            return stack;
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            double d0 = (double)direction.getXOffset();
            double d1 = (double)direction.getYOffset();
            double d2 = (double)direction.getZOffset();
            double d3 = source.getX() + d0;
            double d4 = (double)((float)source.getBlockPos().getY() + 0.2F);
            double d5 = source.getZ() + d2;
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(source.getWorld(), stack, d3, d4, d5, true);
            fireworkrocketentity.shoot(d0, d1, d2, 0.5F, 1.0F);
            source.getWorld().addEntity(fireworkrocketentity);
            stack.shrink(1);
            return stack;
         }

         /**
          * Play the dispense sound from the specified block.
          */
         protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playEvent(1004, source.getBlockPos(), 0);
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            IPosition iposition = DispenserBlock.getDispensePosition(source);
            double d0 = iposition.getX() + (double)((float)direction.getXOffset() * 0.3F);
            double d1 = iposition.getY() + (double)((float)direction.getYOffset() * 0.3F);
            double d2 = iposition.getZ() + (double)((float)direction.getZOffset() * 0.3F);
            World world = source.getWorld();
            Random random = world.rand;
            double d3 = random.nextGaussian() * 0.05D + (double)direction.getXOffset();
            double d4 = random.nextGaussian() * 0.05D + (double)direction.getYOffset();
            double d5 = random.nextGaussian() * 0.05D + (double)direction.getZOffset();
            world.addEntity(Util.make(new SmallFireballEntity(world, d0, d1, d2, d3, d4, d5), (p_229425_1_) -> {
               p_229425_1_.setStack(stack);
            }));
            stack.shrink(1);
            return stack;
         }

         /**
          * Play the dispense sound from the specified block.
          */
         protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playEvent(1018, source.getBlockPos(), 0);
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.OAK_BOAT, new DispenseBoatBehavior(BoatEntity.Type.OAK));
      DispenserBlock.registerDispenseBehavior(Items.SPRUCE_BOAT, new DispenseBoatBehavior(BoatEntity.Type.SPRUCE));
      DispenserBlock.registerDispenseBehavior(Items.BIRCH_BOAT, new DispenseBoatBehavior(BoatEntity.Type.BIRCH));
      DispenserBlock.registerDispenseBehavior(Items.JUNGLE_BOAT, new DispenseBoatBehavior(BoatEntity.Type.JUNGLE));
      DispenserBlock.registerDispenseBehavior(Items.DARK_OAK_BOAT, new DispenseBoatBehavior(BoatEntity.Type.DARK_OAK));
      DispenserBlock.registerDispenseBehavior(Items.ACACIA_BOAT, new DispenseBoatBehavior(BoatEntity.Type.ACACIA));
      IDispenseItemBehavior idispenseitembehavior = new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior field_218406_b = new DefaultDispenseItemBehavior();

         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            BucketItem bucketitem = (BucketItem)stack.getItem();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            World world = source.getWorld();
            if (bucketitem.tryPlaceContainedLiquid((PlayerEntity)null, world, blockpos, (BlockRayTraceResult)null)) {
               bucketitem.onLiquidPlaced(world, stack, blockpos);
               return new ItemStack(Items.BUCKET);
            } else {
               return this.field_218406_b.dispense(source, stack);
            }
         }
      };
      DispenserBlock.registerDispenseBehavior(Items.LAVA_BUCKET, idispenseitembehavior);
      DispenserBlock.registerDispenseBehavior(Items.WATER_BUCKET, idispenseitembehavior);
      DispenserBlock.registerDispenseBehavior(Items.SALMON_BUCKET, idispenseitembehavior);
      DispenserBlock.registerDispenseBehavior(Items.COD_BUCKET, idispenseitembehavior);
      DispenserBlock.registerDispenseBehavior(Items.PUFFERFISH_BUCKET, idispenseitembehavior);
      DispenserBlock.registerDispenseBehavior(Items.TROPICAL_FISH_BUCKET, idispenseitembehavior);
      DispenserBlock.registerDispenseBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior field_229426_b_ = new DefaultDispenseItemBehavior();

         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            IWorld iworld = source.getWorld();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            BlockState blockstate = iworld.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (block instanceof IBucketPickupHandler) {
               Fluid fluid = ((IBucketPickupHandler)block).pickupFluid(iworld, blockpos, blockstate);
               if (!(fluid instanceof FlowingFluid)) {
                  return super.dispenseStack(source, stack);
               } else {
                  Item item = fluid.getFilledBucket();
                  stack.shrink(1);
                  if (stack.isEmpty()) {
                     return new ItemStack(item);
                  } else {
                     if (source.<DispenserTileEntity>getBlockTileEntity().addItemStack(new ItemStack(item)) < 0) {
                        this.field_229426_b_.dispense(source, new ItemStack(item));
                     }

                     return stack;
                  }
               }
            } else {
               return super.dispenseStack(source, stack);
            }
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            this.successful = true;
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            BlockState blockstate = world.getBlockState(blockpos);
            if (FlintAndSteelItem.canSetFire(blockstate, world, blockpos)) {
               world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
            } else if (FlintAndSteelItem.isUnlitCampfire(blockstate)) {
               world.setBlockState(blockpos, blockstate.with(BlockStateProperties.LIT, Boolean.valueOf(true)));
            } else if (blockstate.isFlammable(world, blockpos, source.getBlockState().get(DispenserBlock.FACING).getOpposite())) {
               blockstate.catchFire(world, blockpos, source.getBlockState().get(DispenserBlock.FACING).getOpposite(), null);
               if (blockstate.getBlock() instanceof TNTBlock)
               world.removeBlock(blockpos, false);
            } else {
               this.successful = false;
            }

            if (this.successful && stack.attemptDamageItem(1, world.rand, (ServerPlayerEntity)null)) {
               stack.setCount(0);
            }

            return stack;
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.BONE_MEAL, new OptionalDispenseBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            this.successful = true;
            World world = source.getWorld();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            if (!BoneMealItem.applyBonemeal(stack, world, blockpos) && !BoneMealItem.growSeagrass(stack, world, blockpos, (Direction)null)) {
               this.successful = false;
            } else if (!world.isRemote) {
               world.playEvent(2005, blockpos, 0);
            }

            return stack;
         }
      });
      DispenserBlock.registerDispenseBehavior(Blocks.TNT, new DefaultDispenseItemBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            TNTEntity tntentity = new TNTEntity(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, (LivingEntity)null);
            world.addEntity(tntentity);
            world.playSound((PlayerEntity)null, tntentity.getPosX(), tntentity.getPosY(), tntentity.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            stack.shrink(1);
            return stack;
         }
      });
      IDispenseItemBehavior idispenseitembehavior1 = new OptionalDispenseBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            this.successful = ArmorItem.func_226626_a_(source, stack);
            return stack;
         }
      };
      DispenserBlock.registerDispenseBehavior(Items.CREEPER_HEAD, idispenseitembehavior1);
      DispenserBlock.registerDispenseBehavior(Items.ZOMBIE_HEAD, idispenseitembehavior1);
      DispenserBlock.registerDispenseBehavior(Items.DRAGON_HEAD, idispenseitembehavior1);
      DispenserBlock.registerDispenseBehavior(Items.SKELETON_SKULL, idispenseitembehavior1);
      DispenserBlock.registerDispenseBehavior(Items.PLAYER_HEAD, idispenseitembehavior1);
      DispenserBlock.registerDispenseBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            BlockPos blockpos = source.getBlockPos().offset(direction);
            if (world.isAirBlock(blockpos) && WitherSkeletonSkullBlock.canSpawnMob(world, blockpos, stack)) {
               world.setBlockState(blockpos, Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, Integer.valueOf(direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().getHorizontalIndex() * 4)), 3);
               TileEntity tileentity = world.getTileEntity(blockpos);
               if (tileentity instanceof SkullTileEntity) {
                  WitherSkeletonSkullBlock.checkWitherSpawn(world, blockpos, (SkullTileEntity)tileentity);
               }

               stack.shrink(1);
               this.successful = true;
            } else {
               this.successful = ArmorItem.func_226626_a_(source, stack);
            }

            return stack;
         }
      });
      DispenserBlock.registerDispenseBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            CarvedPumpkinBlock carvedpumpkinblock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
            if (world.isAirBlock(blockpos) && carvedpumpkinblock.canDispenserPlace(world, blockpos)) {
               if (!world.isRemote) {
                  world.setBlockState(blockpos, carvedpumpkinblock.getDefaultState(), 3);
               }

               stack.shrink(1);
               this.successful = true;
            } else {
               this.successful = ArmorItem.func_226626_a_(source, stack);
            }

            return stack;
         }
      });
      DispenserBlock.registerDispenseBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());

      for(DyeColor dyecolor : DyeColor.values()) {
         DispenserBlock.registerDispenseBehavior(ShulkerBoxBlock.getBlockByColor(dyecolor).asItem(), new ShulkerBoxDispenseBehavior());
      }

      DispenserBlock.registerDispenseBehavior(Items.GLASS_BOTTLE.asItem(), new OptionalDispenseBehavior() {
         private final DefaultDispenseItemBehavior field_229423_c_ = new DefaultDispenseItemBehavior();

         private ItemStack func_229424_a_(IBlockSource p_229424_1_, ItemStack p_229424_2_, ItemStack p_229424_3_) {
            p_229424_2_.shrink(1);
            if (p_229424_2_.isEmpty()) {
               return p_229424_3_.copy();
            } else {
               if (p_229424_1_.<DispenserTileEntity>getBlockTileEntity().addItemStack(p_229424_3_.copy()) < 0) {
                  this.field_229423_c_.dispense(p_229424_1_, p_229424_3_.copy());
               }

               return p_229424_2_;
            }
         }

         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            this.successful = false;
            IWorld iworld = source.getWorld();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            BlockState blockstate = iworld.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (block.isIn(BlockTags.BEEHIVES) && blockstate.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
               ((BeehiveBlock)blockstate.getBlock()).takeHoney(iworld.getWorld(), blockstate, blockpos, (PlayerEntity)null, BeehiveTileEntity.State.BEE_RELEASED);
               this.successful = true;
               return this.func_229424_a_(source, stack, new ItemStack(Items.HONEY_BOTTLE));
            } else if (iworld.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
               this.successful = true;
               return this.func_229424_a_(source, stack, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER));
            } else {
               return super.dispenseStack(source, stack);
            }
         }
      });
      DispenserBlock.registerDispenseBehavior(Items.SHEARS.asItem(), new OptionalDispenseBehavior() {
         /**
          * Dispense the specified stack, play the dispense sound and spawn particles.
          */
         @SuppressWarnings("deprecation")
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            if (!world.isRemote()) {
               this.successful = false;
               BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));

               for(net.minecraft.entity.Entity entity : world.getEntitiesInAABBexcluding((net.minecraft.entity.Entity)null, new AxisAlignedBB(blockpos), e -> !e.isSpectator() && e instanceof net.minecraftforge.common.IShearable)) {
                  net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable)entity;
                  if (target.isShearable(stack, world, blockpos)) {
                     java.util.List<ItemStack> drops = target.onSheared(stack, entity.world, blockpos,
                             net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantments.FORTUNE, stack));
                     java.util.Random rand = new java.util.Random();
                     drops.forEach(d -> {
                        net.minecraft.entity.item.ItemEntity ent = entity.entityDropItem(d, 1.0F);
                        ent.setMotion(ent.getMotion().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                     });
                     if (stack.attemptDamageItem(1, world.rand, (ServerPlayerEntity)null)) {
                        stack.setCount(0);
                     }

                     this.successful = true;
                     break;
                  }
               }

               if (!this.successful) {
                  BlockState blockstate = world.getBlockState(blockpos);
                  if (blockstate.isIn(BlockTags.BEEHIVES)) {
                     int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                     if (i >= 5) {
                        if (stack.attemptDamageItem(1, world.rand, (ServerPlayerEntity)null)) {
                           stack.setCount(0);
                        }

                        BeehiveBlock.dropHoneyComb(world, blockpos);
                        ((BeehiveBlock)blockstate.getBlock()).takeHoney(world, blockstate, blockpos, (PlayerEntity)null, BeehiveTileEntity.State.BEE_RELEASED);
                        this.successful = true;
                     }
                  }
               }
            }

            return stack;
         }
      });
   }
}