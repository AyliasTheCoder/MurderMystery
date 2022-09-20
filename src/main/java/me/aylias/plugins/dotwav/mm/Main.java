package me.aylias.plugins.dotwav.mm;

import me.aylias.plugins.dotwav.mm.teams.CosmeticsListener;
import me.aylias.plugins.dotwav.mm.teams.Game;
import me.aylias.plugins.dotwav.mm.teams.SpecialItems;
import me.aylias.plugins.dotwav.mm.teams.TempListener;
import me.aylias.plugins.dotwav.mm.timers.ParticleTimer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;

public class Main extends JavaPlugin implements Listener {

  CosmeticsListener cosmeticsListener;

  Game game;

  ParticleTimer particleTimer;

  public static ParticleTimer getParticleTimer() {
    return getInstance().particleTimer;
  }

  public static Main getInstance() {
    return getPlugin(Main.class);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!sender.isOp()) return true;

    if (label.equalsIgnoreCase("startgame")) {
      game = new Game();

      getServer().getPluginManager()
                 .registerEvents(game, this);

      Player murderer = null;
      Player detective = null;

      if (args.length > 0) {
        if (Arrays.asList(args)
                  .contains("-m")) {
          murderer = Bukkit.getPlayer(args[Arrays.asList(args)
                                                 .indexOf("-m") + 1]);
        }

        if (Arrays.asList(args)
                  .contains("-d")) {
          detective = Bukkit.getPlayer(args[Arrays.asList(args)
                                                  .indexOf("-d") + 1]);
        }
      }

      game.start(new ArrayList<>(getServer().getOnlinePlayers()), murderer, detective);
    }

    if (label.equalsIgnoreCase("giveitem")) {
      System.out.println(args[0]);
      int index = Integer.parseInt(args[0]);
      ItemStack[] items = new ItemStack[]{SpecialItems.sheriffGun, SpecialItems.murdererKnife};

      var p = (Player) sender;
      p.getInventory()
       .addItem(items[index]);
    }

    if (label.equalsIgnoreCase("stopgame")) {
      game.endGame("Force Quit");
    }

    if (label.equalsIgnoreCase("spawntestnpc")) {
      var npc = CitizensAPI.getNPCRegistry()
                           .createNPC(EntityType.PLAYER, "???");
      var trait = npc.getOrAddTrait(SkinTrait.class);
      trait.setSkinName("__Dreamer__1");
      npc.spawn(((Player) sender).getLocation());
    }

    return true;
  }

  @Override
  public void onEnable() {
    Bukkit.getLogger()
          .setFilter(new ActionbarFilter());
    cosmeticsListener = new CosmeticsListener();
    cosmeticsListener.runTaskTimer(this, 1, 1);

    getCommand("startgame").setExecutor(this);
    getCommand("giveitem").setExecutor(this);
    getCommand("stopgame").setExecutor(this);
    getCommand("spawntestnpc").setExecutor(this);

    getServer().getPluginManager()
               .registerEvents(new TempListener(), this);
    getServer().getPluginManager()
               .registerEvents(this, this);

    var scm = Bukkit.getScoreboardManager()
                    .getMainScoreboard();

    try {
      scm.registerNewTeam("Murderer");
    } catch (Exception ignored) {
    }
    try {
      scm.registerNewTeam("Players");
    } catch (Exception ignored) {
    }

    scm.getTeam("Murderer")
       .setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    scm.getTeam("Players")
       .setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

    scm.getTeam("Murderer")
       .setCanSeeFriendlyInvisibles(false);
    scm.getTeam("Players")
       .setCanSeeFriendlyInvisibles(false);

    particleTimer = new ParticleTimer(null);
    particleTimer.runTaskTimer(this, 1, 1);
  }

  @EventHandler
  public void playerJoin(PlayerJoinEvent e) {
    var p = e.getPlayer();
    p.teleport(new Location(Bukkit.getWorld("main"), 63, 59, -64));
    p.setGameMode(GameMode.ADVENTURE);
    if (game != null) {
      if (!game.isOver) {
        p.setGameMode(GameMode.SPECTATOR);
      }
    }
    p.getInventory()
     .clear();

    var scm = Bukkit.getScoreboardManager()
                    .getMainScoreboard();
    scm.getTeam("Players")
       .addPlayer(p);
  }

  @EventHandler
  public void playerInteract(PlayerInteractEvent e) {
    if (e.getPlayer()
         .getGameMode()
         .equals(GameMode.CREATIVE)) return;

    if (e.getAction()
         .equals(Action.RIGHT_CLICK_BLOCK)) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void playerDamage(EntityDamageEvent e) {
//        e.getEntity().setVisualFire(false);
    if (e.getEntity() instanceof Player p) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void playerLoseHunger(FoodLevelChangeEvent e) {
    e.getEntity()
     .setFoodLevel(20);
    e.getEntity()
     .setSaturation(20);
  }

  @EventHandler
  public void craftingPlace(CraftItemEvent e) {
    if (e.getAction()
         .equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
      e.setCancelled(true);
    }
  }
}
