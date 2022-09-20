package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.Main;
import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

public class LightsOutAbility extends BukkitRunnable {

    private final Game game;
    BossBar bar;
    int ticks = 100;
    double initTicks = 100;

    public LightsOutAbility(Game game) {
        this.game = game;

        Bukkit.getOnlinePlayers()
              .forEach(p -> {
                  if (!p.equals(game.murderer)) {
                      // lights out them
                  }
              });
        bar = Bukkit.createBossBar(ChatColor.RED + "Freeze Frame", BarColor.RED, BarStyle.SOLID);
        bar.addPlayer(game.murderer);


        game.abilityCooldown = new AbilityCooldown(game, 15 * 20 + 20 * 30 + 100);
        game.abilityCooldown.runTaskTimer(Main.getInstance(), 1, 1);

        Main.getParticleTimer().current = "particle minecraft:entity_effect ~ ~ ~ 0.1 0.2 0.1 1 0 force @s";
    }

    @Override
    public void run() {
        ticks--;

        double progress = (double) ticks / initTicks;

        bar.setProgress(progress);

        if (ticks <= 0) {
            cancel();
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        bar.removeAll();

        game.mirrorManTimer = new MirrorManTimer(game);
        game.mirrorManTimer.runTaskTimer(Main.getInstance(), 1, 1);
    }
}

