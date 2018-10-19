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
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;


public class BreakEvent implements Listener {

    public ArrayList<Material> allowedBreak = new ArrayList<>();

    public BreakEvent() {
        List<String> list = SettingsManager.getInstance().getConfig().getStringList("block.break.whitelist");
        for (String item : list) {
            Material material = Material.getMaterial(item);
            if (material != null) {
                allowedBreak.add(material);
            } else {
                MineGames.error("Invalid material type: " + item + " for type block.break.whitelist");
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        int pid = GameManager.getInstance().getPlayerGameId(p);


        if (pid == -1) {
            int blockgameid = GameManager.getInstance().getBlockGameId(event.getBlock().getLocation());

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

        if (!allowedBreak.contains(event.getBlock().getType())) event.setCancelled(true);
    }
}