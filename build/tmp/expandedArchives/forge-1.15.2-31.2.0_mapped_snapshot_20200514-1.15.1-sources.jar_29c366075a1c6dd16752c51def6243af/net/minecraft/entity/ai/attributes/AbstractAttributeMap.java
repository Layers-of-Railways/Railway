package net.minecraft.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.LowerStringMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractAttributeMap {
   protected final Map<IAttribute, IAttributeInstance> attributes = Maps.newHashMap();
   protected final Map<String, IAttributeInstance> attributesByName = new LowerStringMap<>();
   protected final Multimap<IAttribute, IAttribute> descendantsByParent = HashMultimap.create();

   @Nullable
   public IAttributeInstance getAttributeInstance(IAttribute attribute) {
      return this.attributes.get(attribute);
   }

   @Nullable
   public IAttributeInstance getAttributeInstanceByName(String attributeName) {
      return this.attributesByName.get(attributeName);
   }

   /**
    * Registers an attribute with this AttributeMap, returns a modifiable AttributeInstance associated with this map
    */
   public IAttributeInstance registerAttribute(IAttribute attribute) {
      if (this.attributesByName.containsKey(attribute.getName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      } else {
         IAttributeInstance iattributeinstance = this.createInstance(attribute);
         this.attributesByName.put(attribute.getName(), iattributeinstance);
         this.attributes.put(attribute, iattributeinstance);

         for(IAttribute iattribute = attribute.getParent(); iattribute != null; iattribute = iattribute.getParent()) {
            this.descendantsByParent.put(iattribute, attribute);
         }

         return iattributeinstance;
      }
   }

   protected abstract IAttributeInstance createInstance(IAttribute attribute);

   public Collection<IAttributeInstance> getAllAttributes() {
      return this.attributesByName.values();
   }

   public void onAttributeModified(IAttributeInstance instance) {
   }

   public void removeAttributeModifiers(Multimap<String, AttributeModifier> modifiers) {
      for(Entry<String, AttributeModifier> entry : modifiers.entries()) {
         IAttributeInstance iattributeinstance = this.getAttributeInstanceByName(entry.getKey());
         if (iattributeinstance != null) {
            iattributeinstance.removeModifier(entry.getValue());
         }
      }

   }

   public void applyAttributeModifiers(Multimap<String, AttributeModifier> modifiers) {
      for(Entry<String, AttributeModifier> entry : modifiers.entries()) {
         IAttributeInstance iattributeinstance = this.getAttributeInstanceByName(entry.getKey());
         if (iattributeinstance != null) {
            iattributeinstance.removeModifier(entry.getValue());
            iattributeinstance.applyModifier(entry.getValue());
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void func_226303_a_(AbstractAttributeMap p_226303_1_) {
      this.getAllAttributes().forEach((p_226304_1_) -> {
         IAttributeInstance iattributeinstance = p_226303_1_.getAttributeInstance(p_226304_1_.getAttribute());
         if (iattributeinstance != null) {
            p_226304_1_.func_226302_a_(iattributeinstance);
         }

      });
   }
}