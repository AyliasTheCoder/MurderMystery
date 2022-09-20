package me.aylias.plugins.dotwav.mm.timers.managers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import me.aylias.plugins.dotwav.mm.timers.KillCooldown;
import org.bukkit.ChatColor;

public class KillCooldownManager extends TimerManager {

    private final Game game;

    public KillCooldownManager(Game game) {
        super(() -> new KillCooldown(game, 20 * 5), false);
        this.game = game;
    }

    @Override
    public void failedRun() {
        game.murderer.sendMessage(ChatColor.GREEN + "(!) You can't kill again yet!");
    }

    @Override
    public void successfulRun() {

    }
}
