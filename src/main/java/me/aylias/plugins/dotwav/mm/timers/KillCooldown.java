package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

public class KillCooldown extends BukkitRunnable {

    private final Game game;
    int ticks;
    double initTicks;
    BossBar bar;

    public KillCooldown(Game game, int ticks) {
        this.game = game;
        this.ticks = ticks;
        initTicks = ticks;
        bar = Bukkit.createBossBar(ChatColor.RED + "Kill Cooldown", BarColor.RED, BarStyle.SOLID);
        bar.addPlayer(game.murderer);

        game.canKill = false;
    }

    @Override
    public void run() {
        ticks--;

        double progress = (double) ticks / initTicks;

        bar.setProgress(progress);

        if (ticks <= 0) {
            cancel();
        }
    }

    @Override
    public synchronized void cancel() {
        super.cancel();
        bar.removeAll();
        bar.removePlayer(game.murderer);

        game.canKill = true;
        game.murderer.sendMessage("You can kill again");
    }
}
