package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityCooldown extends BukkitRunnable {

    private final Game game;
    int ticks = 15*20;
    int initTicks = 15*20;
    BossBar bar;

    public AbilityCooldown(Game game, int ticks) {
        this.game = game;
        game.throwKnife = false;

        bar = Bukkit.createBossBar(ChatColor.RED + "Ability Cooldown", BarColor.RED, BarStyle.SOLID);
        bar.addPlayer(game.murderer);

        this.ticks = ticks;
        this.initTicks = ticks;
    }

    @Override
    public void run() {
        ticks--;

        if (ticks <= 0) {
            cancel();
        }

//        double progress = (double) ticks / 15d*20d;
        double progress = ticks / (double)initTicks;

//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);
//        System.out.println("" + progress);

        bar.setProgress(progress);


    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        bar.removeAll();
    }
}
