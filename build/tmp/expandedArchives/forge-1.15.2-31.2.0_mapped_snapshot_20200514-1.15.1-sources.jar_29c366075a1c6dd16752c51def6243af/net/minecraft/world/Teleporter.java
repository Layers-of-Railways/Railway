package net.minecraft.world;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;

public class Teleporter implements net.minecraftforge.common.util.ITeleporter {
   protected final ServerWorld world;
   protected final Random random;

   public Teleporter(ServerWorld worldIn) {
      this.world = worldIn;
      this.random = new Random(worldIn.getSeed());
   }

   public boolean placeInPortal(Entity p_222268_1_, float p_222268_2_) {
      Vec3d vec3d = p_222268_1_.getLastPortalVec();
      Direction direction = p_222268_1_.getTeleportDirection();
      BlockPattern.PortalInfo blockpattern$portalinfo = this.placeInExistingPortal(new BlockPos(p_222268_1_), p_222268_1_.getMotion(), direction, vec3d.x, vec3d.y, p_222268_1_ instanceof PlayerEntity);
      if (blockpattern$portalinfo == null) {
         return false;
      } else {
         Vec3d vec3d1 = blockpattern$portalinfo.pos;
         Vec3d vec3d2 = blockpattern$portalinfo.motion;
         p_222268_1_.setMotion(vec3d2);
         p_222268_1_.rotationYaw = p_222268_2_ + (float)blockpattern$portalinfo.rotation;
         p_222268_1_.moveForced(vec3d1.x, vec3d1.y, vec3d1.z);
         return true;
      }
   }

   @Nullable
   public BlockPattern.PortalInfo placeInExistingPortal(BlockPos p_222272_1_, Vec3d p_222272_2_, Direction directionIn, double p_222272_4_, double p_222272_6_, boolean p_222272_8_) {
      PointOfInterestManager pointofinterestmanager = this.world.getPointOfInterestManager();
      pointofinterestmanager.ensureLoadedAndValid(this.world, p_222272_1_, 128);
      List<PointOfInterest> list = pointofinterestmanager.getInSquare((p_226705_0_) -> {
         return p_226705_0_ == PointOfInterestType.NETHER_PORTAL;
      }, p_222272_1_, 128, PointOfInterestManager.Status.ANY).collect(Collectors.toList());
      Optional<PointOfInterest> optional = list.stream().min(Comparator.<PointOfInterest>comparingDouble((p_226706_1_) -> {
         return p_226706_1_.getPos().distanceSq(p_222272_1_);
      }).thenComparingInt((p_226704_0_) -> {
         return p_226704_0_.getPos().getY();
      }));
      return optional.map((p_226707_7_) -> {
         BlockPos blockpos = p_226707_7_.getPos();
         this.world.getChunkProvider().registerTicket(TicketType.PORTAL, new ChunkPos(blockpos), 3, blockpos);
         BlockPattern.PatternHelper blockpattern$patternhelper = NetherPortalBlock.createPatternHelper(this.world, blockpos);
         return blockpattern$patternhelper.getPortalInfo(directionIn, blockpos, p_222272_6_, p_222272_2_, p_222272_4_);
      }).orElse((BlockPattern.PortalInfo)null);
   }

   public boolean makePortal(Entity entityIn) {
      int i = 16;
      double d0 = -1.0D;
      int j = MathHelper.floor(entityIn.getPosX());
      int k = MathHelper.floor(entityIn.getPosY());
      int l = MathHelper.floor(entityIn.getPosZ());
      int i1 = j;
      int j1 = k;
      int k1 = l;
      int l1 = 0;
      int i2 = this.random.nextInt(4);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int j2 = j - 16; j2 <= j + 16; ++j2) {
         double d1 = (double)j2 + 0.5D - entityIn.getPosX();

         for(int l2 = l - 16; l2 <= l + 16; ++l2) {
            double d2 = (double)l2 + 0.5D - entityIn.getPosZ();

            label276:
            for(int j3 = this.world.getActualHeight() - 1; j3 >= 0; --j3) {
               if (this.world.isAirBlock(blockpos$mutable.setPos(j2, j3, l2))) {
                  while(j3 > 0 && this.world.isAirBlock(blockpos$mutable.setPos(j2, j3 - 1, l2))) {
                     --j3;
                  }

                  for(int k3 = i2; k3 < i2 + 4; ++k3) {
                     int l3 = k3 % 2;
                     int i4 = 1 - l3;
                     if (k3 % 4 >= 2) {
                        l3 = -l3;
                        i4 = -i4;
                     }

                     for(int j4 = 0; j4 < 3; ++j4) {
                        for(int k4 = 0; k4 < 4; ++k4) {
                           for(int l4 = -1; l4 < 4; ++l4) {
                              int i5 = j2 + (k4 - 1) * l3 + j4 * i4;
                              int j5 = j3 + l4;
                              int k5 = l2 + (k4 - 1) * i4 - j4 * l3;
                              blockpos$mutable.setPos(i5, j5, k5);
                              if (l4 < 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().isSolid() || l4 >= 0 && !this.world.isAirBlock(blockpos$mutable)) {
                                 continue label276;
                              }
                           }
                        }
                     }

                     double d5 = (double)j3 + 0.5D - entityIn.getPosY();
                     double d7 = d1 * d1 + d5 * d5 + d2 * d2;
                     if (d0 < 0.0D || d7 < d0) {
                        d0 = d7;
                        i1 = j2;
                        j1 = j3;
                        k1 = l2;
                        l1 = k3 % 4;
                     }
                  }
               }
            }
         }
      }

