package com.mojang.realmsclient.gui;

import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ListButton {
   public final int field_225125_a;
   public final int field_225126_b;
   public final int field_225127_c;
   public final int field_225128_d;

   public ListButton(int p_i51779_1_, int p_i51779_2_, int p_i51779_3_, int p_i51779_4_) {
      this.field_225125_a = p_i51779_1_;
      this.field_225126_b = p_i51779_2_;
      this.field_225127_c = p_i51779_3_;
      this.field_225128_d = p_i51779_4_;
   }

   public void func_225118_a(int p_225118_1_, int p_225118_2_, int p_225118_3_, int p_225118_4_) {
      int i = p_225118_1_ + this.field_225127_c;
      int j = p_225118_2_ + this.field_225128_d;
      boolean flag = false;
      if (p_225118_3_ >= i && p_225118_3_ <= i + this.field_225125_a && p_225118_4_ >= j && p_225118_4_ <= j + this.field_225126_b) {
         flag = true;
      }

      this.func_225120_a(i, j, flag);
   }

   protected abstract void func_225120_a(int p_225120_1_, int p_225120_2_, boolean p_225120_3_);

   public int func_225122_a() {
      return this.field_225127_c + this.field_225125_a;
   }

   public int func_225123_b() {
      return this.field_225128_d + this.field_225126_b;
   }

   public abstract void func_225121_a(int p_225121_1_);

   public static void func_225124_a(List<ListButton> p_225124_0_, RealmsObjectSelectionList p_225124_1_, int p_225124_2_, int p_225124_3_, int p_225124_4_, int p_225124_5_) {
      for(ListButton listbutton : p_225124_0_) {
         if (p_225124_1_.getRowWidth() > listbutton.func_225122_a()) {
            listbutton.func_225118_a(p_225124_2_, p_225124_3_, p_225124_4_, p_225124_5_);
         }
      }

   }

   public static void func_225119_a(RealmsObjectSelectionList p_225119_0_, RealmListEntry p_225119_1_, List<ListButton> p_225119_2_, int p_225119_3_, double p_225119_4_, double p_225119_6_) {
      if (p_225119_3_ == 0) {
         int i = p_225119_0_.children().indexOf(p_225119_1_);
         if (i > -1) {
            p_225119_0_.selectItem(i);
            int j = p_225119_0_.getRowLeft();
            int k = p_225119_0_.getRowTop(i);
            int l = (int)(p_225119_4_ - (double)j);
            int i1 = (int)(p_225119_6_ - (double)k);

            for(ListButton listbutton : p_225119_2_) {
               if (l >= listbutton.field_225127_c && l <= listbutton.func_225122_a() && i1 >= listbutton.field_225128_d && i1 <= listbutton.func_225123_b()) {
                  listbutton.func_225121_a(i);
               }
            }
         }
      }

   }
}