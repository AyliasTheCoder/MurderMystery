package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.Main;
import me.aylias.plugins.dotwav.mm.teams.Game;
import me.aylias.plugins.dotwav.mm.teams.SpecialItems;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class KnifeThrowTimer extends BukkitRunnable {

  private final Game game;
  private final Player murderer;
  public int ticksLeft = 20 * 5 + 10;
  public ArmorStand knife;
  private Vector direction;

  public KnifeThrowTimer(Game game) {
    direction = game.murderer.getLocation()
                             .getDirection();
    knife = (ArmorStand) game.murderer.getWorld()
                                      .spawnEntity(game.murderer.getLocation()
                                                                .clone()
                                                                .add(0, .5, 0), EntityType.ARMOR_STAND);
    this.murderer = game.murderer;
    this.game = game;

    knife.setHeadPose(new EulerAngle(90, 0, 0));
    knife.setHelmet(SpecialItems.murdererKnife);

//        knife.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(10000, 0));
    knife.setInvisible(true);
    knife.setVisible(false);
    murderer.getInventory()
            .remove(Material.IRON_SWORD);
    murderer.getInventory()
            .addItem(SpecialItems.returnToKnife);
  }

  @Override
  public void run() {
    var dest = knife.getLocation()
                    .add(direction.normalize()
                                  .multiply(ticksLeft > 0 ? .4 : .8));
    var str = dest.getWorld()
                  .getBlockAt(dest)
                  .getType()
                  .toString()
                  .toLowerCase();
    if (str.contains("air") || str.contains("water") || str.contains("lava") || str.contains("carpet")) {
      knife.teleport(dest);
    } else {
      ticksLeft = 0;
    }

    knife.setHeadPose(knife.getHeadPose()
                           .add(0, .8, 0));

    Bukkit.getOnlinePlayers()
          .forEach(p -> {
            if (p.equals(murderer) || p.getGameMode()
                                       .equals(GameMode.SPECTATOR)) return;

            if (p.getLocation()
                 .distance(knife.getLocation()) < .3) {
              if (p.equals(game.detective)) {
                game.killDetective();
              } else {
                game.players.remove(p);
                p.setGameMode(GameMode.SPECTATOR);
                game.playersAlive--;
                game.detectPlayersLose();
              }

              murderer.getWorld()
                      .playSound(murderer.getLocation(), Sound.BLOCK_SLIME_BLOCK_HIT, 1, 1);

              Bukkit.getOnlinePlayers()
                    .forEach(pl ->
                                     pl.playSound(
                                             pl.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1)
                            );
            }


          });

    ticksLeft--;
    if (ticksLeft <= 0) {
      var loc = murderer.getLocation();
      var loc2 = knife.getLocation();
      direction = new Vector(loc.getX(), loc.getY(), loc.getZ()).subtract(
              new Vector(loc2.getX(), loc2.getY(), loc2.getZ()));
      if (loc.distance(loc2) < .3f) {
        cancel();
      }
    }
  }

  @Override
  public synchronized void cancel() throws IllegalStateException {
    super.cancel();
    murderer.getInventory()
            .remove(Material.ENDER_PEARL);
    murderer.getInventory()
            .addItem(SpecialItems.murdererKnife);
    knife.remove();
    game.abilityCooldown = new AbilityCooldown(game, 15 * 20);
    game.abilityCooldown.runTaskTimer(Main.getInstance(), 1, 1);
  }
}
