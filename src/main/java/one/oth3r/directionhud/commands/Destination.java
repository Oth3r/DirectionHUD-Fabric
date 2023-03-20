package one.oth3r.directionhud.commands;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.math.Vec3d;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;

import java.util.*;

public class Destination {
    //todo reset all in settings
    private static MutableText lang(String key) {
        return CUtl.lang("dest."+key);
    }
    private static MutableText lang(String key, Object... args) {
        return CUtl.lang("dest."+key, args);
    }
    //error
    private static Text error(String key) {
        return CUtl.error(CUtl.lang("error."+key));
    }
    private static Text error(String key, Object... args) {
        return CUtl.error(CUtl.lang("error."+key, args));
    }

    public static String get(ServerPlayerEntity player, String str) {
        String xyz = PlayerData.get.dest.getDest(player);
        if (xyz.equals("f")) return "f";
        String[] tmp = xyz.split(" ");
        String x;
        String y;
        String z;
        //TRACK COMMAND
        if (tmp.length == 1) {
            ServerPlayerEntity argsPlayer = DirectionHUD.server.getPlayerManager().getPlayer(tmp[0]);
            if (argsPlayer == null) {
                suspend(player,tmp[0],5,lang("suspended.offline"));
                return "f";
            }
            if (!PlayerData.get.dest.setting.track(player)) {
                clear(player);
                player.sendMessage(Text.literal("").append(CUtl.tag())
                                .append(lang("cleared",
                                        lang("cleared_2").setStyle(CUtl.C('a'))))
                                .append("\n ")
                                .append(lang("cleared_tracking_off").styled(style -> style
                                        .withItalic(true).withColor(CUtl.TC('7')))));
                return "f";
            }
            if (!PlayerData.get.dest.setting.track(argsPlayer)) {
                clear(player);
                player.sendMessage(Text.literal("").append(CUtl.tag())
                        .append(lang("cleared",
                                lang("cleared_2").setStyle(CUtl.C('a'))))
                        .append("\n ")
                        .append(lang("cleared_tracking_off_player").styled(style -> style
                                .withItalic(true).withColor(CUtl.TC('7')))));
                return "f";
            }
            tmp = Utl.player.XYZ(argsPlayer).split(" ");
        }
        x = tmp[0];
        y = tmp[1];
        z = tmp[2];

        if (PlayerData.get.dest.setting.ylevel(player) && Utl.isInt(y)) {
            y = String.valueOf(player.getBlockY());
        }

        if (str.equalsIgnoreCase("xyz")) {
            if (!Utl.isInt(y)) {
                return x +" "+ z;
            }
            return x +" "+ y +" "+ z;
        }
        if (str.equalsIgnoreCase("x")) {
            return x;
        }
        if (str.equalsIgnoreCase("y")) {
            if (!Utl.isInt(y)) {
                return String.valueOf(player.getBlockY());
            }
            return y;
        }
        if (str.equalsIgnoreCase("z")) {
            return z;
        }
        return "f";
    }
    public static boolean isPlayer(ServerPlayerEntity player) {
        String xyz = PlayerData.get.dest.getDest(player);
        String[] tmp = xyz.split(" ");
        if (xyz.equals("f")) return false;
        return tmp.length == 1;
    }
    public static boolean checkDist(ServerPlayerEntity player, String xyz) {
        String[] l = xyz.split(" ");
        if (l[1].equals("n")) l[1] = String.valueOf(player.getBlockY());
        Vec3d loc = new Vec3d(Integer.parseInt(l[0]), Integer.parseInt(l[1]), Integer.parseInt(l[2]));
        if (PlayerData.get.dest.setting.autoclear(player)) return player.getPos().distanceTo(loc) <= PlayerData.get.dest.setting.autoclearrad(player);
        else return false;
    }
    public static int getDist(ServerPlayerEntity player) {
        if (get(player, "xyz").equals("f")) return 0;
        Vec3d loc = new Vec3d(Integer.parseInt(get(player, "x")), Integer.parseInt(get(player, "y")), Integer.parseInt(get(player, "z")));
        return (int) player.getPos().distanceTo(loc);
    }

    public static void clear(ServerPlayerEntity player) {
        PlayerData.set.dest.setDest(player, "f");
    }
    public static void clear(ServerPlayerEntity player, MutableText reason) {
        MutableText msg = CUtl.tag()
                .append(lang("cleared",
                        lang("cleared_2").setStyle(CUtl.C('a'))));
        if (reason == null) {
            if (!checkDestination(player)) {
                player.sendMessage(error("dest.already_clear"));
                return;
            }
            clear(player);
            player.sendMessage(CUtl.tag()
                    .append(lang("cleared",
                            lang("cleared_2").setStyle(CUtl.C('a')))));
            return;
        }
        clear(player);
        player.sendMessage(msg.append("\n ").append(reason));
    }

