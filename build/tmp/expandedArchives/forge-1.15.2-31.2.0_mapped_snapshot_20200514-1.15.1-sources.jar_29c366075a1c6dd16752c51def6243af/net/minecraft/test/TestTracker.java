package net.minecraft.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class TestTracker {
   private final TestFunctionInfo field_229488_a_;
   private BlockPos field_229489_b_;
   private final ServerWorld field_229490_c_;
   private final Collection<ITestCallback> field_229491_d_ = Lists.newArrayList();
   private final int field_229492_e_;
   private final Collection<TestList> field_229493_f_ = Lists.newCopyOnWriteArrayList();
   private Object2LongMap<Runnable> field_229494_g_ = new Object2LongOpenHashMap<>();
   private long field_229495_h_;
   private long field_229496_i_;
   private boolean field_229497_j_ = false;
   private final Stopwatch field_229498_k_ = Stopwatch.createUnstarted();
   private boolean field_229499_l_ = false;
   @Nullable
   private Throwable field_229500_m_;

   public TestTracker(TestFunctionInfo p_i226070_1_, ServerWorld p_i226070_2_) {
      this.field_229488_a_ = p_i226070_1_;
      this.field_229490_c_ = p_i226070_2_;
      this.field_229492_e_ = p_i226070_1_.func_229660_c_();
   }

   public TestTracker(TestFunctionInfo p_i226069_1_, BlockPos p_i226069_2_, ServerWorld p_i226069_3_) {
      this(p_i226069_1_, p_i226069_3_);
      this.func_229503_a_(p_i226069_2_);
   }

   void func_229503_a_(BlockPos p_229503_1_) {
      this.field_229489_b_ = p_229503_1_;
   }

   void func_229501_a_() {
      this.field_229495_h_ = this.field_229490_c_.getGameTime() + 1L + this.field_229488_a_.func_229663_f_();
      this.field_229498_k_.start();
   }

   public void func_229507_b_() {
      if (!this.func_229518_k_()) {
         this.field_229496_i_ = this.field_229490_c_.getGameTime() - this.field_229495_h_;
         if (this.field_229496_i_ >= 0L) {
            if (this.field_229496_i_ == 0L) {
               this.func_229523_t_();
            }

            ObjectIterator<Entry<Runnable>> objectiterator = this.field_229494_g_.object2LongEntrySet().iterator();

            while(objectiterator.hasNext()) {
               Entry<Runnable> entry = objectiterator.next();
               if (entry.getLongValue() <= this.field_229496_i_) {
                  try {
                     entry.getKey().run();
                  } catch (Exception exception) {
                     this.func_229506_a_(exception);
                  }

                  objectiterator.remove();
               }
            }

            if (this.field_229496_i_ > (long)this.field_229492_e_) {
               if (this.field_229493_f_.isEmpty()) {
                  this.func_229506_a_(new TestTimeoutException("Didn't succeed or fail within " + this.field_229488_a_.func_229660_c_() + " ticks"));
               } else {
                  this.field_229493_f_.forEach((p_229509_1_) -> {
                     p_229509_1_.func_229568_b_(this.field_229496_i_);
                  });
                  if (this.field_229500_m_ == null) {
                     this.func_229506_a_(new TestTimeoutException("No sequences finished"));
                  }
               }
            } else {
               this.field_229493_f_.forEach((p_229505_1_) -> {
                  p_229505_1_.func_229567_a_(this.field_229496_i_);
               });
            }

         }
      }
   }

   private void func_229523_t_() {
      if (this.field_229497_j_) {
         throw new IllegalStateException("Test already started");
      } else {
         this.field_229497_j_ = true;

         try {
            this.field_229488_a_.func_229658_a_(new TestTrackerHolder(this));
         } catch (Exception exception) {
            this.func_229506_a_(exception);
         }

      }
   }

   public String func_229510_c_() {
      return this.field_229488_a_.func_229657_a_();
   }

   public BlockPos func_229512_d_() {
      return this.field_229489_b_;
   }

   @Nullable
   public BlockPos func_229513_e_() {
      StructureBlockTileEntity structureblocktileentity = this.func_229524_u_();
      return structureblocktileentity == null ? null : structureblocktileentity.getStructureSize();
   }

   @Nullable
   private StructureBlockTileEntity func_229524_u_() {
      return (StructureBlockTileEntity)this.field_229490_c_.getTileEntity(this.field_229489_b_);
   }

   public ServerWorld func_229514_g_() {
      return this.field_229490_c_;
   }

   public boolean func_229515_h_() {
      return this.field_229499_l_ && this.field_229500_m_ == null;
   }

   public boolean func_229516_i_() {
      return this.field_229500_m_ != null;
   }

   public boolean func_229517_j_() {
      return this.field_229497_j_;
   }

   public boolean func_229518_k_() {
      return this.field_229499_l_;
   }

   private void func_229525_v_() {
      if (!this.field_229499_l_) {
         this.field_229499_l_ = true;
         this.field_229498_k_.stop();
      }

   }

   public void func_229506_a_(Throwable p_229506_1_) {
      this.func_229525_v_();
      this.field_229500_m_ = p_229506_1_;
      this.field_229491_d_.forEach((p_229511_1_) -> {
         p_229511_1_.func_225645_c_(this);
      });
   }

   @Nullable
   public Throwable func_229519_n_() {
      return this.field_229500_m_;
   }

   public String toString() {
      return this.func_229510_c_();
   }

   public void func_229504_a_(ITestCallback p_229504_1_) {
      this.field_229491_d_.add(p_229504_1_);
   }

   public void func_229502_a_(int p_229502_1_) {
      StructureBlockTileEntity structureblocktileentity = StructureHelper.func_229602_a_(this.field_229488_a_.func_229659_b_(), this.field_229489_b_, p_229502_1_, this.field_229490_c_, false);
      structureblocktileentity.setName(this.func_229510_c_());
      StructureHelper.func_229600_a_(this.field_229489_b_.add(1, 0, -1), this.field_229490_c_);
      this.field_229491_d_.forEach((p_229508_1_) -> {
         p_229508_1_.func_225644_a_(this);
      });
   }

   public boolean func_229520_q_() {
      return this.field_229488_a_.func_229661_d_();
   }

   public boolean func_229521_r_() {
      return !this.field_229488_a_.func_229661_d_();
   }

   public String func_229522_s_() {
      return this.field_229488_a_.func_229659_b_();
   }
}