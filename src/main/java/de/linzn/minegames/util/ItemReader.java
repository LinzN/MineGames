package de.linzn.minegames.util;

import de.linzn.minegames.MineGames;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;

public class ItemReader {


    private static HashMap<String, Enchantment> encids;


    private static void loadIds() {

        encids = new HashMap<>();

        for (Enchantment e : Enchantment.values()) {
            encids.put(e.toString(), e);
        }

        //Anything enchants
        encids.put("DURABILITY", Enchantment.DURABILITY);
        encids.put("MENDING", Enchantment.MENDING);

        //Armor Enchants
        encids.put("PROTECTION_ENVIRONMENTAL", Enchantment.PROTECTION_ENVIRONMENTAL);
        encids.put("PROTECTION_FIRE", Enchantment.PROTECTION_FIRE);
        encids.put("PROTECTION_FALL", Enchantment.PROTECTION_FALL);
        encids.put("PROTECTION_EXPLOSIONS", Enchantment.PROTECTION_EXPLOSIONS);
        encids.put("PROTECTION_PROJECTILE", Enchantment.PROTECTION_PROJECTILE);
        encids.put("WATER_WORKER", Enchantment.WATER_WORKER);
        encids.put("OXYGEN", Enchantment.OXYGEN);
        encids.put("THORNS", Enchantment.THORNS);
        encids.put("DEPTH_STRIDER", Enchantment.DEPTH_STRIDER);
        encids.put("FROST_WALKER", Enchantment.FROST_WALKER);

        //Weapon Enchants
        encids.put("KNOCKBACK", Enchantment.KNOCKBACK);
        encids.put("DAMAGE_UNDEAD", Enchantment.DAMAGE_UNDEAD);
        encids.put("DAMAGE_ARTHROPODS", Enchantment.DAMAGE_ARTHROPODS);
        encids.put("DAMAGE_ALL", Enchantment.DAMAGE_ALL);
        encids.put("FIRE_ASPECT", Enchantment.FIRE_ASPECT);
        encids.put("LOOT_BONUS_MOBS", Enchantment.LOOT_BONUS_MOBS);
        encids.put("SWEEPING_EDGE", Enchantment.SWEEPING_EDGE);

        //Tool enchants (Silk Touch's enchantment name is Silk_Touch, so it's covered above)
        encids.put("SILK_TOUCH", Enchantment.SILK_TOUCH);
        encids.put("DIG_SPEED", Enchantment.DIG_SPEED);
        encids.put("LOOT_BONUS_BLOCKS", Enchantment.LOOT_BONUS_BLOCKS);

        //Bow specific enchants
        encids.put("ARROW_KNOCKBACK", Enchantment.ARROW_KNOCKBACK);
        encids.put("ARROW_DAMAGE", Enchantment.ARROW_DAMAGE);
        encids.put("ARROW_INFINITE", Enchantment.ARROW_INFINITE);
        encids.put("ARROW_FIRE", Enchantment.ARROW_FIRE);

        //Fishing Rod specific enchants
        encids.put("LUCK", Enchantment.LUCK);
        encids.put("LURE", Enchantment.LURE);
    }


    @SuppressWarnings("deprecation")
    public static ItemStack read(String str) {
        if (encids == null) {
            loadIds();
        }
        String split[] = str.split(",");
        MineGames.debug("ItemReader: reading : " + Arrays.toString(split));
        for (int a = 0; a < split.length; a++) {
            split[a] = split[a].trim();
        }
        if (split.length < 1) {
            return null;
        } else if (split.length == 1) {
            Material material = Material.matchMaterial(split[0]);
            if (material == null) {
                material = Material.valueOf(split[0]);
            }
            MineGames.debug("Material: " + material.name());
            return new ItemStack(material);
        } else if (split.length == 2) {
            Material material = Material.matchMaterial(split[0]);
            if (material == null) {
                material = Material.valueOf(split[0]);
            }
            MineGames.debug("Material: " + material.name());
            return new ItemStack(material, Integer.parseInt(split[1]));
        } else if (split.length == 3) {
            Material material = Material.matchMaterial(split[0]);
            if (material == null) {
                material = Material.valueOf(split[0]);
            }
            MineGames.debug("Material: " + material.name());
            return new ItemStack(material, Integer.parseInt(split[1]), Short.parseShort(split[2]));
        } else {
            Material material = Material.matchMaterial(split[0]);
            if (material == null) {
                material = Material.valueOf(split[0]);
            }
            MineGames.debug("Material: " + material.name());
            ItemStack i = new ItemStack(material, Integer.parseInt(split[1]), Short.parseShort(split[2]));
            if (!split[3].equalsIgnoreCase("none")) {
                String encs[] = split[3].toLowerCase().split(" ");
                for (String enc : encs) {
                    System.out.println(enc);
                    String e[] = enc.split(":");
                    Enchantment enchantment = encids.get(e[0].toUpperCase());
                    if (enchantment != null) {
                        int level = Integer.parseInt(e[1]);
                        i.addUnsafeEnchantment(enchantment, level);
                    } else {
                        MineGames.debug("Enchant is null ?? :: " + e[0].toUpperCase());
                    }
                }
            }
            if (split.length == 5) {
                ItemMeta im = i.getItemMeta();
                im.setDisplayName(MessageUtil.replaceColors(split[4]));
                i.setItemMeta(im);
            }
            return i;
        }
    }

    public static String getFriendlyItemName(Material m) {
        String str = m.toString();
        str = str.replace('_', ' ');
        str = str.substring(0, 1).toUpperCase() +
                str.substring(1).toLowerCase();
        return str;
    }


}
