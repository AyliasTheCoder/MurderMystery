package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

public class DashCooldownBossbar extends BukkitRunnable {

    private final Game game;
    int ticks = 100;
    BossBar bar;

    public DashCooldownBossbar(Game game) {
        this.game = game;
        bar = Bukkit.createBossBar("Dash Cooldown", BarColor.RED, BarStyle.SOLID);
        bar.addPlayer(game.murderer);
    }

    @Override
    public void run() {
        ticks--;

        double progress = (double) ticks / 100d;

        bar.setProgress(progress);

        if (ticks <= 0) {
            cancel();
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        bar.removeAll();
    }
}
