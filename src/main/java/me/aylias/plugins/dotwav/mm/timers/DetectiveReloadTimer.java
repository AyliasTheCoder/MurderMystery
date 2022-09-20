package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DetectiveReloadTimer extends BukkitRunnable {

  private final Game game;
  int ticks = 100;
  BossBar bar;
  Player old;

  public DetectiveReloadTimer(Game game) {
    this.game = game;
    game.detectiveShoot = false;
    game.reloading = true;
    game.detective.sendMessage("You will gain 1 ammo in 10 seconds");
    game.notReloading = false;

    bar = Bukkit.createBossBar(ChatColor.RED + "Reloading...", BarColor.RED, BarStyle.SOLID);
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
  public synchronized void cancel() {
    super.cancel();
    game.detectiveShoot = true;
    game.detectiveAmmo++;
    game.reloading = false;
    game.detective.sendMessage(
            ChatColor.GREEN + "(!) Your gun has been reloaded. You have " + game.detectiveAmmo + " ammo now!");
    game.notReloading = true;
    bar.removeAll();
  }
}
