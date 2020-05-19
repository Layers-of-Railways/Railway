package net.minecraft.client.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
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
public class SearchTree<T> extends SearchTreeReloadable<T> {
   protected SuffixArray<T> byName = new SuffixArray<>();
   private final Function<T, Stream<String>> nameFunc;

   public SearchTree(Function<T, Stream<String>> nameFuncIn, Function<T, Stream<ResourceLocation>> idFuncIn) {
      super(idFuncIn);
      this.nameFunc = nameFuncIn;
   }

   /**
    * Recalculates the contents of this search tree, reapplying {@link #nameFunc} and {@link #idFunc}. Should be called
    * whenever resources are reloaded (e.g. language changes).
    */
   public void recalculate() {
      this.byName = new SuffixArray<>();
      super.recalculate();
      this.byName.generate();
   }

   /**
    * Directly puts the given item into {@link #byId} and {@link #byName}, applying {@link #nameFunc} and {@link
    * idFunc}.
    */
   protected void index(T element) {
      super.index(element);
      this.nameFunc.apply(element).forEach((p_217880_2_) -> {
         this.byName.add(element, p_217880_2_.toLowerCase(Locale.ROOT));
      });
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
      if (i < 0) {
         return this.byName.search(searchText);
      } else {
         List<T> list = this.namespaceList.search(searchText.substring(0, i).trim());
         String s = searchText.substring(i + 1).trim();
         List<T> list1 = this.pathList.search(s);
         List<T> list2 = this.byName.search(s);
         return Lists.newArrayList(new SearchTreeReloadable.JoinedIterator<>(list.iterator(), new SearchTree.MergingIterator<>(list1.iterator(), list2.iterator(), this::compare), this::compare));
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class MergingIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> leftItr;
      private final PeekingIterator<T> rightItr;
      private final Comparator<T> numbers;

      public MergingIterator(Iterator<T> p_i49977_1_, Iterator<T> p_i49977_2_, Comparator<T> p_i49977_3_) {
         this.leftItr = Iterators.peekingIterator(p_i49977_1_);
         this.rightItr = Iterators.peekingIterator(p_i49977_2_);
         this.numbers = p_i49977_3_;
      }

      protected T computeNext() {
         boolean flag = !this.leftItr.hasNext();
         boolean flag1 = !this.rightItr.hasNext();
         if (flag && flag1) {
            return (T)this.endOfData();
         } else if (flag) {
            return this.rightItr.next();
         } else if (flag1) {
            return this.leftItr.next();
         } else {
            int i = this.numbers.compare(this.leftItr.peek(), this.rightItr.peek());
            if (i == 0) {
               this.rightItr.next();
            }

            return (T)(i <= 0 ? this.leftItr.next() : this.rightItr.next());
         }
      }
   }
}