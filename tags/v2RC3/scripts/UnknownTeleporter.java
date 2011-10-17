
import javax.swing.JOptionPane;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;

@ScriptManifest(authors = "Unknown", category = "Magic", name = "Unknown's Teleporter", version = 1.0, description = "<html><body bgcolor=Black> "
+ "<center><img src=http://image.lazygamerz.org/images/685663_title.png></center>"
+ "<center><font color=red size=20>Unknown's Teleporter</font></h1></center>"
+ "<center><font color=white>Start anywhere with runes in inventory and spells arranged by level.</font> <center>"
+ "<center><font size=10 color=white>Please vist the Forums :)</font></a></center>"
+ "<center><font size=12 color=green>http://www.lazygamerz.org</font></a></center>"
+ "</body></html>")
public class UnknownTeleporter extends Script {

    public UnknownTeleporter() {
        varrockCount = 0;
        faladorCount = 0;
        lumbridgeCount = 0;
        cammyCount = 0;
        quitScript = 0;
    }

    public int loop() {
        do {
            if (quitScript == 0) {
                int i = JOptionPane.showOptionDialog(null, "Which spell would you like to cast?", "Unknown's Teleporter", 0, -1, null, choices, "Varrock");
                switch (i) {
                    case 0: // '\0'
                        game.openTab(7);
                        dummy = JOptionPane.showInputDialog(null, "How many times do you wanna teleport?");
                        for (castTime = Integer.parseInt(dummy); varrockCount < castTime;) {
                            magic.castSpell(40);
                            log((new StringBuilder()).append("Teleporting to Varrock - ").append(++varrockCount).append(".").toString());
                            wait(3500);
                        }

                        varrockCount = 0;
                        return 1500;

                    case 1: // '\001'
                        game.openTab(7);
                        dummy = JOptionPane.showInputDialog(null, "How many times do you want to teleport?");
                        for (castTime = Integer.parseInt(dummy); lumbridgeCount < castTime;) {
                            magic.castSpell(43);
                            log((new StringBuilder()).append("Teleporting to Lumbridge - ").append(++lumbridgeCount).append(".").toString());
                            wait(3500);
                        }

                        lumbridgeCount = 0;
                        return 1500;

                    case 2: // '\002'
                        game.openTab(7);
                        dummy = JOptionPane.showInputDialog(null, "How many times do you wanna teleport?");
                        for (castTime = Integer.parseInt(dummy); faladorCount < castTime;) {
                            magic.castSpell(46);
                            log((new StringBuilder()).append("Teleporting to Falador - ").append(++faladorCount).append(".").toString());
                            wait(3500);
                        }

                        faladorCount = 0;
                        return 1500;

                    case 3: // '\003'
                        game.openTab(7);
                        dummy = JOptionPane.showInputDialog(null, "How many times do you want to teleport?");
                        for (castTime = Integer.parseInt(dummy); cammyCount < castTime;) {
                            magic.castSpell(51);
                            log((new StringBuilder()).append("Teleporting to Falador - ").append(++cammyCount).append(".").toString());
                            wait(3500);
                        }

                        cammyCount = 0;
                        return 1500;

                    case -1:
                    case 4: // '\004'
                        quitScript = 1;
                        log("Bye! Don't forget to visit lazygamerz.org :P");
                        break;
                }
            } else {
                return 0;
            }
        } while (true);
    }
    String choices[] = {
        "Varrock", "Lumbridge", "Falador", "Camelot", "Quit"
    };
    String dummy;
    int castTime;
    int varrockCount;
    int faladorCount;
    int lumbridgeCount;
    int cammyCount;
    int quitScript;
}
