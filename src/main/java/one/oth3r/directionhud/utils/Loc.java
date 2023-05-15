package one.oth3r.directionhud.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;

import static one.oth3r.directionhud.utils.Utl.isInt;
import static one.oth3r.directionhud.utils.Utl.dim.conversionRatios;

public class Loc {
    private Integer x = null;
    private Integer y = null;
    private Integer z = null;
    private String dimension = null;
    public Loc() {}
    public Loc(Integer x, Integer y, Integer z, String dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        if (Utl.dim.checkValid(dimension)) this.dimension = dimension;
    }
    public Loc(Integer x, Integer y, Integer z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Loc(Integer x, Integer y, String dimension) {
        this.x = x;
        this.y = y;
        if (Utl.dim.checkValid(dimension)) this.dimension = dimension;
    }
    public Loc(Integer x, Integer z) {
        this.x = x;
        this.z = z;
    }
    public Loc(String xyz) {
        parseXYZ(xyz);
    }
    public Loc(String xyz, String dimension) {
        parseXYZ(xyz);
        if (Utl.dim.checkValid(dimension)) this.dimension = dimension;
    }
    private void parseXYZ(String xyz) {
        if (xyz == null || xyz.equals("null")) return;
        if (xyz.charAt(0)=='[' && xyz.charAt(xyz.length()-1)==']') {
            String[] list = xyz.substring(1, xyz.length() - 1).split(", ");
            if (list.length >= 3)  {
                this.x = Integer.parseInt(list[0]);
                if (list[1] != null && !list[1].equals("null")) this.y = Integer.parseInt(list[1]);
                this.z = Integer.parseInt(list[2]);
            }
            if (list.length == 4) this.dimension = list[3];
            return;
        }
        ArrayList<String> sp = new ArrayList<>(Arrays.asList(xyz.split(" ")));
        if (sp.size() == 1) {
            this.x = 0;
            this.z = 0;
            return;
        }
        if (!isInt(sp.get(0))) sp.set(0, "0");
        if (!isInt(sp.get(1))) sp.set(1, "0");
        if (sp.size() == 3 && !isInt(sp.get(2))) sp.set(2,"0");
        this.x = Utl.xyz.xzBounds(Integer.parseInt(sp.get(0)));
        if (sp.size() == 2) {
            this.z = Utl.xyz.xzBounds(Integer.parseInt(sp.get(1)));
            return;
        }
        this.y = Utl.xyz.yBounds(Integer.parseInt(sp.get(1)));
        this.z = Utl.xyz.xzBounds(Integer.parseInt(sp.get(2)));
    }
    public Loc(ServerPlayerEntity player) {
        this.x = player.getBlockX();
        this.y = player.getBlockY();
        this.z = player.getBlockZ();
        this.dimension = Utl.player.dim(player);
    }
    public Loc(ServerPlayerEntity player, String dimension) {
        this.x = player.getBlockX();
        this.y = player.getBlockY();
        this.z = player.getBlockZ();
        this.dimension = dimension;
    }
    public void convertTo(String toDimension) {
        String fromDimension = this.getDIM();
        if (fromDimension.equalsIgnoreCase(toDimension)) return;
        if (!Utl.dim.checkValid(toDimension)) return;
        Pair<String, String> dimensionPair = new ImmutablePair<>(fromDimension, toDimension);
        Double ratio;
        if (conversionRatios.containsKey(dimensionPair)) ratio = conversionRatios.get(dimensionPair);
        else {
            dimensionPair = new ImmutablePair<>(toDimension,fromDimension);
            if (conversionRatios.containsKey(dimensionPair)) ratio = 1/conversionRatios.get(dimensionPair);
            else return;
        }
        this.setDIM(toDimension);
        if (this.yExists()) this.setY((int) (this.getY()*ratio));
        this.setX((int) (this.getX()*ratio));
        this.setZ((int) (this.getZ()*ratio));
    }
    public boolean hasXYZ() {
        return this.getXYZ() != null;
    }
    public String getXYZ() {
        if (x == null || z == null) return null;
        if (y == null) return x+" "+z;
        return x+" "+y+" "+z;
    }
    public String getLocC() {
        if (this.dimension == null) return Arrays.toString(new String[]{this.x+"",this.y+"",this.z+""});
        return Arrays.toString(new String[]{this.x+"",this.y+"",this.z+"",this.dimension});
    }
    public Vec3d getVec3d(ServerPlayerEntity player) {
        Integer i = this.y;
        if (i == null) i = player.getBlockY();
        if (this.x != null && this.z != null) return new Vec3d(this.x,i,this.z);
        return new Vec3d(0,0,0);
    }
    public CTxT getBadge() {
        CTxT msg = CTxT.of("");
        if (this.dimension != null) msg.append(Utl.dim.getLetterButton(getDIM())).append(" ");
        return msg.append(CTxT.of(getXYZ()).color('f'));
    }
    public CTxT getBadge(String name,String color) {
        CTxT msg = CTxT.of("");
        if (this.dimension != null) msg.append(Utl.dim.getLetterButton(getDIM())).append(" ");
        return msg.append(CTxT.of(name).color(color).hEvent(CTxT.of(getXYZ())));
    }

    public Integer getX() {
        return x;
    }
    public void setX(Integer x) {
        this.x = x;
    }
    public boolean yExists() {
        return this.y != null;
    }
    public Integer getY() {
        return y;
    }
    public void setY(Integer y) {
        this.y = y;
    }
    public Integer getZ() {
        return z;
    }
    public void setZ(Integer z) {
        this.z = z;
    }
    public String getDIM() {
        return dimension;
    }
    public void setDIM(String setDIM) {
        this.dimension = setDIM;
    }
}