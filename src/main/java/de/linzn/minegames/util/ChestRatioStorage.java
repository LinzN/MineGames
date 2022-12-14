package de.linzn.minegames.util;

import de.linzn.minegames.MineGames;
import de.linzn.minegames.SettingsManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class ChestRatioStorage {

    public static ChestRatioStorage instance = new ChestRatioStorage();
    HashMap<Integer, ArrayList<ItemStack>> lvlstore = new HashMap<>();
    private int ratio = 2;
    private int maxlevel = 0;
    private int minitems = 4;
    private int maxitems = 10;
    private int maxincrease = 5;

    private ChestRatioStorage() {
    }

    public static ChestRatioStorage getInstance() {
        return instance;
    }

    public void setup() {

        FileConfiguration conf = SettingsManager.getInstance().getChest();

        for (int clevel = 1; clevel <= 16; clevel++) {
            ArrayList<ItemStack> lvl = new ArrayList<ItemStack>();
            List<String> list = conf.getStringList("chest.lvl" + clevel);

            if (!list.isEmpty()) {
                for (String aList : list) {
                    ItemStack i = ItemReader.read(aList);
                    lvl.add(i);
                }
                lvlstore.put(clevel, lvl);
            } else {
                maxlevel = clevel - 1;
                break;
            }
        }
        if (maxlevel == 0) {
            MineGames.error("No chest content defined for level 1!");
        }

        ratio = conf.getInt("chest.ratio", ratio);
        minitems = conf.getInt("chest.min", minitems);
        maxitems = conf.getInt("chest.max", maxitems);
        if (minitems < 0) {
            minitems = 0;
        }
        if (maxitems < minitems) {
            maxitems = minitems;
        }
        maxincrease = conf.getInt("chest.maxincrease", maxincrease);
    }

    public int getLevel(int base) {
        Random rand = new Random();
        int max = Math.min(base + maxincrease, maxlevel);
        while (rand.nextInt(ratio) == 0 && (base < max)) {
            base++;
        }
        return base;
    }

    public ArrayList<ItemStack> getItems(int level) {
        int newlevel;
        Random r = new Random();
        ArrayList<ItemStack> items = new ArrayList<>();

        for (int a = 0; a < r.nextInt(maxitems - minitems) + minitems; a++) {
            newlevel = level;
            // is this really necessary?
            while ((newlevel < (level + maxincrease)) && (newlevel < maxlevel) && (r.nextInt(ratio) == 0)) {
                newlevel++;
            }

            ArrayList<ItemStack> lvl = lvlstore.get(newlevel);
            try {
                ItemStack item = lvl.get(r.nextInt(lvl.size()));
                items.add(item);
            } catch (Exception e) {
                MineGames.error("Unable to get item of chest level " + newlevel + " (maxlevel = " + maxlevel + ")");
            }

        }
        return items;
    }
}
