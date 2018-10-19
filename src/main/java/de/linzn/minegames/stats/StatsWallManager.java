package de.linzn.minegames.stats;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Region;
import de.linzn.minegames.GameManager;
import de.linzn.minegames.MineGames;
import de.linzn.minegames.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class StatsWallManager {


    private static StatsWallManager instance = new StatsWallManager();
    //TODO: Possibly clean up
    Sign[][] signs;
    MineGames p;
    private int runningThread = 0;

    private StatsWallManager() {

    }

    public static StatsWallManager getInstance() {
        return instance;

    }


    public void setup(MineGames p) {
        this.p = p;
        loadSigns();
    }


    public void loadSigns() {

        FileConfiguration c = SettingsManager.getInstance().getSystemConfig();
        try {
            if (!c.getBoolean("sg-system.stats.sign.set"))
                return;
        } catch (Exception e) {
            return;
        }
        boolean usingx = false;
        int hdiff = 0;
        int x1 = c.getInt("sg-system.stats.sign.x1");
        int y1 = c.getInt("sg-system.stats.sign.y1");
        int z1 = c.getInt("sg-system.stats.sign.z1");

        int x2 = c.getInt("sg-system.stats.sign.x2");
        int y2 = c.getInt("sg-system.stats.sign.y2");
        int z2 = c.getInt("sg-system.stats.sign.z2");
        int inc = 0;
        Location l;
        //  System.out.println(x1+"  "+y1+"  "+z1);
        @SuppressWarnings("deprecation")
        byte temp = ((Sign) new Location(p.getServer().getWorld(c.getString("sg-system.stats.sign.world")), x1, y1, z1).getBlock().getState()).getData().getData();
        //  System.out.println("facing "+temp);
        if (temp == 3 || temp == 4) {
            l = new Location(Bukkit.getWorld(c.getString("sg-system.stats.sign.world")), x1, y1, z1);
            inc = -1;
        } else {
            l = new Location(Bukkit.getWorld(c.getString("sg-system.stats.sign.world")), x2, y1, z2);
            inc = 1;
        }


        usingx = ((x2 - x1) != 0) ? true : false;
        if (usingx) {
            hdiff = (x1 - x2) + 1;
        } else {
            hdiff = (z1 - z2) + 1;
        }
        int vdiff = (y1 - y2) + 1;


        System.out.println(vdiff + "              " + hdiff);
        signs = new Sign[vdiff][hdiff];
        for (int y = vdiff - 1; y >= 0; y--) {
            for (int x = hdiff - 1; x >= 0; x--) {


                BlockState b = p.getServer().getWorld(SettingsManager.getInstance().getSystemConfig().getString("sg-system.stats.sign.world")).getBlockAt(l).getState();
                if (b instanceof Sign) {
                    signs[y][x] = (Sign) b;
                }
                if (usingx)
                    l = l.add(inc, 0, 0);
                else
                    l = l.add(0, 0, inc);
                //l.getBlock().setTypeId(323);
            }
            l = l.add(0, -1, 0);
            if (inc == -1) {
                l.setX(x1);
                l.setZ(z1);
            } else {
                l.setX(x2);
                l.setZ(z2);
            }
        }
        runningThread++;
    }

    public int[] getSignMidPoint() {
        double x = (signs[0].length * 8);
        double y = (signs.length * 2);

        return new int[]{(int) x, (int) y};
    }


    public void setStatsSignsFromSelection(Player pl) {
        FileConfiguration c = SettingsManager.getInstance().getSystemConfig();
        SettingsManager s = SettingsManager.getInstance();
        if (!c.getBoolean("sg-system.stats.sign.set", false)) {
            c.set("sg-system.stats.sign.set", true);
            s.saveSystemConfig();
        }
        Region sel = null;
        try {
            sel = GameManager.getInstance().getWorldEdit().getSession(pl).getSelection(new BukkitWorld(pl.getWorld()));
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }
        if (sel == null) {
            pl.sendMessage(ChatColor.RED + "You must make a WorldEdit Selection first");
            return;
        }
        if ((sel.getMaximumPoint().getBlockX() - sel.getMaximumPoint().getBlockX()) != 0 && (sel.getMaximumPoint().getBlockZ() - sel.getMaximumPoint().getBlockZ() != 0)) {
            pl.sendMessage(ChatColor.RED + " Must be in a straight line!");
            return;
        }

        Vector max = sel.getMaximumPoint();
        Vector min = sel.getMinimumPoint();

        c.set("sg-system.stats.sign.world", pl.getWorld().getName());
        c.set("sg-system.stats.sign.x1", max.getBlockX());
        c.set("sg-system.stats.sign.y1", max.getBlockY());
        c.set("sg-system.stats.sign.z1", max.getBlockZ());
        c.set("sg-system.stats.sign.x2", min.getBlockX());
        c.set("sg-system.stats.sign.y2", min.getBlockY());
        c.set("sg-system.stats.sign.z2", min.getBlockZ());

        pl.sendMessage(ChatColor.GREEN + "Stats wall successfully created");
        s.saveSystemConfig();
        loadSigns();


    }

    public void updateStatsWall() {


    }

    //String sorts = {""

    class StatsSignUpdater extends Thread {
        @SuppressWarnings("unused")
        public void run() {
            int trun = runningThread;

           /* while(MineGames.isActive() && trun == runningThread){
                try{
                    try{Thread.sleep(1000);}catch(Exception e){}
                    updateStatsWall();
                }catch(Exception e){e.printStackTrace(); signs[0][0].setLine(1, ChatColor.RED+"ERROR");signs[0][0].setLine(1, ChatColor.RED+"Check Console");}
            }*/
        }
    }


}
