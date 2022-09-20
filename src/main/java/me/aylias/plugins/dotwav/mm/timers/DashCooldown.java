package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.Main;
import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class DashCooldown extends BukkitRunnable {

    private final Game game;

    public DashCooldown(Game game) {
        this.game = game;
        game.dash = false;
        game.murderer.sendMessage("Your dash will be recharged in 5 seconds");
        new DashCooldownBossbar(game).runTaskTimer(Main.getPlugin(Main.class), 1, 1);
    }

    @Override
    public void run() {
        game.dash = true;
        game.murderer.sendMessage("Your dash is recharged!");
    }
}