    public static Text setMSG(ServerPlayerEntity player) {
        Text msg = Text.literal(" ");
        if (PlayerData.get.dest.setting.autoclear(player)) {
            Text button = CUtl.button(CUtl.button("off"),CUtl.TC('c'),1,"/dest settings autoclear false n",Text.literal("")
                    .append(Text.literal(CUtl.commandUsage.destSettings()).setStyle(CUtl.C('c')))
                    .append("\n")
                    .append(CUtl.lang("button.state.hover", CUtl.lang("button.on").setStyle(CUtl.C('c'))).setStyle(CUtl.C('7'))));
            msg = Text.literal("").append(msg)
                    .append(lang("set.autoclear_on", button).styled(style -> style
                            .withItalic(true).withColor(CUtl.TC('7'))));
        } else {
            Text button = CUtl.button(CUtl.button("on"),CUtl.TC('a'),1,"/dest settings autoclear true n",Text.literal("")
                    .append(Text.literal(CUtl.commandUsage.destSettings()).setStyle(CUtl.C('a')))
                    .append("\n")
                    .append(CUtl.lang("button.state.hover", CUtl.lang("button.off").setStyle(CUtl.C('c'))).setStyle(CUtl.C('7'))));
            msg = Text.literal("").append(msg)
                    .append(lang("set.autoclear_off", button).styled(style -> style
                    .withItalic(true).withColor(CUtl.TC('7'))));
        }
        return msg;
    }
    public static void set(ServerPlayerEntity player, String xyz) {
        if (!checkDist(player, xyz)) PlayerData.set.dest.setDest(player, xyz);
    }
    //responds to player
    //XYZ HAS TO BE XYZ (x n z, x y z)
    public static void set(boolean send, ServerPlayerEntity player, String xyz) {
        if (!send) {
            set(player, xyz);
        }
        String[] split = xyz.split(" ");
        if (split.length != 3) return;
        if (!Utl.xyz.check(xyz)) {
            player.sendMessage(error("coordinates"));
            return;
        }
        xyz = Utl.xyz.fix(xyz);
        if (checkDist(player, xyz)) {
            player.sendMessage(error("dest.at"));
            return;
        }
        PlayerData.set.dest.setDest(player, xyz);
        player.sendMessage(Text.literal("").append(CUtl.tag())
                .append(lang("set",
                        Text.literal(Utl.xyz.PFormat(xyz)).setStyle(CUtl.sS()))));
        player.sendMessage(setMSG(player));
    }
    public static void setName(ServerPlayerEntity player, String name, boolean convert) {
        if (!saved.getNames(player).contains(name)) {
            player.sendMessage(error("dest.invalid"));
            return;
        }
        int key = saved.getNames(player).indexOf(name);
        Text ctxt = Text.literal("");
        String xyz = saved.getCLocations(player).get(key);
        if (convert) {
            if (!xyz.equals(Utl.dim.convertXYZ(player, saved.getCLocations(player).get(key), saved.getDimensions(player).get(key))))
                ctxt = Text.literal(" ").append(lang("converted").setStyle(CUtl.C('7')).styled(style -> style.withItalic(true)));
            xyz = Utl.dim.convertXYZ(player, saved.getCLocations(player).get(key), saved.getDimensions(player).get(key));
        } else xyz = saved.getCLocations(player).get(key);
        if (checkDist(player, xyz)) {
            player.sendMessage(error("dest.at"));
            return;
        }
        set(player, Utl.xyz.fix(xyz));
        player.sendMessage(Text.literal("").append(CUtl.tag())
                .append(lang("set",
                        Text.literal(saved.getNames(player).get(key)).setStyle(CUtl.sS())
                        .append(Text.literal(" ("+Utl.xyz.PFormat(xyz)+")").setStyle(CUtl.C('7')))
                        .append(ctxt))));
        player.sendMessage(setMSG(player));
    }
    //CONVERT XYZ
    public static void setConvert(ServerPlayerEntity player, String xyz, String DIM) {
        if (!Utl.dim.checkValid(DIM)) {
            player.sendMessage(error("dimension"));
            return;
        }
        String[] split = xyz.split(" ");
        if (split.length != 3) return;
        if (!Utl.xyz.check(xyz)) {
            player.sendMessage(error("coordinates"));
            return;
        }
        xyz = Utl.xyz.fix(xyz);
        Text ctxt = Text.literal("");
        if (!xyz.equals(Utl.dim.convertXYZ(player, xyz, DIM))) ctxt = Text.literal(" ").append(lang("converted").setStyle(CUtl.C('7')).styled(style -> style.withItalic(true)));
        xyz = Utl.dim.convertXYZ(player, xyz, DIM);
        if (checkDist(player, xyz)) {
            player.sendMessage(error("dest.at"));
            return;
        }
        PlayerData.set.dest.setDest(player, xyz);
        player.sendMessage(Text.literal("").append(CUtl.tag())
                .append(lang("set",
                        Text.literal(Utl.xyz.PFormat(xyz)).setStyle(CUtl.sS()).append(ctxt))));
        player.sendMessage(setMSG(player));
    }
    //set to player (sends msg)
    public static void setPlayer(ServerPlayerEntity player, ServerPlayerEntity pl) {
        PlayerData.set.dest.setDest(player, Utl.player.name(pl));
        player.sendMessage(Text.literal("").append(CUtl.tag())
                        .append(lang("track.accepted",
                                Text.literal(Utl.player.name(pl)).setStyle(CUtl.sS()))));
        player.sendMessage(setMSG(player));
        pl.sendMessage(Text.literal("").append(CUtl.tag())
                .append(lang("track.accept",
                        Text.literal(Utl.player.name(player)).setStyle(CUtl.sS())))
                .append(" ")
                .append(CUtl.button(CUtl.button("off"),CUtl.TC('c'),1,"/dest settings track false n",Text.literal("")
                        .append(Text.literal(CUtl.commandUsage.destSettings()).setStyle(CUtl.C('c')))
                        .append("\n")
                        .append(CUtl.lang("button.state.hover", CUtl.lang("button.off").setStyle(CUtl.C('c'))).setStyle(CUtl.C('7'))))));
    }
    public static void silentSetPlayer(ServerPlayerEntity player, ServerPlayerEntity pl) {
        PlayerData.set.dest.setDest(player, Utl.player.name(pl));
    }
    public static void suspend(ServerPlayerEntity player, String tplayerName, int timeM, MutableText reason) {
        PlayerData.set.dest.suspended.expire(player, timeM*60);
        PlayerData.set.dest.suspended.target(player, tplayerName);
        Destination.clear(player);
        Text text = CUtl.tag()
                .append(lang("suspended",
                        lang("suspended_time",timeM).setStyle(CUtl.C('7'))))
                .append("\n ").append(reason.styled(style -> style.withItalic(true).withColor(CUtl.TC('7'))));
        player.sendMessage(text);
    }
    public static boolean checkDestination(ServerPlayerEntity player) {
        return !get(player, "xyz").equals("f");
    }

