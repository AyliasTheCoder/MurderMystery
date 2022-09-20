package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class MurdererActionBar extends BukkitRunnable {

    private final Game game;

    public MurdererActionBar(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
//        builder.append(ChatColor.GREEN);
//        builder.append("Ammo: ");
//        builder.append(ChatColor.WHITE);
//        builder.append(game.detectiveAmmo);
//        spacer(builder);
//        builder.append(ChatColor.GREEN);
//        builder.append("Reload: ");
//
//        if (game.detectiveAmmo < 3 && game.notReloading) {
//            builder.append(ChatColor.GREEN);
//            builder.append("Available");
//        } else if (game.detectiveAmmo >= 3) {
//            builder.append(ChatColor.GRAY);
//            builder.append("Ammo Full");
//        } else if (!game.notReloading) {
//            builder.append(ChatColor.YELLOW);
//            builder.append("In Use");
//        }
//        spacer(builder);
//
//        builder.append(ChatColor.GREEN);
//        builder.append("Shoot: ");
//
//        if (game.detectiveShoot && game.detectiveAmmo != 0) {
//            builder.append(ChatColor.GREEN);
//            builder.append("Available");
//        } else if (game.detectiveAmmo == 0) {
//            builder.append(ChatColor.RED);
//            builder.append("No Ammo");
//        } else {
//            builder.append(ChatColor.YELLOW);
//            builder.append("On Cooldown");
//        }
//
//        spacer(builder);

        builder.append(ChatColor.GREEN);
        builder.append("Coins: ");

        builder.append(ChatColor.YELLOW);
        builder.append(game.getCoins(game.murderer));

        spacer(builder);

        builder.append(ChatColor.GREEN);
        builder.append("Players Alive: ");

        builder.append(ChatColor.RED);
        builder.append(game.playersAlive);

        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "title playernamehere actionbar \"message\""
                        .replace("playernamehere", game.murderer.getName())
                        .replace("message", builder.toString())
        );
    }

    private void spacer(StringBuilder builder) {
        builder.append(ChatColor.DARK_GRAY);
        builder.append(" | ");
    }
}
