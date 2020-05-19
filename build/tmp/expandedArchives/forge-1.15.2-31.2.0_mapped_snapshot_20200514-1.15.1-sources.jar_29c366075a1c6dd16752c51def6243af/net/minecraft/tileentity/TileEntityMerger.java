package net.minecraft.tileentity;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TileEntityMerger {
   public static <S extends TileEntity> TileEntityMerger.ICallbackWrapper<S> func_226924_a_(TileEntityType<S> p_226924_0_, Function<BlockState, TileEntityMerger.Type> p_226924_1_, Function<BlockState, Direction> p_226924_2_, DirectionProperty p_226924_3_, BlockState p_226924_4_, IWorld p_226924_5_, BlockPos p_226924_6_, BiPredicate<IWorld, BlockPos> p_226924_7_) {
      S s = p_226924_0_.func_226986_a_(p_226924_5_, p_226924_6_);
      if (s == null) {
         return TileEntityMerger.ICallback::func_225537_b_;
      } else if (p_226924_7_.test(p_226924_5_, p_226924_6_)) {
         return TileEntityMerger.ICallback::func_225537_b_;
      } else {
         TileEntityMerger.Type tileentitymerger$type = p_226924_1_.apply(p_226924_4_);
         boolean flag = tileentitymerger$type == TileEntityMerger.Type.SINGLE;
         boolean flag1 = tileentitymerger$type == TileEntityMerger.Type.FIRST;
         if (flag) {
            return new TileEntityMerger.ICallbackWrapper.Single<>(s);
         } else {
            BlockPos blockpos = p_226924_6_.offset(p_226924_2_.apply(p_226924_4_));
            BlockState blockstate = p_226924_5_.getBlockState(blockpos);
            if (blockstate.getBlock() == p_226924_4_.getBlock()) {
               TileEntityMerger.Type tileentitymerger$type1 = p_226924_1_.apply(blockstate);
               if (tileentitymerger$type1 != TileEntityMerger.Type.SINGLE && tileentitymerger$type != tileentitymerger$type1 && blockstate.get(p_226924_3_) == p_226924_4_.get(p_226924_3_)) {
                  if (p_226924_7_.test(p_226924_5_, blockpos)) {
                     return TileEntityMerger.ICallback::func_225537_b_;
                  }

                  S s1 = p_226924_0_.func_226986_a_(p_226924_5_, blockpos);
                  if (s1 != null) {
                     S s2 = flag1 ? s : s1;
                     S s3 = flag1 ? s1 : s;
                     return new TileEntityMerger.ICallbackWrapper.Double<>(s2, s3);
                  }
               }
            }

            return new TileEntityMerger.ICallbackWrapper.Single<>(s);
         }
      }
   }

   public interface ICallback<S, T> {
      T func_225539_a_(S p_225539_1_, S p_225539_2_);

      T func_225538_a_(S p_225538_1_);

      T func_225537_b_();
   }

   public interface ICallbackWrapper<S> {
      <T> T apply(TileEntityMerger.ICallback<? super S, T> p_apply_1_);

      public static final class Double<S> implements TileEntityMerger.ICallbackWrapper<S> {
         private final S field_226925_a_;
         private final S field_226926_b_;

         public Double(S p_i225760_1_, S p_i225760_2_) {
            this.field_226925_a_ = p_i225760_1_;
            this.field_226926_b_ = p_i225760_2_;
         }

         public <T> T apply(TileEntityMerger.ICallback<? super S, T> p_apply_1_) {
            return p_apply_1_.func_225539_a_(this.field_226925_a_, this.field_226926_b_);
         }
      }

      public static final class Single<S> implements TileEntityMerger.ICallbackWrapper<S> {
         private final S field_226927_a_;

         public Single(S p_i225761_1_) {
            this.field_226927_a_ = p_i225761_1_;
         }

         public <T> T apply(TileEntityMerger.ICallback<? super S, T> p_apply_1_) {
            return p_apply_1_.func_225538_a_(this.field_226927_a_);
         }
      }
   }

   public static enum Type {
      SINGLE,
      FIRST,
      SECOND;
   }
}