      if (d0 < 0.0D) {
         for(int l5 = j - 16; l5 <= j + 16; ++l5) {
            double d3 = (double)l5 + 0.5D - entityIn.getPosX();

            for(int j6 = l - 16; j6 <= l + 16; ++j6) {
               double d4 = (double)j6 + 0.5D - entityIn.getPosZ();

               label214:
               for(int i7 = this.world.getActualHeight() - 1; i7 >= 0; --i7) {
                  if (this.world.isAirBlock(blockpos$mutable.setPos(l5, i7, j6))) {
                     while(i7 > 0 && this.world.isAirBlock(blockpos$mutable.setPos(l5, i7 - 1, j6))) {
                        --i7;
                     }

                     for(int l7 = i2; l7 < i2 + 2; ++l7) {
                        int l8 = l7 % 2;
                        int k9 = 1 - l8;

                        for(int i10 = 0; i10 < 4; ++i10) {
                           for(int k10 = -1; k10 < 4; ++k10) {
                              int i11 = l5 + (i10 - 1) * l8;
                              int j11 = i7 + k10;
                              int k11 = j6 + (i10 - 1) * k9;
                              blockpos$mutable.setPos(i11, j11, k11);
                              if (k10 < 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().isSolid() || k10 >= 0 && !this.world.isAirBlock(blockpos$mutable)) {
                                 continue label214;
                              }
                           }
                        }

                        double d6 = (double)i7 + 0.5D - entityIn.getPosY();
                        double d8 = d3 * d3 + d6 * d6 + d4 * d4;
                        if (d0 < 0.0D || d8 < d0) {
                           d0 = d8;
                           i1 = l5;
                           j1 = i7;
                           k1 = j6;
                           l1 = l7 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int i6 = i1;
      int k2 = j1;
      int k6 = k1;
      int l6 = l1 % 2;
      int i3 = 1 - l6;
      if (l1 % 4 >= 2) {
         l6 = -l6;
         i3 = -i3;
      }

      if (d0 < 0.0D) {
         j1 = MathHelper.clamp(j1, 70, this.world.getActualHeight() - 10);
         k2 = j1;

         for(int j7 = -1; j7 <= 1; ++j7) {
            for(int i8 = 1; i8 < 3; ++i8) {
               for(int i9 = -1; i9 < 3; ++i9) {
                  int l9 = i6 + (i8 - 1) * l6 + j7 * i3;
                  int j10 = k2 + i9;
                  int l10 = k6 + (i8 - 1) * i3 - j7 * l6;
                  boolean flag = i9 < 0;
                  blockpos$mutable.setPos(l9, j10, l10);
                  this.world.setBlockState(blockpos$mutable, flag ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
               }
            }
         }
      }

      for(int k7 = -1; k7 < 3; ++k7) {
         for(int j8 = -1; j8 < 4; ++j8) {
            if (k7 == -1 || k7 == 2 || j8 == -1 || j8 == 3) {
               blockpos$mutable.setPos(i6 + k7 * l6, k2 + j8, k6 + k7 * i3);
               this.world.setBlockState(blockpos$mutable, Blocks.OBSIDIAN.getDefaultState(), 3);
            }
         }
      }

      BlockState blockstate = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, l6 == 0 ? Direction.Axis.Z : Direction.Axis.X);

      for(int k8 = 0; k8 < 2; ++k8) {
         for(int j9 = 0; j9 < 3; ++j9) {
            blockpos$mutable.setPos(i6 + k8 * l6, k2 + j9, k6 + k8 * i3);
            this.world.setBlockState(blockpos$mutable, blockstate, 18);
         }
      }

      return true;
   }
}