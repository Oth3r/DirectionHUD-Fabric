package one.oth3r.directionhud.utils;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Arrays;

import static one.oth3r.directionhud.utils.Utl.isInt;

public class Loc {
    private Integer x;
    private Integer y = null;
    private Integer z;
    private String dimension;
    public Loc(int x, int y, int z, String dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }
    public Loc(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Loc(int x, int y, String dimension) {
        this.x = x;
        this.y = y;
        this.dimension = dimension;
    }
    public Loc(int x, int z) {
        this.x = x;
        this.z = z;
    }
    public Loc(String xyz) {
        ArrayList<String> sp = new ArrayList<>(Arrays.asList(xyz.split(" ")));
        if (sp.size() == 1) sp = new ArrayList<>(Arrays.asList(xyz.split("_")));
        if (sp.size() == 1) {
            this.x = 0;
            this.z = 0;
            return;
        }
        if (!isInt(sp.get(0))) sp.set(0, "0");
        if (sp.size() == 3) {
            if (sp.get(1).equals("n")) sp.remove(1);
            else if (!isInt(sp.get(2))) sp.set(2,"0");
        }
        if (!isInt(sp.get(1))) sp.set(1, "0");
        if (sp.size() == 2) {
            this.x = Utl.xyz.xzBounds(Integer.parseInt(sp.get(0)));
            this.z = Utl.xyz.xzBounds(Integer.parseInt(sp.get(1)));
            return;
        }
        this.x = Utl.xyz.xzBounds(Integer.parseInt(sp.get(0)));
        this.y = Utl.xyz.yBounds(Integer.parseInt(sp.get(1)));
        this.z = Utl.xyz.xzBounds(Integer.parseInt(sp.get(2)));
    }
    public Loc(ServerPlayerEntity player) {
        this.x = player.getBlockX();
        this.y = player.getBlockY();
        this.z = player.getBlockZ();
        this.dimension = Utl.player.dim(player);
    }
    public String getXYZ() {
        if (y == null) return x+" "+z;
        return x+" "+y+" "+z;
    }
    public String getXYZ_C() {
        if (y == null) return x+"_"+z;
        return x+" "+y+" "+z;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getZ() {
        return z;
    }
    public void setZ(int z) {
        this.z = z;
    }
    public String getDIM() {
        return dimension;
    }
    public void setDIM(String setDIM) {
        this.dimension = setDIM;
    }
}
