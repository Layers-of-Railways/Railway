package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class TextComponent implements ITextComponent {
   /**
    * The later siblings of this component. If this component turns the text bold, that will apply to all the siblings
    * until a later sibling turns the text something else.
    */
   protected final List<ITextComponent> siblings = Lists.newArrayList();
   private Style style;

   /**
    * Adds a new component to the end of the sibling list, setting that component's style's parent style to this
    * component's style.
    *  
    * @return This component, for chaining (and not the newly added component)
    */
   public ITextComponent appendSibling(ITextComponent component) {
      component.getStyle().setParentStyle(this.getStyle());
      this.siblings.add(component);
      return this;
   }

   /**
    * Gets the sibling components of this one.
    */
   public List<ITextComponent> getSiblings() {
      return this.siblings;
   }

   /**
    * Sets the style of this component and updates the parent style of all of the sibling components.
    */
   public ITextComponent setStyle(Style style) {
      this.style = style;

      for(ITextComponent itextcomponent : this.siblings) {
         itextcomponent.getStyle().setParentStyle(this.getStyle());
      }

      return this;
   }

   /**
    * Gets the style of this component. Returns a direct reference; changes to this style will modify the style of this
    * component (IE, there is no need to call {@link #setStyle(Style)} again after modifying it).
    *  
    * If this component's style is currently <code>null</code>, it will be initialized to the default style, and the
    * parent style of all sibling components will be set to that style. (IE, changes to this style will also be
    * reflected in sibling components.)
    *  
    * This method never returns <code>null</code>.
    */
   public Style getStyle() {
      if (this.style == null) {
         this.style = new Style();

         for(ITextComponent itextcomponent : this.siblings) {
            itextcomponent.getStyle().setParentStyle(this.style);
         }
      }

      return this.style;
   }

   public Stream<ITextComponent> stream() {
      return Streams.concat(Stream.of(this), this.siblings.stream().flatMap(ITextComponent::stream));
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponent)) {
         return false;
      } else {
         TextComponent textcomponent = (TextComponent)p_equals_1_;
         return this.siblings.equals(textcomponent.siblings) && this.getStyle().equals(textcomponent.getStyle());
      }
   }

   public int hashCode() {
      return Objects.hash(this.getStyle(), this.siblings);
   }

   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }
}