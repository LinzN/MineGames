package de.linzn.minegames.events;

import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SecondaryAbilityEvent;
import de.linzn.minegames.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class McMMOPreventer implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSecondaryAbility(SecondaryAbilityEvent event) {
        Player player = event.getPlayer();
        int gameid = GameManager.getInstance().getPlayerGameId(player);
        if (gameid == -1)
            return;
        if (!GameManager.getInstance().isPlayerActive(player))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMcMMOPlayerAbilityActivate(McMMOPlayerAbilityActivateEvent event) {
        Player player = event.getPlayer();
        int gameid = GameManager.getInstance().getPlayerGameId(player);
        if (gameid == -1)
            return;
        if (!GameManager.getInstance().isPlayerActive(player))
            return;
        event.setCancelled(true);
    }

}
