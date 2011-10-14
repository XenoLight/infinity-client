import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Robot;
import java.util.Map;
import java.util.Random;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;



@ScriptManifest(authors = { "Somanayr" },
name = "Chaos Temple Burier",
category = "Prayer",
version = 1.00,
description = "Start it in the chaos temple. If you are attacked by a revenant, auto-retaliate off will signify a flight, on will signify a fight. If anyone can fix my deathwalk/update combat handler, please do.")
public class ChaosTempleBurier extends Script implements PaintListener {
	Temple_antiban t;
	int deaths = 0, bones_buried = 0, failsafe = 0;
	long startTime = 0, curTime = 0;
	String status = "idle";
	boolean fight = combat.isAutoRetaliateEnabled();
	public void deathHandler(){
		status = "Doing death walk";
		deaths++;
		death_walk();
	}

	public boolean isDead(){
		RSTile location = player.getMyLocation();
		int y = location.getY();
		if (y <= 3500) return true;
		else return false;
	}

	public boolean isFighting(){
		int [] id = {424};
		return  player.animationIs(id);
	}

	public boolean cross_ditch(){
		log("crossing ditch");
		mouse.click(t.random(536, 550), t.random(15, 38), true);
		mouse.click(t.random(170, 180), t.random(100, 450), true);
		wait(3000);
		RSTile location = player.getMyLocation();
		int y = location.getY();
		if (y >= 3523) return true;
		else {
			mouse.click(t.random(164, 224), t.random(210, 245), true);
			log("failed!");
			wait(3000);
			if (y >= 3523) return true;
			else if(isDead()) game.logout();
			else game.logout();
		}
		return true;
	}

	public void walktoTileMM(RSTile tile){
		int x_end = tile.getX();
		int y_end = tile.getY();
		RSTile location = player.getMyLocation();
		int x = location.getX();
		int y = location.getY();
		int x_prev = location.getX();
		int y_prev = location.getY();
		int failsafe = 0;
		while(x != x_end && y != y_end){
			RSTile[] path = walk.generateFixedPath(tile);
			RSTile next_tile = walk.nextTile(path, t.random(5, 15));
			try{
				walk.tileMM(next_tile);} catch (NullPointerException e){}
			x = location.getX();
			y = location.getY();
			run();
			wait(t.random(1000,2000));
			if(x_prev == x && y_prev == y) failsafe ++;
			else failsafe = 0;
			if(failsafe == 10) game.logout();
			x_prev = x;
			y_prev = y;
		}
	}

	public void death_walk(){
		RSTile tile1 = new RSTile(3237,3607);
		RSTile tile2 = new RSTile(3237,3607);
		RSTile tile3 = new RSTile(3237,3607);
		RSTile tile4 = new RSTile(3237,3607);
		int r = t.random(1, 4);
		walk.tileMM(new RSTile(3244, 3520));
		if(r == 1){
			cross_ditch();
			walktoTileMM(tile1);
		}
		if(r == 2){
			cross_ditch();
			walktoTileMM(tile2);
		}
		if(r == 3){
			cross_ditch();
			walktoTileMM(tile3);
		}
		else{
			cross_ditch();
			walktoTileMM(tile4);
		}

	}

	public void combat_handler(){
		boolean b = isFighting();
		if(b){
			if(fight) fight();
			else if(!fight) flight();
		}
	}

	public void fight(){
		log("Entered fight, action chosen: fight");
		String[] names = {"Revenant imp", "Revenant goblin", "Revenant icefiend", "Revenant pyrefiend", "Revenant hobgoblin", "Revenant vampire", "Revenant werewolf", "Revenant cyclops", "Revenant hellhound", "Revenant demon", "Revenant ork", "Revenant dark beast", "Revenant knight", "Revenant dragon"};
		RSNPC rev = npc.getNearestByName(names);
		log("Scanning for enemies");
		while(rev == null){
			rev = npc.getNearestByName(names);
		}
		log("Enemy name: " + rev.getName()); 
		npc.action(rev, "Attack");
		prayer.set(14, true);
		prayer.set(10, true);
		for(int i = 0; i == 1;){
			rev = npc.getNearestByName(names);
			if(rev == null) i = 1;
			wait(1000);
		}
	}

