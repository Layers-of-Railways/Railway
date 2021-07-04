package com.railwayteam.railways.content.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.railwayteam.railways.registry.CRBlocks.R_BLOCK_BOGIE;

public class BogieBlock extends Block {
    public static BogieBlock getInstance() {
        return R_BLOCK_BOGIE.get();
    }

    public static final IntegerProperty BOGIE_X = IntegerProperty.create("bogie_x", 0, 2);
    public static final IntegerProperty BOGIE_Z = IntegerProperty.create("bogie_z", 0, 2);

    public static class Bogie {
        public static class BogiePos extends Vector2f {
            public final PosType type;

            /**
             * The bogie position type
             *
             * Makes it easier to switch from real and negative positions
             */
            public enum PosType {
                /**
                 * 0 to 2
                 *
                 * Used in the bogie part blockstate
                 */
                REAL,
                /**
                 * -1 to 1
                 *
                 * A more readable type, also used to offset
                 */
                NORMAL
            }

            // x = x, y = z
            public BogiePos(int x, int z, PosType type) {
                super(x, z);
                this.type = type;
            }

            public BogiePos(BlockState state) {
                this(state.get(BOGIE_X), state.get(BOGIE_Z), PosType.REAL);
            }

            @Override
            public String toString() {
                return "BogiePos{" +
                        "type=" + type +
                        ", x=" + getX() +
                        ", z=" + getZ() +
                        ", isCenter:" + isCenter() +
                        '}';
            }

            public int getX() {
                return (int) x;
            }

            public int getZ() {
                return (int) y;
            }

            public BogiePos realPos() {
                if(type == PosType.REAL) return this;
                return new BogiePos(getX() + 1, getZ() + 1, PosType.REAL);
            }

            public BogiePos normalPos() {
                if(type == PosType.NORMAL) return this;
                return new BogiePos(getX() - 1, getZ() - 1, PosType.NORMAL);
            }

            public BogiePos toType(PosType type) {
                if(type == PosType.NORMAL) return normalPos();
                return realPos();
            }

            public BogiePos reverse() { // shut up. 29 of june: shut up
                return new BogiePos(normalPos().getX() * -1, normalPos().getZ() * -1, PosType.NORMAL).toType(this.type);
            }

            public BlockPos offsetBlockPos(BlockPos pos) {
                BogiePos r = normalPos(); // I HAVE NO IDEA WHAT IM DOING
                return pos.add(r.getX(), 0, r.getZ());
            }

            public boolean isCenter() {
                BogiePos p = normalPos();
                return p.getX() == 0 && p.getZ() == 0;
            }

            public BlockState setToState(BlockState state) {
                return state.with(BOGIE_X, realPos().getX()).with(BOGIE_Z, realPos().getZ());
            }

            public BogiePos withX(int x1) {
                return copy(x1, nullPos, null);
            }

            public BogiePos withZ(int z1) {
                return copy(nullPos, z1, null);
            }

            public BogiePos withPos(int x1, int z1) {
                return copy(x1, z1, null);
            }

            public BogiePos withType(PosType type1) {
                return copy(nullPos, nullPos, type1);
            }

            public BogiePos copy(int x1, int z1, PosType type1) {
                int x2 = getX();
                int z2 = getZ();
                PosType type2 = type;
                if(x1 != nullPos) x2 = x1;
                if(z1 != nullPos) z2 = z1;
                if(type1 != null) type2 = type1;
                return new BogiePos(x2, z2, type2);
            }

            public static int nullPos = 98;
        }

        public static class BogiePart {
            public final BogiePos pos;
            public final IWorld world;
            public final BlockPos blockPos;

            public BogiePart(BogiePos pos, IWorld world, BlockPos blockPos) {
                this.pos = pos;
                this.world = world;
                this.blockPos = blockPos;
            }

            public BogiePart(IWorld world, BlockPos pos) {
                this(new BogiePos(world.getBlockState(pos)), world, pos);
            }

