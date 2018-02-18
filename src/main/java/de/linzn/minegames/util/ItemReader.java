package de.linzn.minegames.util;

import de.linzn.minegames.MineGames;
import org.bukkit.Bukkit;
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
            encids.put(e.toString().toLowerCase().replace("_", ""), e);
        }

        //Anything enchants
        encids.put("unbreaking", Enchantment.DURABILITY);
        encids.put("mending", Enchantment.MENDING);

        //Armor Enchants
        encids.put("prot", Enchantment.PROTECTION_ENVIRONMENTAL);
        encids.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        encids.put("fireprot", Enchantment.PROTECTION_FIRE);
        encids.put("fireprotection", Enchantment.PROTECTION_FIRE);
        encids.put("featherfall", Enchantment.PROTECTION_FALL);
        encids.put("featherfalling", Enchantment.PROTECTION_FALL);
        encids.put("blastprot", Enchantment.PROTECTION_EXPLOSIONS);
        encids.put("blastprotection", Enchantment.PROTECTION_EXPLOSIONS);
        encids.put("projectileprot", Enchantment.PROTECTION_PROJECTILE);
        encids.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
        encids.put("aquaaffinity", Enchantment.WATER_WORKER);
        encids.put("respiration", Enchantment.OXYGEN);
        encids.put("thorns", Enchantment.THORNS);
        encids.put("depthstrider", Enchantment.DEPTH_STRIDER);
        encids.put("frostwalker", Enchantment.FROST_WALKER);

        //Weapon Enchants
        encids.put("knockback", Enchantment.KNOCKBACK);
        encids.put("smite", Enchantment.DAMAGE_UNDEAD);
        encids.put("baneofarthropods", Enchantment.DAMAGE_ARTHROPODS);
        encids.put("sharpness", Enchantment.DAMAGE_ALL);
        encids.put("dmg", Enchantment.DAMAGE_ALL);
        encids.put("fire", Enchantment.FIRE_ASPECT);
        encids.put("looting", Enchantment.LOOT_BONUS_MOBS);
        encids.put("loot", Enchantment.LOOT_BONUS_MOBS);
        encids.put("sweepingedge", Enchantment.SWEEPING_EDGE);

        //Tool enchants (Silk Touch's enchantment name is Silk_Touch, so it's covered above)
        encids.put("silktouch", Enchantment.SILK_TOUCH);
        encids.put("efficiency", Enchantment.DIG_SPEED);
        encids.put("fort", Enchantment.LOOT_BONUS_BLOCKS);
        encids.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);

        //Bow specific enchants
        encids.put("punch", Enchantment.ARROW_KNOCKBACK);
        encids.put("power", Enchantment.ARROW_DAMAGE);
        encids.put("infinity", Enchantment.ARROW_INFINITE);
        encids.put("flame", Enchantment.ARROW_FIRE);

        //Fishing Rod specific enchants
        encids.put("luckofthesea", Enchantment.LUCK);
        encids.put("lure", Enchantment.LURE);
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
                material = Bukkit.getUnsafe().getMaterialFromInternalName(split[0]);
            }
            MineGames.debug("Material: " + material.name());
            return new ItemStack(material);
        } else if (split.length == 2) {
            Material material = Material.matchMaterial(split[0]);
            if (material == null) {
                material = Bukkit.getUnsafe().getMaterialFromInternalName(split[0]);
            }
            MineGames.debug("Material: " + material.name());
            return new ItemStack(material, Integer.parseInt(split[1]));
        } else if (split.length == 3) {
            Material material = Material.matchMaterial(split[0]);
            if (material == null) {
                material = Bukkit.getUnsafe().getMaterialFromInternalName(split[0]);
            }
            MineGames.debug("Material: " + material.name());
            return new ItemStack(material, Integer.parseInt(split[1]), Short.parseShort(split[2]));
        } else {
            Material material = Material.matchMaterial(split[0]);
            if (material == null) {
                material = Bukkit.getUnsafe().getMaterialFromInternalName(split[0]);
            }
            MineGames.debug("Material: " + material.name());
            ItemStack i = new ItemStack(material, Integer.parseInt(split[1]), Short.parseShort(split[2]));
            if (!split[3].equalsIgnoreCase("none")) {
                String encs[] = split[3].toLowerCase().split(" ");
                for (String enc : encs) {
                    System.out.println(enc);
                    String e[] = enc.split(":");
                    i.addUnsafeEnchantment(encids.get(e[0]), Integer.parseInt(e[1]));
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
