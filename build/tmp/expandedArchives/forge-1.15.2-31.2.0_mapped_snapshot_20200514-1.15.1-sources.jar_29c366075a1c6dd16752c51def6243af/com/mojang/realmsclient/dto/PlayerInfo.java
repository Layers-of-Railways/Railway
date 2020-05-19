package com.mojang.realmsclient.dto;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerInfo extends ValueObject {
   private String name;
   private String uuid;
   private boolean operator;
   private boolean accepted;
   private boolean online;

   public String getName() {
      return this.name;
   }

   public void setName(String p_setName_1_) {
      this.name = p_setName_1_;
   }

   public String getUuid() {
      return this.uuid;
   }

   public void setUuid(String p_setUuid_1_) {
      this.uuid = p_setUuid_1_;
   }

   public boolean isOperator() {
      return this.operator;
   }

   public void setOperator(boolean p_setOperator_1_) {
      this.operator = p_setOperator_1_;
   }

   public boolean getAccepted() {
      return this.accepted;
   }

   public void setAccepted(boolean p_setAccepted_1_) {
      this.accepted = p_setAccepted_1_;
   }

   public boolean getOnline() {
      return this.online;
   }

   public void setOnline(boolean p_setOnline_1_) {
      this.online = p_setOnline_1_;
   }
}