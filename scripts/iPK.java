import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSNPC;

/*Change Log
* 1.8 Added Vengeance Detection, and time-until-next-venge
* 1.6 Added Paint Invisible when: Attacking NPC, Using Bank
* 1.4 Added Opponents Mage Level
* 1.2 Added Next-Hit
* 1.0 First Script
*/
@ScriptManifest(authors = "Gweetle",
category = "Player Killing",
        name = "iPK",
        version = 1.8,
        description = "<html><style type='text/css'>"
        + "body {background:url('http://lazygamerz.org/client/images/back_2.png') repeat}"
        + "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"
        + "<h1><center><font color=#FFFFFF>"
        + "iPK by; Gweetle"
        + "</center></font color></h1>"
        + "</head><br><body>"
        + "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
        + "<td width=90% align=justify>"
        + "<font size=2><center><font color=#0000FF>How to set up and start this script.</font></font size></center>"
        + "<font size=3>Remember to turn user input on<br>"
        + "<center><font color=#0000FF>Good luck and bot safe</font></center></font size>"
        + "</td></tr></table><br />"
        )
public class iPK extends Script implements PaintListener {
    String playerName = "None", spec;
    int attLVL, defLVL, strLVL, hpLVL, rangeLVL, mageLVL, y = 200, numbItems = 1, nextHit = 0;
    final int range = Skills.getStatIndex("range");
    final int def = Skills.getStatIndex("defence");
    final int att = Skills.getStatIndex("attack");
    final int str = Skills.getStatIndex("strength");
    int vengeTime = 0;
    long newXP = 0, oldXP = 0;
    long timeLast;
    boolean firstVenge = true;
    RSCharacter playernew, playerold;

    public boolean onStart(final Map<String, String> args) {
        stopRandom("eward");
        return true;
    }

    public void onRepaint(Graphics g) {
        if (game.isLoggedIn()) {
            if (player.getMine().getInteracting() instanceof RSNPC || bank.isOpen()) {
                //catssss
            } else {
                if (settings.get(301) == 1) spec = "On";
                else spec = "Off";
                numbItems = 3;
                if (attLVL != 1) numbItems++;
                if (strLVL != 1) numbItems++;
                if (defLVL != 1) numbItems++;
                if (hpLVL != 1) numbItems++;
                if (rangeLVL != 1) numbItems++;
                if (mageLVL != 1) numbItems++;
                y = 338 - numbItems * 15;
                g.setColor(new Color(100, 100, 100, 150));
                g.fill3DRect(3, y, 127, numbItems * 15, true);
                g.setColor(new Color(255, 255, 255, 200));
                g.setFont(new Font("Tekton Pro", Font.PLAIN, 15));
                g.drawString("Special: " + spec + " (" + settings.get(300) / 10 + "%)", 3, y + 15);
                String timeLeft = "";
                //if (settings.get(439)==3202||settings.get(439)==3266) {
                if (System.currentTimeMillis() - timeLast < 30000) {
                    timeLeft = "(" + (31000 - (System.currentTimeMillis() - timeLast)) / 1000 + "s)";
                }
                if (settings.get(439) == 3266 || settings.get(439) == 3138) {
                    g.drawString("Venge: On " + timeLeft, 3, y + 30);
                } else {
                    g.drawString("Venge: Off " + timeLeft, 3, y + 30);
                }
                g.drawString(playerName, 3, y + 45);
                numbItems = 4;
                //vengeance
                if (attLVL != 1) {
                    g.drawString("Attack: " + attLVL, 3, y + numbItems * 15);
                    numbItems += 1;
                }
                if (strLVL != 1) {
                    g.drawString("Strength: " + strLVL, 3, y + numbItems * 15);
                    numbItems += 1;
                }
                if (defLVL != 1) {
                    g.drawString("Defence: " + defLVL, 3, y + numbItems * 15);
                    numbItems += 1;
                }
                if (hpLVL != 1) {
                    g.drawString("Hitpoints: " + hpLVL, 3, y + numbItems * 15);
                    numbItems += 1;
                }
                if (rangeLVL != 1) {
                    g.drawString("Ranged: " + rangeLVL, 3, y + numbItems * 15);
                    numbItems += 1;
                }
                if (mageLVL != 1) {
                    g.drawString("Magic: " + mageLVL, 3, y + numbItems * 15);
                }
                if (playerold != null && tile.onScreen(playerold.getLocation()) && playerold.isInCombat()) {
                    //playerold.getClass();
                    g.setColor(new Color(150, 0, 0, 200));
                    g.setFont(new Font("Tekton Pro", Font.PLAIN, 27));
                    String enemyHP = "";
                    if (hpLVL != 1) {
                        enemyHP = (playerold.getHPPercent() * hpLVL / 100 + 1) + "/" + hpLVL;
                    } else {
                        enemyHP = playerold.getHPPercent() + "%";
                    }
                    g.drawString(enemyHP, 160, 334);
                    newXP = skills.getCurrentXP(range) + skills.getCurrentXP(str) +
                            skills.getCurrentXP(def) + skills.getCurrentXP(att);
                    if (newXP != oldXP) {
                        nextHit = (int) ((newXP - oldXP) / 4);
                    }
                    g.drawString(String.valueOf(nextHit), 325, 334);
                }
            }
        }
    }