            public BlockState getBlockState() {
                return world.getBlockState(blockPos);
            }

            @Override
            public String toString() {
                return "BogiePart{" +
                        "pos=" + pos +
                        ", world=" + world +
                        ", blockPos=" + blockPos +
                        '}';
            }
        }

        public final List<BogiePart> parts;
        public final IWorld world;
        public final BlockPos center;

        @Override
        public String toString() {
            return "Bogie{" +
                    "parts=" + parts +
                    ", world=" + world +
                    ", center=" + center +
                    '}';
        }

        public Bogie(List<BogiePart> parts, IWorld world, BlockPos center) {
            this.parts = parts;
            this.world = world;
            this.center = center;
        }

        public BogiePart getPart(int x, int z) {
            for(BogiePart part : parts) {
                if(part.pos.normalPos().getX() == x && part.pos.normalPos().getZ() == z) return part;
            }
            return null;
        }

        public BogiePart getPart(BogiePos pos) {
            BogiePos n = pos.normalPos();
            return getPart(n.getX(), n.getZ());
        }

        public BogiePart getCenter() {
            return getPart(new BogiePos(0, 0, BogiePos.PosType.NORMAL));
        }

        public void destroyAllParts(boolean dropCenter) {
            parts.forEach((part) -> {
                world.destroyBlock(part.blockPos, part.pos.isCenter() && dropCenter);
            });
        }

        public static Bogie fromCenter(IWorld world, BlockPos pos) {
            List<BogiePart> parts = new ArrayList<>();

            runForEachBogiePart((bogiePos) -> {
                if(world.getBlockState(bogiePos.offsetBlockPos(pos)).getBlock() instanceof BogieBlock) {
                    parts.add(new BogiePart(world, bogiePos.offsetBlockPos(pos)));
                }
            });

            return new Bogie(parts, world, pos);
        }

        public static Bogie get(IWorld world, BlockPos pos) {
            return fromCenter(world, new BogiePos(world.getBlockState(pos)).reverse().offsetBlockPos(pos));
        }

        public static Bogie createAt(BlockPos pos, World world, boolean createCenter) {
            BogieBlock block = getInstance();
            runForEachBogiePart((bpos) -> {
                if(bpos.isCenter() && !createCenter) return;
                world.setBlockState(bpos.offsetBlockPos(pos), bpos.setToState(block.getDefaultState()));
            });
            return fromCenter(world, pos);
        }

        public boolean isComplete() {
            return parts.size() >= 9;
        }
    }

    public BogieBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.setDefaultState(this.stateContainer.getBaseState().with(BOGIE_X, 1).with(BOGIE_Z, 1));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> c) {
        c.add(BOGIE_X, BOGIE_Z);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
//        BlockPos center = p_196258_1_.getPos();BlockState state, Direction p_196271_2_, BlockState p_196271_3_, IWorld world, BlockPos pos, BlockPos oldPos
//        BlockPos blockpos1 = blockpos.offset(direction);
//        for (int i = 0; i < 9; i++) {
//
//        }
        // replaced this with getPushReaction(): block
//        if(!pos.equals(oldPos) && !world.isRemote()) { // so pistons cant move parts of the bogie
//            Bogie.get(world, pos).destroyAllParts(true);
//            destroyAllConnectedBogies(world, null, getCenter(state, oldPos));
//        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public static void runForEachBogiePart(Consumer<Bogie.BogiePos> consumer, Runnable afterDone) {
        for(int x = -1; x < 2; x++) {
            for(int z = -1; z < 2; z++) {
                consumer.accept(new Bogie.BogiePos(x, z, Bogie.BogiePos.PosType.NORMAL));
            }
        }
        afterDone.run();
    }

    public static void runForEachBogiePart(Consumer<Bogie.BogiePos> consumer) {
        runForEachBogiePart(consumer, () -> {});
    }

//    public static BlockPos getCenter(int x, int z, BlockPos pos) {
//        return pos.add(x - 1, 0, z - 1);
//    }

