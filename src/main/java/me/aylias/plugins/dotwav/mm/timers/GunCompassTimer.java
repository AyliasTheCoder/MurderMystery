package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

public class GunCompassTimer extends BukkitRunnable {

  private final Game game;
  ArmorStand stand;

  public GunCompassTimer(Game game, boolean doStuff) {
    this.game = game;

//        Bukkit.getOnlinePlayers()
//              .forEach(player -> {
//                  if (!player.equals(game.murderer) && !player.getGameMode()
//                                                              .equals(GameMode.SPECTATOR)) {
//                      ItemStack compass = new ItemStack(Material.COMPASS);
//                      var meta = compass.getItemMeta();
//
//                      meta.setDisplayName("Gun Compass");
//
//                      player.getInventory()
//                            .addItem(compass);
//                  }
//              });

    if (doStuff) {
      stand = game.detective.getWorld()
                            .spawn(game.getGunLocation(), ArmorStand.class);
      stand.setInvisible(true);
      stand.setCustomName(ChatColor.GREEN + "GUN");
      stand.setCustomNameVisible(true);
    }
  }

  @Override
  public void run() {
//        if (game.mirrorManTimer != null) {
//            if (game.mirrorManTimer.isCancelled()) {
//                Bukkit.getOnlinePlayers()
//                      .forEach(player -> player.setCompassTarget(game.getGunLocation()));
//            }
//        }
  }

  @Override
  public synchronized void cancel() throws IllegalStateException {
    super.cancel();

//        Bukkit.getOnlinePlayers()
//              .forEach(player -> player.getInventory()
//                                       .removeItem(new ItemStack(Material.COMPASS, 1)));

    stand.remove();
  }
}
