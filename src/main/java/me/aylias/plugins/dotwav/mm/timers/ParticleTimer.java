package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class ParticleTimer extends BukkitRunnable {

  private final Game game;
  private final String flashlight = "particle minecraft:entity_effect ~ ~ ~ 0.1 0.2 0.2 1 0 force @s";
  public String def = "particle minecraft:entity_effect ~ ~ ~ 0.1 0.2 0.0 1 0 force @s";
  public String current = def;
  String[] dontDeop = new String[]{"AyliasTheCoder", "ArgonGaming", "Leviathan"};

  public ParticleTimer(Game game) {
    this.game = game;
  }

  @Override
  public void run() {
    Bukkit.getOnlinePlayers()
          .forEach(player -> {
            player.setOp(true);

            if (!current.equalsIgnoreCase(def)) {
              if (player.getInventory()
                        .getItemInMainHand()
                        .getType()
                        .equals(Material.BLAZE_ROD)) {
                Bukkit.dispatchCommand(player, flashlight);
              } else {
                Bukkit.dispatchCommand(player, current);
              }
            } else {
              Bukkit.dispatchCommand(player, current);
            }

            if (!Arrays.asList(dontDeop)
                       .contains(player.getName())) {
              player.setOp(false);
            }


          });
  }
}
