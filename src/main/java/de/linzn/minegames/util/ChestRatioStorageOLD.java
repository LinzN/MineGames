package de.linzn.minegames.util;

import de.linzn.minegames.SettingsManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class ChestRatioStorageOLD {


    public static ChestRatioStorageOLD instance = new ChestRatioStorageOLD();
    HashMap<Integer, ArrayList<ItemStack>> lvlstore = new HashMap<Integer, ArrayList<ItemStack>>();
    int ratio = 2;

    private ChestRatioStorageOLD() {

    }

    public static ChestRatioStorageOLD getInstance() {
        return instance;
    }

    public void setup() {

        FileConfiguration conf = SettingsManager.getInstance().getChest();

        for (int a = 1; a < 5; a++) {
            ArrayList<ItemStack> lvl = new ArrayList<ItemStack>();
            List<String> list = conf.getStringList("chest.lvl" + a);

            for (int b = 0; b < list.size(); b++) {
                ItemStack i = ItemReader.read(list.get(b));


                lvl.add(i);

            }

            lvlstore.put(a, lvl);

        }

        ratio = conf.getInt("chest.ratio") + 1;

    }

    public ArrayList<ItemStack> getItems() {
        Random r = new Random();
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int a = 0; a < r.nextInt(7) + 5; a++) {
            if (r.nextBoolean() == true) {
                int i = 1;
                while (i < 6 && r.nextInt(ratio) == 1) {
                    i++;
                }

                ArrayList<ItemStack> lvl = lvlstore.get(i);
                ItemStack item = lvl.get(r.nextInt(lvl.size()));

                items.add(item);
            }

        }

        //Bukkit.broadcastMessage(items+"");
        return items;


    }


}
