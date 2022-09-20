package me.aylias.plugins.dotwav.mm.teams;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;

public class CosmeticsListener extends BukkitRunnable implements Listener {

    ArrayList<Projectile> projectiles = new ArrayList<>();

    @EventHandler
    public void entityCreated(EntitySpawnEvent e) {
        if (e.getEntity() instanceof Projectile p) {
            Bukkit.broadcastMessage("lkfdshkdsfhkads h");
            projectiles.add(p);
        }
    }

    @Override
    public void run() {
        projectiles.removeIf(Objects::isNull);

        projectiles.forEach(p -> p.getWorld().spawnParticle(Particle.CRIT, p.getLocation(), 10));
    }
}
