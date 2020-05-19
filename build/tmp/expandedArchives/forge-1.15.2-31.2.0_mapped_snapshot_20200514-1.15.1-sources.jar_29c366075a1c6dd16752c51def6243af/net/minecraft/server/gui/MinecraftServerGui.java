package net.minecraft.server.gui;

import com.google.common.collect.Lists;
import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftServerGui extends JComponent {
   private static final Font SERVER_GUI_FONT = new Font("Monospaced", 0, 12);
   private static final Logger LOGGER = LogManager.getLogger();
   private final DedicatedServer server;
   private Thread field_206932_d;
   private final Collection<Runnable> field_219051_e = Lists.newArrayList();
   private final AtomicBoolean field_219052_f = new AtomicBoolean();

   public static MinecraftServerGui func_219048_a(final DedicatedServer p_219048_0_) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception var3) {
         ;
      }

      final JFrame jframe = new JFrame("Minecraft server");
      final MinecraftServerGui minecraftservergui = new MinecraftServerGui(p_219048_0_);
      jframe.setDefaultCloseOperation(2);
      jframe.add(minecraftservergui);
      jframe.pack();
      jframe.setLocationRelativeTo((Component)null);
      jframe.setVisible(true);
      jframe.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent p_windowClosing_1_) {
            if (!minecraftservergui.field_219052_f.getAndSet(true)) {
               jframe.setTitle("Minecraft server - shutting down!");
               p_219048_0_.initiateShutdown(true);
               minecraftservergui.func_219046_f();
            }

         }
      });
      minecraftservergui.func_219045_a(jframe::dispose);
      minecraftservergui.start();
      return minecraftservergui;
   }

   private MinecraftServerGui(DedicatedServer serverIn) {
      this.server = serverIn;
      this.setPreferredSize(new Dimension(854, 480));
      this.setLayout(new BorderLayout());

      try {
         this.add(this.getLogComponent(), "Center");
         this.add(this.getStatsComponent(), "West");
      } catch (Exception exception) {
         LOGGER.error("Couldn't build server GUI", (Throwable)exception);
      }

   }

   public void func_219045_a(Runnable p_219045_1_) {
      this.field_219051_e.add(p_219045_1_);
   }

   /**
    * Generates new StatsComponent and returns it.
    */
   private JComponent getStatsComponent() {
      JPanel jpanel = new JPanel(new BorderLayout());
      StatsComponent statscomponent = new StatsComponent(this.server);
      this.field_219051_e.add(statscomponent::func_219053_a);
      jpanel.add(statscomponent, "North");
      jpanel.add(this.getPlayerListComponent(), "Center");
      jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
      return jpanel;
   }

   /**
    * Generates new PlayerListComponent and returns it.
    */
   private JComponent getPlayerListComponent() {
      JList<?> jlist = new PlayerListComponent(this.server);
      JScrollPane jscrollpane = new JScrollPane(jlist, 22, 30);
      jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
      return jscrollpane;
   }

   private JComponent getLogComponent() {
      JPanel jpanel = new JPanel(new BorderLayout());
      JTextArea jtextarea = new JTextArea();
      JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);
      jtextarea.setEditable(false);
      jtextarea.setFont(SERVER_GUI_FONT);
      JTextField jtextfield = new JTextField();
      jtextfield.addActionListener((p_210465_2_) -> {
         String s = jtextfield.getText().trim();
         if (!s.isEmpty()) {
            this.server.handleConsoleInput(s, this.server.getCommandSource());
         }

         jtextfield.setText("");
      });
      jtextarea.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent p_focusGained_1_) {
         }
      });
      jpanel.add(jscrollpane, "Center");
      jpanel.add(jtextfield, "South");
      jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
      this.field_206932_d = new Thread(() -> {
         String s;
         while((s = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null) {
            this.appendLine(jtextarea, jscrollpane, s);
         }

      });
      this.field_206932_d.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.field_206932_d.setDaemon(true);
      return jpanel;
   }

   private java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
   public void start() {
      this.field_206932_d.start();
      latch.countDown();
   }

   public void func_219050_b() {
      if (!this.field_219052_f.getAndSet(true)) {
         this.func_219046_f();
      }

   }

   private void func_219046_f() {
      this.field_219051_e.forEach(Runnable::run);
   }

   public void appendLine(JTextArea textArea, JScrollPane scrollPane, String line) {
      try {
         latch.await();
      } catch (InterruptedException e){} //Prevent logging until after constructor has ended.
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> {
            this.appendLine(textArea, scrollPane, line);
         });
      } else {
         Document document = textArea.getDocument();
         JScrollBar jscrollbar = scrollPane.getVerticalScrollBar();
         boolean flag = false;
         if (scrollPane.getViewport().getView() == textArea) {
            flag = (double)jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double)(SERVER_GUI_FONT.getSize() * 4) > (double)jscrollbar.getMaximum();
         }

         try {
            document.insertString(document.getLength(), line, (AttributeSet)null);
         } catch (BadLocationException var8) {
            ;
         }

         if (flag) {
            jscrollbar.setValue(Integer.MAX_VALUE);
         }

      }
   }
}