//    public static BlockPos getCenter(BlockState state, BlockPos pos) {
//        return getCenter(state.get(BOGIE_X), state.get(BOGIE_Z), pos);
//    }

//    public static boolean isCenter(int x, int z) {
//        return x == 1 && z == 1;
//    }

//    public static boolean isCenter(BlockState state) {
//        return isCenter(state.get(BOGIE_X), state.get(BOGIE_Z));
//    }

//    public static BlockPos fromCenter(BlockPos center, int x, int z) {
//        return center.add(x - 1, 0, z - 1);
//    }

//    public static BlockPos fromCenter(BlockPos center, BlockState state) {
//        return fromCenter(center, state.get(BOGIE_X), state.get(BOGIE_Z));
//    }

//    public static void runForEachBogiePart(TriConsumer<Integer, Integer, BlockPos> consumer, BlockPos center) {
//        runForEachBogiePart((x, z) -> consumer.accept(x, z, fromCenter(center, x + 1, z + 1)));
//    }

//    public static void runForEachBogiePartExceptCenter(BiConsumer<Integer, Integer> consumer) {
//        runForEachBogiePart((x, z) -> {
//            if(isCenter(x + 1, z + 1)) return;
//            consumer.accept(x, z);
//        });
//    }

//    public static void runForEachBogiePartExceptCenter(TriConsumer<Integer, Integer, BlockPos> consumer, BlockPos center) {
//        runForEachBogiePartExceptCenter((x, z) -> consumer.accept(x, z, fromCenter(center, x + 1, z + 1)));
//    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
//        runForEachBogiePart((x, z) -> ctx.getWorld().setBlockState(
//                ctx.getPos().add(-(x), 0, -(z)),
//                getDefaultState().with(BOGIE_X, x + 1).with(BOGIE_Z, z + 1)
//        ));
        Bogie.createAt(ctx.getPos(), ctx.getWorld(), true);
        return super.getStateForPlacement(ctx);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder loot) {
        if(new Bogie.BogiePos(state).isCenter()) {
            return super.getDrops(state, loot);
        }
        return new ArrayList<>();
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity plr) {
        Bogie.BogiePos pos1 = new Bogie.BogiePos(state);
        Bogie b = Bogie.get(world, pos);
        if(pos1.isCenter()) {
            b.destroyAllParts(!plr.isCreative());
        } else {
//            BlockPos center = getCenter(x, z, pos);
//            BlockState centerState = world.getBlockState(center);
//            if(centerState.getBlock() instanceof BogieBlock) {
            if(b.getCenter() != null && world.getBlockState(b.getCenter().blockPos).getBlock() instanceof BogieBlock) {
                b.destroyAllParts(!plr.isCreative());
//                world.breakBlock(b.getCenter().blockPos, !plr.isCreative(), plr);
            }
//                destroyAllConnectedBogies(world, plr, center);
//            }
        }
        super.onBlockHarvested(world, pos, state, plr);
    }

//    public static void destroyAllConnectedBogies(IWorld world, PlayerEntity plr, BlockPos pos) {
//        runForEachBogiePartExceptCenter((x1, z1, pos1) -> {
//            if(world.getBlockState(pos1).getBlock() instanceof BogieBlock) {
//                world.breakBlock(pos1, false, plr);
//            }
//        }, pos);
//    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return isValidPosition(world::isAirBlock, ($) -> false, pos);
    }

    public static boolean isValidPosition(Predicate<BlockPos> isAir, Predicate<BlockPos> isReplaceable, BlockPos center) {
        AtomicBoolean valid = new AtomicBoolean(true);
        runForEachBogiePart((bogiePos) -> {
            BlockPos p = bogiePos.offsetBlockPos(center);
            if(!isAir.test(p) && !isReplaceable.test(p)) {
                valid.set(false);
            }
        });
        return valid.get();
    }

    @Override
    public PushReaction getPushReaction(BlockState p_149656_1_) {
        return PushReaction.BLOCK;
    }
}