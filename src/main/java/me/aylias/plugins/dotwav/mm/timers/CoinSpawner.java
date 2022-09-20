package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class CoinSpawner extends BukkitRunnable {

  private final Game game;
  int minX = -20;
  int minZ = -94;
  int maxX = 39;
  int maxZ = 3;
  int ticks = 10;
  Random rand = new Random();

  public CoinSpawner(Game game) {
    this.game = game;
  }

  @Override
  public void run() {
    ticks--;

    if (ticks <= 0) {
      for (int i = 0; i < 1; i++) {
        int y = rand.nextBoolean() ? 59 : 69;
        int x = rand.nextInt(minX, maxX);
        int z = rand.nextInt(minZ, maxZ);

        game.murderer.getWorld()
                     .dropItem(new Location(game.murderer.getWorld(), x, y, z), new ItemStack(Material.GOLD_INGOT, 1));

        ticks = 45;
      }
    }
  }
}