    public static class saved {
        public static List<String> getList(ServerPlayerEntity player) {
            return PlayerData.get.dest.getSaved(player);
        }
        public static void setList(ServerPlayerEntity player, List<String> list) {
            PlayerData.set.dest.setSaved(player, list);
        }
        public static List<String> getListIndex(ServerPlayerEntity player, Integer i) {
            return Arrays.asList(getList(player).get(i).split(" "));
        }
        public static List<String> getNames(ServerPlayerEntity player) {
            List<String> list = getList(player);
            List<String> all = new ArrayList<>();
            for (String i: list) {
                all.add(i.split(" ")[0]);
            }
            return all;
        }
        public static List<String> getPLocations(ServerPlayerEntity player) {
            List<String> list = getList(player);
            List<String> all = new ArrayList<>();
            for (String i: list) {
                all.add(Utl.xyz.PFormat(i.split(" ")[1]));
            }
            return all;
        }
        public static List<String> getCLocations(ServerPlayerEntity player) {
            List<String> list = getList(player);
            List<String> all = new ArrayList<>();
            for (String i: list) {
                all.add(Utl.xyz.CFormat(i.split(" ")[1]));
            }
            return all;
        }
        public static List<String> getDimensions(ServerPlayerEntity player) {
            List<String> list = getList(player);
            List<String> all = new ArrayList<>();
            for (String i: list) {
                all.add(i.split(" ")[2]);
            }
            return all;
        }
        public static List<String> getColors(ServerPlayerEntity player) {
            List<String> list = getList(player);
            List<String> all = new ArrayList<>();
            for (String i: list) {
                all.add(i.split(" ")[3]);
            }
            return all;
        }
        public static Integer getMaxPage(ServerPlayerEntity player) {
            double i = getList(player).size() - 1;
            i = i / 8;
            i = i - 0.5;
            return (int) Math.round(i) + 1;
        }
        public static Integer getPGOf (ServerPlayerEntity player, String name) {
            List<String> names = getNames(player);
            if (!names.contains(name)) return 1;
            double i = names.indexOf(name);
            i = i / 8;
            i = i - 0.5;
            return (int) Math.round(i) + 1;
        }
        public static void add(boolean send, ServerPlayerEntity player, String name, String xyz, String dimension, String color) {
            List<String> names = getNames(player);
            List<String> all = getList(player);
            if (color == null) color = "white";
            if (names.contains(name)) {
                if (send) player.sendMessage(error("dest.saved.duplicate"));
                return;
            }
            if (name.length() > 16) {
                if (send) player.sendMessage(error("dest.saved.length",16));
                return;
            }
            if (!Utl.dim.checkValid(dimension)) {
                if (send) player.sendMessage(error("dimension"));
                return;
            }
            color = Utl.color.fix(color,false,"white");
            //todo config file shit
//            if (getList(player).size() >= Utl.Config.maxSavedDestinations()) {
//                if (send) player.sendMessage(CUtl.error("Reached the max number of saved destinations!"));
//                return;
//            }
            if (!Utl.xyz.check(xyz)) {
                player.sendMessage(error("coordinates"));
                return;
            }
            dimension = Utl.dim.CFormat(dimension);
            xyz = Utl.xyz.fix(xyz);
            xyz = Utl.xyz.DFormat(xyz);

            all.add(name+" "+xyz+" "+dimension.toLowerCase()+" "+color.toLowerCase());
            setList(player, all);
            if (send) {
                player.sendMessage(Text.literal("").append(CUtl.tag())
                        .append(lang("saved.add", Text.literal("")
                                        .append(Utl.color.add(color,name + " "))
                                                .append(Text.literal("("+Utl.xyz.PFormat(xyz) + ")").setStyle(CUtl.C('7')))
                                        .styled(style -> style.withItalic(true)),
                                Text.literal(Utl.dim.PFormat(dimension).toUpperCase()).setStyle(CUtl.sS()))));
            }
        }
        public static void delete(boolean send, ServerPlayerEntity player, String name) {
            List<String> names = getNames(player);
            List<String> all = getList(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            int pg = getPGOf(player, name);
            all.remove(names.indexOf(name));
            setList(player, all);
            if (send) {
                player.sendMessage(Text.literal("").append(CUtl.tag())
                        .append(lang("saved.delete",
                                Text.literal(name).setStyle(CUtl.sS()))));
                Utl.player.sendAs("dest saved "+pg, player);
            }
        }
        public static void editName(boolean send, ServerPlayerEntity player, String name, String newName) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            if (names.contains(newName)) {
                if (send) player.sendMessage(error("dest.saved.duplicat"));
                return;
            }
            if (newName.length() > 16) {
                if (send) player.sendMessage(error("dest.saved.length", 16));
                return;
            }
            int i = names.indexOf(name);
            List<String> all = getList(player);
            List<String> current = getListIndex(player, i);
            current.set(0, newName);
            all.set(i, String.join(" ", current));
            setList(player, all);
            if (send) {
                player.sendMessage(Text.literal("").append(CUtl.tag())
                                .append(lang("saved.name",
                                        Text.literal(name).setStyle(CUtl.sS()),
                                        Text.literal(newName).setStyle(CUtl.sS()))));
                Utl.player.sendAs("dest saved edit "+newName, player);
            }
        }
        public static void editOrder(boolean send, ServerPlayerEntity player, String name, String orderNumber) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            if (!Utl.isInt(orderNumber)) {
                if (send) player.sendMessage(error("number"));
                return;
            }
            int newOrderNum = Integer.parseInt(orderNumber);
            if (newOrderNum == 0) newOrderNum = 1;
            List<String> all = getList(player);
            String move = all.get(names.indexOf(name));
            //IF ORDER NUM TOO HIGH
            if (newOrderNum > all.size()) {
                all.remove(move);
                all.add(all.size(), move);
            } else {
                all.remove(move);
                all.add(newOrderNum - 1, move);
            }
            setList(player, all);
            if (send) {
                player.sendMessage(Text.literal("").append(CUtl.tag())
                                .append(lang("saved.order",
                                        Text.literal(name).setStyle(CUtl.sS()),
                                        Text.literal(""+(getList(player).indexOf(move)+1)).setStyle(CUtl.sS()))));
                Utl.player.sendAs("dest saved edit "+name, player);
            }
        }
        public static void editLocation(boolean send, ServerPlayerEntity player, String name, String xyz) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            if (!Utl.xyz.check(xyz)) {
                player.sendMessage(error("coordinates"));
                return;
            }
            xyz = Utl.xyz.fix(xyz);
            int i = names.indexOf(name);
            if (getPLocations(player).get(i).equals(xyz)) {
                if (send) player.sendMessage(error("dest.saved.duplicate.coordinates", xyz));
                return;
            }
            List<String> all = getList(player);
            List<String> current = getListIndex(player, i);
            current.set(1, Utl.xyz.DFormat(xyz));
            all.set(i, String.join(" ", current));
            setList(player, all);
            if (send) {
                player.sendMessage(Text.literal("").append(CUtl.tag())
                                .append(lang("saved.set",
                                        Text.literal(name).setStyle(CUtl.sS()),
                                        Text.literal(xyz).setStyle(CUtl.sS()))));
                Utl.player.sendAs("dest saved edit "+name, player);
            }
        }
        public static void editDimension(boolean send, ServerPlayerEntity player, String name, String dimension) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            int i = names.indexOf(name);
            if (!Utl.dim.checkValid(dimension)) {
                if (send) player.sendMessage(error("dimension"));
                return;
            }
            if (getDimensions(player).get(i).equalsIgnoreCase(Utl.dim.CFormat(dimension))) {
                if (send) player.sendMessage(error("dest.saved.duplicate.dimension", Utl.dim.PFormat(dimension).toUpperCase()));
                return;
            }
            List<String> all = getList(player);
            List<String> current = getListIndex(player, i);
            current.set(2, Utl.dim.CFormat(dimension));
            all.set(i, String.join(" ", current));
            setList(player, all);
            if (send) {
                player.sendMessage(Text.literal("").append(CUtl.tag())
                                .append(lang("saved.dimension",
                                        Text.literal(name).setStyle(CUtl.sS()),
                                        Text.literal(Utl.dim.PFormat(dimension).toUpperCase()).setStyle(CUtl.sS()))));
                Utl.player.sendAs("dest saved edit "+name, player);
            }
        }
        public static void editColor(boolean send, ServerPlayerEntity player, String name, String color) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            int i = names.indexOf(name);
            color = Utl.color.fix(color,false,"white");
            if (getColors(player).get(i).equals(color.toLowerCase())) {
                if (send) player.sendMessage(error("dest.saved.duplicate.color",
                        Utl.color.add(getColors(player).get(i),getColors(player).get(i))));
                return;
            }
            List<String> all = getList(player);
            List<String> current = getListIndex(player, i);
            current.set(3, color.toLowerCase());
            all.set(i, String.join(" ", current));
            setList(player, all);
            if (send) {
                player.sendMessage(Text.literal("").append(CUtl.tag())
                                .append(lang("saved.color",
                                        Text.literal(name).setStyle(CUtl.sS()),
                                        Utl.color.add(color,Utl.color.formatPlayer(color,true)))));
                Utl.player.sendAs("dest saved edit "+name, player);
            }
        }
        public static void viewDestinationUI(boolean send, ServerPlayerEntity player, String name) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            MutableText msg = Text.literal(" ");
            msg
                    .append(lang("ui.saved.edit").setStyle(CUtl.pS()))
                    .append(Text.literal("\n                                               \n").styled(style -> style.withStrikethrough(true)));
            int i = names.indexOf(name);

            msg
                    .append(" ")
                    //NAME
                    .append(CUtl.button("✎",CUtl.HEX(CUtl.c.edit),2,"/dest saved edit name " + names.get(i) + " ",
                            CUtl.lang("button.saved.edit.hover",
                                    CUtl.lang("button.saved.edit.hover_2").setStyle(Style.EMPTY.withColor(CUtl.HEX(CUtl.c.edit))))))
                    .append(" ")
                    .append(lang("saved.edit.name").setStyle(CUtl.pS()))
                    .append(" "+names.get(i))
                    .append("\n ")
                    //COLOR
                    .append(CUtl.button("✎",CUtl.HEX(CUtl.c.edit),2,"/dest saved edit color " + names.get(i) + " ",
                            CUtl.lang("button.saved.edit.hover",
                                    CUtl.lang("button.saved.edit.hover_2").setStyle(Style.EMPTY.withColor(CUtl.HEX(CUtl.c.edit))))))
                    .append(" ")
                    .append(lang("saved.edit.color").setStyle(CUtl.pS()))
                    .append(" ")
                    .append(Utl.color.add(getColors(player).get(i),Utl.color.formatPlayer(getColors(player).get(i),true)))
                    .append("\n ")
                    //ORDER
                    .append(CUtl.button("✎",CUtl.HEX(CUtl.c.edit),2,"/dest saved edit order " + names.get(i) + " ",
                            CUtl.lang("button.saved.edit.hover",
                                    CUtl.lang("button.saved.edit.hover_2").setStyle(Style.EMPTY.withColor(CUtl.HEX(CUtl.c.edit))))))
                    .append(" ")
                    .append(lang("saved.edit.order").setStyle(CUtl.pS()))
                    .append(" "+(i + 1))
                    .append("\n ")
                    //DIMENSION
                    .append(CUtl.button("✎",CUtl.HEX(CUtl.c.edit),2,"/dest saved edit dim " + names.get(i) + " ",
                            CUtl.lang("button.saved.edit.hover",
                                    CUtl.lang("button.saved.edit.hover_2").setStyle(Style.EMPTY.withColor(CUtl.HEX(CUtl.c.edit))))))
                    .append(" ")
                    .append(lang("saved.edit.dimension").setStyle(CUtl.pS()))
                    .append(" "+Utl.dim.PFormat(getDimensions(player).get(i)))
                    .append("\n ")
                    //LOCATION
                    .append(CUtl.button("✎",CUtl.HEX(CUtl.c.edit),2,"/dest saved edit loc " + names.get(i) + " ",
                            CUtl.lang("button.saved.edit.hover",
                                    CUtl.lang("button.saved.edit.hover_2").setStyle(Style.EMPTY.withColor(CUtl.HEX(CUtl.c.edit))))))
                    .append(" ")
                    .append(lang("saved.edit.location").setStyle(CUtl.pS()))
                    .append(" "+getPLocations(player).get(i))
                    .append("\n       ");
                    //SEND BUTTON
            if (PlayerData.get.dest.setting.send(player) && DirectionHUD.server.isRemote()) {
                msg.append(CUtl.button("SEND", CUtl.HEX(CUtl.c.send), 2, "/dest send saved " + names.get(i) + " ",
                        Text.literal("Click to send the destination to another player")))
                        .append(" ");
            }
            //SET BUTTON
            msg.append(CUtl.CButton.dest.set("/dest set saved " + names.get(i))).append(" ");
            //CONVERT
            if (Utl.dim.showConvertButton(Utl.player.dim(player), getDimensions(player).get(i))) {
                msg.append(CUtl.CButton.dest.convert("/dest set saved " + names.get(i) + " convert"));
            }
            //DELETE
            msg = Text.literal("").append(msg)
                    .append("\n\n ")
                    .append(CUtl.button("DELETE", CUtl.TC('c'),2,"/dest remove " + names.get(i),Text.literal("")
                            .append(Text.literal("Click to delete this destination").setStyle(CUtl.C('c')))))
                    .append(" ")
                    //BACK
                    .append(CUtl.CButton.back("/dest saved " + getPGOf(player, name)));
            msg = Text.literal("").append(msg)
                            .append(Text.literal("\n                                               ").styled(style -> style.withStrikethrough(true)));
            player.sendMessage(msg);
        }
        public static void UI(ServerPlayerEntity player, int pg) {
            Text addB = CUtl.button(CUtl.button("add"), CUtl.HEX(CUtl.c.save), 2, "/dest saved add ", Text.literal("")
                    .append(Text.literal("/dest saved add <name> (x) (y) (z) (dimension) (color)").setStyle(CUtl.HEXS(CUtl.c.save)))
                            .append("\n").append(CUtl.lang("button.add.hover").setStyle(CUtl.C('f'))));
            Text msg = Text.literal(" ");
            msg = Text.literal("").append(msg)
                            .append(lang("ui.saved").setStyle(CUtl.pS()))
                            .append(Text.literal("\n                                               \n").styled(style -> style.withStrikethrough(true)));
            List<String> names = getNames(player);
            if (pg > getMaxPage(player)) {
                pg = 1;
            }
            if (pg == 0) pg = 1;
            String plDimension = player.getWorld().getRegistryKey().getValue().getPath();
            if (names.size() != 0) {
                for (int i = 1; i <= 8; i++) {
                    int realInt = i + ((pg - 1) * 8) - 1;
                    if (names.size() > realInt) {
                        String dimension = getDimensions(player).get(realInt);
                        msg = Text.literal("").append(msg)
                                //DIM
                                .append(Text.literal(" ["))
                                .append(Text.literal(Utl.dim.getLetter(getDimensions(player).get(realInt)))
                                        .styled(style -> style.withColor(CUtl.HEX(Utl.dim.getHEX(getDimensions(player).get(realInt))))))
                                .append(Text.literal("] "))
                                //NAME
                                .append(Utl.color.add(getColors(player).get(realInt),names.get(realInt)+" ").styled(style -> style
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                Text.literal(getPLocations(player).get(realInt)).setStyle(CUtl.C('7'))))))
                                //EDIT
                                .append(CUtl.button(CUtl.button("edit"), CUtl.HEX(CUtl.c.edit),1,"/dest saved edit " + names.get(realInt),Text.literal("")
                                                .append(CUtl.lang("button.edit.hover").setStyle(CUtl.HEXS(CUtl.c.edit)))))
                                .append(" ")
                                //SET
                                .append(CUtl.CButton.dest.set("/dest set saved " + names.get(realInt)));
                        //CONVERT
                        if (Utl.dim.showConvertButton(plDimension, dimension)) {
                            msg = Text.literal("").append(msg)
                                    .append(" ")
                                    .append(CUtl.CButton.dest.convert("/dest set saved " + names.get(realInt) + " convert"));
                        }
                        msg = Text.literal("").append(msg).append("\n");
                    }
                }
            } else {
                msg = Text.literal("").append(msg)
                        .append(" ").append(lang("saved.none"))
                        .append("\n ").append(lang("saved.none_2", addB))
                        .append("\n");
                msg = Text.literal("").append(msg)
                        .append(CUtl.button("<<", CUtl.TC('7')))
                        .append(" ")
                        .append(lang("saved.page", 1).setStyle(CUtl.sS()))
                        .append(" ")
                        .append(CUtl.button(">>", CUtl.TC('7')))
                        .append(" ").append(addB).append(" ")
                        .append(CUtl.CButton.back("/dest"))
                        .append(Text.literal("\n                                               ").styled(style -> style.withStrikethrough(true)));
                player.sendMessage(msg);
                return;
            }
            int finalPg = pg;
            msg = Text.literal("").append(msg).append(" ");
            if (pg == 1) {
                msg = Text.literal("").append(msg)
                        .append(CUtl.button("<<", CUtl.TC('7')));
            } else {
                msg = Text.literal("").append(msg)
                        .append(CUtl.button("<<", CUtl.pTC(),1,"/dest saved " + (finalPg - 1)));
            }
            msg = Text.literal("").append(msg).append(" ")
                    .append(lang("saved.page", pg).setStyle(CUtl.sS()))
                    .append(" ");
            if (pg == getMaxPage(player)) {
                msg = Text.literal("").append(msg)
                        .append(CUtl.button(">>", CUtl.TC('7')));
            } else {
                msg = Text.literal("").append(msg)
                        .append(CUtl.button(">>", CUtl.pTC(), 1, "/dest saved " + (finalPg + 1)));
            }
            msg = Text.literal("").append(msg)
                    .append(" ").append(addB).append(" ")
                    .append(CUtl.CButton.back("/dest"))
                    .append(Text.literal("\n                                               ").styled(style -> style.withStrikethrough(true)));
            player.sendMessage(msg);
        }
    }

    public static class lastdeath {
        public static String get(ServerPlayerEntity player, int dim) {
            if (dim==1) {
                if (getC(player)[0].equals("f")) return "false";
                else return Utl.xyz.PFormat(getC(player)[0]);
            }
            if (dim==2) {
                if (getC(player)[1].equals("f")) return "false";
                else return Utl.xyz.PFormat(getC(player)[1]);
            }
            if (dim==3) {
                if (getC(player)[2].equals("f")) return "false";
                else return Utl.xyz.PFormat(getC(player)[2]);
            }
            return "false";
        }
        public static String[] getC(ServerPlayerEntity player) {
            return PlayerData.get.dest.getLastdeath(player).split(" ");
        }
        public static void set(ServerPlayerEntity player, int dim, String xyz) {
            ArrayList<String> s = new ArrayList<>(Arrays.asList(getC(player)));
            if (!xyz.equals("f")) xyz = Utl.xyz.DFormat(xyz);
            if (dim==1) {
                s.remove(0);
                s.add(0, xyz);
                PlayerData.set.dest.setLastdeath(player, String.join(" ", s));
            }
            if (dim==2) {
                s.remove(1);
                s.add(1, xyz);
                PlayerData.set.dest.setLastdeath(player, String.join(" ", s));
            }
            if (dim==3) {
                s.remove(2);
                s.add(xyz);
                PlayerData.set.dest.setLastdeath(player, String.join(" ", s));
            }
        }
        public static void clear(boolean send, ServerPlayerEntity player, String type) {
            Text msg = Text.literal("");
            List<String> locs = new ArrayList<>(Arrays.asList(lastdeath.getC(player)));
            if (type.equals("all")) {
                set(player, 1, "f");
                set(player, 2, "f");
                set(player, 3, "f");
                if (Collections.frequency(locs, "f") == 3) return;
                msg = Text.literal("").append(CUtl.tag())
                        .append(lang("lastdeath.clear",
                                lang("lastdeath.clear_all").setStyle(CUtl.C('c'))));
            }
            if (type.equals("ow")) {
                if (locs.get(0).equals("f")) return;
                set(player, 1, "f");
                msg = Text.literal("").append(CUtl.tag())
                        .append(Text.literal("Cleared "))
                        .append(Text.literal("OVERWORLD").styled(style -> style.withColor(CUtl.HEX(Utl.dim.getHEX("OVERWORLD")))))
                        .append(Text.literal(" lastdeaths"));
            }
            if (type.equals("n")) {
                if (locs.get(1).equals("f")) return;
                set(player, 2, "f");
                msg = Text.literal("").append(CUtl.tag())
                        .append(lang("lastdeath.clear",
                                Text.literal("NETHER").styled(style -> style.withColor(CUtl.HEX(Utl.dim.getHEX("NETHER"))))));
            }
            if (type.equals("e")) {
                if (locs.get(2).equals("f")) return;
                set(player, 3, "f");
                msg = Text.literal("").append(CUtl.tag())
                        .append(lang("lastdeath.clear",
                                Text.literal("END").styled(style -> style.withColor(CUtl.HEX(Utl.dim.getHEX("END"))))));
            }
            if (send) UI(player, msg);
        }
        public static void UI(ServerPlayerEntity player, Text abovemsg) {
            Text noDeath = Text.literal("").append(lang("lastdeath.no_deaths").setStyle(CUtl.C('c')).append("\n "));
            Text msg = Text.literal(" ");
            if (abovemsg != null) msg = Text.literal("").append(abovemsg).append("\n");
            msg = Text.literal("").append(msg)
                    .append(lang("ui.lastdeath").setStyle(CUtl.pS()))
                    .append(Text.literal("\n                                     \n").styled(style -> style.withStrikethrough(true)));
            String pDIM = Utl.player.dim(player);
            //OVERWORLD
            msg = Text.literal("").append(msg)
                    .append(Text.literal(" Overworld: ").styled(style -> style.withColor(CUtl.HEX(Utl.dim.getHEX("OVERWORLD")))));
            if (!(get(player, 1).equals("false"))) {
                msg = Text.literal("").append(msg)
                        .append(Text.literal(get(player, 1) + "\n  "))
                        .append(CUtl.CButton.dest.set("/dest set " + get(player, 1)))
                        .append(" ");
                if (pDIM.equals("NETHER")) {
                    msg = Text.literal("").append(msg)
                            .append(CUtl.CButton.dest.convert("/dest set " + Utl.xyz.divide(get(player, 1))))
                            .append(" ");
                }
                msg = Text.literal("").append(msg)
                        .append(CUtl.button(CUtl.button("clear"), CUtl.TC('c'), 1, "/dest lastdeath cl_ow",
                                CUtl.lang("button.clear.hover_lastdeath").setStyle(CUtl.C('c'))))
                        .append("\n ");
            } else {
                msg = Text.literal("").append(msg).append(noDeath);
            }
            //NETHER
            msg = Text.literal("").append(msg)
                    .append(Text.literal("Nether: ").styled(style -> style.withColor(CUtl.HEX(Utl.dim.getHEX("NETHER")))));
            if (!(get(player, 2).equals("false"))) {
                msg = Text.literal("").append(msg).append(get(player, 2) + "\n  ")
                        .append(CUtl.CButton.dest.set("/dest set " + get(player, 2)))
                        .append(" ");
                if (pDIM.equals("NORMAL")) {
                    msg = Text.literal("").append(msg)
                            .append(CUtl.CButton.dest.convert("/dest set " + Utl.xyz.divide(get(player, 2))))
                            .append(" ");
                }
                msg = Text.literal("").append(msg)
                        .append(CUtl.button(CUtl.button("clear"), CUtl.TC('c'), 1, "/dest lastdeath cl_n",
                                CUtl.lang("button.clear.hover_lastdeath").setStyle(CUtl.C('c'))))
                        .append("\n ");
            } else {
                msg = Text.literal("").append(msg).append(noDeath);
            }

            //END
            msg = Text.literal("").append(msg)
                    .append(Text.literal("End: ").styled(style -> style.withColor(CUtl.HEX(Utl.dim.getHEX("END")))));
            if (!(get(player, 3).equals("false"))) {
                msg = Text.literal("").append(msg).append(get(player, 3) + "\n  ")
                        .append(CUtl.CButton.dest.set("/dest set " + get(player, 3)))
                        .append(" ")
                        .append(CUtl.button(CUtl.button("clear"), CUtl.TC('c'), 1, "/dest lastdeath cl_e",
                                CUtl.lang("button.clear.hover_lastdeath").setStyle(CUtl.C('c'))))
                        .append("\n ");
            } else {
                msg = Text.literal("").append(msg).append(noDeath);
            }
            msg = Text.literal("").append(msg).append("\n      ");
            List<String> locs = new ArrayList<>(Arrays.asList(getC(player)));
            int reset = 0;
            TextColor resetC = CUtl.TC('7');
            if (Collections.frequency(locs, "f")!=3) {
                reset = 1;
                resetC = CUtl.TC('c');
            }
            msg = Text.literal("").append(msg)
                    .append(CUtl.button(CUtl.button("delete"), resetC, reset, "/dest lastdeath cl",
                            CUtl.lang("button.delete.hover_lastdeath", lang("lastdeath.clear_all")).setStyle(CUtl.C('c'))));
            msg = Text.literal("").append(msg).append("  ")
                    .append(CUtl.CButton.back("/dest"))
                    .append(Text.literal("\n                                     ").styled(style -> style.withStrikethrough(true)));
            player.sendMessage(msg);
        }
    }

    public static class player {
        public static void send(ServerPlayerEntity player, String sendPLayer, String xyz, String DIM, String name) {
            ServerPlayerEntity pl = DirectionHUD.server.getPlayerManager().getPlayer(sendPLayer);
            if (pl == null) {
                player.sendMessage(error("player", Text.literal(sendPLayer).setStyle(CUtl.sS())));
                return;
            }
            if (!PlayerData.get.dest.setting.send(player)) {
                player.sendMessage(error("disabled"));
                return;
            }
            if (pl == player) {
                player.sendMessage(error("dest.send.alone"));
                return;
            }
            if (!PlayerData.get.dest.setting.send(pl)) {
                player.sendMessage(error("dest.send.disabled_player", Text.literal(Utl.player.name(pl)).setStyle(CUtl.sS())));
                return;
            }
            if (name.length() > 16) {
                player.sendMessage(error("dest.saved.length",16));
                return;
            }
            MutableText pxyz = null;
            MutableText pname = null;
            String color = "";

            if (DIM.equals("saved")) {
                if (!saved.getNames(player).contains(xyz)) {
                    player.sendMessage(error("dest.invalid"));
                    return;
                }
                int i = saved.getNames(player).indexOf(xyz);
                xyz = saved.getPLocations(player).get(i);
                pxyz = Text.literal(" ("+xyz+")").setStyle(CUtl.C('7'));
                pname = Utl.color.add(saved.getColors(player).get(i),saved.getNames(player).get(i));
                name = saved.getNames(player).get(i);
                DIM = Utl.dim.PFormat(saved.getDimensions(player).get(i));
                color = " "+saved.getColors(player).get(i);
            }

            if (!Utl.dim.checkValid(DIM)) {
                player.sendMessage(error("dimension"));
                return;
            }
            if (!Utl.xyz.check(xyz)) {
                player.sendMessage(error("coordinates"));
                return;
            }

            xyz = Utl.xyz.fix(xyz);
            //huh
            if (name==null) name = lang("send.change_name").getString()+" ";
            else if (pxyz==null) {
                pname = Text.literal(name).setStyle(CUtl.sS());
                pxyz = Text.literal(" ("+xyz+")").setStyle(CUtl.C('7'));
            }
            if (pxyz == null) pxyz = Text.literal(xyz).setStyle(CUtl.sS());
            if (pname == null) pname = Text.literal("");
            String plDimension = pl.getWorld().getRegistryKey().getValue().getPath();

            MutableText msg = Text.literal("\n ");
            msg
                    .append(Text.literal("["))
                    .append(Text.literal(Utl.dim.getLetter(DIM)).setStyle(CUtl.HEXS(Utl.dim.getHEX(DIM))))
                    .append(Text.literal("] "))
                    .append(pname).append(pxyz).append(" ")
                    .append(CUtl.button(CUtl.button("save"), CUtl.HEX(CUtl.c.save),2, "/dest saved add "+ name +" "+ xyz +" "+ DIM + color,
                                    CUtl.lang("button.save.hover").setStyle(CUtl.HEXS(CUtl.c.save))))
                    .append(" ")
                    .append(CUtl.CButton.dest.set("/dest set " + xyz))
                    .append(" ");
            if (Utl.dim.showConvertButton(plDimension, Utl.dim.CFormat(DIM))) {
                msg = Text.literal("").append(msg).append(CUtl.CButton.dest.convert("/dest set " +xyz+" "+DIM)).append(" ");
            }
            player.sendMessage(Text.literal("").append(CUtl.tag())
                            .append(lang("send",
                                    Text.literal(Utl.player.name(pl)).setStyle(CUtl.sS()),
                                    Text.literal("\n ")
                                            .append(Text.literal("["))
                                            .append(Text.literal(Utl.dim.getLetter(DIM)).setStyle(CUtl.HEXS(Utl.dim.getHEX(DIM))))
                                            .append(Text.literal("] "))
                                            .append(pname).append(pxyz))));
            pl.sendMessage(Text.literal("").append(CUtl.tag())
                            .append(lang("send_player",
                                    Text.literal(Utl.player.name(player)).setStyle(CUtl.sS()),
                                    msg)).append("\n ")
                            .append(lang("send.disable", CUtl.CButton.dest.settings()).styled(style -> style
                                    .withColor(CUtl.TC('7')).withItalic(true))));
        }
        public static void track(ServerPlayerEntity player, String player2) {
            ServerPlayerEntity pl = DirectionHUD.server.getPlayerManager().getPlayer(player2);
            if (pl == null) {
                player.sendMessage(error("player",
                        Text.literal(player2).setStyle(CUtl.sS())));
                return;
            }
            if (pl == player) {
                player.sendMessage(error("dest.track.alone"));
                return;
            }
            if (!PlayerData.get.dest.setting.track(player)) {
                player.sendMessage(error("disabled"));
                return;
            }
            if (!PlayerData.get.dest.setting.track(pl)) {
                player.sendMessage(error("dest.track.disabled",
                        Text.literal(Utl.player.name(pl)).setStyle(CUtl.sS())));
                return;
            }
            if (PlayerData.get.dest.getTrackingPending(player)) {
                player.sendMessage(error("dest.track.pending"));
                return;
            }
            if (PlayerData.get.dest.getDest(player).equalsIgnoreCase(Utl.player.name(pl)+"")) {
                player.sendMessage(error("dest.track.already_tracking"));
                return;
            }
            String trackID = Utl.createID();
            PlayerData.set.dest.track.id(player, trackID);
            PlayerData.set.dest.track.expire(player, 90);
            PlayerData.set.dest.track.target(player, Utl.player.name(pl));
            player.sendMessage(Text.literal("").append(CUtl.tag())
                            .append(lang("track",
                                    Text.literal(Utl.player.name(pl)).setStyle(CUtl.sS())))
                            .append("\n ")
                            .append(lang("track_expire", 90).styled(style -> style
                                    .withItalic(true).withColor(CUtl.TC('7')))));
            Text msg = Text.literal("").append(CUtl.tag())
                    .append(lang("track_player",
                            Text.literal(Utl.player.name(player)).setStyle(CUtl.sS())))
                    .append("\n ")
                    .append(CUtl.button(CUtl.button("accept"), CUtl.TC('a'), 1, "/dest track acp "+Utl.player.name(player)+" "+trackID,
                            CUtl.lang("button.accept.hover")))
                    .append(" ")
                    .append(CUtl.button(CUtl.button("deny"), CUtl.TC('c'), 1, "/dest track dny "+Utl.player.name(player)+" "+trackID,
                            CUtl.lang("button.deny.hover")));
            pl.sendMessage(msg);
        }
        public static void trackAccept(ServerPlayerEntity pl, String player2, String ID) {
            ServerPlayerEntity player = DirectionHUD.server.getPlayerManager().getPlayer(player2);
            // player is tracker, pl is tracked
            if (player == null) {
                pl.sendMessage(error("player",
                        Text.literal(player2).setStyle(CUtl.sS())));
                return;
            }
            if (pl == player) {
                pl.sendMessage(error("how"));
                return;
            }
            if (!PlayerData.get.dest.getTrackingPending(player) || !PlayerData.get.dest.track.id(player).equals(ID)) {
                //expired
                pl.sendMessage(error("dest.track.expired"));
                return;
            }
            if (!PlayerData.get.dest.setting.track(player)) {
                pl.sendMessage(error("dest.track.disabled",
                        Text.literal(Utl.player.name(pl)).setStyle(CUtl.sS())));
                PlayerData.set.dest.setTrackNull(player);
                return;
            }
            if (!Objects.equals(PlayerData.get.dest.track.target(player), Utl.player.name(pl))) {
                pl.sendMessage(error("how"));
                return;
            }
            setPlayer(player, pl);
            PlayerData.set.dest.setTrackNull(player);
        }
        public static void trackDeny(ServerPlayerEntity pl, String player2, String ID) {
            // player is tracker, pl is tracked
            ServerPlayerEntity player = DirectionHUD.server.getPlayerManager().getPlayer(player2);
            if (player == null) {
                pl.sendMessage(error("player",
                        Text.literal(player2).setStyle(CUtl.sS())));
                return;
            }
            if (pl == player) {
                pl.sendMessage(error("how"));
                return;
            }
            if (PlayerData.get.dest.track.id(player) == null || !PlayerData.get.dest.track.id(player).equals(ID)) {
                pl.sendMessage(error("dest.track.expired"));
                return;
            }
            if (!Objects.equals(PlayerData.get.dest.track.target(player), Utl.player.name(pl))) {
                pl.sendMessage(error("how"));
                return;
            }
            player.sendMessage(lang("track.denied",
                    Utl.player.name(pl)).setStyle(CUtl.sS()));
            PlayerData.set.dest.setTrackNull(player);
            pl.sendMessage(CUtl.tag(lang("track.deny",
                    Text.literal(Utl.player.name(player)).setStyle(CUtl.sS()))));
        }
    }

    public static class settings {
        public static void change(ServerPlayerEntity player, String type, String setting, boolean Return) {
            Text msg = Text.literal("");
            if (type.equals("autoclearrad")) {
                if (!Utl.isInt(setting)) {
                    player.sendMessage(error("number"));
                    return;
                }
                int i = Integer.parseInt(setting);
                if (i > 15) i = 15;
                if (i < 2) i = 2;
                PlayerData.set.dest.setting.autoclearrad(player, i);
                if (PlayerData.get.dest.setting.autoclear(player)) msg = Text.literal("").append(CUtl.tag())
                        .append(lang("setting.autoclear_rad.set", Text.literal(i+"").setStyle(CUtl.C('a'))));
                else msg = Text.literal("").append(CUtl.tag())
                        .append(lang("setting.autoclear_rad.set", Text.literal(i+"").setStyle(CUtl.C('c'))));
            }
            if (type.equals("particlesdestc")) {
                setting = Utl.color.fix(setting,false, config.defaults.DESTDestParticleColor);
                PlayerData.set.dest.setting.particles.destcolor(player, setting);
                msg = Text.literal("").append(CUtl.tag())
                        .append(lang("setting.particle.dest_color.set", Utl.color.add(setting,Utl.color.formatPlayer(setting,true))));
            }
            if (type.equals("particleslinec")) {
                setting = Utl.color.fix(setting,false, config.defaults.DESTLineParticleColor);
                PlayerData.set.dest.setting.particles.linecolor(player, setting);
                msg = Text.literal("").append(CUtl.tag())
                        .append(lang("setting.particle.line_color.set", Utl.color.add(setting,Utl.color.formatPlayer(setting,true))));
            }
            boolean state = setting.equals("true");
            Text onoff = Text.literal("ON").setStyle(CUtl.C('a'));
            if (!state) onoff = Text.literal("OFF").setStyle(CUtl.C('c'));
            if (type.equals("autoclear")) {
                PlayerData.set.dest.setting.autoclear(player, state);
                msg = Text.literal("").append(CUtl.tag()).append(lang("setting.autoclear.set", onoff));
            }
            if (type.equals("ylevel")) {
                PlayerData.set.dest.setting.ylevel(player, state);
                msg = Text.literal("").append(CUtl.tag()).append(lang("setting.ylevel.set", onoff));
            }
            if (type.equals("send")) {
                PlayerData.set.dest.setting.send(player, state);
                msg = Text.literal("").append(CUtl.tag()).append(lang("setting.send.set", onoff));
            }
            if (type.equals("track")) {
                PlayerData.set.dest.setting.track(player, state);
                msg = Text.literal("").append(CUtl.tag()).append(lang("setting.track.set", onoff));
            }
            if (type.equals("particlesdest")) {
                PlayerData.set.dest.setting.particles.dest(player, state);
                msg = Text.literal("").append(CUtl.tag()).append(lang("setting.particle.dest.set", onoff));
            }
            if (type.equals("particlesline")) {
                PlayerData.set.dest.setting.particles.line(player, state);
                msg = Text.literal("").append(CUtl.tag()).append(lang("setting.particle.line.set", onoff));
            }
            if (Return) UI(player, msg);
            else player.sendMessage(msg);
        }
        public static Text toggleB(boolean button) {
            Text msg;
            if (button) {
                msg = CUtl.button(CUtl.button("on"), CUtl.TC('a'),
                                CUtl.lang("button.state.hover", Text.literal("OFF").setStyle(CUtl.C('c'))));
            } else {
                msg = CUtl.button(CUtl.button("off"), CUtl.TC('c'),
                                CUtl.lang("button.state.hover", Text.literal("ON").setStyle(CUtl.C('a'))));
            }
            return msg;
        }

        public static void UI(ServerPlayerEntity player, Text abovemsg) {
            Text msg = Text.literal("");
            if (abovemsg != null) msg = Text.literal("").append(abovemsg).append("\n");
            msg = Text.literal("").append(msg).append(" ").append(lang("ui.settings").setStyle(CUtl.pS()))
                    .append(Text.literal("\n                              \n").styled(style -> style.withStrikethrough(true)));
            char c;
            if (PlayerData.get.dest.setting.autoclear(player)) c = 'a'; else c = 'c';
            msg = Text.literal("").append(msg)
                    .append(" ")
                    .append(lang("setting.destination").setStyle(CUtl.pS()))
                    .append("\n  ")
                    //AUTOCLEAR
                    .append(lang("setting.autoclear").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("")
                            .append(lang("setting.autoclear.info"))
                            .append("\n")
                            .append(lang("setting.autoclear.info_2")
                                    .styled(style1 -> style1.withItalic(true).withColor(CUtl.TC('7'))))))))
                    .append(Text.literal(" "))
                    .append(Text.literal("").append(toggleB(PlayerData.get.dest.setting.autoclear(player))).styled(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dest settings autoclear " + !PlayerData.get.dest.setting.autoclear(player)))))
                    .append(Text.literal(" "))
                    .append(CUtl.button(PlayerData.get.dest.setting.autoclearrad(player)+"", CUtl.TC(c),2,
                            "/dest settings autoclearrad ", CUtl.lang("button.autoclear_rad.hover").append("\n")
                                    .append(CUtl.lang("button.autoclear_rad.hover_2").styled(style -> style
                                            .withColor(CUtl.TC('7')).withItalic(true)))))
                    .append(Text.literal("\n  "))
                    //YLEVEL
                    .append(lang("setting.ylevel").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            lang("setting.ylevel.info",
                                    lang("setting.ylevel.info_2").setStyle(CUtl.sS()),
                                    lang("setting.ylevel.info_2").setStyle(CUtl.sS()))))))
                    .append(Text.literal(" "))
                    .append(Text.literal("").append(toggleB(PlayerData.get.dest.setting.ylevel(player)))
                            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dest settings ylevel " + !PlayerData.get.dest.setting.ylevel(player)))))
                    .append(Text.literal("\n "))
                    //PARTICLES
                    .append(lang("setting.particle").setStyle(CUtl.pS()))
                    .append("\n  ")
                    //DESTINATION
                    .append(lang("setting.particle.dest").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            lang("setting.particle.dest.info")))))
                    .append(Text.literal(" "))
                    .append(Text.literal("").append(toggleB(PlayerData.get.dest.setting.particle.dest(player))).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dest settings particlesdest " + !PlayerData.get.dest.setting.particle.dest(player)))))
                    .append(Text.literal(" "))
                    //COLOR
                    .append(CUtl.button(CUtl.button("particle"), Utl.color.getTC(PlayerData.get.dest.setting.particle.destcolor(player)), 2,
                            "/dest settings particlesdestc ", CUtl.lang("button.particle.hover")))
                    .append(Text.literal("\n  "))
                    //LINE
                    .append(lang("setting.particle.line").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            lang("setting.particle.line.info")))))
                    .append(Text.literal(" "))
                    .append(Text.literal("").append(toggleB(PlayerData.get.dest.setting.particle.line(player))).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dest settings particlesline " + !PlayerData.get.dest.setting.particle.line(player)))))
                    .append(Text.literal(" "))
                    //COLOR
                    .append(CUtl.button(CUtl.button("particle"),Utl.color.getTC(PlayerData.get.dest.setting.particle.linecolor(player)),2,
                            "/dest settings particleslinec ", CUtl.lang("button.particle.hover")))
                    .append(Text.literal("\n "))
                    //SOCIAL
                    .append(lang("setting.social").setStyle(CUtl.pS()))
                    .append("\n  ")
                    //SEND
                    .append(lang("setting.send").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            lang("setting.send.info")))))
                    .append(Text.literal(" "))
                    .append(Text.literal("").append(toggleB(PlayerData.get.dest.setting.send(player))).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dest settings send " + !PlayerData.get.dest.setting.send(player)))))
                    .append(Text.literal("\n  "))
                    //TRACK
                    .append(lang("setting.track").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            lang("setting.track.info")))))
                    .append(Text.literal(" "))
                    .append(Text.literal("").append(toggleB(PlayerData.get.dest.setting.track(player))).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dest settings track " + !PlayerData.get.dest.setting.track(player)))))
                    .append(Text.literal("\n           "));

            msg = Text.literal("").append(msg)
                    .append(CUtl.CButton.back("/dest"))
                    .append(Text.literal("\n"))
                    .append(Text.literal("                              ").styled(style -> style.withStrikethrough(true)));
            player.sendMessage(msg);
        }
    }

    public static void UI(ServerPlayerEntity player) {
        Text msg = Text.literal(" ");
        msg = Text.literal("").append(msg).append(lang("ui").setStyle(CUtl.pS()))
                .append(Text.literal("\n                                 ").styled(style -> style.withStrikethrough(true)))
                .append(Text.literal("\n "));
        //SAVED
        msg = Text.literal("").append(msg)
                .append(CUtl.CButton.dest.saved())
                .append(Text.literal("  "));
        //SET
        msg = Text.literal("").append(msg)
                .append(CUtl.CButton.dest.set())
                .append(Text.literal("  "));
        //CLEAR
        int clear = 0;
        TextColor clearC = CUtl.TC('7');
        if (!get(player, "xyz").equals("f")) {
            clear = 1;
            clearC = CUtl.TC('c');
        }
        msg = Text.literal("").append(msg)
                .append(CUtl.CButton.dest.clear(clearC,clear))
                .append(Text.literal("\n\n "));
        //LASTDEATH
        msg = Text.literal("").append(msg)
                .append(CUtl.CButton.dest.lastdeath())
                .append(Text.literal(" "));
        //SETTINGS
        msg = Text.literal("").append(msg)
                .append(CUtl.CButton.dest.settings())
                .append(Text.literal("\n\n "));
        //SEND
        int send = 0;
        TextColor sendC = CUtl.TC('7');
        if (PlayerData.get.dest.setting.send(player) && DirectionHUD.server.isRemote()) {
            send = 2;
            sendC = CUtl.HEX(CUtl.c.send);
        }
        msg = Text.literal("").append(msg)
                .append(CUtl.CButton.dest.send(sendC,send))
                .append(Text.literal("  "));
        //TRACK
        int track = 0;
        TextColor trackC = CUtl.TC('7');
        if (PlayerData.get.dest.setting.track(player) && DirectionHUD.server.isRemote()) {
            track = 2;
            trackC = CUtl.HEX(CUtl.c.track);
        }
        msg = Text.literal("").append(msg)
                .append(CUtl.CButton.dest.track(trackC,track))
                .append(Text.literal("  "));

        msg = Text.literal("").append(msg)
                .append(CUtl.CButton.back("/directionhud"))
                .append(Text.literal("\n                                 ").styled(style -> style.withStrikethrough(true)));
        player.sendMessage(msg);
    }
    //1309
    //1470
}
