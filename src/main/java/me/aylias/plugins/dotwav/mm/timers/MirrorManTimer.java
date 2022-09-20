package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.Main;
import me.aylias.plugins.dotwav.mm.teams.Game;
import me.aylias.plugins.dotwav.mm.teams.SpecialItems;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;

public class MirrorManTimer extends BukkitRunnable {

  private final Game game;
  int ticks = 20 * 30;
  BossBar bar;

  int heartbeat = 10;

  int footstep = 10;

  HashMap<Player, ArmorStand> stands = new HashMap<>();

  public MirrorManTimer(Game game) {
    this.game = game;
    bar = Bukkit.createBossBar(ChatColor.RED + "Shadow Strike", BarColor.RED, BarStyle.SOLID);
    bar.addPlayer(game.murderer);


    Bukkit.getOnlinePlayers()
          .forEach(player -> {

            player.setPlayerListName("Citizen");
            player.setDisplayName("Citizen");
            player.getInventory()
                  .addItem(SpecialItems.flashlight);

            player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(100000, 1));

            ItemStack compass = new ItemStack(Material.COMPASS);
            var meta = compass.getItemMeta();

            meta.setDisplayName("Nearest Enemy");

            compass.setItemMeta(meta);

            if (!player.equals(game.murderer))
              player.getInventory()
                    .addItem(compass);
          });


    var scm = Bukkit.getScoreboardManager()
                    .getMainScoreboard();
    var rand = new Random();
    scm.getTeam("Players")
       .getPlayers()
       .forEach(player -> {
         if (player instanceof Player p) {
           if (p.getGameMode()
                .equals(GameMode.SPECTATOR)) {
             return;
           }
//                p.getWorld().spawnParticle(Particle.DRIP_LAVA, p.getLocation().add(rand.nextDouble() * .1, rand.nextDouble() * .1, rand.nextDouble() * .1), 10);
           var stack = new ItemStack(Material.PLAYER_HEAD);

           stack.addEnchantment(Enchantment.BINDING_CURSE, 1);

           //region armour

           var meta = (SkullMeta) stack.getItemMeta();
           meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Question"));

           meta.setDisplayName("???");

           stack.setItemMeta(meta);
           p.getInventory()
            .setHelmet(stack);

           stack = new ItemStack(Material.LEATHER_CHESTPLATE);

           var meta2 = (LeatherArmorMeta) stack.getItemMeta();
           meta2.setColor(Color.BLACK);

           stack.setItemMeta(meta2);

           p.getInventory()
            .setChestplate(stack);

           stack = new ItemStack(Material.LEATHER_LEGGINGS);

           meta2 = (LeatherArmorMeta) stack.getItemMeta();
           meta2.setColor(Color.BLACK);

           stack.setItemMeta(meta2);

           p.getInventory()
            .setLeggings(stack);

           stack = new ItemStack(Material.LEATHER_BOOTS);

           meta2 = (LeatherArmorMeta) stack.getItemMeta();
           meta2.setColor(Color.BLACK);

           stack.setItemMeta(meta2);

           p.getInventory()
            .setBoots(stack);

           p.playSound(p.getLocation(), "mm.sfx.abilityusage", 1, 1);

           p.getInventory()
            .addItem(SpecialItems.flashlight);
           //endregion
         }
       });
  }

  @Override
  public void run() {
    Bukkit.getOnlinePlayers()
          .forEach(player -> {
            if (player.getGameMode()
                      .equals(GameMode.SPECTATOR)) {
              return;
            }
            player.setCompassTarget(game.murderer.getLocation());
          });

    ticks--;

    double progress = (double) ticks / (20d * 30d);

    bar.setProgress(progress);

    if (ticks % 60 == 0) {
      Bukkit.getOnlinePlayers()
            .forEach(p -> {
              p.playSound(p.getLocation(), Sound.AMBIENT_CAVE, .25f, 1);
            });
    }

    footstep--;

    if (footstep == 0) {
      game.murderer.getWorld()
                   .spawnParticle(Particle.SMOKE_NORMAL, game.murderer.getLocation(), 1);
      footstep = 10;
    }

    if (ticks <= 0) {
      cancel();
    }
  }

  @Override
  public synchronized void cancel() {
    super.cancel();
    bar.removeAll();
    bar.removePlayer(game.murderer);

    Bukkit.getOnlinePlayers()
          .forEach(player -> {
            if (!player.equals(game.murderer)) {
              player.showPlayer(Main.getPlugin(Main.class), game.murderer);
            }

            player.setPlayerListName(player.getName());
            player.setDisplayName(player.getName());

            player.removePotionEffect(PotionEffectType.INVISIBILITY);

            player.getInventory().remove(Material.COMPASS);

            player.getInventory()
                  .setHelmet(new ItemStack(Material.AIR));

            player.getInventory()
                  .setChestplate(new ItemStack(Material.AIR));

            player.getInventory()
                  .setLeggings(new ItemStack(Material.AIR));

            player.getInventory()
                  .setBoots(new ItemStack(Material.AIR));


            player.getInventory()
                  .removeItem(SpecialItems.flashlight);

            player.getInventory()
                  .removeItem(new ItemStack(Material.COMPASS, 1));

            player.getInventory()
                  .removeItem(SpecialItems.flashlight);
          });

    Main
            .getParticleTimer().current = Main
            .getParticleTimer().def;

    stands.forEach(((player, armorStand) -> armorStand.remove()));
  }
}
