package me.aylias.plugins.dotwav.mm.teams;

import dev.sergiferry.playernpc.api.NPC.Global;
import dev.sergiferry.playernpc.api.NPCLib;
import me.aylias.plugins.dotwav.mm.Main;
import me.aylias.plugins.dotwav.mm.timers.*;
import me.aylias.plugins.dotwav.mm.timers.managers.CoinSpawnerManager;
import me.aylias.plugins.dotwav.mm.timers.managers.DetectiveActionBarManager;
import me.aylias.plugins.dotwav.mm.timers.managers.KillCooldownManager;
import me.aylias.plugins.dotwav.mm.timers.managers.KnifeThrowTimerManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.util.PlayerAnimation;
import net.minecraft.server.level.EntityPlayer;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game implements Listener {

  // region Fields
  private final Location[] playerSpawns;
  private final Location lobbySpawn;
  public boolean canKill;
  public boolean notReloading = true;
  public Player murderer;
  public Player detective;
  public AbilityCooldown abilityCooldown;
  public int detectiveAmmo = 3;
  public boolean detectiveShoot = true;
  public boolean reloading = false;
  public boolean cooldown = false;
  public boolean throwKnife = true;
  public boolean dash = true;
  public ArrayList<Player> players;
  public int playersAlive;
  public World world;
  public HidePlayerList hidePlayerList;
  public MirrorManTimer mirrorManTimer;
  public boolean isOver = false;
  Random rand = new Random();
  DetectiveCooldownTimer detectiveCooldownTimer;
  DetectiveReloadTimer detectiveReloadTimer;
  DashCooldown dashCooldown;
  KillCooldownManager killCooldown;
  DetectiveActionBarManager detectiveActionBar;
  PlayerActionBar playerActionBar;
  MurdererActionBar murdererActionBar;
  CoinSpawnerManager coinSpawner;
  boolean murdererAbility = true;
  KnifeThrowTimerManager knifeThrowTimer;
  HashMap<Player, Integer> coins = new HashMap<>();
  Location gunDrop;
  GunCompassTimer gunCompassTimer = new GunCompassTimer(this, false);
  List<NPC> npcs = new ArrayList<>();
  //endregion

  public Game() {
    // Initialize the places in which players will spawn in the map when the game is started.
    world = Bukkit.getWorld("main");

    world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
    world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
    world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
    world.setDifficulty(Difficulty.PEACEFUL);

    playerSpawns = new Location[]{new Location(world, 0, 59, -64), new Location(world, 0, 59, -64)};
    // Initialize the lobby spawnpoint
    lobbySpawn = new Location(world, 63, 59, -64);

    world.getEntities()
         .forEach(entity -> {
           if (entity instanceof ArmorStand stand) {
             if (stand.getName()
                      .toLowerCase()
                      .contains("slot")) {
               stand.remove();
             }
           }
         });

    world.setStorm(true);
    world.setWeatherDuration(100000);

    makeSlotMachine();

    hidePlayerList = new HidePlayerList(Main.getInstance());

    new BukkitRunnable() {
      @Override
      public void run() {
        if (isOver) {
          cancel();
          return;
        }

        npcs.forEach(npc -> {
          PlayerAnimation.SLEEP.play((Player) npc.getEntity());
        });
      }
    }.runTaskTimer(Main.getInstance(), 0, 1);
  }

  private void makeSlotMachine() {
    var stand = world.spawn(new Location(world, 0.5, 59, -64.5), ArmorStand.class);

    stand.setRotation(-90, 0);

//    stand.setCustomName("§a§lSlot Machine");

//    stand.setCustomNameVisible(true);
    stand.setInvulnerable(true);

    stand.setCustomName("" + ChatColor.BOLD + ChatColor.GOLD + "Right click to use! " + ChatColor.RESET + ChatColor.GRAY + "(5 coins)");
    stand.setCustomNameVisible(true);

    stand.setVisible(false);

    stand.setHelmet(new ItemStack(Material.SPONGE));
//    stand.setHeadPose(new EulerAngle(0, -90, 0));
  }

  public void start(ArrayList<Player> players, @Nullable Player murderer, @Nullable Player detective) {
    this.players = players;
    chooseMurdererAndDetective(murderer, detective);
    spawnPlayersInMap();
    setPlayersToAdventure();
    clearPlayersInventory();
    givePlayersItems();
    initalMurderCooldown();
    tellPlayerRoles();

    detectiveActionBar = new DetectiveActionBarManager(this);
    playerActionBar = new PlayerActionBar(this);
    murdererActionBar = new MurdererActionBar(this);

    playerActionBar.runTaskTimer(Main.getInstance(), 1, 1);
    murdererActionBar.runTaskTimer(Main.getInstance(), 1, 1);
    detectiveActionBar.run();

    knifeThrowTimer = new KnifeThrowTimerManager(this);
    coinSpawner = new CoinSpawnerManager(this);

    var scm = Bukkit.getScoreboardManager()
                    .getMainScoreboard();
    scm.getTeam("Players")
       .removePlayer(this.murderer);
    scm.getTeam("Murderer")
       .addPlayer(this.murderer);
  }

  private void chooseMurdererAndDetective(@Nullable Player m, @Nullable Player d) {
    if (m == null) murderer = players.remove(rand.nextInt(players.size()));
    else {
      murderer = m;
      players.remove(m);
    }

    if (d == null) detective = players.remove(rand.nextInt(players.size()));
    else {
      detective = d;
      players.remove(d);
    }

    playersAlive = players.size() + 1;

    players.add(murderer);
    players.add(detective);
  }

  private void spawnPlayersInMap() {
    players.forEach(p -> p.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(100, 0)));
    players.forEach(p -> p.teleport(playerSpawns[rand.nextInt(playerSpawns.length)]));

    players.remove(murderer);
    players.remove(detective);
  }

  private void setPlayersToAdventure() {
    Bukkit.getOnlinePlayers()
          .forEach(p -> p.setGameMode(GameMode.ADVENTURE));
  }

  private void clearPlayersInventory() {
    Bukkit.getOnlinePlayers()
          .forEach(p -> p.getInventory()
                         .clear());
  }

  private void givePlayersItems() {
    murderer.getInventory()
            .addItem(SpecialItems.murdererKnife);
    murderer.getInventory()
            .addItem(SpecialItems.mirrorMan);
    detective.getInventory()
             .addItem(SpecialItems.sheriffGun);

    murderer.getInventory()
            .setHeldItemSlot(8);
    detective.getInventory()
             .setHeldItemSlot(8);
  }

  private void initalMurderCooldown() {
    killCooldown = new KillCooldownManager(this);
    killCooldown.run();
  }

  private void tellPlayerRoles() {
    murderer.sendTitle(ChatColor.RED + "You are the MURDERER", "", 10, 40, 10);
    detective.sendTitle(ChatColor.BLUE + "You are the DETECTIVE", "", 10, 40, 10);

    players.forEach(p -> p.sendTitle(ChatColor.GREEN + "You are a CIVILIAN", "", 10, 40, 10));
  }

  @EventHandler
  public void playerChat(AsyncPlayerChatEvent e) {
    if (e.getPlayer()
         .getGameMode()
         .equals(GameMode.SPECTATOR)) {
      e.setCancelled(true);

      Bukkit.getOnlinePlayers()
            .forEach(player -> {
              if (player.getGameMode()
                        .equals(GameMode.SPECTATOR)) {
                player.sendMessage("<###> MMM".replace("###", e.getPlayer()
                                                               .getDisplayName())
                                              .replace("MMM", e.getMessage()));
              }
            });
      return;
    }

    if (mirrorManTimer == null) return;
    if (mirrorManTimer.isCancelled()) return;

    e.setCancelled(true);

    Bukkit.broadcastMessage("<?????> " + e.getMessage());
  }

  @EventHandler
  public void playerInteractEntity(PlayerInteractAtEntityEvent e) {
    if (e.getRightClicked() instanceof ArmorStand stand) {
      e.setCancelled(true);

      var coinCount = 0;

      if (coins.containsKey(e.getPlayer())) coinCount = coins.get(e.getPlayer());

      if (!stand.getName()
                .toLowerCase()
                .contains("right")) {
        return;
      }


      if (coinCount >= 5) {

        e.getPlayer()
         .playSound(stand.getLocation(), "mm:sfx.slotmachine", 1, 1);

        var items = new ItemStack[]{SpecialItems.camera, SpecialItems.camera, SpecialItems.knockbackStick, SpecialItems.knockbackStick, SpecialItems.knockbackStick, SpecialItems.speedPotion, SpecialItems.invisibilityPotion, SpecialItems.jumpPotion,};

        e.getPlayer()
         .getInventory()
         .addItem(items[rand.nextInt(items.length)]);

        coinCount -= 5;
        coins.put(e.getPlayer(), coinCount);
        e.getPlayer()
         .sendMessage(ChatColor.GREEN + "(!) You have " + coinCount + " coins left");
      } else {
        e.getPlayer()
         .sendMessage(ChatColor.RED + "(!) You need 5 coins to use the slot machine");
      }


    }
  }

  @EventHandler
  public void playerHitPlayer(EntityDamageByEntityEvent e) {
    e.setCancelled(true);

    for (NPC npc : npcs) {
      if (e.getEntity().getUniqueId().equals(npc.getEntity().getUniqueId())) {
        return;
      }
    }

    if (e.getEntity() instanceof EntityPlayer) return;
    if (e.getDamager() instanceof EntityPlayer) return;

    if (!(e.getDamager() instanceof Player damager) || !(e.getEntity() instanceof Player attacked)) return;

    Location bloodLocation = e.getEntity()
                              .getLocation();

    bloodLocation.setY(bloodLocation.getY() + (e.getEntity()
                                                .getHeight() / 1.5));

    Double particleCount = 500d;
    Double particleMultiplier = 5.0;
    Double particleMax = 50.0;
    Double particleRange = 0.4;

    if (e.getDamager()
         .equals(murderer)) {
      if (((Player) e.getDamager()).getInventory()
                                   .getItemInMainHand()
                                   .getItemMeta()
                                   .getCustomModelData() == SpecialItems.murdererKnife.getItemMeta()
                                                                                      .getCustomModelData() && !damager.getInventory()
                                                                                                                       .getItemInMainHand()
                                                                                                                       .getType()
                                                                                                                       .equals(Material.STICK)) {
        if (!canKill) {
          murderer.playSound(murderer.getLocation(), "mm:sfx.abilityerrorv2", 1, 1);
          return;
        }

        e.getEntity()
         .getWorld()
         .spawnParticle(Particle.BLOCK_CRACK, bloodLocation, particleCount.intValue(), particleRange,
                        particleRange, particleRange, Material.REDSTONE_BLOCK.createBlockData()
                       );

        ((Player) e.getEntity()).getInventory()
                                .clear();

        if (mirrorManTimer != null) {
          if (!mirrorManTimer.isCancelled()) {
            mirrorManTimer.cancel();
          }
        }

        if (e.getEntity()
             .equals(detective)) {
          killDetective();
        } else {
          players.remove((Player) e.getEntity());
          ((Player) e.getEntity()).setGameMode(GameMode.SPECTATOR);
          playersAlive--;
          detectPlayersLose();
        }

        ((Player) e.getEntity()).sendTitle("You Died!", "", 10, 40, 10);

        murderer.getWorld()
                .playSound(murderer.getLocation(), "mm:sfx.playerkill", 1, 1);

        killCooldown.run();

        dropCoins(attacked);
        spawnDownedNPC(attacked);
      }
    }

    if (damager.getInventory()
               .getItemInMainHand()
               .getItemMeta()
               .hasCustomModelData()) {
      if (damager.getInventory()
                 .getItemInMainHand()
                 .getItemMeta()
                 .getCustomModelData() == SpecialItems.knockbackStick.getItemMeta()
                                                                     .getCustomModelData() && damager.getInventory()
                                                                                                     .getItemInMainHand()
                                                                                                     .getType()
                                                                                                     .equals(Material.STICK)) {
        var direction = damager.getLocation()
                               .getDirection()
                               .clone()
                               .normalize()
                               .multiply(1)
                               .setY(.5);
        attacked.setVelocity(direction);
        damager.getInventory()
               .getItemInMainHand()
               .setAmount(damager.getInventory()
                                 .getItemInMainHand()
                                 .getAmount() - 1);
      }

    }
  }

  private void dropCoins(Player player) {
    var rand = new Random();
    for (int i = 0; i < getCoins(player); i++) {
      player
              .getWorld()
              .dropItem(player.getEyeLocation(), new ItemStack(Material.GOLD_INGOT))
              .setVelocity(new Vector(
                      (rand.nextDouble() - .5) / 3,
                      (rand.nextDouble() - .5) / 3,
                      (rand.nextDouble() - .5) / 3
              ));
    }
  }

  public void killDetective() {
    detective.setGameMode(GameMode.SPECTATOR);
    detective.getWorld()
             .dropItemNaturally(detective.getLocation(), SpecialItems.sheriffGun);
    gunDrop = detective.getLocation()
                       .clone();
    playersAlive -= 1;
    gunCompassTimer.runTaskTimer(Main.getInstance(), 1, 1);
    if (!detectPlayersLose()) {
      Bukkit.getOnlinePlayers()
            .forEach(player -> {
              player.sendTitle("The " + ChatColor.GREEN + "DETECTIVE" + ChatColor.WHITE + " died!",
                               ChatColor.GRAY + "Find their gun to become the detective!", 10, 60, 10
                              );
            });
    }
  }

  public boolean detectPlayersLose() {
    if (playersAlive < 1) {
      murdererWins();
      return true;
    }

    return false;
  }

  private void murdererWins() {
    isOver = true;
    Bukkit.getOnlinePlayers()
          .forEach(player -> {
            player.playSound(player.getLocation(), "mm:sfx.murderlaughv2", 1, 1);
          });
    endGame(ChatColor.RED + "Murderer wins");
  }

  private void detectiveWins() {
    isOver = true;
    Bukkit.getOnlinePlayers()
          .forEach(player -> {
            player.playSound(player.getLocation(), "mm:sfx.win", 1, 1);
          });
    endGame(ChatColor.GREEN + "Players win");
  }

  public void tpToLobby(String message) {
    Bukkit.getOnlinePlayers()
          .forEach(p -> {
            p.setGameMode(GameMode.ADVENTURE);
            p.teleport(lobbySpawn);
            p.sendTitle(ChatColor.GREEN + "Thanks for playing!", "", 20, 40, 20);
            p.getInventory()
             .clear();
          });
  }

  public int getCoins(Player player) {
    if (!coins.containsKey(player)) {
      coins.put(player, 0);
    }

    return coins.get(player);
  }

  @EventHandler
  public void hangingBreakEvent(HangingBreakByEntityEvent e) {
    e.setCancelled(true);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
//        if (e.getAction().equals(Action.))

    if (e.getItem().getType().equals(Material.ENDER_PEARL)) {
      e.setCancelled(true);
    }

    if (e.getItem() == null) {
      return;
    }

    if (e.getItem()
         .getType()
         .equals(Material.DIAMOND)) {
      e.getPlayer()
       .getInventory()
       .remove(new ItemStack(Material.DIAMOND, 1));
      e.getItem()
       .setAmount(e.getItem()
                   .getAmount() - 1);
      Bukkit.getOnlinePlayers()
            .forEach(player -> {
              if (player.getLocation()
                        .distance(e.getPlayer()
                                   .getLocation()) < 10) {
                if (!player.equals(e.getPlayer()))
                  flashBang(player);
                player.playSound(player.getLocation(), "mm:sfx.camerashootv2", 1, 1);
              }
            });
    }

    if (e.getPlayer()
         .equals(detective)) {
      if (e.getAction()
           .equals(Action.RIGHT_CLICK_AIR) || e.getAction()
                                               .equals(Action.RIGHT_CLICK_BLOCK)) {

        if (e.getItem()
             .getItemMeta()
             .hasCustomModelData()) {
          if (e.getItem()
               .getItemMeta()
               .getCustomModelData() == SpecialItems.sheriffGun.getItemMeta()
                                                               .getCustomModelData()) {
            if (e.getItem()
                 .isSimilar(SpecialItems.sheriffGun)) {

              if (reloading) {
                detective.sendMessage(ChatColor.RED + "(!) You are reloading right now!");
                detective.playSound(detective.getLocation(), "mm:sfx.abilityerrorv2", 1, 1);
                return;
              }

              if (cooldown) {
                detective.sendMessage(ChatColor.RED + "(!) You are on cooldown right now!");
                detective.playSound(detective.getLocation(), "mm:sfx.abilityerrorv2", 1, 1);
                return;
              }

              if (detectiveAmmo <= 0) {
                detective.sendMessage(ChatColor.RED + "(!) You are out of ammo! Reload with q!");
                detective.playSound(detective.getLocation(), "mm:sfx.abilityerrorv2", 1, 1);
                return;
              }

              attemptDetectiveShoot();
            }
          }
        }
      }
      return;
    }

    if (e.getPlayer()
         .equals(murderer)) {
      if (e.getItem() != null) {
        e.setCancelled(true);
        murdererInteract(e);
//                else if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
//                    if (knifeThrowTimer != null && !knifeThrowTimer.isCancelled()) {
//                        knifeThrowTimer.ticksLeft = -1;
//                        murderer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 0, true, false, false));
//                        murderer.teleport(knifeThrowTimer.knife);
//                    }
//                }
//            } else if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
//                if (knifeThrowTimer != null && !knifeThrowTimer.isCancelled()) {
//                    knifeThrowTimer.ticksLeft = -1;
//                    murderer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 0, true, false, false));
//                    murderer.teleport(knifeThrowTimer.knife);
//                }
      }


    }
  }

  private void flashBang(Player player) {
    player.sendTitle("\uF116", "\uF116", 5, 10, 20 * 8);
    player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(5 + 10 + (20 * 8), 0));
  }

  private void attemptDetectiveShoot() {
    detective.getWorld()
             .playSound(detective.getLocation(), "mm:sfx.gunshot", 1, 1);

    var arrowLoc = detective.getEyeLocation();
    arrowLoc.add(detective.getLocation()
                          .getDirection()
                          .normalize()
                          .multiply(.1));
    var arrow = detective.getWorld()
                         .spawnArrow(arrowLoc, detective.getLocation()
                                                        .getDirection()
                                                        .normalize()
                                                        .multiply(250), 1, 1);
    arrow.setVelocity(detective.getLocation()
                               .getDirection()
                               .normalize()
                               .multiply(5));
    detectiveCooldownTimer = new DetectiveCooldownTimer(this);
    detectiveCooldownTimer.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
  }

  private void murdererInteract(PlayerInteractEvent e) {
    if (e.getItem()
         .getItemMeta()
         .getCustomModelData() == SpecialItems.returnToKnife.getItemMeta()
                                                            .getCustomModelData()) {
      attemptMurdererReturn(e);
    }

    if (e.getItem()
         .getItemMeta()
         .getCustomModelData() == SpecialItems.mirrorMan.getItemMeta()
                                                        .getCustomModelData()) {
      if (attemptMirrorMan(e)) return;
    }

    if (e.getItem()
         .getItemMeta()
         .getCustomModelData() == SpecialItems.murdererKnife.getItemMeta()
                                                            .getCustomModelData()) {
      attemptMurdererThrow(e);
    }
  }

  private void attemptMurdererReturn(PlayerInteractEvent e) {
    if (SpecialItems.returnToKnife.isSimilar(e.getItem())) {
      if (e.getAction()
           .equals(Action.RIGHT_CLICK_AIR) || e.getAction()
                                               .equals(Action.RIGHT_CLICK_BLOCK)) {
        if (knifeThrowTimer.isRunning()) {
          murderer.addPotionEffect(
                  new PotionEffect(PotionEffectType.BLINDNESS, 25, 0, true, false, false));
          murderer.teleport(((KnifeThrowTimer) knifeThrowTimer.timer).knife);
          knifeThrowTimer.cancel();
        }
      }
    }
  }

  private boolean attemptMirrorMan(PlayerInteractEvent e) {
    if (SpecialItems.mirrorMan.isSimilar(e.getItem())) {
      if (killCooldown.isRunning()) return true;
      if (abilityCooldown != null) {
        if (!abilityCooldown.isCancelled()) return true;
      }

//                        if (mirrorManTimer == null) mirrorManTimer = new MirrorManTimer(this);
//                        else if (mirrorManTimer.isCancelled()) mirrorManTimer = new MirrorManTimer(this);
//                        mirrorManTimer.runTaskTimer(Main.getPlugin(Main.class), 1, 1);

      new LightsOutAbility(this).runTaskTimer(Main.getPlugin(Main.class), 1, 1);
      murderer.playSound(murderer.getLocation(), "mm:sfx.abilityusage", 1, 1);
    }
    return false;
  }

  private void attemptMurdererThrow(PlayerInteractEvent e) {
    if (SpecialItems.murdererKnife.isSimilar(e.getItem())) {
      if (abilityCooldown != null) {
        if (!abilityCooldown.isCancelled()) return;
      }

      if (e.getAction()
           .equals(Action.RIGHT_CLICK_AIR) || e.getAction()
                                               .equals(Action.RIGHT_CLICK_BLOCK)) {
        throwKnife();
      }
    }
  }

  private void throwKnife() {
    murderer.playSound(murderer.getLocation(), "mm:sfx.abilityusage", 1, 1);
    knifeThrowTimer.run();
  }

  @EventHandler
  public void onPlayerDamaged(EntityDamageEvent e) {
    if (!(e.getEntity() instanceof Player player)) return;

    if (e.getCause()
         .equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
      if (player.equals(detective)) {
        e.setCancelled(true);
        return;
      }

      if (player.equals(murderer)) {
        detectiveWins();
        e.setCancelled(true);
        murderer.setGameMode(GameMode.SPECTATOR);
        return;
      }

      if (players.contains(player)) {
        detectiveMisfire(player);
      }
      e.setCancelled(true);
    }

  }

  @EventHandler
  public void onItemPickUp(PlayerPickupItemEvent e) {
    if (e.getItem()
         .getItemStack()
         .getItemMeta()
         .getDisplayName()
         .toLowerCase()
         .contains("gun")) {
      gunPickup(e);
    }

    if (e.getItem()
         .getItemStack()
         .getType()
         .equals(Material.GOLD_INGOT)) {
      playerPickupCoin(e);
    }
  }

  private void gunPickup(PlayerPickupItemEvent e) {
    if (players.contains(e.getPlayer())) {
      players.remove(e.getPlayer());
      detective = e.getPlayer();
      Bukkit.getOnlinePlayers()
            .forEach(player -> player.sendTitle("Someone found the gun!", "", 10, 40, 10));
      e.getPlayer()
       .playSound(e.getPlayer()
                   .getLocation(), "mm:sfx.gun_pickup", 1, 1);
      gunCompassTimer.cancel();
      gunCompassTimer = new GunCompassTimer(this, true);
    } else if (e.getPlayer()
                .equals(murderer)) {
      e.setCancelled(true);
    } else {
      e.setCancelled(true);
    }
  }

  private void playerPickupCoin(PlayerPickupItemEvent e) {
    e.getItem()
     .remove();
    e.setCancelled(true);
    if (!coins.containsKey(e.getPlayer())) {
      coins.put(e.getPlayer(), 1);
    } else {
      coins.put(e.getPlayer(), coins.get(e.getPlayer()) + 1);
    }

    e.getPlayer()
     .playSound(e.getPlayer()
                 .getLocation(), "mm:sfx.coinv2", 1, 1);
  }

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent e) {
    if (e.getPlayer()
         .equals(detective)) {
      if (e.getItemDrop()
           .getItemStack()
           .getType()
           .equals(Material.CARROT_ON_A_STICK)) {
        e.setCancelled(true);
        detectiveReload();
      }
      return;
    }

    if (e.getPlayer()
         .equals(murderer)) {
      if (e.getItemDrop()
           .getItemStack()
           .getItemMeta()
           .getCustomModelData() == SpecialItems.murdererKnife.getItemMeta()
                                                              .getCustomModelData()) {
        if (SpecialItems.murdererKnife.isSimilar(e.getItemDrop()
                                                  .getItemStack())) {
          e.setCancelled(true);
          murdererDash();
        }
      }
    }

    e.setCancelled(true);
  }

  private void detectiveReload() {
    if (detectiveAmmo < 3 && notReloading && !cooldown) {
      detectiveReloadTimer = new DetectiveReloadTimer(this);
      detectiveReloadTimer.runTaskTimer(Main.getPlugin(Main.class), 1, 1);
      detective.playSound(detective.getLocation(), "mm:sfx.gun_reload", 1, 1);
    } else if (detectiveAmmo >= 3) {
      detective.sendMessage(ChatColor.RED + "(!) You can't reload because you are full on ammo!");
      // play error sound to the detective
      detective.playSound(detective.getLocation(), "mm:sfx.abilityerrorv2", 1, 1);
    } else if (!notReloading) {
      detective.sendMessage(ChatColor.RED + "(!) You can't reload because you are already reloading!");
      // play error sound to the detective
      detective.playSound(detective.getLocation(), "mm:sfx.abilityerrorv2", 1, 1);
    } else {
      detective.sendMessage(ChatColor.RED + "(!) You can't reload because you are on cooldown!");
      // play error sound to the detective
      detective.playSound(detective.getLocation(), "mm:sfx.abilityerrorv2", 1, 1);
    }
  }

  private void murdererDash() {
    if (!dash) {
      murderer.playSound(murderer.getLocation(), "mm:sfx.abilityerrorv2", 1, 1);
      return;
    }

    murderer.addPotionEffect(PotionEffectType.SPEED.createEffect(8, 10));

    dashCooldown = new DashCooldown(this);
    dashCooldown.runTaskLater(Main.getPlugin(Main.class), 100);
  }

  private void detectiveMisfire(Player hit) {
    players.remove(hit);
    detective.setGameMode(GameMode.SPECTATOR);

    playersAlive--;
    detectPlayersLose();

    detective = hit;
    detective.getInventory()
             .addItem(SpecialItems.sheriffGun);
    detective.sendMessage(ChatColor.GREEN + "(!) You were shot by the detective, and have now become the detective.");
  }

  @EventHandler
  public void playerLeave(PlayerQuitEvent e) {
    if (e.getPlayer()
         .equals(murderer)) {
      murdererLeave();
    }
  }

  private void murdererLeave() {
    endGame(ChatColor.GREEN + "The murderer quit");
  }

  public void endGame(String message) {
    isOver = true;
    CitizensAPI.getNPCRegistries().forEach(npcr -> {
      npcr.sorted().forEach(NPC::destroy);
    });

    try {
      NPCLib.getInstance()
            .getAllGlobalNPCs()
            .forEach(Global::destroy);
    } catch (Exception ignored) {
    }
    try {
      clearPlayersInventory();
    } catch (Exception ignored) {
    }
    try {
      startEndingTimer(message);
    } catch (Exception ignored) {
    }
    try {
      unregisterEvents();
    } catch (Exception ignored) {
    }
    try {
      clearExtraEntities();
    } catch (Exception ignored) {
    }
    try {
      endAllTimers();
    } catch (Exception ignored) {
    }
    try {
      unhideMurderer();
    } catch (Exception ignored) {
    }
    try {
      killAllCoins();
    } catch (Exception ignored) {
    }
    try {
      clearBossBars();
    } catch (Exception ignored) {
    }

    try {
      var scm = Bukkit.getScoreboardManager()
                      .getMainScoreboard();
      scm.getTeam("Murderer")
         .removePlayer(this.murderer);
      scm.getTeam("Players")
         .addPlayer(this.murderer);
    } catch (Exception ignored) {
    }

    try {
      collectGarbage();
    } catch (Exception ignored) {
    }
  }

  private void startEndingTimer(String message) {
    new EndingTimer(this, message).runTaskLater(Main.getInstance(), 10 * 20);

    Bukkit.getOnlinePlayers()
          .forEach(player -> {
            player.sendTitle(message, "", 20, 40, 20);
          });
  }

  private void unregisterEvents() {
    HandlerList.unregisterAll(this);
  }

  private void clearExtraEntities() {
    Bukkit.getWorld("main")
          .getEntities()
          .forEach(entity -> {
            if (entity instanceof Item || entity instanceof ArmorStand || entity instanceof Arrow) {
              entity.remove();
            }
          });
  }

  private void endAllTimers() {
    if (detectiveReloadTimer != null) {
      detectiveReloadTimer.cancel();
      detectiveReloadTimer = null;
    }

    if (detectiveCooldownTimer != null) {
      detectiveCooldownTimer.cancel();
      detectiveCooldownTimer = null;
    }

    if (knifeThrowTimer != null) {
      knifeThrowTimer.cancel();
      knifeThrowTimer = null;
    }

    if (mirrorManTimer != null) {
      mirrorManTimer.cancel();
      mirrorManTimer = null;
    }

    try {
      detectiveActionBar.cancel();
    } catch (Exception ignored) {
    }
    try {
      playerActionBar.cancel();
    } catch (Exception ignored) {
    }

    try {
      murdererActionBar.cancel();
    } catch (Exception ignored) {
    }

    try {
      coinSpawner.cancel();
    } catch (Exception ignored) {
    }

    try {
      if (!gunCompassTimer.isCancelled()) {
        gunCompassTimer.cancel();
      }
    } catch (Exception ignored) {
    }

    try {
      mirrorManTimer.cancel();
    } catch (Exception ignored) {
    }
  }

  private void unhideMurderer() {
    Bukkit.getOnlinePlayers()
          .forEach(p -> {
            p.showPlayer(Main.getPlugin(Main.class), murderer);
          });
  }

  private void killAllCoins() {

  }

  private void clearBossBars() {
    Bukkit.getBossBars()
          .forEachRemaining((BossBar::removeAll));
  }

  private void collectGarbage() {
    System.gc();
  }

  public Location getGunLocation() {
    return gunDrop;
  }

  @EventHandler
  public void playerDrinkPotion(PlayerItemConsumeEvent e) {
    new BukkitRunnable() {

      @Override
      public void run() {
        e.getPlayer()
         .getInventory()
         .remove(Material.GLASS_BOTTLE);
      }
    }.runTaskLater(Main.getInstance(), 2);
  }

  public void spawnDownedNPC(Player player) {
    if (isOver) return;

//    // If the NPC already exists then remove the old one.
////        if (this.playerNPCMap.containsKey(player.getUniqueId())) {
////            this.destroyDownedNPC(player);
////        }
//
    var npc = CitizensAPI.getNPCRegistry()
                         .createNPC(EntityType.PLAYER, RandomStringUtils.random(16, true, true));
//    var craftPlayer = (CraftPlayer) player;
//    var texture = (Property) craftPlayer.getHandle()
//                                        .getGameProfile()
//                                        .getProperties()
//                                        .get("textures")
//                                        .toArray()[0];

    npc.getOrAddTrait(SkinTrait.class)
       .setSkinName(player.getDisplayName(), false);

    npc.getOrAddTrait(Owner.class)
       .setOwner(player.getUniqueId());

    npc.data()
       .setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, "false");

    npc.spawn(player.getLocation()
                    .clone()
                    .add(0, 0.25, 0));

    if (npc.isSpawned()) {
      PlayerAnimation.SLEEP.play((Player) npc.getEntity());
    }

    npcs.add(npc);

//    Personal npc = NPCLib.getInstance()
//                         .generatePersonalNPC(player, Main.getInstance(), "", player.getLocation());
//    npc.setSkin(player);
//    npc.setPose(Pose.SLEEPING);
//    npc.setCollidable(false);
//    npc.setShowOnTabList(false);
//    npc.create();
//    npc.show();
//
//    npcs.add(npc.getEntity());
  }
}