    public static void stopRandom(String name) {
        ArrayList<Random> randoms = (ArrayList<Random>) Bot.getScriptHandler()
                .getRandoms();
        for (Random r : randoms) {
            if (r.getClass().getAnnotation(ScriptManifest.class).name()
                    .contains(name)) {
                r.isUsed = false;
                break;
            }
        }
    }

    public static void startRandom(String name) {
        ArrayList<Random> randoms = (ArrayList<Random>) Bot.getScriptHandler()
                .getRandoms();
        for (Random r : randoms) {
            if (r.getClass().getAnnotation(ScriptManifest.class).name()
                    .contains(name)) {
                r.isUsed = true;
                break;
            }
        }
    }

    @Override
    //s
    public int loop() {
        oldXP = skills.getCurrentXP(range) + skills.getCurrentXP(str) +
                skills.getCurrentXP(def) + skills.getCurrentXP(att);
        playernew = player.getMine().getInteracting();
        if (playernew != playerold && playernew != null) {
            if (player.getMine().getInteracting() instanceof RSNPC) {
                //its a monster!@!@!@ wtf?????
            } else {
                playerold = playernew;
                playerName = playernew.getName();
                getStats(playerName);
            }
        }
        if (settings.get(439) == 3202 || settings.get(439) == 3266) {
            if (firstVenge) {
                timeLast = System.currentTimeMillis();
                firstVenge = false;
            }
            //still can't venge
            //vengeTime+=25;
        }
        if (settings.get(439) == 3138 || settings.get(439) == 3074) {
            //now you can venge
            firstVenge = true;
            //vengeTime=0;
        }
        return 50;
    }

    private void getStats(String name) {
        try {
            URL url = new URL("http://hiscore.runescape.com/index_lite.ws?player=" + name);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            int lineIndex = 0;
            while ((inputLine = in.readLine()) != null) {
                lineIndex++;
                if (lineIndex == 2) {
                    StringTokenizer st = new StringTokenizer(inputLine, ",");
                    st.nextToken();
                    inputLine = st.nextToken();
                    if (Integer.parseInt(inputLine) != -1) {
                        attLVL = Integer.parseInt(inputLine);
                    } else {
                        attLVL = 1;
                    }
                }
                if (lineIndex == 3) {
                    StringTokenizer st = new StringTokenizer(inputLine, ",");
                    st.nextToken();
                    inputLine = st.nextToken();
                    if (Integer.parseInt(inputLine) != -1) {
                        defLVL = Integer.parseInt(inputLine);
                    } else {
                        defLVL = 1;
                    }
                }
                if (lineIndex == 4) {
                    StringTokenizer st = new StringTokenizer(inputLine, ",");
                    st.nextToken();
                    inputLine = st.nextToken();
                    if (Integer.parseInt(inputLine) != -1) {
                        strLVL = Integer.parseInt(inputLine);
                    } else {
                        strLVL = 1;
                    }
                }
                if (lineIndex == 5) {
                    StringTokenizer st = new StringTokenizer(inputLine, ",");
                    st.nextToken();
                    inputLine = st.nextToken();
                    if (Integer.parseInt(inputLine) != -1) {
                        hpLVL = Integer.parseInt(inputLine);
                    } else {
                        hpLVL = 1;
                    }
                }
                if (lineIndex == 6) {
                    StringTokenizer st = new StringTokenizer(inputLine, ",");
                    st.nextToken();
                    inputLine = st.nextToken();
                    if (Integer.parseInt(inputLine) != -1) {
                        rangeLVL = Integer.parseInt(inputLine);
                    } else {
                        rangeLVL = 1;
                    }
                }
                if (lineIndex == 8) {
                    StringTokenizer st = new StringTokenizer(inputLine, ",");
                    st.nextToken();
                    inputLine = st.nextToken();
                    if (Integer.parseInt(inputLine) != -1) {
                        mageLVL = Integer.parseInt(inputLine);
                    } else {
                        mageLVL = 1;
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            e.getCause();
        }
    }
}  
