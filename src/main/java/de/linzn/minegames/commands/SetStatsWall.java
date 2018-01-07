package de.linzn.minegames.commands;

import de.linzn.minegames.stats.StatsWallManager;
import org.bukkit.entity.Player;

// import SettingsManager;


public class SetStatsWall implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        StatsWallManager.getInstance().setStatsSignsFromSelection(player);
        return false;
    }


    public String help(Player p) {
        return null; //"/sg setstatswall - "+ SettingsManager.getInstance().getMessageConfig().getString("messages.help.setstatswall", "Sets the stats wall");
    }

    public String permission() {
        return "sg.admin.setstatswall";
    }
}
