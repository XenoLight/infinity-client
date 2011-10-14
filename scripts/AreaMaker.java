
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSPolygon;

@ScriptManifest(authors = "KaruLont",
name = "Area Maker",
        version = 1.00,
        category = "Development",
        description = "<html><style type='text/css'>"
        + "body {background:url('http://lazygamerz.org/client/images/back_2.png') repeat}"
        + "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"
        + "<h1><center><font color=#FFFFFF>"
        + "Area Maker by: KaruLont"
        + "</center></font color></h1>"
        + "</head><br><body>"
        + "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
        + "<td width=90% align=justify>"
        + "<font size=2><center><font color=#0000FF>How to set up and start this script.</font></font size></center>"
        + "<font size=3>This is the Area Maker development script. Simply hit start to use the"
        + "features of this script...<br>"
        + "<center><font color=#0000FF>Good luck and bot safe</font></center></font size>"
        + "</td></tr></table><br />"/*end box*/
        )
public class AreaMaker extends Script implements PaintListener {

    public RSPolygon area;
    public final javax.swing.JFrame theGui = new javax.swing.JFrame();
    public final ArrayList<RSTile> areaTiles = new ArrayList<RSTile>();
    public JPanel jPanel1;
    public JTextArea output;
    public JButton btnAdd;
    public JPanel jPanel2;
    public JButton btnClear;

