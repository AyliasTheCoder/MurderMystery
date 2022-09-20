package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DetectiveCooldownTimer extends BukkitRunnable {

    private Game game;
    int ticks = 100;
    BossBar bar;
    Player old;

    public DetectiveCooldownTimer(Game game) {
        this.game = game;
        game.detectiveShoot = false;
        game.detectiveAmmo--;
        game.detective.sendMessage("You have " + game.detectiveAmmo + " ammo left. Press q to reload the gun.");
        game.cooldown = true;

        bar = Bukkit.createBossBar(ChatColor.RED + "Cooling Down...", BarColor.RED, BarStyle.SOLID);
        bar.addPlayer(game.detective);

        old = game.detective;
    }

    @Override
    public void run() {
        if (!old.equals(game.detective)) {
            bar.removeAll();
            bar.addPlayer(game.detective);
            old = game.detective;
        }

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

        game.detectiveShoot = true;
        game.cooldown = false;
        game.detective.sendMessage("You can shoot again");

        bar.removeAll();
    }
}
