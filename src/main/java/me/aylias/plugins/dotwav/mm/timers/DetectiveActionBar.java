package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

public class DetectiveActionBar extends BukkitRunnable {

  private final Game game;

  public DetectiveActionBar(Game game) {
    this.game = game;
  }

  @Override
  public void run() {

    StringBuilder builder = new StringBuilder();
    if (!game.detective.getGameMode()
                       .equals(GameMode.SPECTATOR)) {
      builder.append(ChatColor.GREEN);
      builder.append("Ammo: ");
      builder.append(ChatColor.WHITE);
      builder.append(game.detectiveAmmo);
      spacer(builder);
      builder.append(ChatColor.GREEN);
      builder.append("Reload: ");

      if (game.detectiveAmmo < 3 && game.notReloading && game.detectiveShoot) {
        builder.append(ChatColor.GREEN);
        builder.append("Available");

      } else if (game.detectiveAmmo >= 3) {
        builder.append(ChatColor.GRAY);
        builder.append("Ammo Full");
      } else if (!game.notReloading) {
        builder.append(ChatColor.YELLOW);
        builder.append("In Use");
      } else if (!game.detectiveShoot) {
        builder.append(ChatColor.GRAY);
        builder.append("Unavailable");
      }
      builder.append(ChatColor.GRAY);
      builder.append(" [Q]");
      spacer(builder);

      builder.append(ChatColor.GREEN);
      builder.append("Shoot: ");

      if (game.detectiveShoot && game.detectiveAmmo != 0) {
        builder.append(ChatColor.GREEN);
        builder.append("Available");
      } else if (game.detectiveAmmo == 0) {
        builder.append(ChatColor.RED);
        builder.append("No Ammo");
      } else if (!game.notReloading) {
        builder.append(ChatColor.GRAY);
        builder.append("Disabled");
      } else {
        builder.append(ChatColor.YELLOW);
        builder.append("On Cooldown");
      }

      spacer(builder);

      builder.append(ChatColor.GREEN);
      builder.append("Coins: ");

      builder.append(ChatColor.YELLOW);
      builder.append(game.getCoins(game.detective));

      spacer(builder);
    }

    builder.append(ChatColor.GREEN);
    builder.append("Players Alive: ");

    builder.append(ChatColor.RED);
    builder.append(game.playersAlive);

    Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "title playernamehere actionbar \"message\""
                    .replace("playernamehere", game.detective.getName())
                    .replace("message", builder.toString())
                          );
  }

  private void spacer(StringBuilder builder) {
    builder.append(ChatColor.DARK_GRAY);
    builder.append(" | ");
  }
}
