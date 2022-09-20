package me.aylias.plugins.dotwav.mm.timers;

import me.aylias.plugins.dotwav.mm.teams.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarTimer extends BukkitRunnable {

    private final Game game;

    public ActionBarTimer(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        var murderer = game.murderer.getName();
        var detective = game.detective.getName();

        var canDash = game.dash;
//        var dashCooldown = !game.dashCooldown.isCancelled();
        var canThrow = game.throwKnife;
        if (!canDash) {
//            var bartext = "Dash: Cooling Down    Knife Throw: "
        }
    }
}