	public void flight(){
		log("Entered fight, action chosen: flight");
		walktoTileMM(new RSTile(3244, 3523));
		wait(20000);
		walktoTileMM(new RSTile(3237,3607));
	}
	public void run(){
		if(player.getMyEnergy() > t.random(50, 100)){
			game.setRun(true);
		}
	}

	public void collect(){
		int[] bones_id = {526};
		RSGroundItem bones = ground.getNearestItemByID(bones_id);
		status = "Collecting bones";
		RSTile current_tile = player.getMyLocation(), last_tile = player.getMyLocation();
		int num_items, num_items_prev = -1;
		while(!inventory.isFull()){
			current_tile = player.getMyLocation();
			if(current_tile != last_tile){
				last_tile = current_tile;
				if(isDead()){
					deathHandler();
				}
				run();
				bones = ground.getNearestItemByID(bones_id);
				if (bones!=null) bones.action("take");
				num_items = inventory.getCount();
				if(num_items == num_items_prev) failsafe++;
				if(num_items > num_items_prev) failsafe = 0;
				if(failsafe > 4) camera.turnTo(t.random(0, 16000), 1024);
				if(failsafe > 15){walk.tileMM(new RSTile(3237,3607));
				failsafe = 0;}
				wait(t.random(600,900));
				num_items_prev = num_items;
				
				if(bones == null) bury();
			}
		}
	}

	public void bury(){
		final int bone = 526;
		int [] bones = {526};
		status = "Burying bones";
		while(inventory.contains(bones)){
			inventory.clickItem(bone, "bury");
			bones_buried++;
			wait(t.random(600,900));
		}
	}

	public int loop() {
		while(true){
			if(isDead()){
				deathHandler();
			}
			collect();
			if(isDead()){
				deathHandler();
			}
			bury();
			if(isDead()){
				deathHandler();
			}
			combat_handler();
			return (5);
		}
	}

	public void onRepaint(final Graphics render) {
		Color black = new Color(0,0,0,(float)0.5);
		Point b  = mouse.getLocation();
		int x = (int)b.getX();
		int y = (int)b.getY();
		curTime = System.currentTimeMillis();
		render.setColor(black);
		render.fillRect(0, 0, 300, 65);
		render.setColor(Color.RED);
		render.drawRect(0, 0, 300, 65);
		render.setColor(Color.WHITE);
		render.drawString("Number of normal bones buried: " + String.valueOf(bones_buried), 10, 10);
		render.drawString("Number of deaths: " + String.valueOf(deaths), 10, 20);
		render.drawString("Status: " + status, 10, 30);
		render.drawString("Runtime in seconds: " + String.valueOf((curTime - startTime)/1000), 10, 40);
		render.drawString("Failsafe (kicks in at 5): " + failsafe, 10, 50);
		render.drawLine(0, y, 800, y);
		render.drawLine(x, 0, x, 600);
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		t = new Temple_antiban();
		startTime = System.currentTimeMillis();
		curTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public void onFinish() {
	}
}

class Temple_antiban extends Thread{
	public boolean running = true;
	Random r = new Random(100L);
	public Temple_antiban(){
		super();
	}
	public void run(){
		while(running){
			int turn = random(1,3);
			if(turn == 1){
				try{
					Robot turn_screen = new Robot();
					int rand_int = random(37, 40);
					int turn_time = random(300, 1000);
					turn_screen.keyPress(rand_int);
					turn_screen.delay(turn_time);
					turn_screen.keyRelease(rand_int);
				} catch (AWTException ex) {}
			}
			try {
				sleep(1000);
			} catch (InterruptedException ex) { }
		}
	}
	public void halt(){
		running = false;
	}
	public int random(int min, int max){
		float f = r.nextFloat();
		int value = (int) (f * (max - min));
		value = value + min;
		return value;
	}
}