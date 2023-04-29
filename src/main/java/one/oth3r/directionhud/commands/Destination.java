package one.oth3r.directionhud.commands;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec3d;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Destination {
    private static CTxT lang(String lang) {
        return CUtl.lang("dest."+lang);
    }
    private static CTxT lang(String key, Object... args) {
        return CUtl.lang("dest."+key, args);
    }
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
            if (!PlayerData.get.dest.setting.track(player) || !PlayerData.get.dest.setting.track(argsPlayer)) {
                clear(player);
                player.sendMessage(CUtl.tag().append(lang("cleared", lang("cleared_2").color('a'))).append("\n ")
                        .append(lang("cleared_tracking_off"+(!PlayerData.get.dest.setting.track(argsPlayer)?"_player":"")).italic(true).color('7')).b());
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
    public static void clear(ServerPlayerEntity player, CTxT reason) {
        CTxT msg = CUtl.tag().append(lang("cleared", lang("cleared_2").color('a')));
        if (reason == null) {
            if (!checkDestination(player)) {
                player.sendMessage(error("dest.already_clear"));
                return;
            }
            clear(player);
            player.sendMessage(msg.b());
            return;
        }
        clear(player);
        player.sendMessage(msg.append("\n ").append(reason).b());
    }
    public static CTxT setMSG(ServerPlayerEntity player) {
        boolean ac = PlayerData.get.dest.setting.autoclear(player);
        CTxT btn = CUtl.TBtn(ac?"off":"on").btn(true).color(ac?'c':'a').cEvent(1,"/dest settings autoclear "+!ac+" n").hEvent(
                CTxT.of(CUtl.commandUsage.destSettings()).color(ac?'c':'a').append("\n").append(CUtl.TBtn("state.hover",
                        CUtl.TBtn(ac?"off":"on").color(ac?'c':'a'))));
        return CTxT.of(" ").append(lang("set.autoclear_"+(ac?"on":"off"),btn).color('7').italic(true));
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
        player.sendMessage(CUtl.tag().append(
                lang("set",CUtl.xyzBadge(Utl.xyz.PFormat(xyz),Utl.player.dim(player),null,null))).b());
        player.sendMessage(setMSG(player).b());
    }
    public static void setName(ServerPlayerEntity player, String name, boolean convert) {
        if (!saved.getNames(player).contains(name)) {
            player.sendMessage(error("dest.invalid"));
            return;
        }
        int key = saved.getNames(player).indexOf(name);
        CTxT convertMsg = CTxT.of("");
        String xyz = saved.getCLocations(player).get(key);
        String DIM = saved.getDimensions(player).get(key);
        if (convert && Utl.dim.showConvertButton(Utl.player.dim(player),DIM)) {
            convertMsg.append(" ").append(lang("converted").color('7').italic(true)
                    .hEvent(CUtl.xyzBadge(Utl.xyz.PFormat(xyz),DIM,null,null)));
            xyz = Utl.dim.convertXYZ(player, saved.getCLocations(player).get(key), saved.getDimensions(player).get(key));
            DIM = Utl.player.dim(player);
        } else xyz = saved.getCLocations(player).get(key);
        if (checkDist(player, xyz)) {
            player.sendMessage(error("dest.at"));
            return;
        }
        set(player, Utl.xyz.fix(xyz));
        player.sendMessage(CUtl.tag().append(lang("set",
                CTxT.of("").append(CUtl.xyzBadge(saved.getNames(player).get(key),DIM,
                        saved.getColors(player).get(key),CTxT.of(Utl.xyz.PFormat(xyz)).color('7'))).append(convertMsg))).b());
        player.sendMessage(setMSG(player).b());
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
        CTxT convertMsg = CTxT.of("");
        if (!xyz.equals(Utl.dim.convertXYZ(player, xyz, DIM))) convertMsg.append(" ").append(lang("converted").color('7').italic(true)
                .hEvent(CUtl.xyzBadge(Utl.xyz.PFormat(xyz),DIM,null,null)));
        xyz = Utl.dim.convertXYZ(player, xyz, DIM);
        if (checkDist(player, xyz)) {
            player.sendMessage(error("dest.at"));
            return;
        }
        PlayerData.set.dest.setDest(player, xyz);
        player.sendMessage(CUtl.tag().append(lang("set",
                CTxT.of("").append(CUtl.xyzBadge(Utl.xyz.PFormat(xyz),Utl.player.dim(player),null,null)).append(convertMsg))).b());
        player.sendMessage(setMSG(player).b());
    }
    //set to player (sends msg)
    public static void setPlayer(ServerPlayerEntity player, ServerPlayerEntity pl) {
        PlayerData.set.dest.setDest(player, Utl.player.name(pl));
        player.sendMessage(CUtl.tag().append(lang("track.accepted",CTxT.of(Utl.player.name(pl)).color(CUtl.sTC()))).b());
        player.sendMessage(setMSG(player).b());
        pl.sendMessage(CUtl.tag()
                .append(lang("track.accept", CTxT.of(Utl.player.name(player)).color(CUtl.sTC())))
                .append(" ")
                .append(CUtl.TBtn("off").btn(true).color('c').cEvent(1,"/dest settings track false n").hEvent(
                        CTxT.of(CUtl.commandUsage.destSettings()).color('c').append("\n").append(
                                CUtl.TBtn("state.hover",CUtl.TBtn("off").color('c'))))).b());
    }
    public static void silentSetPlayer(ServerPlayerEntity player, ServerPlayerEntity pl) {
        PlayerData.set.dest.setDest(player, Utl.player.name(pl));
    }
    public static void suspend(ServerPlayerEntity player, String tplayerName, int timeM, CTxT reason) {
        PlayerData.set.dest.suspended.expire(player, timeM*60);
        PlayerData.set.dest.suspended.target(player, tplayerName);
        clear(player);
        player.sendMessage(CUtl.tag()
                .append(lang("suspended",lang("suspended_time",timeM).color('7')))
                .append("\n").append(reason.italic(true).color('7')).b());
    }
    public static boolean checkDestination(ServerPlayerEntity player) {
        return !get(player, "xyz").equals("f");
    }
    public static class commandExecutor {
        public static int setCMD(ServerPlayerEntity player, String[] args) {
            if (!Utl.inBetween(args.length, 2,4)) {
                player.sendMessage(CUtl.usage(CUtl.commandUsage.destSet()));
                return 1;
            }
            // /dest set saved <name> (convert)
            if (args[0].equalsIgnoreCase("saved")) {
                if (!config.DESTSaving) return 1;
                if (args.length == 2) Destination.setName(player, args[1], false);
                if (args.length == 3 && args[2].equalsIgnoreCase("convert")) Destination.setName(player, args[1], true);
                return 1;
            }
            if (!Utl.isInt(args[0]) || !Utl.isInt(args[1])) return 1;
            // /dest set x z
            if (args.length == 2) {
                Destination.set(true, player, args[0] +" n "+ args[1]);
                return 1;
            }
            // /dest set x z DIM (hidden)
            if (args.length == 3 && !Utl.isInt(args[2])) {
                Destination.setConvert(player, args[0] +" n "+ args[1], args[2]);
                return 1;
            }
            // /dest set x y z
            if (args.length == 3 && Utl.isInt(args[2])) {
                Destination.set(true, player, args[0] +" "+ args[1] +" "+ args[2]);
                return 1;
            }
            // /dest set x y z DIM (hidden)
            if (args.length == 4) {
                Destination.setConvert(player, args[0] +" "+ args[1] +" "+ args[2], args[3]);
                return 1;
            }
            return 1;
        }
        public static int addCMD(ServerPlayerEntity player, String[] args) {
            String playerDIM = Utl.player.dim(player);
            //dest saved add <name>
            if (args.length == 1) {
                saved.add(true, player, args[0], player.getBlockX() + " " + player.getBlockZ(), playerDIM, null);
                return 1;
            }
            if (!Utl.inBetween(args.length, 2, 6)) {
                player.sendMessage(CUtl.usage(CUtl.commandUsage.destAdd()));
                return 1;
            }
            //dest saved add <name> color
            //dest saved add <name> dim
            if (args.length == 2) {
                if (Utl.dim.checkValid(args[1])) saved.add(true, player, args[0], Utl.player.XYZ(player), args[1], null);
                else saved.add(true, player, args[0], Utl.player.XYZ(player), playerDIM, args[1]);
                return 1;
            }
            //dest saved add <name> x y
            if (args.length == 3) {
                saved.add(true, player, args[0], args[1] + " " + args[2], playerDIM, null);
                return 1;
            }
            //dest saved add <name> x y color
            if (args.length == 4 && !Utl.isInt(args[3]) && !Utl.dim.checkValid(args[3])) {
                saved.add(true, player, args[0],args[1] + " " + args[2], playerDIM, args[3]);
                return 1;
            }
            //dest saved add <name> x y DIM
            if (args.length == 4 && !Utl.isInt(args[3])) {
                saved.add(true, player, args[0],args[1] + " " + args[2], args[3], null);
                return 1;
            }
            //dest saved add <name> x y z
            if (args.length == 4 && Utl.isInt(args[3])) {
                saved.add(true, player, args[0], args[1] +" "+ args[2] +" "+ args[3], playerDIM, null);
                return 1;
            }
            //dest saved add <name> x y DIM color
            if (args.length == 5 && !Utl.isInt(args[3])) {
                saved.add(true, player, args[0], args[1] +" "+ args[2], args[3], args[4]);
                return 1;
            }
            //dest saved add <name> x y z color
            if (args.length == 5 && !Utl.dim.checkValid(args[4])) {
                saved.add(true, player, args[0], args[1] +" "+ args[2] +" "+ args[3], playerDIM ,args[4]);
                return 1;
            }
            //dest saved add <name> x y z DIM
            if (args.length == 5) {
                saved.add(true, player, args[0], args[1] +" "+ args[2] +" "+ args[3], args[4], null);
            }
            //dest saved add <name> x y z DIM color
            if (args.length == 6) {
                saved.add(true, player, args[0], args[1] +" "+ args[2] +" "+ args[3], args[4], args[5]);
            }
            return 1;
        }
        public static int removeCMD(ServerPlayerEntity player, String[] args) {
            if (!config.DESTSaving) return 1;
            if (args.length == 1) Destination.saved.delete(true, player, args[0]);
            return 1;
        }
        public static int savedCMD(ServerPlayerEntity player, String[] args) {
            if (!config.DESTSaving) {
                player.sendMessage(CUtl.error(CUtl.lang("error.command")));
                return 1;
            }
            if (args.length == 0) {
                saved.UI(player, 1);
                return 1;
            }
            if (args.length == 1 && Utl.isInt(args[0])) {
                saved.UI(player, Integer.parseInt(args[0]));
                return 1;
            }
            if (args[0].equalsIgnoreCase("edit")) {
                if (args.length == 1) return 1;
                if (args.length == 2) saved.viewDestinationUI(true, player, args[1]);
                if (args[1].equalsIgnoreCase("name")) {
                    if (args.length == 3) player.sendMessage(error("dest.edit.name"));
                    if (args.length == 4) saved.editName(true, player, args[2], args[3]);
                }
                if (args[1].equalsIgnoreCase("color")) {
                    if (args.length == 3) player.sendMessage(error("dest.edit.color"));
                    if (args.length == 4) saved.editColor(true, player, args[2], args[3]);
                }
                if (args[1].equalsIgnoreCase("order")) {
                    if (args.length == 3) player.sendMessage(error("dest.edit.order"));
                    if (args.length == 4) saved.editOrder(true, player, args[2], args[3]);
                }
                if (args[1].equalsIgnoreCase("dim")) {
                    if (args.length == 3) player.sendMessage(error("dest.edit.dimension"));
                    if (args.length == 4) saved.editDimension(true, player, args[2], args[3]);
                }
                if (args[1].equalsIgnoreCase("loc")) {
                    if (args.length == 3) player.sendMessage(error("dest.edit.location"));
                    if (args.length == 5) saved.editLocation(true, player, args[2], args[3] +" "+ args[4]);
                    if (args.length == 6) saved.editLocation(true, player, args[2], args[3] +" "+ args[4] +" "+ args[5]);
                }
                return 1;
            }
            //SEND
            if (args[0].equalsIgnoreCase("send")) {
                if (args.length == 2) player.sendMessage(error("dest.send.player"));
                if (args.length == 3) social.send(player,args[2],args[1],"saved",null);
                return 1;
            }
            //ADD
            if (args[0].equalsIgnoreCase("add")) {
                return addCMD(player,Utl.trimStart(args,1));
            }
            player.sendMessage(CUtl.usage(CUtl.commandUsage.destSaved()));
            return 1;
        }
        public static int lastdeathCMD(ServerPlayerEntity player, String[] args) {
            if (!config.deathsaving || !PlayerData.get.dest.setting.lastdeath(player)) return 1;
            if (args.length == 0) {
                Destination.lastdeath.UI(player, null);
                return 1;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("clear_all")) {
                    Destination.lastdeath.clearAll(true, player);
                }
                return 1;
            }
            player.sendMessage(CUtl.usage(CUtl.commandUsage.destLastdeath()));
            return 1;
        }
        public static int settingsCMD(ServerPlayerEntity player, String[] args) {
            if (args.length == 0) Destination.settings.UI(player, null);
            if (args.length == 1 && args[0].equalsIgnoreCase("reset")) Destination.settings.reset(player, false);
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("reset")) Destination.settings.reset(player, true);
                else Destination.settings.change(player, args[0], args[1], true);
            }
            if (args.length == 3) Destination.settings.change(player, args[0], args[1], false);
            return 1;
        }
        public static int sendCMD(ServerPlayerEntity player, String[] args) {
            if (!Utl.inBetween(args.length, 3, 6)) {
                player.sendMessage(CUtl.usage(CUtl.commandUsage.destSend()));
                return 1;
            }
            // /dest send <IGN> saved <name>
            if (args[1].equalsIgnoreCase("saved")) {
                if (args.length > 3) player.sendMessage(CUtl.usage(CUtl.commandUsage.destSend()));
                else Destination.social.send(player, args[0], args[2], "saved", null);
                return 1;
            }
            String DIM = Utl.player.dim(player);
            //dest send <IGN> <xyz or xy> (dimension)
            //dest send <IGN> (name) <xyz or xy> (dimension)
            //dest send IGN x z
            if (args.length == 3) {
                Destination.social.send(player, args[0], args[1]+" "+args[2], DIM, null);
            }
            //dest send IGN NAME x z
            if (args.length == 4 && !Utl.isInt(args[1])) {
                Destination.social.send(player, args[0], args[2]+" "+args[3], DIM, args[1]);
                return 1;
            }
            //dest send IGN x z DIM
            if (args.length == 4 && !Utl.isInt(args[3])) {
                Destination.social.send(player, args[0], args[1]+" "+args[2], args[3], null);
                return 1;
            }
            //dest send IGN x y z
            if (args.length == 4) {
                Destination.social.send(player, args[0], args[1]+" "+args[2]+" "+args[3], DIM, null);
            }
            //dest send IGN NAME x z DIM
            if (args.length == 5 && !Utl.isInt(args[1]) && !Utl.isInt(args[4])) {
                Destination.social.send(player, args[0], args[2]+" "+args[3], args[4], args[1]);
                return 1;
            }
            //dest send IGN NAME x y z
            if (args.length == 5 && !Utl.isInt(args[1])) {
                Destination.social.send(player, args[0], args[2]+" "+args[3]+" "+args[4], DIM, args[1]);
                return 1;
            }
            //dest send IGN x y z DIM
            if (args.length == 5) {
                Destination.social.send(player, args[0], args[1]+" "+args[2]+" "+args[3], args[4], null);
            }
            //dest send IGN NAME x y z DIM
            if (args.length == 6 && !Utl.isInt(args[1])) {
                Destination.social.send(player, args[0], args[2]+" "+args[3]+" "+args[4], args[5],args[1]);
            }
            return 1;
        }
        public static int trackCMD(ServerPlayerEntity player, String[] args) {
            //dest track <name>
            if (args.length == 1) {
                Destination.social.track(player, args[0]);
                return 1;
            }
            if (args.length == 3) {
                //dest track accept/deny <name> <id>
                if (args[0].equalsIgnoreCase("acp")) {
                    Destination.social.trackAccept(player, args[1], args[2]);
                    return 1;
                }
                if (args[0].equalsIgnoreCase("dny")) {
                    Destination.social.trackDeny(player, args[1], args[2]);
                    return 1;
                }
            }
            player.sendMessage(CUtl.usage(CUtl.commandUsage.destTrack()));
            return 1;
        }
    }
    public static class commandSuggester {
        public static CompletableFuture<Suggestions> addCMD(ServerPlayerEntity player, SuggestionsBuilder builder, int pos, String[] args) {
            // add <name> <x> (y) <z> (dim) (color)
            if (pos == 0) return builder.suggest("name").buildFuture();
            // add <name> (<x> (dim) (color))
            if (pos == 1 && args.length == 2) {
                if (!Utl.isInt(args[1])) {
                    for (String s : Utl.color.getList()) builder.suggest(s);
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
            }
            // add <name> (<x>)
            if (pos == 1) return Utl.xyzSuggester(player,builder,"x").buildFuture();
            // add <name> <x> ((y))
            if (pos == 2) {
                if (Utl.isInt(args[1])) return Utl.xyzSuggester(player,builder,"y").buildFuture();
            }
            // add <name> <x> (y) (<z> (dim) (color))
            if (pos == 3) {
                if (Utl.isInt(args[1])) builder.add(Utl.xyzSuggester(player,builder,"z"));
                if (args.length == 4 && !Utl.isInt(args[3])) {
                    for (String s : Utl.color.getList()) builder.suggest(s);
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                }
                return builder.buildFuture();
            }
            // add <name> <x> (y) <z> ((dim) (color))
            if (pos == 4) {
                if (Utl.isInt(args[3])) {
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                    if (args.length == 5 && !Utl.dim.checkValid(args[4]))
                        for (String s : Utl.color.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
                if (Utl.dim.checkValid(args[3]))
                    for (String s : Utl.color.getList()) builder.suggest(s);
                return builder.buildFuture();
            }
            // add <name> <x> (y) <z> (dim) ((color))
            if (pos == 5) {
                if (Utl.isInt(args[3]) && Utl.dim.checkValid(args[4])) {
                    for (String s : Utl.color.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
            }
            return builder.buildFuture();
        }
        public static CompletableFuture<Suggestions> savedCMD(ServerPlayerEntity player, SuggestionsBuilder builder, int pos, String[] args) {
            if (!config.DESTSaving) return builder.buildFuture();
            // saved add
            // saved edit type name <arg>
            // saved send name <IGN>
            if (pos == 0) return builder.suggest("add").buildFuture();
            // saved add
            if (args[0].equalsIgnoreCase("add")) {
                return Destination.commandSuggester.addCMD(player,builder,pos-1,Utl.trimStart(args,1));
            }
            // saved edit
            if (args[0].equalsIgnoreCase("edit")) {
                if (pos < 1) return builder.buildFuture();
                // saved edit type name (<arg>)
                if (args[1].equalsIgnoreCase("loc")) {
                    if (pos == 3) return Utl.xyzSuggester(player,builder,"x").buildFuture();
                    if (pos == 4) return Utl.xyzSuggester(player,builder,"y").buildFuture();
                    if (pos == 5) return Utl.xyzSuggester(player,builder,"z").buildFuture();
                }
                if (pos == 3) {
                    if (args[1].equalsIgnoreCase("name")) builder.suggest("name");
                    if (args[1].equalsIgnoreCase("color")) for (String s : Utl.color.getList()) builder.suggest(s);
                    if (args[1].equalsIgnoreCase("dim")) for (String s : Utl.dim.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
            }
            // saved send
            if (args[0].equalsIgnoreCase("send")) {
                // saved send name (<ign>)
                if (pos != 2) return builder.buildFuture();
                for (String s : Utl.player.getList()) {
                    if (s.equals(Utl.player.name(player))) continue;
                    builder.suggest(s);
                }
                return builder.buildFuture();
            }
            return builder.buildFuture();
        }
        public static CompletableFuture<Suggestions> settingsCMD(SuggestionsBuilder builder, int pos, String[] args) {
            // settings setting <arg>
            // settings <reset>
            if (pos == 0) return builder.suggest("reset").buildFuture();
            if (pos == 1) {
                if (args[0].equals("particlesdestc") || args[0].equals("particleslinec")) {
                    for (String s : Utl.color.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
            }
            return builder.buildFuture();
        }
        public static CompletableFuture<Suggestions> setCMD(ServerPlayerEntity player, SuggestionsBuilder builder, int pos, String[] args) {
            // set <saved> <name> (convert)
            // set <x> (y) <z> (dim)
            if (pos == 0) {
                if (config.DESTSaving) builder.suggest("saved");
                builder.add(Utl.xyzSuggester(player,builder,"x"));
                return builder.buildFuture();
            }
            // set <saved, x> ((name) (y))
            if (pos == 1) {
                if (args[0].equalsIgnoreCase("saved") && config.DESTSaving) {
                    for (String s : Destination.saved.getNames(player)) builder.suggest(s);
                    return builder.buildFuture();
                }
                return Utl.xyzSuggester(player,builder,"y").buildFuture();
            }
            // set <saved> <name> ((convert))
            // set <x> (y) (<z> (dim))
            if (pos == 2) {
                if (!Utl.isInt(args[1])) {
                    return builder.suggest("convert").buildFuture();
                }
                for (String s : Utl.dim.getList()) builder.suggest(s);
                return Utl.xyzSuggester(player,builder,"z").buildFuture();
            }
            // set <x> (y) <z> (dim)
            if (pos == 3) {
                if (Utl.isInt(args[1]))
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                return builder.buildFuture();
            }
            return builder.buildFuture();
        }
        public static CompletableFuture<Suggestions> sendCMD(ServerPlayerEntity player, SuggestionsBuilder builder, int pos, String[] args) {
            // send <player> <saved> <name>
            // send <player> (name) <x> (y) <z> (dimension)
            if (pos == 0) {
                for (String p : Utl.player.getList()) {
                    if (p.equals(Utl.player.name(player))) continue;
                    builder.suggest(p);
                }
                return builder.buildFuture();
            }
            // send <player> (<saved>, (name), <x>)
            if (pos == 1) {
                if (config.DESTSaving) builder.suggest("saved");
                builder.add(Utl.xyzSuggester(player,builder,"x"));
                builder.suggest("name");
                builder.buildFuture();
            }
            // send <player> <saved> (<name>)
            // send <player> (name) (<x>)
            // send <player> <x> ((y))
            if (pos == 2) {
                if (args[1].equalsIgnoreCase("saved") && config.DESTSaving) {
                    for (String s : Destination.saved.getNames(player)) builder.suggest(s);
                    return builder.buildFuture();
                }
                if (!Utl.isInt(args[1])) {
                    return Utl.xyzSuggester(player,builder,"x").buildFuture();
                }
                return Utl.xyzSuggester(player,builder,"y").buildFuture();
            }
            // send <player> (name) <x> ((y))
            // send <player> <x> (y) (<z> (dimension))
            if (pos == 3) {
                if (!Utl.isInt(args[1])) {
                    return Utl.xyzSuggester(player,builder,"y").buildFuture();
                }
                if (args.length == 4)
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                return builder.add(Utl.xyzSuggester(player,builder,"z")).buildFuture();
            }
            // send <player> (name) <x> (y) (<z> (dimension))
            // send <player> <x> (y) <z> ((dimension))
            if (pos == 4) {
                if (!Utl.isInt(args[1])) {
                    if (args.length == 5)
                        for (String s : Utl.dim.getList()) builder.suggest(s);
                    builder.add(Utl.xyzSuggester(player,builder,"z"));
                    return builder.buildFuture();
                }
                for (String s : Utl.dim.getList()) builder.suggest(s);
                return builder.buildFuture();
            }
            // send <player> (name) <x> (y) <z> ((dimension))
            if (pos == 5) {
                if (!Utl.isInt(args[1])) {
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
            }
            return builder.buildFuture();
        }
        public static CompletableFuture<Suggestions> trackCMD(ServerPlayerEntity player, SuggestionsBuilder builder, int pos) {
            // track <player>
            if (pos == 0) {
                for (String p : Utl.player.getList()) {
                    if (p.equals(Utl.player.name(player))) continue;
                    builder.suggest(p);
                }
            }
            return builder.buildFuture();
        }
    }
    public static class saved {
        public static List<String> getList(ServerPlayerEntity player) {
            return PlayerData.get.dest.getSaved(player);
        }
        public static void setList(ServerPlayerEntity player, List<String> list) {
            PlayerData.set.dest.setSaved(player, new ArrayList<>(list));
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
            if (getList(player).size() >= config.MAXSaved) {
                if (send) player.sendMessage(error("dest.saved.max"));
                return;
            }
            if (names.contains(name)) {
                if (send) player.sendMessage(error("dest.saved.duplicate"));
                return;
            }
            if (name.equalsIgnoreCase("saved")) {
                if (send) player.sendMessage(error("dest.saved.not_allowed"));
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
            if (!Utl.xyz.check(xyz)) {
                player.sendMessage(error("coordinates"));
                return;
            }
            color = Utl.color.fix(color==null?"white":color,false,"white");
            String colorMsg = Utl.color.fix(color,false,"white");
            xyz = Utl.xyz.fix(xyz);
            xyz = Utl.xyz.DFormat(xyz);

            all.add(name+" "+xyz+" "+dimension.toLowerCase()+" "+color.toLowerCase());

            setList(player, all);
            if (send) {
                String finalXyz = Utl.xyz.PFormat(xyz);
                CTxT buttons = CTxT.of(" ");
                buttons.append(CUtl.CButton.dest.edit(1,"/dest saved edit " + name))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set saved "+name));
                if (Utl.dim.showConvertButton(Utl.player.dim(player),dimension))
                    buttons.append(" ").append(CUtl.CButton.dest.convert("/dest set saved "+name+" convert"));
                player.sendMessage(CUtl.tag().append(lang("saved.add",
                        CUtl.xyzBadge(name,dimension,colorMsg,CTxT.of(finalXyz).color('7')).append(buttons))).b());
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
                player.sendMessage(CUtl.tag().append(lang("saved.delete",CTxT.of(name).color(CUtl.sTC()))).b());
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
            if (newName.equalsIgnoreCase("saved")) {
                if (send) player.sendMessage(error("dest.saved.not_allowed"));
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
                player.sendMessage(CUtl.tag().append(lang("saved.name",CTxT.of(name).color(CUtl.sTC()),CTxT.of(newName).color(CUtl.sTC()))).b());
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
                player.sendMessage(CUtl.tag().append(lang("saved.order",CTxT.of(name).color(CUtl.sTC()),CTxT.of(""+(getList(player).indexOf(move)+1)).color(CUtl.sTC()))).b());
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
                player.sendMessage(CUtl.tag().append(lang("saved.edit",CTxT.of(name).color(CUtl.sTC()),CTxT.of(xyz).color(CUtl.sTC()))).b());
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
            if (getDimensions(player).get(i).equalsIgnoreCase(dimension)) {
                if (send) player.sendMessage(error("dest.saved.duplicate.dimension", Utl.dim.PFormat(dimension).toUpperCase()));
                return;
            }
            List<String> all = getList(player);
            List<String> current = getListIndex(player, i);
            current.set(2, dimension);
            all.set(i, String.join(" ", current));
            setList(player, all);
            if (send) {
                player.sendMessage(CUtl.tag().append(lang("saved.dimension",CTxT.of(name).color(CUtl.sTC()),CTxT.of(Utl.dim.PFormat(dimension).toUpperCase()).color(CUtl.sTC()))).b());
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
                        CTxT.of(getColors(player).get(i)).color(getColors(player).get(i))));
                return;
            }
            List<String> all = getList(player);
            List<String> current = getListIndex(player, i);
            current.set(3, color.toLowerCase());
            all.set(i, String.join(" ", current));
            setList(player, all);
            if (send) {
                player.sendMessage(CUtl.tag().append(lang("saved.color",CTxT.of(name).color(CUtl.sTC()),CTxT.of(Utl.color.formatPlayer(color,true)).color(color))).b());
                Utl.player.sendAs("dest saved edit "+name, player);
            }
        }
        public static void viewDestinationUI(boolean send, ServerPlayerEntity player, String name) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            CTxT msg = CTxT.of(" ");
            msg.append(lang("ui.saved.edit").color(CUtl.c.saved)).append(CTxT.of("\n                                               \n").strikethrough(true));
            int i = names.indexOf(name);
            msg.append(" ")
                    //NAME
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit name " + names.get(i) + " ")).append(" ")
                    .append(lang("saved.edit.name").color(CUtl.pTC())).append(" "+names.get(i)).append("\n ")
                    //COLOR
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit color " + names.get(i) + " ")).append(" ")
                    .append(lang("saved.edit.color").color(CUtl.pTC())).append(" ")
                    .append(CTxT.of(Utl.color.formatPlayer(getColors(player).get(i),true)).color(getColors(player).get(i))).append("\n ")
                    //ORDER
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit order " + names.get(i) + " ")).append(" ")
                    .append(lang("saved.edit.order").color(CUtl.pTC())).append(" "+(i + 1)).append("\n ")
                    //DIMENSION
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit dim " + names.get(i) + " ")).append(" ")
                    .append(lang("saved.edit.dimension").color(CUtl.pTC())).append(" "+Utl.dim.PFormat(getDimensions(player).get(i))).append("\n ")
                    //LOCATION
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit loc " + names.get(i) + " ")).append(" ")
                    .append(lang("saved.edit.location").color(CUtl.pTC())).append(" "+getPLocations(player).get(i)).append("\n       ");
            //SEND BUTTON
            if (PlayerData.get.dest.setting.send(player) && DirectionHUD.server.isRemote()) {
                msg.append(CUtl.TBtn("dest.send").btn(true).color(CUtl.c.send).cEvent(2,"/dest saved send "+names.get(i)+" ")
                        .hEvent(CTxT.of("/dest saved send "+names.get(i)+" <player>").color(CUtl.c.send)
                                .append("\n").append(CUtl.TBtn("dest.send.hover_saved")))).append(" ");
            }
            //SET BUTTON
            msg.append(CUtl.CButton.dest.set("/dest set saved " + names.get(i))).append(" ");
            //CONVERT
            if (Utl.dim.showConvertButton(Utl.player.dim(player), getDimensions(player).get(i)))
                msg.append(CUtl.CButton.dest.convert("/dest set saved " + names.get(i) + " convert"));
            //DELETE
            msg.append("\n\n ")
                    .append(CUtl.TBtn("delete").btn(true).color('c').cEvent(2,"/dest remove "+names.get(i))
                            .hEvent(CUtl.TBtn("delete.hover_dest").color('c'))).append(" ")
                    //BACK
                    .append(CUtl.CButton.back("/dest saved " + getPGOf(player, name)))
                    .append(CTxT.of("\n                                               ").strikethrough(true));
            player.sendMessage(msg.b());
        }
        public static void UI(ServerPlayerEntity player, int pg) {
            CTxT addB = CUtl.TBtn("dest.add").btn(true).color(CUtl.c.add).cEvent(2,"/dest add ").hEvent(
                    CTxT.of(CUtl.commandUsage.destAdd()).color(CUtl.c.add).append("\n").append(CUtl.TBtn("dest.add.hover",
                            CUtl.TBtn("dest.add.hover_2").color(CUtl.c.add))));
            CTxT msg = CTxT.of(" ");
            msg.append(lang("ui.saved").color(CUtl.c.saved)).append(CTxT.of("\n                                               \n").strikethrough(true));
            List<String> names = getNames(player);
            if (pg > getMaxPage(player)) {
                pg = 1;
            }
            if (pg == 0) pg = 1;
            String plDimension = Utl.player.dim(player);
            if (names.size() != 0) {
                for (int i = 1; i <= 8; i++) {
                    int get = i + ((pg - 1) * 8) - 1;
                    if (names.size() > get) {
                        String dimension = getDimensions(player).get(get);
                        msg     //DIM
                                .append(" ").append(Utl.dim.getLetterButton(dimension)).append(" ")
                                //NAME
                                .append(CTxT.of(names.get(get)).color(getColors(player).get(get)).hEvent(CTxT.of(getPLocations(player).get(get)).color('7'))).append(" ")
                                //EDIT
                                .append(CUtl.CButton.dest.edit(1,"/dest saved edit " + names.get(get))).append(" ")
                                //SET
                                .append(CUtl.CButton.dest.set("/dest set saved " + names.get(get)));
                        //CONVERT
                        if (Utl.dim.showConvertButton(plDimension, dimension))
                            msg.append(" ").append(CUtl.CButton.dest.convert("/dest set saved " + names.get(get) + " convert"));
                        msg.append("\n");
                    }
                }
            } else {
                msg.append(" ").append(lang("saved.none")).append("\n ").append(lang("saved.none_2", addB)).append("\n\n ");
                msg
                        .append(CTxT.of("<<").btn(true).color('7')).append(" ")
                        .append(CUtl.TBtn("dest.saved.page.hover", 1).color(CUtl.sTC())).append(" ")
                        .append(CTxT.of(">>").btn(true).color('7')).append(" ").append(addB).append(" ")
                        .append(CUtl.CButton.back("/dest"))
                        .append(CTxT.of("\n                                               ").strikethrough(true));
                player.sendMessage(msg.b());
                return;
            }
            int finalPg = pg;
            msg.append(" ");
            if (pg == 1) msg.append(CTxT.of("<<").btn(true).color('7'));
            else msg.append(CTxT.of("<<").btn(true).color(CUtl.pTC()).cEvent(1,"/dest saved " + (finalPg-1)));
            msg.append(" ").append(CUtl.TBtn("dest.saved.page.hover", pg).color(CUtl.sTC())).append(" ");
            if (pg == getMaxPage(player)) msg.append(CTxT.of(">>").btn(true).color('7'));
            else msg.append(CTxT.of(">>").btn(true).color(CUtl.pTC()).cEvent(1,"/dest saved " + (finalPg+1)));
            msg.append(" ").append(addB).append(" ")
                    .append(CUtl.CButton.back("/dest"))
                    .append(CTxT.of("\n                                               ").strikethrough(true));
            player.sendMessage(msg.b());
        }
    }
    public static class lastdeath {
        public static HashMap<String,String> getMap(ServerPlayerEntity player) {
            ArrayList<String> deaths = PlayerData.get.dest.getLastdeaths(player);
            HashMap<String,String> output = new HashMap<>();
            for (String s:deaths) {
                String[] split = s.split("\\|");
                output.put(split[0],split[1]);
            }
            return output;
        }
        public static void add(ServerPlayerEntity player, String dim, String xyz) {
            ArrayList<String> deaths = PlayerData.get.dest.getLastdeaths(player);
            if (Utl.dim.checkValid(dim)) deaths.add(dim+"|"+Utl.xyz.fix(xyz));
            PlayerData.set.dest.setLastdeaths(player,deaths);
        }
        public static void clearAll(boolean send, ServerPlayerEntity player) {
            PlayerData.set.dest.setLastdeaths(player,new ArrayList<>());
            if (send) UI(player, lang("lastdeath.clear",CUtl.TBtn("all").color('c')));
        }
        public static void UI(ServerPlayerEntity player, CTxT abovemsg) {
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui.lastdeath").color(CUtl.c.lastdeath)).append(CTxT.of("\n                                  \n").strikethrough(true));
            HashMap<String,String> deathMap = getMap(player);
            int num = 0;
            msg.append(" ");
            for (String s:Utl.dim.getList()) {
                if (!deathMap.containsKey(s)) continue;
                num++;
                String xyz = deathMap.get(s);
                msg
                        .append(CUtl.xyzBadge(xyz,s,null,null)).append("\n  ")
                        .append(CUtl.CButton.dest.add("/dest add "+Utl.dim.PFormat(s).toLowerCase()+"_death "+xyz+" "+s+" "+Utl.dim.getHEX(s).substring(1)))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set "+xyz));
                if (Utl.dim.showConvertButton(Utl.player.dim(player),s)) msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+xyz+" "+s));
                msg.append("\n ");
            }
            int reset = 1;
            TextColor resetC = CUtl.TC('c');
            if (num == 0) {
                reset = 0;
                resetC = CUtl.TC('7');
                msg.append(lang("lastdeath.no_deaths").color('c')).append("\n");
            }
            msg.append("\n      ")
                    .append(CUtl.TBtn("clear").btn(true).color(resetC).cEvent(reset,"/dest lastdeath clear_all")
                    .hEvent(CUtl.TBtn("clear.hover_ld",CUtl.TBtn("all").color('c'))))
                    .append("  ").append(CUtl.CButton.back("/dest"))
                    .append(CTxT.of("\n                                  ").strikethrough(true));
            player.sendMessage(msg.b());
        }
    }
    public static class social {
        public static void send(ServerPlayerEntity player, String sendPLayer, String xyz, String DIM, String name) {
            ServerPlayerEntity pl = DirectionHUD.server.getPlayerManager().getPlayer(sendPLayer);
            if (pl == null) {
                player.sendMessage(error("player", CTxT.of(sendPLayer).color(CUtl.sTC())));
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
                player.sendMessage(error("dest.send.disabled_player",CTxT.of(Utl.player.name(pl)).color(CUtl.sTC())));
                return;
            }
            if (name != null && name.length() > 16) {
                player.sendMessage(error("dest.saved.length",16));
                return;
            }
            String color = "";
            if (DIM.equals("saved")) {
                if (!saved.getNames(player).contains(xyz)) {
                    player.sendMessage(error("dest.invalid"));
                    return;
                }
                int i = saved.getNames(player).indexOf(xyz);
                xyz = saved.getPLocations(player).get(i);
                name = saved.getNames(player).get(i);
                DIM = saved.getDimensions(player).get(i);
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
            CTxT xyzB = CTxT.of("");
            xyz = Utl.xyz.fix(xyz);
            if (name==null) {
                name = lang("send.change_name").getString()+" ";
                xyzB.append(CUtl.xyzBadge(xyz,DIM,null,null));
            } else xyzB.append(CUtl.xyzBadge(name,DIM,color.equals("")?null:color,CTxT.of(xyz).color('7')));
            String plDimension = Utl.player.dim(pl);

            CTxT msg = CTxT.of("\n ");
            msg.append(xyzB).append(" ");
            if (config.DESTSaving)
                msg.append(CUtl.CButton.dest.add("/dest saved add "+name+" "+xyz+" "+DIM+color))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set " + xyz)).append(" ");
            if (Utl.dim.showConvertButton(plDimension, DIM))
                msg.append(CUtl.CButton.dest.convert("/dest set " +xyz+" "+DIM)).append(" ");
            player.sendMessage(CUtl.tag().append(lang("send",CTxT.of(Utl.player.name(pl)).color(CUtl.sTC()),
                                    CTxT.of("\n ").append(xyzB))).b());
            pl.sendMessage(CUtl.tag().append(lang("send_player",CTxT.of(Utl.player.name(player)).color(CUtl.sTC()),msg)).b());
        }
        public static void track(ServerPlayerEntity player, String player2) {
            ServerPlayerEntity pl = DirectionHUD.server.getPlayerManager().getPlayer(player2);
            if (pl == null) {
                player.sendMessage(error("player",CTxT.of(player2).color(CUtl.sTC())));
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
                player.sendMessage(error("dest.track.disabled",CTxT.of(Utl.player.name(pl)).color(CUtl.sTC())));
                return;
            }
            if (PlayerData.get.dest.getTrackingPending(player)) {
                player.sendMessage(error("dest.track.pending"));
                return;
            }
            if (PlayerData.get.dest.getDest(player).equalsIgnoreCase(Utl.player.name(pl)+"")) {
                player.sendMessage(error("dest.track.already_tracking",CTxT.of(Utl.player.name(pl)).color(CUtl.sTC())));
                return;
            }
            String trackID = Utl.createID();
            PlayerData.set.dest.track.id(player, trackID);
            PlayerData.set.dest.track.expire(player, 90);
            PlayerData.set.dest.track.target(player, Utl.player.name(pl));
            player.sendMessage(CUtl.tag().append(lang("track",CTxT.of(Utl.player.name(pl)).color(CUtl.sTC())))
                    .append("\n ").append(lang("track_expire", 90).color('7').italic(true)).b());
            pl.sendMessage(CUtl.tag().append(lang("track_player",CTxT.of(Utl.player.name(player)).color(CUtl.sTC()))).append("\n ")
                    .append(CUtl.TBtn("accept").btn(true).color('a').cEvent(1,"/dest track acp "+Utl.player.name(player)+" "+trackID)
                            .hEvent(CUtl.TBtn("accept.hover"))).append(" ")
                    .append(CUtl.TBtn("deny").btn(true).color('c').cEvent(1,"/dest track dny "+Utl.player.name(player)+" "+trackID)
                            .hEvent(CUtl.TBtn("deny.hover"))).b());
        }
        public static void trackAccept(ServerPlayerEntity pl, String player2, String ID) {
            ServerPlayerEntity player = DirectionHUD.server.getPlayerManager().getPlayer(player2);
            // player is tracker, pl is tracked
            if (player == null) {
                pl.sendMessage(error("player",CTxT.of(player2).color(CUtl.sTC())));
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
                pl.sendMessage(error("dest.track.disabled",CTxT.of(Utl.player.name(pl)).color(CUtl.sTC())));
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
                pl.sendMessage(error("player",CTxT.of(player2).color(CUtl.sTC())));
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
            player.sendMessage(CUtl.tag().append(lang("track.denied",CTxT.of(Utl.player.name(pl)).color(CUtl.sTC()))).b());
            PlayerData.set.dest.setTrackNull(player);
            pl.sendMessage(CUtl.tag().append(lang("track.deny",CTxT.of(Utl.player.name(player)).color(CUtl.sTC()))).b());
        }
    }
    public static class settings {
        public static void reset(ServerPlayerEntity player, boolean Return) {
            PlayerData.set.dest.setting.autoclear(player,config.DESTAutoClear);
            PlayerData.set.dest.setting.autoclearrad(player,config.DESTAutoClearRad);
            PlayerData.set.dest.setting.ylevel(player,config.DESTYLevel);
            PlayerData.set.dest.setting.particles.line(player,config.DESTLineParticles);
            PlayerData.set.dest.setting.particles.linecolor(player,config.DESTLineParticleColor);
            PlayerData.set.dest.setting.particles.dest(player,config.DESTDestParticles);
            PlayerData.set.dest.setting.particles.destcolor(player,config.DESTDestParticleColor);
            PlayerData.set.dest.setting.track(player,config.DESTTrack);
            PlayerData.set.dest.setting.send(player,config.DESTSend);
            CTxT msg = CUtl.tag().append(lang("setting.reset", CUtl.TBtn("all").color('c')));
            if (Return) UI(player, msg);
            else UI(player, null);
        }
        public static void change(ServerPlayerEntity player, String type, String setting, boolean Return) {
            CTxT msg = CUtl.tag();
            if (type.equals("autoclearrad")) {
                if (!Utl.isInt(setting)) {
                    player.sendMessage(error("number"));
                    return;
                }
                int i = Integer.parseInt(setting);
                if (i > 15) i = 15;
                if (i < 2) i = 2;
                PlayerData.set.dest.setting.autoclearrad(player, i);
                msg.append(lang("setting.autoclear_rad.set",CTxT.of(i+"").color(PlayerData.get.dest.setting.autoclear(player)?'a':'c')));
            }
            if (type.equals("particlesdestc")) {
                setting = Utl.color.fix(setting,false, config.defaults.DESTDestParticleColor);
                PlayerData.set.dest.setting.particles.destcolor(player, setting);
                msg.append(lang("setting.particle.dest_color.set",CTxT.of(Utl.color.formatPlayer(setting,true)).color(setting)));
            }
            if (type.equals("particleslinec")) {
                setting = Utl.color.fix(setting,false, config.defaults.DESTLineParticleColor);
                PlayerData.set.dest.setting.particles.linecolor(player, setting);
                msg.append(lang("setting.particle.line_color.set",CTxT.of(Utl.color.formatPlayer(setting,true)).color(setting)));
            }
            boolean state = setting.equals("true");
            CTxT onoff = CTxT.of("ON").color('a');
            if (!state) onoff = CTxT.of("OFF").color('c');
            if (type.equals("autoclear")) {
                PlayerData.set.dest.setting.autoclear(player, state);
                msg.append(lang("setting.autoclear.set", onoff));
            }
            if (type.equals("ylevel")) {
                PlayerData.set.dest.setting.ylevel(player, state);
                msg.append(lang("setting.ylevel.set", onoff));
            }
            if (type.equals("send")) {
                PlayerData.set.dest.setting.send(player, state);
                msg.append(lang("setting.send.set", onoff));
            }
            if (type.equals("track")) {
                PlayerData.set.dest.setting.track(player, state);
                msg.append(lang("setting.track.set", onoff));
            }
            if (type.equals("lastdeath")) {
                PlayerData.set.dest.setting.lastdeath(player, state);
                msg.append(lang("setting.lastdeath.set", onoff));
            }
            if (type.equals("particlesdest")) {
                PlayerData.set.dest.setting.particles.dest(player, state);
                msg.append(lang("setting.particle.dest.set", onoff));
            }
            if (type.equals("particlesline")) {
                PlayerData.set.dest.setting.particles.line(player, state);
                msg.append(lang("setting.particle.line.set", onoff));
            }
            if (Return) UI(player, msg);
            else player.sendMessage(msg.b());
        }
        public static CTxT toggleB(boolean button) {
            return CUtl.TBtn(button?"on":"off").btn(true).color(button?'a':'c').hEvent(CUtl.TBtn("state.hover",
                            CUtl.TBtn(button?"off":"on").color(button?'c':'a')));
        }
        public static void UI(ServerPlayerEntity player, CTxT abovemsg) {
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui.settings").color(CUtl.c.setting)).append(CTxT.of("\n                              \n").strikethrough(true));
            char c;
            if (PlayerData.get.dest.setting.autoclear(player)) c = 'a'; else c = 'c';
            msg.append(" ").append(lang("setting.destination").color(CUtl.pTC())).append(":\n  ")
                    //AUTOCLEAR
                    .append(lang("setting.autoclear").hEvent(lang("setting.autoclear.info").append("\n")
                            .append(lang("setting.autoclear.info_2").color('7').italic(true)))).append(": ")
                    .append(toggleB(PlayerData.get.dest.setting.autoclear(player)).cEvent(1,"/dest settings autoclear "+!PlayerData.get.dest.setting.autoclear(player)))
                    .append(" ")
                    .append(CTxT.of(PlayerData.get.dest.setting.autoclearrad(player)+"").btn(true).color(c).cEvent(2,"/dest settings autoclearrad ")
                            .hEvent(CUtl.TBtn("autoclear_rad.hover").append("\n").append(CUtl.TBtn("autoclear_rad.hover_2").color('7').italic(true))))
                    .append("\n  ")
                    //YLEVEL
                    .append(lang("setting.ylevel").hEvent(lang("setting.ylevel.info",
                            lang("setting.ylevel.info_2").color(CUtl.sTC()),lang("setting.ylevel.info_2").color(CUtl.sTC())))).append(": ")
                    .append(toggleB(PlayerData.get.dest.setting.ylevel(player)).cEvent(1,"/dest settings ylevel "+!PlayerData.get.dest.setting.ylevel(player)))
                    .append("\n ")
                    //PARTICLES
                    .append(lang("setting.particle").color(CUtl.pTC())).append(":\n  ")
                    //DESTINATION
                    .append(lang("setting.particle.dest").hEvent(lang("setting.particle.dest.info"))).append(": ")
                    .append(toggleB(PlayerData.get.dest.setting.particle.dest(player)).cEvent(1,"/dest settings particlesdest "+!PlayerData.get.dest.setting.particle.dest(player)))
                    .append(" ")
                    //COLOR
                    .append(CUtl.TBtn("particle").btn(true).color(PlayerData.get.dest.setting.particle.destcolor(player)).cEvent(2,"/dest settings particlesdestc ").hEvent(CUtl.TBtn("particle.hover")))
                    .append("\n  ")
                    //LINE
                    .append(lang("setting.particle.line").hEvent(lang("setting.particle.line.info"))).append(": ")
                    .append(toggleB(PlayerData.get.dest.setting.particle.line(player)).cEvent(1,"/dest settings particlesline "+!PlayerData.get.dest.setting.particle.line(player)))
                    .append(" ")
                    //COLOR
                    .append(CUtl.TBtn("particle").btn(true).color(PlayerData.get.dest.setting.particle.linecolor(player)).cEvent(2,"/dest settings particleslinec ").hEvent(CUtl.TBtn("particle.hover")))
                    .append("\n ")
                    //FEATURES
                    .append(lang("setting.features").color(CUtl.pTC())).append(":\n  ")
                    //SEND
                    .append(lang("setting.send").hEvent(lang("setting.send.info"))).append(": ")
                    .append(toggleB(PlayerData.get.dest.setting.send(player)).cEvent(1,"/dest settings send "+!PlayerData.get.dest.setting.send(player)))
                    .append("\n  ")
                    //TRACK
                    .append(lang("setting.track").hEvent(lang("setting.track.info"))).append(": ")
                    .append(toggleB(PlayerData.get.dest.setting.track(player)).cEvent(1,"/dest settings track "+!PlayerData.get.dest.setting.track(player)))
                    .append("\n  ");
                    //LASTDEATH
            if (config.deathsaving) msg
                    .append(lang("setting.lastdeath").hEvent(lang("setting.lastdeath.info"))).append(": ")
                    .append(toggleB(PlayerData.get.dest.setting.lastdeath(player)).cEvent(1,"/dest settings lastdeath "+!PlayerData.get.dest.setting.lastdeath(player)))
                    .append("\n");
            msg.append("\n    ")
                    .append(CUtl.TBtn("dest.settings.reset").btn(true).color('c').cEvent(1,"/dest settings reset return")
                            .hEvent(CUtl.TBtn("dest.settings.reset.hover",CUtl.TBtn("all").color('c'))))
                    .append("  ").append(CUtl.CButton.back("/dest")).append("\n")
                    .append(CTxT.of("                              ").strikethrough(true));
            player.sendMessage(msg.b());
        }
    }
    public static void UI(ServerPlayerEntity player) {
        CTxT msg = CTxT.of(" ");
        msg.append(lang("ui").color(CUtl.c.dest)).append(CTxT.of("\n                                  ").strikethrough(true)).append("\n ");
        // lmao this is a mess but is it the best way to do it? dunno
        boolean line1Free = false;
        boolean line2Free = !(PlayerData.get.dest.setting.lastdeath(player) && config.deathsaving);
        //SAVED + ADD
        if (config.DESTSaving) {
            msg.append(CUtl.CButton.dest.saved()).append(CUtl.CButton.dest.add());
            if (!line2Free) msg.append("        ");
            else msg.append("  ");
        } else line1Free = true;
        //SET + CLEAR
        msg.append(CUtl.CButton.dest.set()).append(CUtl.CButton.dest.clear(player));
        if (line1Free) msg.append(" ");
        else msg.append("\n\n ");
        //LASTDEATH
        if (PlayerData.get.dest.setting.lastdeath(player) && config.deathsaving) {
            msg.append(CUtl.CButton.dest.lastdeath());
            if (line1Free) {
                line1Free = false;
                line2Free = true;
                msg.append("\n\n ");
            } else msg.append("  ");
        }
        //SETTINGS
        msg.append(CUtl.CButton.dest.settings());
        if (line1Free) {
            msg.append("\n\n ");
        } else if (line2Free) msg.append("  ");
        else msg.append("\n\n ");
        //SEND
        if (PlayerData.get.dest.setting.send(player) && DirectionHUD.server.isRemote()) {
            msg.append(CUtl.CButton.dest.send());
            if (line2Free && !line1Free) {
                msg.append("\n\n ");
                line2Free = false;
            } else msg.append("   ");
        }
        //TRACK
        if (PlayerData.get.dest.setting.track(player) && DirectionHUD.server.isRemote()) {
            msg.append(CUtl.CButton.dest.track());
            if (line2Free && !line1Free) {
                msg.append("\n\n ");
            } else msg.append("   ");
        }
        //back
        msg.append(CUtl.CButton.back("/directionhud")).append(CTxT.of("\n                                  ").strikethrough(true));
        player.sendMessage(msg.b());
    }
}