    public void initGUI() {
            theGui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            theGui.getContentPane().setLayout(null);
            theGui.setTitle("Area Maker v1.0 by KaruLont");
            theGui.setResizable(false);
            {
                jPanel1 = new JPanel();
                final FlowLayout jPanel1Layout = new FlowLayout();
                theGui.getContentPane().add(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1.setBounds(0, 0, 388, 266);
                {
                    btnClear = new JButton();
                    jPanel1.add(btnClear);
                    btnClear.setText("Clear Tiles");
                    btnClear.setPreferredSize(new java.awt.Dimension(100, 33));
                    btnClear.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {

                            output.setText("Add tiles to get the output");
                            areaTiles.clear();
                            area = new RSPolygon(areaTiles.toArray(new RSTile[areaTiles.size()]));
                        }
                    });

                    btnAdd = new JButton();
                    jPanel1.add(btnAdd);
                    btnAdd.setText("Add Tile");
                    btnAdd.setBounds(117, 13, 177, 54);
                    btnAdd.setPreferredSize(new java.awt.Dimension(79, 33));
                    btnAdd.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            areaTiles.add(player.getMine().getLocation());
                            area = new RSPolygon(areaTiles.toArray(new RSTile[areaTiles.size()]));
                            String s = "";
                            boolean first = true;
                            for (RSTile t : areaTiles){
                                if (!first){
                                    s += ", ";
                                } else {                                
                                    first = false;
                                }
                                s += "new RSTile(" + t.getX() + ", " + t.getY() + ")";
                            }
                            output.setText("/*Imports: */" +
                            "\nimport org.rsbot.script.wrappers.RSTile;" + 
                            "\nimport org.rsbot.script.wrappers.RSPolygon;" +     
                            "\n\n/*Declaration:*/" +                             
                            "\nRSPolygon name = new RSPolygon(" + s + ");" + 
                            "\n\n/*Usage:*/" +     
                            "\nif (name.contains(getMyPlayer().getLocation())) {" +     
                            "\n     //code" +                     
                            "\n}");
                        }
                    });
                }
                {
                    jPanel2 = new JPanel();
                    final FlowLayout jPanel2Layout = new FlowLayout();
                    jPanel1.add(jPanel2);
                    jPanel2.setBounds(6, 78, 382, 188);
                    jPanel2.setLayout(jPanel2Layout);
                    {
                        output = new JTextArea();
                        output.setLineWrap(true);
                        jPanel2.add(output);

                        output.setText("");
                        output.setBounds(6, 78, 382, 188);
                        output.setPreferredSize(new java.awt.Dimension(360,
                                161));
                    }
                }
            }
            theGui.pack();
            theGui.setSize(400, 300);
    }

    @Override
    public int loop() {
        if (!theGui.isVisible()) {
            return -1;
        } else {
            return random(100, 500);
        }
    }

    @Override
    public void onFinish() {
    }

    public void onRepaint(final Graphics render) {
        boolean mayPaint = true;
        if (!game.isLoggedIn() || area == null) {
            mayPaint = false;
        }
        render.setColor(Color.CYAN);
        render.drawString("Number of points: " + areaTiles.size(), 10,
                10);
        if (area.size() == 0) {
            mayPaint = false;
        }
        // int plane=Bot.getClient().getPlane();
        // int blocks[][] =
        // Bot.getClient().getGroundIntArray()[Bot.getClient().getPlane()];
        // int
        // blocks[][]=Bot.getClient().getGroundDataArray()[plane].getBlocks();
        if (mayPaint){
            final int baseX = Bot.getClient().getBaseX();
            final int baseY = Bot.getClient().getBaseY();
            for (int i = 1; i < 103; i++) {
                for (int j = 1; j < 103; j++) {
                    if (area.contains(new RSTile(baseX + i, baseY + j))) {
                        // int curBlock = blocks[i][j];
                        Point miniBL = calculate.worldToMinimap(i + baseX - 0.5,
                                j + baseY - 0.5);
                        if (miniBL.x == -1 || miniBL.y == -1) {
                            miniBL = null;
                        }
                        Point miniBR = calculate.worldToMinimap(i + baseX - 0.5,
                                j + baseY + 0.5);
                        if (miniBR.x == -1 || miniBR.y == -1) {
                            miniBR = null;
                        }
                        Point miniTL = calculate.worldToMinimap(i + baseX + 0.5,
                                j + baseY - 0.5);
                        if (miniTL.x == -1 || miniTL.y == -1) {
                            miniTL = null;
                        }
                        Point miniTR = calculate.worldToMinimap(i + baseX + 0.5,
                                j + baseY + 0.5);
                        if (miniTR.x == -1 || miniTR.y == -1) {
                            miniTR = null;
                        }
                        Point bl = Calculations.tileToScreen(i + baseX, j + baseY,
                                0, 0, 0);
                        if (bl.x == -1 || bl.y == -1) {
                            bl = null;
                        }
                        Point br = Calculations.tileToScreen(i + baseX, j + 1
                                + baseY, 0, 0, 0);
                        if (br.x == -1 || br.y == -1) {
                            br = null;
                        }
                        Point tl = Calculations.tileToScreen(i + 1 + baseX, j
                                + baseY, 0, 0, 0);
                        if (tl.x == -1 || tl.y == -1) {
                            tl = null;
                        }
                        Point tr = Calculations.tileToScreen(i + 1 + baseX, j + 1
                                + baseY, 0, 0, 0);
                        if (tr.x == -1 || tr.y == -1) {
                            tr = null;
                        }
                        // if ((curBlock & 0x1280100) != 0) {
                        if (true) {
                            render.setColor(Color.black);
                            if (tl != null && br != null && tr != null
                                    && bl != null) {
                                render.fillPolygon(new int[]{bl.x, br.x, tr.x,
                                            tl.x},
                                        new int[]{bl.y, br.y, tr.y, tl.y}, 4);
                            }
                            if (miniBL != null && miniBR != null && miniTR != null
                                    && miniTL != null) {
                                render.fillPolygon(new int[]{miniBL.x, miniBR.x,
                                            miniTR.x, miniTL.x}, new int[]{miniBL.y,
                                            miniBR.y, miniTR.y, miniTL.y}, 4);
                            }
                        }
                        // if ((blocks[i][j - 1] & 0x1280102) != 0 || (curBlock &
                        // 0x1280120) != 0) {
                        if (true) {
                            render.setColor(Color.RED);
                            if (tl != null && bl != null) {
                                render.drawLine(bl.x, bl.y, tl.x, tl.y);
                            }
                            if (miniBL != null && miniTL != null) {
                                render.drawLine(miniBL.x, miniBL.y, miniTL.x,
                                        miniTL.y);
                            }
                        }
                        // if ((blocks[i - 1][j] & 0x1280108) != 0 || (curBlock &
                        // 0x1280180) != 0) {
                        if (true) {
                            render.setColor(Color.RED);
                            if (br != null && bl != null) {
                                render.drawLine(bl.x, bl.y, br.x, br.y);
                            }
                            if (miniBR != null && miniBL != null) {
                                render.drawLine(miniBL.x, miniBL.y, miniBR.x,
                                        miniBR.y);
                            }
                        }
                    }
                    /*
                     * render.setColor(Color.cyan); if ((curBlock & (1<<20)) != 0) {
                     * if (miniBL != null && miniBR != null && miniTR != null &&
                     * miniTL != null) { render.fillPolygon(new
                     * int[]{miniBL.x,miniBR.x,miniTR.x,miniTL.x}, new
                     * int[]{miniBL.y,miniBR.y,miniTR.y,miniTL.y},4); } if (tl !=
                     * null && br != null && tr != null && bl != null) {
                     * render.fillPolygon(new int[]{bl.x,br.x,tr.x,tl.x}, new
                     * int[]{bl.y,br.y,tr.y,tl.y},4); } }
                     */
                    // Point miniCent = Calculations.worldToMinimap(i+ baseX, j+
                    // baseY);
                    // Point cent = Calculations.tileToScreen(i+ baseX, j+ baseY,
                    // 0.5,0.5, 0);
                    /*
                     * if (cent.x != -1 && cent.y != -1) {
                     * render.setColor(Color.yellow); render.drawString("" +
                     * Calculations.getRealDistanceTo(cur.getX()-baseX,
                     * cur.getY()-baseY, i, j, false), (int)cent.getX(),
                     * (int)cent.getY()); }
                     */
                }
            }
        }
    }

    public boolean onStart(final Map<String, String> args) {
        try {
            log("Area Maker by KaruLont, fixed and modded by Nullpointer");
            initGUI();
            theGui.setVisible(true);
            return true;
        } catch (Exception e) {
            log("Crashed on start");
            return false;
        }
        }
}

