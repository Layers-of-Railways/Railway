package net.minecraft.client.util;

import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISearchTree<T> {
   /**
    * Searches this search tree for the given text.
    * <p>
    * If the query does not contain a <code>:</code>, then only {@link #byName} is searched; if it does contain a colon,
    * both {@link #byName} and {@link #byId} are searched and the results are merged using a {@link MergingIterator}.
    * @return A list of all matching items in this search tree.
    */
   List<T> search(String searchText);
}