package de.linzn.minegames.events;

import de.linzn.minegames.Game;
import de.linzn.minegames.GameManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class EntityDestroyEvent implements Listener {


    public EntityDestroyEvent() {
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityBreak(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        int pid = -1;
        if (damager instanceof Player) {
            Player p = (Player) damager;
            pid = GameManager.getInstance().getPlayerGameId(p);
        }
        Entity entity = event.getEntity();

        if (pid == -1) {
            int blockgameid = GameManager.getInstance().getBlockGameId(entity.getLocation());
            if (blockgameid != -1) {
                if (GameManager.getInstance().getGame(blockgameid).getGameMode() != Game.GameMode.DISABLED) {
                    event.setCancelled(true);
                }
            }
            return;
        }
        Game g = GameManager.getInstance().getGame(pid);

        if (g.getMode() == Game.GameMode.DISABLED) {
            return;
        }
        if (g.getMode() != Game.GameMode.INGAME) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
    }
}