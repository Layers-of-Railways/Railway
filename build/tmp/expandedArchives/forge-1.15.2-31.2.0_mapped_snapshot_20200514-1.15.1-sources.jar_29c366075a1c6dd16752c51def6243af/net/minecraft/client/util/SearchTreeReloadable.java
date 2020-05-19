package net.minecraft.client.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTreeReloadable<T> implements IMutableSearchTree<T> {
   protected SuffixArray<T> namespaceList = new SuffixArray<>();
   protected SuffixArray<T> pathList = new SuffixArray<>();
   private final Function<T, Stream<ResourceLocation>> field_217877_c;
   private final List<T> field_217878_d = Lists.newArrayList();
   private final Object2IntMap<T> field_217879_e = new Object2IntOpenHashMap<>();

   public SearchTreeReloadable(Function<T, Stream<ResourceLocation>> p_i50896_1_) {
      this.field_217877_c = p_i50896_1_;
   }

   /**
    * Recalculates the contents of this search tree, reapplying {@link #nameFunc} and {@link #idFunc}. Should be called
    * whenever resources are reloaded (e.g. language changes).
    */
   public void recalculate() {
      this.namespaceList = new SuffixArray<>();
      this.pathList = new SuffixArray<>();

      for(T t : this.field_217878_d) {
         this.index(t);
      }

      this.namespaceList.generate();
      this.pathList.generate();
   }

   public void func_217872_a(T element) {
      this.field_217879_e.put(element, this.field_217878_d.size());
      this.field_217878_d.add(element);
      this.index(element);
   }

   public void clear() {
      this.field_217878_d.clear();
      this.field_217879_e.clear();
   }

   /**
    * Directly puts the given item into {@link #byId} and {@link #byName}, applying {@link #nameFunc} and {@link
    * idFunc}.
    */
   protected void index(T element) {
      this.field_217877_c.apply(element).forEach((p_217873_2_) -> {
         this.namespaceList.add(element, p_217873_2_.getNamespace().toLowerCase(Locale.ROOT));
         this.pathList.add(element, p_217873_2_.getPath().toLowerCase(Locale.ROOT));
      });
   }

   /**
    * Compares two elements. Returns {@code 1} if the first element has more entries, {@code 0} if they have the same
    * number of entries, and {@code -1} if the second element has more enties.
    */
   protected int compare(T p_217874_1_, T p_217874_2_) {
      return Integer.compare(this.field_217879_e.getInt(p_217874_1_), this.field_217879_e.getInt(p_217874_2_));
   }

   /**
    * Searches this search tree for the given text.
    * <p>
    * If the query does not contain a <code>:</code>, then only {@link #byName} is searched; if it does contain a colon,
    * both {@link #byName} and {@link #byId} are searched and the results are merged using a {@link MergingIterator}.
    * @return A list of all matching items in this search tree.
    */
   public List<T> search(String searchText) {
      int i = searchText.indexOf(58);
      if (i == -1) {
         return this.pathList.search(searchText);
      } else {
         List<T> list = this.namespaceList.search(searchText.substring(0, i).trim());
         String s = searchText.substring(i + 1).trim();
         List<T> list1 = this.pathList.search(s);
         return Lists.newArrayList(new SearchTreeReloadable.JoinedIterator<>(list.iterator(), list1.iterator(), this::compare));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class JoinedIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> field_217881_a;
      private final PeekingIterator<T> field_217882_b;
      private final Comparator<T> field_217883_c;

      public JoinedIterator(Iterator<T> p_i50270_1_, Iterator<T> p_i50270_2_, Comparator<T> p_i50270_3_) {
         this.field_217881_a = Iterators.peekingIterator(p_i50270_1_);
         this.field_217882_b = Iterators.peekingIterator(p_i50270_2_);
         this.field_217883_c = p_i50270_3_;
      }

      protected T computeNext() {
         while(this.field_217881_a.hasNext() && this.field_217882_b.hasNext()) {
            int i = this.field_217883_c.compare(this.field_217881_a.peek(), this.field_217882_b.peek());
            if (i == 0) {
               this.field_217882_b.next();
               return this.field_217881_a.next();
            }

            if (i < 0) {
               this.field_217881_a.next();
            } else {
               this.field_217882_b.next();
            }
         }

         return (T)this.endOfData();
      }
   }
}