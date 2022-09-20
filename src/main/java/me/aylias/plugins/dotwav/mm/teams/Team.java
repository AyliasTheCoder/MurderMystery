package me.aylias.plugins.dotwav.mm.teams;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class Team implements Listener {

    private static List<Team> teams = new ArrayList<>();

    List<Player> players = new ArrayList<>();

    public Team() {
        teams.add(this);
    }

    public void addPlayer(Player p) {
        teams.forEach(team -> team.players.remove(p));
        players.add(p);
    }

    @EventHandler
    public void playerDamageByPlayer(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p && e.getEntity() instanceof Player p1) {
            if (players.contains(p) && players.contains(p1)) {
                e.setCancelled(true);
            }
        }
    }

    public void clear() {
        players.clear();
    }
}
