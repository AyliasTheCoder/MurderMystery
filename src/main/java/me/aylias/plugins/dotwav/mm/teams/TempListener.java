package me.aylias.plugins.dotwav.mm.teams;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class TempListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
//        if (e.getItem().getItemMeta().hasCustomModelData()) {
//            if (e.getItem().getItemMeta().getCustomModelData() == SpecialItems.sheriffGun.getItemMeta().getCustomModelData()) {
//                if (e.getItem().isSimilar(SpecialItems.sheriffGun)) {
//                    var arrowLoc = e.getPlayer().getEyeLocation();
//                    arrowLoc.add(e.getPlayer().getLocation().getDirection().normalize().multiply(.1));
//                    var arrow = e.getPlayer().getWorld().spawnArrow(arrowLoc, e.getPlayer().getLocation().getDirection().normalize().multiply(50), 1, 1);
//                    arrow.setShooter(e.getPlayer());
//                }
//            }
//        }
    }
}
