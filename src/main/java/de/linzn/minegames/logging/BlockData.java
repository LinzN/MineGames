package de.linzn.minegames.logging;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class BlockData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String world;
    private Material prevType;
    private Material newType;
    private org.bukkit.block.data.BlockData prevdata, newdata;
    private int x, y, z;
    private int gameid;
    private ItemStack[] items;

    /**
     * @param prevType
     * @param newType
     * @param x
     * @param y
     * @param z        Provides a object for holding the data for block changes
     */
    public BlockData(int gameid, String world, Material prevType, org.bukkit.block.data.BlockData prevdata, Material newType, org.bukkit.block.data.BlockData newdata, int x, int y, int z, ItemStack[] items) {
        this.gameid = gameid;
        this.world = world;
        this.prevType = prevType;
        this.prevdata = prevdata;
        this.newType = newType;
        this.newdata = newdata;
        this.x = x;
        this.y = y;
        this.z = z;
        this.items = items;
    }

    public int getGameId() {
        return gameid;
    }

    public String getWorld() {
        return world;
    }

    public org.bukkit.block.data.BlockData getPrevdata() {
        return prevdata;
    }

    public org.bukkit.block.data.BlockData getNewdata() {
        return newdata;
    }

    public Material getPrevType() {
        return prevType;
    }

    public Material getNewType() {
        return newType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public ItemStack[] getItems() {
        return items;
    }
}
