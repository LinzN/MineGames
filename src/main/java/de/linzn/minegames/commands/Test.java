package de.linzn.minegames.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import de.linzn.minegames.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Test implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        Region sel = null;
        try {
            sel = GameManager.getInstance().getWorldEdit().getSession(player).getSelection(new BukkitWorld(player.getWorld()));
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }

        BlockVector3 max = sel.getMaximumPoint();
        BlockVector3 min = sel.getMinimumPoint();

        World w = player.getWorld();

        HashSet<Location> mark = new HashSet<Location>();

        for (int a = min.getBlockZ(); a < max.getBlockZ(); a++) {
            mark.add(getYLocation(w, max.getBlockX(), max.getBlockY(), a));
            mark.add(getYLocation(w, min.getBlockX(), max.getBlockY(), a));
        }
        for (int a = min.getBlockX(); a < max.getBlockX(); a++) {
            mark.add(getYLocation(w, a, max.getBlockY(), max.getBlockZ()));
            mark.add(getYLocation(w, a, max.getBlockY(), min.getBlockZ()));
        }

        setFence(mark);
        return true;

    }

    @SuppressWarnings("deprecation")
    public Location getYLocation(World w, int x, int y, int z) {
        Location l = new Location(w, x, y, z);
        while (l.getBlock().getType() == Material.AIR) {
            l.add(0, -1, 0);
        }
        return l.add(0, 1, 0);
    }

    public void setFence(HashSet<Location> locs) {
        for (Location l : locs) {
            l.getBlock().setType(Material.OAK_FENCE);
        }
    }

    public String help(Player p) {
        // TODO Auto-generated method stub
        return null;
    }

    public String permission() {
        // TODO Auto-generated method stub
        return null;
    }

}
