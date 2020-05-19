package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;

public class PlayerListComponent extends JList<String> {
   private final MinecraftServer server;
   private int ticks;

   public PlayerListComponent(MinecraftServer server) {
      this.server = server;
      server.registerTickable(this::tick);
   }

   public void tick() {
      if (this.ticks++ % 20 == 0) {
         Vector<String> vector = new Vector<>();

         for(int i = 0; i < this.server.getPlayerList().getPlayers().size(); ++i) {
            vector.add(this.server.getPlayerList().getPlayers().get(i).getGameProfile().getName());
         }

         this.setListData(vector);
      }

   }
}