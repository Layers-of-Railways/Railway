package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestExecutor {
   private static final Logger field_229466_a_ = LogManager.getLogger();
   private final BlockPos field_229467_b_;
   private final ServerWorld field_229468_c_;
   private final TestCollection field_229469_d_;
   private final List<TestTracker> field_229470_e_ = Lists.newArrayList();
   private final List<Pair<TestBatch, Collection<TestTracker>>> field_229471_f_ = Lists.newArrayList();
   private TestResultList field_229472_g_;
   private int field_229473_h_ = 0;
   private BlockPos.Mutable field_229474_i_;
   private int field_229475_j_ = 0;

   public TestExecutor(Collection<TestBatch> p_i226066_1_, BlockPos p_i226066_2_, ServerWorld p_i226066_3_, TestCollection p_i226066_4_) {
      this.field_229474_i_ = new BlockPos.Mutable(p_i226066_2_);
      this.field_229467_b_ = p_i226066_2_;
      this.field_229468_c_ = p_i226066_3_;
      this.field_229469_d_ = p_i226066_4_;
      p_i226066_1_.forEach((p_229481_2_) -> {
         Collection<TestTracker> collection = Lists.newArrayList();

         for(TestFunctionInfo testfunctioninfo : p_229481_2_.func_229465_b_()) {
            TestTracker testtracker = new TestTracker(testfunctioninfo, p_i226066_3_);
            collection.add(testtracker);
            this.field_229470_e_.add(testtracker);
         }

         this.field_229471_f_.add(Pair.of(p_229481_2_, collection));
      });
   }

   public List<TestTracker> func_229476_a_() {
      return this.field_229470_e_;
   }

   public void func_229482_b_() {
      this.func_229477_a_(0);
   }

   private void func_229477_a_(int p_229477_1_) {
      this.field_229473_h_ = p_229477_1_;
      this.field_229472_g_ = new TestResultList();
      if (p_229477_1_ < this.field_229471_f_.size()) {
         Pair<TestBatch, Collection<TestTracker>> pair = this.field_229471_f_.get(this.field_229473_h_);
         TestBatch testbatch = pair.getFirst();
         Collection<TestTracker> collection = pair.getSecond();
         this.func_229480_a_(collection);
         testbatch.func_229464_a_(this.field_229468_c_);
         String s = testbatch.func_229463_a_();
         field_229466_a_.info("Running test batch '" + s + "' (" + collection.size() + " tests)...");
         collection.forEach((p_229483_1_) -> {
            this.field_229472_g_.func_229579_a_(p_229483_1_);
            this.field_229472_g_.func_229580_a_(new ITestCallback() {
               public void func_225644_a_(TestTracker p_225644_1_) {
               }

               public void func_225645_c_(TestTracker p_225645_1_) {
                  TestExecutor.this.func_229479_a_(p_225645_1_);
               }
            });
            TestUtils.func_229542_a_(p_229483_1_, this.field_229469_d_);
         });
      }
   }

   private void func_229479_a_(TestTracker p_229479_1_) {
      if (this.field_229472_g_.func_229588_i_()) {
         this.func_229477_a_(this.field_229473_h_ + 1);
      }

   }

   private void func_229480_a_(Collection<TestTracker> p_229480_1_) {
      int i = 0;

      for(TestTracker testtracker : p_229480_1_) {
         BlockPos blockpos = new BlockPos(this.field_229474_i_);
         testtracker.func_229503_a_(blockpos);
         StructureHelper.func_229602_a_(testtracker.func_229522_s_(), blockpos, 2, this.field_229468_c_, true);
         BlockPos blockpos1 = testtracker.func_229513_e_();
         int j = blockpos1 == null ? 1 : blockpos1.getX();
         int k = blockpos1 == null ? 1 : blockpos1.getZ();
         this.field_229475_j_ = Math.max(this.field_229475_j_, k);
         this.field_229474_i_.move(j + 4, 0, 0);
         if (i++ % 8 == 0) {
            this.field_229474_i_.move(0, 0, this.field_229475_j_ + 5);
            this.field_229474_i_.setX(this.field_229467_b_.getX());
            this.field_229475_j_ = 0;
         }
      }

   }
}