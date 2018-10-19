package de.linzn.minegames.events;


import de.linzn.minegames.Game;
import de.linzn.minegames.GameManager;
import de.linzn.minegames.MineGames;
import de.linzn.minegames.SettingsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;


public class PlaceEvent implements Listener {

    public ArrayList<Material> allowedPlace = new ArrayList<>();

    public PlaceEvent() {
        List<String> list = SettingsManager.getInstance().getConfig().getStringList("block.place.whitelist");
        for (String item : list) {
            Material material = Material.getMaterial(item);
            if (material != null) {
                allowedPlace.add(material);
            } else {
                MineGames.error("Invalid material type: " + item + " for type block.place.whitelist");
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        int id = GameManager.getInstance().getPlayerGameId(p);

        if (id == -1) {
            int gameblockid = GameManager.getInstance().getBlockGameId(event.getBlock().getLocation());
            if (gameblockid != -1) {
                if (GameManager.getInstance().getGame(gameblockid).getGameMode() != Game.GameMode.DISABLED) {
                    event.setCancelled(true);
                }
            }
            return;
        }


        Game g = GameManager.getInstance().getGame(id);
        if (g.isPlayerInactive(p)) {
            return;
        }
        if (g.getMode() == Game.GameMode.DISABLED) {
            return;
        }
        if (g.getMode() != Game.GameMode.INGAME) {
            event.setCancelled(true);
            return;

        }

        if (!allowedPlace.contains(event.getBlock().getType())) {
            event.setCancelled(true);
        }

    }
}
