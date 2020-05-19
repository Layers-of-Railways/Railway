package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockState extends StateHolder<Block, BlockState> implements IStateHolder<BlockState>, net.minecraftforge.common.extensions.IForgeBlockState {
   @Nullable
   private BlockState.Cache cache;
   private final int lightLevel;
   private final boolean transparent;

   public BlockState(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> properties) {
      super(blockIn, properties);
      this.lightLevel = blockIn.getLightValue(this);
      this.transparent = blockIn.isTransparent(this);
   }

   public void cacheState() {
      if (!this.getBlock().isVariableOpacity()) {
         this.cache = new BlockState.Cache(this);
      }

   }

   public Block getBlock() {
      return this.object;
   }

   public Material getMaterial() {
      return this.getBlock().getMaterial(this);
   }

   public boolean canEntitySpawn(IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
      return this.getBlock().canEntitySpawn(this, worldIn, pos, type);
   }

   public boolean propagatesSkylightDown(IBlockReader worldIn, BlockPos pos) {
      return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this, worldIn, pos);
   }

   public int getOpacity(IBlockReader worldIn, BlockPos pos) {
      return this.cache != null ? this.cache.opacity : this.getBlock().getOpacity(this, worldIn, pos);
   }

   public VoxelShape getFaceOcclusionShape(IBlockReader worldIn, BlockPos pos, Direction directionIn) {
      return this.cache != null && this.cache.renderShapes != null ? this.cache.renderShapes[directionIn.ordinal()] : VoxelShapes.getFaceShape(this.getRenderShape(worldIn, pos), directionIn);
   }

   public boolean isCollisionShapeLargerThanFullBlock() {
      return this.cache == null || this.cache.isCollisionShapeLargerThanFullBlock;
   }

   public boolean isTransparent() {
      return this.transparent;
   }

   public int getLightValue() {
      return this.lightLevel;
   }

   /** @deprecated use {@link BlockState#isAir(IBlockReader, BlockPos) */
   @Deprecated
   public boolean isAir() {
      return this.getBlock().isAir(this);
   }

   /** @deprecated use {@link BlockState#rotate(IWorld, BlockPos, Rotation) */
   @Deprecated
   public MaterialColor getMaterialColor(IBlockReader worldIn, BlockPos pos) {
      return this.getBlock().getMaterialColor(this, worldIn, pos);
   }

   /**
    * Returns the blockstate with the given rotation. If inapplicable, returns itself.
    */
   public BlockState rotate(Rotation rot) {
      return this.getBlock().rotate(this, rot);
   }

   /**
    * Returns the blockstate mirrored in the given way. If inapplicable, returns itself.
    */
   public BlockState mirror(Mirror mirrorIn) {
      return this.getBlock().mirror(this, mirrorIn);
   }

   public BlockRenderType getRenderType() {
      return this.getBlock().getRenderType(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEmissiveRendering() {
      return this.getBlock().isEmissiveRendering(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getAmbientOcclusionLightValue(IBlockReader reader, BlockPos pos) {
      return this.getBlock().getAmbientOcclusionLightValue(this, reader, pos);
   }

   public boolean isNormalCube(IBlockReader reader, BlockPos pos) {
      return this.getBlock().isNormalCube(this, reader, pos);
   }

   public boolean canProvidePower() {
      return this.getBlock().canProvidePower(this);
   }

   public int getWeakPower(IBlockReader blockAccess, BlockPos pos, Direction side) {
      return this.getBlock().getWeakPower(this, blockAccess, pos, side);
   }

   public boolean hasComparatorInputOverride() {
      return this.getBlock().hasComparatorInputOverride(this);
   }

   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return this.getBlock().getComparatorInputOverride(this, worldIn, pos);
   }

   public float getBlockHardness(IBlockReader worldIn, BlockPos pos) {
      return this.getBlock().getBlockHardness(this, worldIn, pos);
   }

   public float getPlayerRelativeBlockHardness(PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
      return this.getBlock().getPlayerRelativeBlockHardness(this, player, worldIn, pos);
   }

   public int getStrongPower(IBlockReader blockAccess, BlockPos pos, Direction side) {
      return this.getBlock().getStrongPower(this, blockAccess, pos, side);
   }

   public PushReaction getPushReaction() {
      return this.getBlock().getPushReaction(this);
   }

   public boolean isOpaqueCube(IBlockReader worldIn, BlockPos pos) {
      return this.cache != null ? this.cache.opaqueCube : this.getBlock().isOpaqueCube(this, worldIn, pos);
   }

   public boolean isSolid() {
      return this.cache != null ? this.cache.solid : this.getBlock().isSolid(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(BlockState state, Direction face) {
      return this.getBlock().isSideInvisible(this, state, face);
   }

   public VoxelShape getShape(IBlockReader worldIn, BlockPos pos) {
      return this.getShape(worldIn, pos, ISelectionContext.dummy());
   }

   public VoxelShape getShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return this.getBlock().getShape(this, worldIn, pos, context);
   }

   public VoxelShape getCollisionShape(IBlockReader worldIn, BlockPos pos) {
      return this.cache != null ? this.cache.collisionShape : this.getCollisionShape(worldIn, pos, ISelectionContext.dummy());
   }

   public VoxelShape getCollisionShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return this.getBlock().getCollisionShape(this, worldIn, pos, context);
   }

   public VoxelShape getRenderShape(IBlockReader worldIn, BlockPos pos) {
      return this.getBlock().getRenderShape(this, worldIn, pos);
   }

   public VoxelShape getRaytraceShape(IBlockReader worldIn, BlockPos pos) {
      return this.getBlock().getRaytraceShape(this, worldIn, pos);
   }

   /**
    * True if the collision box of this state covers the entire upper face of the blockspace
    */
   public final boolean isTopSolid(IBlockReader reader, BlockPos pos, Entity entityIn) {
      return Block.doesSideFillSquare(this.getCollisionShape(reader, pos, ISelectionContext.forEntity(entityIn)), Direction.UP);
   }

   public Vec3d getOffset(IBlockReader access, BlockPos pos) {
      return this.getBlock().getOffset(this, access, pos);
   }

   /**
    * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform additional
    * changes to the world, like pistons replacing the block with an extended base. On the client, the update may
    * involve replacing tile entities, playing sounds, or performing other visual actions to reflect the server side
    * changes.
    */
   public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param) {
      return this.getBlock().eventReceived(this, worldIn, pos, id, param);
   }

   public void neighborChanged(World worldIn, BlockPos posIn, Block blockIn, BlockPos fromPosIn, boolean isMoving) {
      this.getBlock().neighborChanged(this, worldIn, posIn, blockIn, fromPosIn, isMoving);
   }

   /**
    * For all neighbors, have them react to this block's existence, potentially updating their states as needed. For
    * example, fences make their connections to this block if possible and observers pulse if this block was placed in
    * front of their detector
    */
   public void updateNeighbors(IWorld worldIn, BlockPos pos, int flags) {
      this.getBlock().updateNeighbors(this, worldIn, pos, flags);
   }

   /**
    * Performs validations on the block state and possibly neighboring blocks to validate whether the incoming state is
    * valid to stay in the world. Currently used only by redstone wire to update itself if neighboring blocks have
    * changed and to possibly break itself.
    */
   public void updateDiagonalNeighbors(IWorld worldIn, BlockPos pos, int flags) {
      this.getBlock().updateDiagonalNeighbors(this, worldIn, pos, flags);
   }

   public void onBlockAdded(World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      this.getBlock().onBlockAdded(this, worldIn, pos, oldState, isMoving);
   }

   public void onReplaced(World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      this.getBlock().onReplaced(this, worldIn, pos, newState, isMoving);
   }

   public void tick(ServerWorld worldIn, BlockPos posIn, Random randomIn) {
      this.getBlock().tick(this, worldIn, posIn, randomIn);
   }

   public void randomTick(ServerWorld worldIn, BlockPos posIn, Random randomIn) {
      this.getBlock().randomTick(this, worldIn, posIn, randomIn);
   }

   public void onEntityCollision(World worldIn, BlockPos pos, Entity entityIn) {
      this.getBlock().onEntityCollision(this, worldIn, pos, entityIn);
   }

   public void spawnAdditionalDrops(World worldIn, BlockPos pos, ItemStack stack) {
      this.getBlock().spawnAdditionalDrops(this, worldIn, pos, stack);
   }

   public List<ItemStack> getDrops(LootContext.Builder builder) {
      return this.getBlock().getDrops(this, builder);
   }

   public ActionResultType onBlockActivated(World worldIn, PlayerEntity player, Hand handIn, BlockRayTraceResult resultIn) {
      return this.getBlock().onBlockActivated(this, worldIn, resultIn.getPos(), player, handIn, resultIn);
   }

   public void onBlockClicked(World worldIn, BlockPos pos, PlayerEntity player) {
      this.getBlock().onBlockClicked(this, worldIn, pos, player);
   }

   public boolean isSuffocating(IBlockReader blockReaderIn, BlockPos blockPosIn) {
      return this.getBlock().causesSuffocation(this, blockReaderIn, blockPosIn);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean causesSuffocation(IBlockReader worldIn, BlockPos pos) {
      return this.getBlock().isViewBlocking(this, worldIn, pos);
   }

   public BlockState updatePostPlacement(Direction face, BlockState queried, IWorld worldIn, BlockPos currentPos, BlockPos offsetPos) {
      return this.getBlock().updatePostPlacement(this, face, queried, worldIn, currentPos, offsetPos);
   }

   public boolean allowsMovement(IBlockReader worldIn, BlockPos pos, PathType type) {
      return this.getBlock().allowsMovement(this, worldIn, pos, type);
   }

   public boolean isReplaceable(BlockItemUseContext useContext) {
      return this.getBlock().isReplaceable(this, useContext);
   }

   public boolean isReplaceable(Fluid fluidIn) {
      return this.getBlock().isReplaceable(this, fluidIn);
   }

   public boolean isValidPosition(IWorldReader worldIn, BlockPos pos) {
      return this.getBlock().isValidPosition(this, worldIn, pos);
   }

   public boolean blockNeedsPostProcessing(IBlockReader worldIn, BlockPos pos) {
      return this.getBlock().needsPostProcessing(this, worldIn, pos);
   }

   @Nullable
   public INamedContainerProvider getContainer(World worldIn, BlockPos pos) {
      return this.getBlock().getContainer(this, worldIn, pos);
   }

   public boolean isIn(Tag<Block> tagIn) {
      return this.getBlock().isIn(tagIn);
   }

   public IFluidState getFluidState() {
      return this.getBlock().getFluidState(this);
   }

   public boolean ticksRandomly() {
      return this.getBlock().ticksRandomly(this);
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(BlockPos pos) {
      return this.getBlock().getPositionRandom(this, pos);
   }

   public SoundType getSoundType() {
      return this.getBlock().getSoundType(this);
   }

   public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, Entity projectile) {
      this.getBlock().onProjectileCollision(worldIn, state, hit, projectile);
   }

   public boolean isSolidSide(IBlockReader blockReaderIn, BlockPos blockPosIn, Direction directionIn) {
      return this.cache != null ? this.cache.solidSides[directionIn.ordinal()] : Block.hasSolidSide(this, blockReaderIn, blockPosIn, directionIn);
   }

   public boolean isCollisionShapeOpaque(IBlockReader blockReaderIn, BlockPos blockPosIn) {
      return this.cache != null ? this.cache.opaqueCollisionShape : Block.isOpaque(this.getCollisionShape(blockReaderIn, blockPosIn));
   }

   public static <T> Dynamic<T> serialize(DynamicOps<T> opsIn, BlockState state) {
      ImmutableMap<IProperty<?>, Comparable<?>> immutablemap = state.getValues();
      T t;
      if (immutablemap.isEmpty()) {
         t = opsIn.createMap(ImmutableMap.of(opsIn.createString("Name"), opsIn.createString(Registry.BLOCK.getKey(state.getBlock()).toString())));
      } else {
         t = opsIn.createMap(ImmutableMap.of(opsIn.createString("Name"), opsIn.createString(Registry.BLOCK.getKey(state.getBlock()).toString()), opsIn.createString("Properties"), opsIn.createMap(immutablemap.entrySet().stream().map((p_215683_1_) -> {
            return Pair.of(opsIn.createString(p_215683_1_.getKey().getName()), opsIn.createString(IStateHolder.getName(p_215683_1_.getKey(), p_215683_1_.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic<>(opsIn, t);
   }

   public static <T> BlockState deserialize(Dynamic<T> dynamic) {
      Block block = Registry.BLOCK.getOrDefault(new ResourceLocation(dynamic.getElement("Name").flatMap(dynamic.getOps()::getStringValue).orElse("minecraft:air")));
      Map<String, String> map = dynamic.get("Properties").asMap((p_215701_0_) -> {
         return p_215701_0_.asString("");
      }, (p_215694_0_) -> {
         return p_215694_0_.asString("");
      });
      BlockState blockstate = block.getDefaultState();
      StateContainer<Block, BlockState> statecontainer = block.getStateContainer();

      for(Entry<String, String> entry : map.entrySet()) {
         String s = entry.getKey();
         IProperty<?> iproperty = statecontainer.getProperty(s);
         if (iproperty != null) {
            blockstate = IStateHolder.withString(blockstate, iproperty, s, dynamic.toString(), entry.getValue());
         }
      }

      return blockstate;
   }

   static final class Cache {
      private static final Direction[] DIRECTIONS = Direction.values();
      private final boolean solid;
      private final boolean opaqueCube;
      private final boolean propagatesSkylightDown;
      private final int opacity;
      private final VoxelShape[] renderShapes;
      private final VoxelShape collisionShape;
      private final boolean isCollisionShapeLargerThanFullBlock;
      private final boolean[] solidSides;
      private final boolean opaqueCollisionShape;

      private Cache(BlockState stateIn) {
         Block block = stateIn.getBlock();
         this.solid = block.isSolid(stateIn);
         this.opaqueCube = block.isOpaqueCube(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
         this.propagatesSkylightDown = block.propagatesSkylightDown(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
         this.opacity = block.getOpacity(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
         if (!stateIn.isSolid()) {
            this.renderShapes = null;
         } else {
            this.renderShapes = new VoxelShape[DIRECTIONS.length];
            VoxelShape voxelshape = block.getRenderShape(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO);

            for(Direction direction : DIRECTIONS) {
               this.renderShapes[direction.ordinal()] = VoxelShapes.getFaceShape(voxelshape, direction);
            }
         }

         this.collisionShape = block.getCollisionShape(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO, ISelectionContext.dummy());
         this.isCollisionShapeLargerThanFullBlock = Arrays.stream(Direction.Axis.values()).anyMatch((p_222491_1_) -> {
            return this.collisionShape.getStart(p_222491_1_) < 0.0D || this.collisionShape.getEnd(p_222491_1_) > 1.0D;
         });
         this.solidSides = new boolean[6];

         for(Direction direction1 : DIRECTIONS) {
            this.solidSides[direction1.ordinal()] = Block.hasSolidSide(stateIn, EmptyBlockReader.INSTANCE, BlockPos.ZERO, direction1);
         }

         this.opaqueCollisionShape = Block.isOpaque(stateIn.getCollisionShape(EmptyBlockReader.INSTANCE, BlockPos.ZERO));
      }
   }
}