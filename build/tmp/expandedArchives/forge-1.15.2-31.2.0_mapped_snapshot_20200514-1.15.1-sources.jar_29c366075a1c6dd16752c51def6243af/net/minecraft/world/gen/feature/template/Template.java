package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Template {
   private final List<List<Template.BlockInfo>> blocks = Lists.newArrayList();
   private final List<Template.EntityInfo> entities = Lists.newArrayList();
   private BlockPos size = BlockPos.ZERO;
   private String author = "?";

   public BlockPos getSize() {
      return this.size;
   }

   public void setAuthor(String authorIn) {
      this.author = authorIn;
   }

   public String getAuthor() {
      return this.author;
   }

   /**
    * takes blocks from the world and puts the data them into this template
    */
   public void takeBlocksFromWorld(World worldIn, BlockPos startPos, BlockPos size, boolean takeEntities, @Nullable Block toIgnore) {
      if (size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
         BlockPos blockpos = startPos.add(size).add(-1, -1, -1);
         List<Template.BlockInfo> list = Lists.newArrayList();
         List<Template.BlockInfo> list1 = Lists.newArrayList();
         List<Template.BlockInfo> list2 = Lists.newArrayList();
         BlockPos blockpos1 = new BlockPos(Math.min(startPos.getX(), blockpos.getX()), Math.min(startPos.getY(), blockpos.getY()), Math.min(startPos.getZ(), blockpos.getZ()));
         BlockPos blockpos2 = new BlockPos(Math.max(startPos.getX(), blockpos.getX()), Math.max(startPos.getY(), blockpos.getY()), Math.max(startPos.getZ(), blockpos.getZ()));
         this.size = size;

         for(BlockPos blockpos3 : BlockPos.getAllInBoxMutable(blockpos1, blockpos2)) {
            BlockPos blockpos4 = blockpos3.subtract(blockpos1);
            BlockState blockstate = worldIn.getBlockState(blockpos3);
            if (toIgnore == null || toIgnore != blockstate.getBlock()) {
               TileEntity tileentity = worldIn.getTileEntity(blockpos3);
               if (tileentity != null) {
                  CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());
                  compoundnbt.remove("x");
                  compoundnbt.remove("y");
                  compoundnbt.remove("z");
                  list1.add(new Template.BlockInfo(blockpos4, blockstate, compoundnbt));
               } else if (!blockstate.isOpaqueCube(worldIn, blockpos3) && !blockstate.isCollisionShapeOpaque(worldIn, blockpos3)) {
                  list2.add(new Template.BlockInfo(blockpos4, blockstate, (CompoundNBT)null));
               } else {
                  list.add(new Template.BlockInfo(blockpos4, blockstate, (CompoundNBT)null));
               }
            }
         }

         List<Template.BlockInfo> list3 = Lists.newArrayList();
         list3.addAll(list);
         list3.addAll(list1);
         list3.addAll(list2);
         this.blocks.clear();
         this.blocks.add(list3);
         if (takeEntities) {
            this.takeEntitiesFromWorld(worldIn, blockpos1, blockpos2.add(1, 1, 1));
         } else {
            this.entities.clear();
         }

      }
   }

   /**
    * takes blocks from the world and puts the data them into this template
    */
   private void takeEntitiesFromWorld(World worldIn, BlockPos startPos, BlockPos endPos) {
      List<Entity> list = worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(startPos, endPos), (p_201048_0_) -> {
         return !(p_201048_0_ instanceof PlayerEntity);
      });
      this.entities.clear();

      for(Entity entity : list) {
         Vec3d vec3d = new Vec3d(entity.getPosX() - (double)startPos.getX(), entity.getPosY() - (double)startPos.getY(), entity.getPosZ() - (double)startPos.getZ());
         CompoundNBT compoundnbt = new CompoundNBT();
         entity.writeUnlessPassenger(compoundnbt);
         BlockPos blockpos;
         if (entity instanceof PaintingEntity) {
            blockpos = ((PaintingEntity)entity).getHangingPosition().subtract(startPos);
         } else {
            blockpos = new BlockPos(vec3d);
         }

         this.entities.add(new Template.EntityInfo(vec3d, blockpos, compoundnbt));
      }

   }

   public List<Template.BlockInfo> func_215381_a(BlockPos p_215381_1_, PlacementSettings p_215381_2_, Block p_215381_3_) {
      return this.func_215386_a(p_215381_1_, p_215381_2_, p_215381_3_, true);
   }

   public List<Template.BlockInfo> func_215386_a(BlockPos p_215386_1_, PlacementSettings p_215386_2_, Block p_215386_3_, boolean p_215386_4_) {
      List<Template.BlockInfo> list = Lists.newArrayList();
      MutableBoundingBox mutableboundingbox = p_215386_2_.getBoundingBox();

      for(Template.BlockInfo template$blockinfo : p_215386_2_.func_227459_a_(this.blocks, p_215386_1_)) {
         BlockPos blockpos = p_215386_4_ ? transformedBlockPos(p_215386_2_, template$blockinfo.pos).add(p_215386_1_) : template$blockinfo.pos;
         if (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos)) {
            BlockState blockstate = template$blockinfo.state;
            if (blockstate.getBlock() == p_215386_3_) {
               list.add(new Template.BlockInfo(blockpos, blockstate.rotate(p_215386_2_.getRotation()), template$blockinfo.nbt));
            }
         }
      }

      return list;
   }

   public BlockPos calculateConnectedPos(PlacementSettings placementIn, BlockPos p_186262_2_, PlacementSettings p_186262_3_, BlockPos p_186262_4_) {
      BlockPos blockpos = transformedBlockPos(placementIn, p_186262_2_);
      BlockPos blockpos1 = transformedBlockPos(p_186262_3_, p_186262_4_);
      return blockpos.subtract(blockpos1);
   }

   public static BlockPos transformedBlockPos(PlacementSettings placementIn, BlockPos pos) {
      return getTransformedPos(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getCenterOffset());
   }

   // FORGE: Add overload accepting Vec3d
   public static Vec3d transformedVec3d(PlacementSettings placementIn, Vec3d pos) {
      return getTransformedPos(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getCenterOffset());
   }

   /**
    * Add blocks and entities from this structure to the given world, restricting placement to within the chunk bounding
    * box.
    *  
    * @see PlacementSettings#setBoundingBoxFromChunk
    */
   public void addBlocksToWorldChunk(IWorld worldIn, BlockPos pos, PlacementSettings placementIn) {
      placementIn.setBoundingBoxFromChunk();
      this.addBlocksToWorld(worldIn, pos, placementIn);
   }

   /**
    * This takes the data stored in this instance and puts them into the world.
    */
   public void addBlocksToWorld(IWorld worldIn, BlockPos pos, PlacementSettings placementIn) {
      this.addBlocksToWorld(worldIn, pos, placementIn, 2);
   }

   /**
    * Adds blocks and entities from this structure to the given world.
    */
   public boolean addBlocksToWorld(IWorld worldIn, BlockPos pos, PlacementSettings placementIn, int flags) {
      if (this.blocks.isEmpty()) {
         return false;
      } else {
         List<Template.BlockInfo> list = placementIn.func_227459_a_(this.blocks, pos);
         if ((!list.isEmpty() || !placementIn.getIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            MutableBoundingBox mutableboundingbox = placementIn.getBoundingBox();
            List<BlockPos> list1 = Lists.newArrayListWithCapacity(placementIn.func_204763_l() ? list.size() : 0);
            List<Pair<BlockPos, CompoundNBT>> list2 = Lists.newArrayListWithCapacity(list.size());
            int i = Integer.MAX_VALUE;
            int j = Integer.MAX_VALUE;
            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            int i1 = Integer.MIN_VALUE;
            int j1 = Integer.MIN_VALUE;

            for(Template.BlockInfo template$blockinfo : processBlockInfos(this, worldIn, pos, placementIn, list)) {
               BlockPos blockpos = template$blockinfo.pos;
               if (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos)) {
                  IFluidState ifluidstate = placementIn.func_204763_l() ? worldIn.getFluidState(blockpos) : null;
                  BlockState blockstate = template$blockinfo.state.mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
                  if (template$blockinfo.nbt != null) {
                     TileEntity tileentity = worldIn.getTileEntity(blockpos);
                     IClearable.clearObj(tileentity);
                     worldIn.setBlockState(blockpos, Blocks.BARRIER.getDefaultState(), 20);
                  }

                  if (worldIn.setBlockState(blockpos, blockstate, flags)) {
                     i = Math.min(i, blockpos.getX());
                     j = Math.min(j, blockpos.getY());
                     k = Math.min(k, blockpos.getZ());
                     l = Math.max(l, blockpos.getX());
                     i1 = Math.max(i1, blockpos.getY());
                     j1 = Math.max(j1, blockpos.getZ());
                     list2.add(Pair.of(blockpos, template$blockinfo.nbt));
                     if (template$blockinfo.nbt != null) {
                        TileEntity tileentity1 = worldIn.getTileEntity(blockpos);
                        if (tileentity1 != null) {
                           template$blockinfo.nbt.putInt("x", blockpos.getX());
                           template$blockinfo.nbt.putInt("y", blockpos.getY());
                           template$blockinfo.nbt.putInt("z", blockpos.getZ());
                           tileentity1.read(template$blockinfo.nbt);
                           tileentity1.mirror(placementIn.getMirror());
                           tileentity1.rotate(placementIn.getRotation());
                        }
                     }

                     if (ifluidstate != null && blockstate.getBlock() instanceof ILiquidContainer) {
                        ((ILiquidContainer)blockstate.getBlock()).receiveFluid(worldIn, blockpos, blockstate, ifluidstate);
                        if (!ifluidstate.isSource()) {
                           list1.add(blockpos);
                        }
                     }
                  }
               }
            }

            boolean flag = true;
            Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

            while(flag && !list1.isEmpty()) {
               flag = false;
               Iterator<BlockPos> iterator = list1.iterator();

               while(iterator.hasNext()) {
                  BlockPos blockpos2 = iterator.next();
                  BlockPos blockpos3 = blockpos2;
                  IFluidState ifluidstate2 = worldIn.getFluidState(blockpos2);

                  for(int k1 = 0; k1 < adirection.length && !ifluidstate2.isSource(); ++k1) {
                     BlockPos blockpos1 = blockpos3.offset(adirection[k1]);
                     IFluidState ifluidstate1 = worldIn.getFluidState(blockpos1);
                     if (ifluidstate1.getActualHeight(worldIn, blockpos1) > ifluidstate2.getActualHeight(worldIn, blockpos3) || ifluidstate1.isSource() && !ifluidstate2.isSource()) {
                        ifluidstate2 = ifluidstate1;
                        blockpos3 = blockpos1;
                     }
                  }

                  if (ifluidstate2.isSource()) {
                     BlockState blockstate2 = worldIn.getBlockState(blockpos2);
                     Block block = blockstate2.getBlock();
                     if (block instanceof ILiquidContainer) {
                        ((ILiquidContainer)block).receiveFluid(worldIn, blockpos2, blockstate2, ifluidstate2);
                        flag = true;
                        iterator.remove();
                     }
                  }
               }
            }

            if (i <= l) {
               if (!placementIn.func_215218_i()) {
                  VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(l - i + 1, i1 - j + 1, j1 - k + 1);
                  int l1 = i;
                  int i2 = j;
                  int j2 = k;

                  for(Pair<BlockPos, CompoundNBT> pair1 : list2) {
                     BlockPos blockpos5 = pair1.getFirst();
                     voxelshapepart.setFilled(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2, true, true);
                  }

                  func_222857_a(worldIn, flags, voxelshapepart, l1, i2, j2);
               }

               for(Pair<BlockPos, CompoundNBT> pair : list2) {
                  BlockPos blockpos4 = pair.getFirst();
                  if (!placementIn.func_215218_i()) {
                     BlockState blockstate1 = worldIn.getBlockState(blockpos4);
                     BlockState blockstate3 = Block.getValidBlockForPosition(blockstate1, worldIn, blockpos4);
                     if (blockstate1 != blockstate3) {
                        worldIn.setBlockState(blockpos4, blockstate3, flags & -2 | 16);
                     }

                     worldIn.notifyNeighbors(blockpos4, blockstate3.getBlock());
                  }

                  if (pair.getSecond() != null) {
                     TileEntity tileentity2 = worldIn.getTileEntity(blockpos4);
                     if (tileentity2 != null) {
                        tileentity2.markDirty();
                     }
                  }
               }
            }

            if (!placementIn.getIgnoreEntities()) {
               this.addEntitiesToWorld(worldIn, pos, placementIn, placementIn.getMirror(), placementIn.getRotation(), placementIn.getCenterOffset(), placementIn.getBoundingBox());
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static void func_222857_a(IWorld worldIn, int p_222857_1_, VoxelShapePart voxelShapePartIn, int xIn, int yIn, int zIn) {
      voxelShapePartIn.forEachFace((p_222856_5_, p_222856_6_, p_222856_7_, p_222856_8_) -> {
         BlockPos blockpos = new BlockPos(xIn + p_222856_6_, yIn + p_222856_7_, zIn + p_222856_8_);
         BlockPos blockpos1 = blockpos.offset(p_222856_5_);
         BlockState blockstate = worldIn.getBlockState(blockpos);
         BlockState blockstate1 = worldIn.getBlockState(blockpos1);
         BlockState blockstate2 = blockstate.updatePostPlacement(p_222856_5_, blockstate1, worldIn, blockpos, blockpos1);
         if (blockstate != blockstate2) {
            worldIn.setBlockState(blockpos, blockstate2, p_222857_1_ & -2 | 16);
         }

         BlockState blockstate3 = blockstate1.updatePostPlacement(p_222856_5_.getOpposite(), blockstate2, worldIn, blockpos1, blockpos);
         if (blockstate1 != blockstate3) {
            worldIn.setBlockState(blockpos1, blockstate3, p_222857_1_ & -2 | 16);
         }

      });
   }

   @Deprecated // FORGE: Add template parameter
   public static List<Template.BlockInfo> processBlockInfos(IWorld worldIn, BlockPos offsetPos, PlacementSettings placementSettingsIn, List<Template.BlockInfo> blockInfos) {
      return processBlockInfos(null, worldIn, offsetPos, placementSettingsIn, blockInfos);
   }

   public static List<Template.BlockInfo> processBlockInfos(@Nullable Template template, IWorld worldIn, BlockPos offsetPos, PlacementSettings placementSettingsIn, List<Template.BlockInfo> blockInfos) {
      List<Template.BlockInfo> list = Lists.newArrayList();

      for(Template.BlockInfo template$blockinfo : blockInfos) {
         BlockPos blockpos = transformedBlockPos(placementSettingsIn, template$blockinfo.pos).add(offsetPos);
         Template.BlockInfo template$blockinfo1 = new Template.BlockInfo(blockpos, template$blockinfo.state, template$blockinfo.nbt);

         for(Iterator<StructureProcessor> iterator = placementSettingsIn.getProcessors().iterator(); template$blockinfo1 != null && iterator.hasNext(); template$blockinfo1 = iterator.next().process(worldIn, offsetPos, template$blockinfo, template$blockinfo1, placementSettingsIn, template)) {
            ;
         }

         if (template$blockinfo1 != null) {
            list.add(template$blockinfo1);
         }
      }

      return list;
   }

   // FORGE: Add processing for entities
   public static List<Template.EntityInfo> processEntityInfos(@Nullable Template template, IWorld worldIn, BlockPos offsetPos, PlacementSettings placementSettingsIn, List<Template.EntityInfo> blockInfos) {
      List<Template.EntityInfo> list = Lists.newArrayList();

      for(Template.EntityInfo entityInfo : blockInfos) {
         Vec3d pos = transformedVec3d(placementSettingsIn, entityInfo.pos).add(new Vec3d(offsetPos));
         BlockPos blockpos = transformedBlockPos(placementSettingsIn, entityInfo.blockPos).add(offsetPos);
         Template.EntityInfo info = new Template.EntityInfo(pos, blockpos, entityInfo.nbt);

         for (StructureProcessor proc : placementSettingsIn.getProcessors()) {
             info = proc.processEntity(worldIn, offsetPos, entityInfo, info, placementSettingsIn, template);
             if (info == null)
                 break;
         }

         if (info != null)
            list.add(info);
      }

      return list;
   }

   @Deprecated // FORGE: Add PlacementSettings parameter (below) to pass to entity processors
   private void addEntitiesToWorld(IWorld worldIn, BlockPos offsetPos, Mirror mirrorIn, Rotation rotationIn, BlockPos centerOffset, @Nullable MutableBoundingBox boundsIn) {
      addEntitiesToWorld(worldIn, offsetPos, new PlacementSettings().setMirror(mirrorIn).setRotation(rotationIn).setCenterOffset(centerOffset).setBoundingBox(boundsIn), mirrorIn, rotationIn, offsetPos, boundsIn);
   }

   private void addEntitiesToWorld(IWorld worldIn, BlockPos offsetPos, PlacementSettings placementIn, Mirror mirrorIn, Rotation rotationIn, BlockPos centerOffset, @Nullable MutableBoundingBox boundsIn) {
      for(Template.EntityInfo template$entityinfo : processEntityInfos(this, worldIn, offsetPos, placementIn, this.entities)) {
         BlockPos blockpos = getTransformedPos(template$entityinfo.blockPos, mirrorIn, rotationIn, centerOffset).add(offsetPos);
         blockpos = template$entityinfo.blockPos; // FORGE: Position will have already been transformed by processEntityInfos
         if (boundsIn == null || boundsIn.isVecInside(blockpos)) {
            CompoundNBT compoundnbt = template$entityinfo.nbt;
            Vec3d vec3d = getTransformedPos(template$entityinfo.pos, mirrorIn, rotationIn, centerOffset);
            vec3d = vec3d.add((double)offsetPos.getX(), (double)offsetPos.getY(), (double)offsetPos.getZ());
            Vec3d vec3d1 = template$entityinfo.pos; // FORGE: Position will have already been transformed by processEntityInfos
            ListNBT listnbt = new ListNBT();
            listnbt.add(DoubleNBT.valueOf(vec3d1.x));
            listnbt.add(DoubleNBT.valueOf(vec3d1.y));
            listnbt.add(DoubleNBT.valueOf(vec3d1.z));
            compoundnbt.put("Pos", listnbt);
            compoundnbt.remove("UUIDMost");
            compoundnbt.remove("UUIDLeast");
            loadEntity(worldIn, compoundnbt).ifPresent((p_215383_4_) -> {
               float f = p_215383_4_.getMirroredYaw(mirrorIn);
               f = f + (p_215383_4_.rotationYaw - p_215383_4_.getRotatedYaw(rotationIn));
               p_215383_4_.setLocationAndAngles(vec3d1.x, vec3d1.y, vec3d1.z, f, p_215383_4_.rotationPitch);
               worldIn.addEntity(p_215383_4_);
            });
         }
      }

   }

   private static Optional<Entity> loadEntity(IWorld worldIn, CompoundNBT nbt) {
      try {
         return EntityType.loadEntityUnchecked(nbt, worldIn.getWorld());
      } catch (Exception var3) {
         return Optional.empty();
      }
   }

   public BlockPos transformedSize(Rotation rotationIn) {
      switch(rotationIn) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
      default:
         return this.size;
      }
   }

   public static BlockPos getTransformedPos(BlockPos targetPos, Mirror mirrorIn, Rotation rotationIn, BlockPos offset) {
      int i = targetPos.getX();
      int j = targetPos.getY();
      int k = targetPos.getZ();
      boolean flag = true;
      switch(mirrorIn) {
      case LEFT_RIGHT:
         k = -k;
         break;
      case FRONT_BACK:
         i = -i;
         break;
      default:
         flag = false;
      }

      int l = offset.getX();
      int i1 = offset.getZ();
      switch(rotationIn) {
      case COUNTERCLOCKWISE_90:
         return new BlockPos(l - i1 + k, j, l + i1 - i);
      case CLOCKWISE_90:
         return new BlockPos(l + i1 - k, j, i1 - l + i);
      case CLOCKWISE_180:
         return new BlockPos(l + l - i, j, i1 + i1 - k);
      default:
         return flag ? new BlockPos(i, j, k) : targetPos;
      }
   }

   private static Vec3d getTransformedPos(Vec3d target, Mirror mirrorIn, Rotation rotationIn, BlockPos centerOffset) {
      double d0 = target.x;
      double d1 = target.y;
      double d2 = target.z;
      boolean flag = true;
      switch(mirrorIn) {
      case LEFT_RIGHT:
         d2 = 1.0D - d2;
         break;
      case FRONT_BACK:
         d0 = 1.0D - d0;
         break;
      default:
         flag = false;
      }

      int i = centerOffset.getX();
      int j = centerOffset.getZ();
      switch(rotationIn) {
      case COUNTERCLOCKWISE_90:
         return new Vec3d((double)(i - j) + d2, d1, (double)(i + j + 1) - d0);
      case CLOCKWISE_90:
         return new Vec3d((double)(i + j + 1) - d2, d1, (double)(j - i) + d0);
      case CLOCKWISE_180:
         return new Vec3d((double)(i + i + 1) - d0, d1, (double)(j + j + 1) - d2);
      default:
         return flag ? new Vec3d(d0, d1, d2) : target;
      }
   }

   public BlockPos getZeroPositionWithTransform(BlockPos p_189961_1_, Mirror p_189961_2_, Rotation p_189961_3_) {
      return getZeroPositionWithTransform(p_189961_1_, p_189961_2_, p_189961_3_, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos getZeroPositionWithTransform(BlockPos p_191157_0_, Mirror p_191157_1_, Rotation p_191157_2_, int p_191157_3_, int p_191157_4_) {
      --p_191157_3_;
      --p_191157_4_;
      int i = p_191157_1_ == Mirror.FRONT_BACK ? p_191157_3_ : 0;
      int j = p_191157_1_ == Mirror.LEFT_RIGHT ? p_191157_4_ : 0;
      BlockPos blockpos = p_191157_0_;
      switch(p_191157_2_) {
      case COUNTERCLOCKWISE_90:
         blockpos = p_191157_0_.add(j, 0, p_191157_3_ - i);
         break;
      case CLOCKWISE_90:
         blockpos = p_191157_0_.add(p_191157_4_ - j, 0, i);
         break;
      case CLOCKWISE_180:
         blockpos = p_191157_0_.add(p_191157_3_ - i, 0, p_191157_4_ - j);
         break;
      case NONE:
         blockpos = p_191157_0_.add(i, 0, j);
      }

      return blockpos;
   }

   public MutableBoundingBox getMutableBoundingBox(PlacementSettings p_215388_1_, BlockPos p_215388_2_) {
      Rotation rotation = p_215388_1_.getRotation();
      BlockPos blockpos = p_215388_1_.getCenterOffset();
      BlockPos blockpos1 = this.transformedSize(rotation);
      Mirror mirror = p_215388_1_.getMirror();
      int i = blockpos.getX();
      int j = blockpos.getZ();
      int k = blockpos1.getX() - 1;
      int l = blockpos1.getY() - 1;
      int i1 = blockpos1.getZ() - 1;
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(0, 0, 0, 0, 0, 0);
      switch(rotation) {
      case COUNTERCLOCKWISE_90:
         mutableboundingbox = new MutableBoundingBox(i - j, 0, i + j - i1, i - j + k, l, i + j);
         break;
      case CLOCKWISE_90:
         mutableboundingbox = new MutableBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + i1);
         break;
      case CLOCKWISE_180:
         mutableboundingbox = new MutableBoundingBox(i + i - k, 0, j + j - i1, i + i, l, j + j);
         break;
      case NONE:
         mutableboundingbox = new MutableBoundingBox(0, 0, 0, k, l, i1);
      }

      switch(mirror) {
      case LEFT_RIGHT:
         this.func_215385_a(rotation, i1, k, mutableboundingbox, Direction.NORTH, Direction.SOUTH);
         break;
      case FRONT_BACK:
         this.func_215385_a(rotation, k, i1, mutableboundingbox, Direction.WEST, Direction.EAST);
      case NONE:
      }

      mutableboundingbox.offset(p_215388_2_.getX(), p_215388_2_.getY(), p_215388_2_.getZ());
      return mutableboundingbox;
   }

   private void func_215385_a(Rotation rotationIn, int offsetFront, int p_215385_3_, MutableBoundingBox p_215385_4_, Direction p_215385_5_, Direction p_215385_6_) {
      BlockPos blockpos = BlockPos.ZERO;
      if (rotationIn != Rotation.CLOCKWISE_90 && rotationIn != Rotation.COUNTERCLOCKWISE_90) {
         if (rotationIn == Rotation.CLOCKWISE_180) {
            blockpos = blockpos.offset(p_215385_6_, offsetFront);
         } else {
            blockpos = blockpos.offset(p_215385_5_, offsetFront);
         }
      } else {
         blockpos = blockpos.offset(rotationIn.rotate(p_215385_5_), p_215385_3_);
      }

      p_215385_4_.offset(blockpos.getX(), 0, blockpos.getZ());
   }

   public CompoundNBT writeToNBT(CompoundNBT nbt) {
      if (this.blocks.isEmpty()) {
         nbt.put("blocks", new ListNBT());
         nbt.put("palette", new ListNBT());
      } else {
         List<Template.BasicPalette> list = Lists.newArrayList();
         Template.BasicPalette template$basicpalette = new Template.BasicPalette();
         list.add(template$basicpalette);

         for(int i = 1; i < this.blocks.size(); ++i) {
            list.add(new Template.BasicPalette());
         }

         ListNBT listnbt1 = new ListNBT();
         List<Template.BlockInfo> list1 = this.blocks.get(0);

         for(int j = 0; j < list1.size(); ++j) {
            Template.BlockInfo template$blockinfo = list1.get(j);
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.put("pos", this.writeInts(template$blockinfo.pos.getX(), template$blockinfo.pos.getY(), template$blockinfo.pos.getZ()));
            int k = template$basicpalette.idFor(template$blockinfo.state);
            compoundnbt.putInt("state", k);
            if (template$blockinfo.nbt != null) {
               compoundnbt.put("nbt", template$blockinfo.nbt);
            }

            listnbt1.add(compoundnbt);

            for(int l = 1; l < this.blocks.size(); ++l) {
               Template.BasicPalette template$basicpalette1 = list.get(l);
               template$basicpalette1.addMapping((this.blocks.get(l).get(j)).state, k);
            }
         }

         nbt.put("blocks", listnbt1);
         if (list.size() == 1) {
            ListNBT listnbt2 = new ListNBT();

            for(BlockState blockstate : template$basicpalette) {
               listnbt2.add(NBTUtil.writeBlockState(blockstate));
            }

            nbt.put("palette", listnbt2);
         } else {
            ListNBT listnbt3 = new ListNBT();

            for(Template.BasicPalette template$basicpalette2 : list) {
               ListNBT listnbt4 = new ListNBT();

               for(BlockState blockstate1 : template$basicpalette2) {
                  listnbt4.add(NBTUtil.writeBlockState(blockstate1));
               }

               listnbt3.add(listnbt4);
            }

            nbt.put("palettes", listnbt3);
         }
      }

      ListNBT listnbt = new ListNBT();

      for(Template.EntityInfo template$entityinfo : this.entities) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         compoundnbt1.put("pos", this.writeDoubles(template$entityinfo.pos.x, template$entityinfo.pos.y, template$entityinfo.pos.z));
         compoundnbt1.put("blockPos", this.writeInts(template$entityinfo.blockPos.getX(), template$entityinfo.blockPos.getY(), template$entityinfo.blockPos.getZ()));
         if (template$entityinfo.nbt != null) {
            compoundnbt1.put("nbt", template$entityinfo.nbt);
         }

         listnbt.add(compoundnbt1);
      }

      nbt.put("entities", listnbt);
      nbt.put("size", this.writeInts(this.size.getX(), this.size.getY(), this.size.getZ()));
      nbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      return nbt;
   }

   public void read(CompoundNBT compound) {
      this.blocks.clear();
      this.entities.clear();
      ListNBT listnbt = compound.getList("size", 3);
      this.size = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
      ListNBT listnbt1 = compound.getList("blocks", 10);
      if (compound.contains("palettes", 9)) {
         ListNBT listnbt2 = compound.getList("palettes", 9);

         for(int i = 0; i < listnbt2.size(); ++i) {
            this.readPalletesAndBlocks(listnbt2.getList(i), listnbt1);
         }
      } else {
         this.readPalletesAndBlocks(compound.getList("palette", 10), listnbt1);
      }

      ListNBT listnbt5 = compound.getList("entities", 10);

      for(int j = 0; j < listnbt5.size(); ++j) {
         CompoundNBT compoundnbt = listnbt5.getCompound(j);
         ListNBT listnbt3 = compoundnbt.getList("pos", 6);
         Vec3d vec3d = new Vec3d(listnbt3.getDouble(0), listnbt3.getDouble(1), listnbt3.getDouble(2));
         ListNBT listnbt4 = compoundnbt.getList("blockPos", 3);
         BlockPos blockpos = new BlockPos(listnbt4.getInt(0), listnbt4.getInt(1), listnbt4.getInt(2));
         if (compoundnbt.contains("nbt")) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("nbt");
            this.entities.add(new Template.EntityInfo(vec3d, blockpos, compoundnbt1));
         }
      }

   }

   private void readPalletesAndBlocks(ListNBT palletesNBT, ListNBT blocksNBT) {
      Template.BasicPalette template$basicpalette = new Template.BasicPalette();
      List<Template.BlockInfo> list = Lists.newArrayList();

      for(int i = 0; i < palletesNBT.size(); ++i) {
         template$basicpalette.addMapping(NBTUtil.readBlockState(palletesNBT.getCompound(i)), i);
      }

      for(int j = 0; j < blocksNBT.size(); ++j) {
         CompoundNBT compoundnbt = blocksNBT.getCompound(j);
         ListNBT listnbt = compoundnbt.getList("pos", 3);
         BlockPos blockpos = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
         BlockState blockstate = template$basicpalette.stateFor(compoundnbt.getInt("state"));
         CompoundNBT compoundnbt1;
         if (compoundnbt.contains("nbt")) {
            compoundnbt1 = compoundnbt.getCompound("nbt");
         } else {
            compoundnbt1 = null;
         }

         list.add(new Template.BlockInfo(blockpos, blockstate, compoundnbt1));
      }

      list.sort(Comparator.comparingInt((p_215384_0_) -> {
         return p_215384_0_.pos.getY();
      }));
      this.blocks.add(list);
   }

   private ListNBT writeInts(int... values) {
      ListNBT listnbt = new ListNBT();

      for(int i : values) {
         listnbt.add(IntNBT.valueOf(i));
      }

      return listnbt;
   }

   private ListNBT writeDoubles(double... values) {
      ListNBT listnbt = new ListNBT();

      for(double d0 : values) {
         listnbt.add(DoubleNBT.valueOf(d0));
      }

      return listnbt;
   }

   static class BasicPalette implements Iterable<BlockState> {
      public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.getDefaultState();
      private final ObjectIntIdentityMap<BlockState> ids = new ObjectIntIdentityMap<>(16);
      private int lastId;

      private BasicPalette() {
      }

      public int idFor(BlockState state) {
         int i = this.ids.get(state);
         if (i == -1) {
            i = this.lastId++;
            this.ids.put(state, i);
         }

         return i;
      }

      @Nullable
      public BlockState stateFor(int id) {
         BlockState blockstate = this.ids.getByValue(id);
         return blockstate == null ? DEFAULT_BLOCK_STATE : blockstate;
      }

      public Iterator<BlockState> iterator() {
         return this.ids.iterator();
      }

      public void addMapping(BlockState p_189956_1_, int p_189956_2_) {
         this.ids.put(p_189956_1_, p_189956_2_);
      }
   }

   public static class BlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      public final CompoundNBT nbt;

      public BlockInfo(BlockPos pos, BlockState state, @Nullable CompoundNBT nbt) {
         this.pos = pos;
         this.state = state;
         this.nbt = nbt;
      }

      public String toString() {
         return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
      }
   }

   public static class EntityInfo {
      public final Vec3d pos;
      public final BlockPos blockPos;
      public final CompoundNBT nbt;

      public EntityInfo(Vec3d vecIn, BlockPos posIn, CompoundNBT nbt) {
         this.pos = vecIn;
         this.blockPos = posIn;
         this.nbt = nbt;
      }
   }
}