package me.aylias.plugins.dotwav.mm.timers.managers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import me.aylias.plugins.dotwav.mm.timers.KnifeThrowTimer;
import org.bukkit.ChatColor;

public class KnifeThrowTimerManager extends TimerManager {

    private final Game game;

    public KnifeThrowTimerManager(Game game) {
        super(() -> new KnifeThrowTimer(game), false);
        this.game = game;
    }

    @Override
    public void failedRun() {
        game.murderer.sendMessage(ChatColor.GREEN + "(!) You are already throwing your knife!");
    }

    @Override
    public void successfulRun() {

    }
}
