package net.minecraft.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IMutableSearchTree<T> extends ISearchTree<T> {
   void func_217872_a(T element);

   void clear();

   /**
    * Recalculates the contents of this search tree, reapplying {@link #nameFunc} and {@link #idFunc}. Should be called
    * whenever resources are reloaded (e.g. language changes).
    */
   void recalculate();
}