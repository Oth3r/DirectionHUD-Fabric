package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.LoopManager;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;

import java.util.*;
import java.util.List;

public class HUD {
    private static MutableText lang(String key) {
        return CUtl.lang("hud."+key);
    }
    private static String langS(String key) {
        return CUtl.lang("hud."+key).getString();
    }
    private static MutableText lang(String key, Object... args) {
        return CUtl.lang("hud."+key, args);
    }

    public static void build(ServerPlayerEntity player) {
        ArrayList<String> coordinates = new ArrayList<>();
        coordinates.add("pXYZ: ");
        coordinates.add("s"+player.getBlockX()+" "+player.getBlockY()+" "+player.getBlockZ());

        ArrayList<String> destination = new ArrayList<>();
        ArrayList<String> distance = new ArrayList<>();
        ArrayList<String> compass = new ArrayList<>();

        if (!Destination.get(player, "xyz").equals("f")) {
            destination.add("pDEST: ");
            destination.add("s"+Destination.get(player, "xyz"));
            distance.add("p[");
            distance.add("s"+Destination.getDist(player));
            distance.add("p]");
            compass.add("p[");
            compass.add("s"+getCompass(player));
            compass.add("p]");
        }
        ArrayList<String> direction = new ArrayList<>();
        direction.add("p"+HUD.getPlayerDirection(player));
        ArrayList<String> time = new ArrayList<>();
        if (PlayerData.get.hud.setting.time24h(player)) {
            time.add("s"+HUD.getGameTime(false));
        } else {
            time.add("s"+HUD.getGameTime(true)+" ");
            time.add("p"+HUD.getAMPM());
        }
        ArrayList<String> weather = new ArrayList<>();
        weather.add("p"+HUD.getWeatherIcon());

        HashMap<String, ArrayList<String>> modules = new HashMap<>();
        modules.put("coordinates", coordinates);
        modules.put("distance", distance);
        modules.put("destination", destination);
        modules.put("direction", direction);
        modules.put("time", time);
        modules.put("weather", weather);
        modules.put("compass", compass);

        int start = 1;
        MutableText text = Text.literal("");
        for (int i=0; i < order.getEnabled(player).size(); i++) {
            if (!Destination.checkDestination(player)) {
                if (modules.get(order.getEnabled(player).get(i)).equals(destination) ||
                        modules.get(order.getEnabled(player).get(i)).equals(distance) ||
                        modules.get(order.getEnabled(player).get(i)).equals(compass)) continue;
            }
            for (String str : modules.get(order.getEnabled(player).get(i))) {
                String string = str.substring(1);
                if (str.charAt(0) == 'p') {
                    text.append(color.addColor(player,string,1,LoopManager.rainbowF+start,5));
                    if (color.getHUDColors(player)[0].equals("rainbow"))
                        start = start + (string.replaceAll("\\s", "").length()*5);
                } else if (str.charAt(0) == 's') {
                    text.append(color.addColor(player,string,2,LoopManager.rainbowF+start,5));
                    if (color.getHUDColors(player)[1].equals("rainbow"))
                        start = start + (string.replaceAll("\\s", "").length()*5);
                }
            }
            if (i-1 < order.getEnabled(player).size()) text = Text.literal("").append(text).append(" ");
        }
        if (text.equals(Text.literal(""))) return;
        player.sendMessage(text, true);
    }
    public static String getPlayerDirection(ServerPlayerEntity player) {
        double rotation = (player.getYaw() - 180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return "N";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        } else {
            return "?";
        }
    }
    public static String getGameTime(boolean t12hr) {
        int hour = LoopManager.hour;
        String min = "0" + LoopManager.minute;
        min = min.substring(min.length() - 2);
        int minute = Integer.parseInt(min);

        if (t12hr) {
            String time = "";
            if(hour == 0) hour = 12;
            else if(hour > 12) {hour -= 12;}
            time += hour;
            time += ":";
            if(minute < 10) time += "0";
            time += minute;
            return time;
        }
        return hour + ":" + min;
    }
    public static String getAMPM() {
        int hour = LoopManager.hour;
        String ampm = "AM";
        if(hour > 12) {ampm = "PM";}
        else if(hour == 12) ampm = "PM";

        return ampm;
    }
    public static String getWeatherIcon() {
        double time = LoopManager.hour+(LoopManager.minute/100.0);
        if (DirectionHUD.server.getOverworld().isRaining()) {
            String str;
            if (time >= 18 || time < 5.31) {
                str = "☽";
            } else {
                str = "☀";
            }
            if (DirectionHUD.server.getOverworld().isThundering()) return str + "⛈";
            if (DirectionHUD.server.getOverworld().isRaining()) return str + "🌧";
        }
        if (time > 18.32 || time < 5.31) {
            return "☽";
        }
        return "☀";
    }
    public static String getCompass(ServerPlayerEntity player) {
        if (!Destination.checkDestination(player)) return "";
        int x = Integer.parseInt(Destination.get(player, "x")) - player.getBlockX();
        int z = (Integer.parseInt(Destination.get(player, "z")) - player.getBlockZ()) * -1;
        double rotation = (player.getYaw() - 180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        double d = Math.toDegrees(Math.atan2(x, z));
        if (d < 0) d = d + 360;
        if (Utl.inBetweenD(rotation, Utl.sub(d, 15, 360), (d+15)%360)) return "▲";
//        if (Utl.inBetweenR(rotation, d, (d+65)%360)) return "⬉";
        if (Utl.inBetweenD(rotation, d, (d+115)%360)) return "◀";
//        if (Utl.inBetweenR(rotation, d, (d+165)%360)) return "⬋";
//        if (Utl.inBetweenR(rotation, Utl.sub(d, 65, 360), d)) return "⬈";
        if (Utl.inBetweenD(rotation, Utl.sub(d, 115, 360), d)) return "▶";
//        if (Utl.inBetweenR(rotation, Utl.sub(d, 165, 360), d)) return "⬊";
        return "▼";
    }

    public static class order {
        //has to be lowercase
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public static boolean validCheck(String s) {
            if (s.equals("coordinates")) return true;
            if (s.equals("distance")) return true;
            if (s.equals("destination")) return true;
            if (s.equals("direction")) return true;
            if (s.equals("compass")) return true;
            if (s.equals("time")) return true;
            return s.equals("weather");
        }
        public static void reset(ServerPlayerEntity player, boolean Return) {
            PlayerData.set.hud.order(player, config.HUDOrder);
            PlayerData.set.hud.setting.time24h(player, config.HUD24HR);
            PlayerData.set.hud.setModule(player, PlayerData.defaults.hudModule());
            Text msg = Text.literal("").append(CUtl.tag())
                    .append(lang("module.reset",
                            lang("module.reset_2").setStyle(CUtl.C('c'))));
            if (Return) UI(player, msg, null);
            else player.sendMessage(msg);
        }
        public static void move(ServerPlayerEntity player, String module, String direction, boolean Return) {
            ArrayList<String> order = getEnabled(player);
            int pos = order.indexOf(module.toLowerCase());
            if (!validCheck(module)) return;
            Text text = Text.literal("")
                    .append(CUtl.tag())
                    .append(lang("module.move",
                            Text.literal(langName(module)).setStyle(CUtl.sS()),
                            lang("module.move_"+direction)));
            if (direction.equals("down")) {
                if (pos == order.size() - 1) return;
                order.remove(pos);
                order.add(pos + 1, module);
                order.addAll(HUD.order.getDisabled(player));
                HUD.order.setOrderC(player, order);
            } else if (direction.equals("up")) {
                if (pos == 0) return;
                order.remove(pos);
                order.add(pos - 1, module);
                order.addAll(HUD.order.getDisabled(player));
                HUD.order.setOrderC(player, order);
            } else return;
            if (Return) UI(player, text, module);
            else player.sendMessage(text);
        }
        public static void toggle(ServerPlayerEntity player, String module, boolean toggle, boolean Return) {
            if (!HUD.order.validCheck(module)) return;
            Text text = Text.literal("").append(CUtl.tag())
                    .append(lang("module.toggle",
                            CUtl.SBtn(toggle ? "on" : "off"),
                            Text.literal(langName(module)).setStyle(CUtl.sS())));
            //OFF
            if (!toggle && HUD.order.moduleState(player, module)) HUD.order.removeModule(player, module);
            //ON
            else if (toggle && !HUD.order.moduleState(player, module)) HUD.order.addModule(player, module);

            if (Return) UI(player, text, module);
            else player.sendMessage(text);
        }
        public static void setting(ServerPlayerEntity player, String setting, String option, boolean Return) {
            if (setting.equals("time")) {
                if (!PlayerData.get.hud.module.time(player)) return;
                if (!(option.equals("12hr") || option.equals("24hr"))) return;
                PlayerData.set.hud.setting.time24h(player, option.equals("24hr"));
                Text text = Text.literal("").append(CUtl.tag())
                        .append(lang("module.time.change",
                                CUtl.lang("button.time."+option).setStyle(CUtl.sS())));
                if (Return) UI(player, text, "time");
                else player.sendMessage(text);
            }
        }
        public static boolean moduleState(ServerPlayerEntity player, String s) {
            if (s.equalsIgnoreCase("coordinates")) {
                return PlayerData.get.hud.module.coordinates(player);
            }
            if (s.equalsIgnoreCase("distance")) {
                return PlayerData.get.hud.module.distance(player);
            }
            if (s.equalsIgnoreCase("destination")) {
                return PlayerData.get.hud.module.destination(player);
            }
            if (s.equalsIgnoreCase("direction")) {
                return PlayerData.get.hud.module.direction(player);
            }
            if (s.equalsIgnoreCase("compass")) {
                return PlayerData.get.hud.module.compass(player);
            }
            if (s.equalsIgnoreCase("time")) {
                return PlayerData.get.hud.module.time(player);
            }
            if (s.equalsIgnoreCase("weather")) {
                return PlayerData.get.hud.module.weather(player);
            }
            return false;
        }
        public static String allModules() {
            return "coordinates distance compass destination direction time weather";
        }
        public static String[] getOrderC(ServerPlayerEntity player) {
            return PlayerData.get.hud.order(player).split(" ");
        }
        public static void setOrderC(ServerPlayerEntity player, List<String> ls) {
            PlayerData.set.hud.order(player, String.join(" ", ls));
        }
        public static String fixOrder(String order) {
            ArrayList<String> list = new ArrayList<>(List.of(order.split(" ")));
            ArrayList<String> allModules = new ArrayList<>(List.of(allModules().split(" ")));
            list.removeIf(s -> !validCheck(s));
            for (String a: allModules) {
                if (Collections.frequency(list, a) > 1) list.remove(a);
            }
            allModules.removeAll(list);
            list.addAll(allModules);
            return String.join(" ", list);
        }
        public static ArrayList<String> getEnabled(ServerPlayerEntity player) {
            String[] order = getOrderC(player);
            ArrayList<String> list = new ArrayList<>();
            for (String s: order) {
                if (moduleState(player, s)) list.add(s);
            }
            return list;
        }
        public static ArrayList<String> getDisabled(ServerPlayerEntity player) {
            String[] order = getOrderC(player);
            ArrayList<String> list = new ArrayList<>();
            for (String s: order) {
                if (!moduleState(player, s)) list.add(s);
            }
            return list;
        }
        public static void removeModule(ServerPlayerEntity player, String s) {
            if (!validCheck(s)) return;
            ArrayList<String> order = getEnabled(player);
            ArrayList<String> orderD = getDisabled(player);
            if (!order.contains(s)) return;
            order.remove(s);
            orderD.add(s);
            PlayerData.set.hud.module.byName(player, s, false);
            order.addAll(orderD);
            setOrderC(player, order);
        }
        public static void addModule(ServerPlayerEntity player, String s) {
            if (!validCheck(s)) return;
            ArrayList<String> order = getEnabled(player);
            ArrayList<String> orderD = getDisabled(player);
            if (!orderD.contains(s)) return;
            orderD.remove(s);
            order.add(s);
            PlayerData.set.hud.module.byName(player, s, true);
            order.addAll(orderD);
            setOrderC(player, order);
        }

        public static Text arrow(boolean up, boolean gray, String name) {
            if (up) {
                if (gray) {
                    return Text.literal("").append(CUtl.button("▲", CUtl.TC('7')));
                }
                return Text.literal("").append(CUtl.button("▲",CUtl.pTC(),1,"/hud edit move " +name+ " up"));
            }
            if (gray) {
                return Text.literal("").append(CUtl.button("▼", CUtl.TC('7')));
            }
            return Text.literal("").append(CUtl.button("▼",CUtl.pTC(),1,"/hud edit move " +name+ " down"));
        }
        public static Text xButton(String name) {
            return CUtl.button("✕", CUtl.TC('c'),1,"/hud edit state " + name + " false",
                    CUtl.lang("button.module.disable.hover").setStyle(CUtl.C('c')));
        }
        public static String langName(String s) {
            if (s.equalsIgnoreCase("coordinates")) return langS("module.coordinates");
            if (s.equalsIgnoreCase("distance")) return langS("module.distance");
            if (s.equalsIgnoreCase("destination")) return langS("module.destination");
            if (s.equalsIgnoreCase("direction")) return langS("module.direction");
            if (s.equalsIgnoreCase("compass")) return langS("module.compass");
            if (s.equalsIgnoreCase("time")) return langS("module.time");
            if (s.equalsIgnoreCase("weather")) return langS("module.weather");
            return "";
        }
        public static Text moduleName(ServerPlayerEntity player, String s, Text addStart) {
            Text hoverT = Text.literal("");
            if (s.equalsIgnoreCase("coordinates")) {
                hoverT = Text.literal("")
                        .append(lang("module.coordinates.info")).append("\n")
                        .append(color.addColor(player,"XYZ: ",1,15,20))
                        .append(color.addColor(player,"0 0 0",2,95,20));
            }
            if (s.equalsIgnoreCase("distance")) {
                hoverT = Text.literal("")
                        .append(lang("module.distance.info")).append("\n")
                        .append(color.addColor(player,"[",1,15,20))
                        .append(color.addColor(player,"0",2,35,20))
                        .append(color.addColor(player,"]",1,55,20));
            }
            if (s.equalsIgnoreCase("destination")) {
                hoverT = Text.literal("")
                        .append(lang("module.destination.info")).append("\n")
                        .append(color.addColor(player,"DEST: ",1,15,20))
                        .append(color.addColor(player,"0 0 0",2,115,20));
            }
            if (s.equalsIgnoreCase("direction")) {
                hoverT = Text.literal("")
                        .append(lang("module.direction.info")).append("\n")
                        .append(color.addColor(player,"N",1,15,20));
            }
            if (s.equalsIgnoreCase("compass")) {
                hoverT = Text.literal("")
                        .append(lang("module.compass.info")).append("\n")
                        .append(color.addColor(player,"[",1,15,20))
                        .append(color.addColor(player,"▲",2,35,20))
                        .append(color.addColor(player,"]",1,55,20));
            }
            if (s.equalsIgnoreCase("time")) {
                if (PlayerData.get.hud.setting.time24h(player)) {
                    hoverT = Text.literal("")
                            .append(lang("module.time.info")).append("\n")
                            .append(color.addColor(player,"22:22",1,15,20));
                } else {
                    hoverT = Text.literal("")
                            .append(lang("module.time.info")).append("\n")
                            .append(color.addColor(player,"11:11 ",2,15,20))
                            .append(color.addColor(player,"AM",1,115,20));
                }
            }
            if (s.equalsIgnoreCase("weather")) {
                hoverT = Text.literal("")
                        .append(lang("module.weather.info")).append("\n")
                        .append(color.addColor(player,"☀",1,15,20));
            }
            final Text hoverTF = hoverT;
            if (addStart == null) return Text.literal(langName(s)).styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverTF)));
            return Text.literal(langName(s)).styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("")
                    .append(addStart)
                    .append("\n")
                    .append(hoverTF))));
        }
        public static void UI(ServerPlayerEntity player, Text abovemsg, String highlight) {
            if (highlight == null) highlight = "";
            //MODULES
            Text coordinates = Text.literal(" ");
            Text distance = Text.literal(" ");
            Text destination = Text.literal(" ");
            Text compass = Text.literal(" ");
            Text direction = Text.literal(" ");
            Text time = Text.literal(" ");
            Text weather = Text.literal(" ");

            HashMap<String, Text> modules = new HashMap<>();
            modules.put("coordinates", coordinates);
            modules.put("distance", distance);
            modules.put("destination", destination);
            modules.put("compass", compass);
            modules.put("direction", direction);
            modules.put("time", time);
            modules.put("weather", weather);

            //MAKE THE TEXT
            if (getEnabled(player).size() > 0) {
                for (int i = 0; i < getEnabled(player).size(); i++) {
                    String moduleName = getEnabled(player).get(i);
                    Text moduleNameText = moduleName(player, moduleName, null);
                    if (highlight.equals(moduleName)) moduleNameText = Text.literal("").append(moduleNameText).setStyle(CUtl.sS());
                    if (i == 0) {
                        modules.put(moduleName, Text.literal("").append(modules.get(moduleName)).append(arrow(true, true, moduleName)).append(" "));
                        modules.put(moduleName, Text.literal("").append(modules.get(moduleName)).append(xButton(moduleName)).append(" "));
                        //IF ONLY 1
                        if (getEnabled(player).size() == 1) {
                            modules.put(moduleName, Text.literal("").append(modules.get(moduleName)).append(arrow(false, true, moduleName)).append(" "));
                        } else {
                            modules.put(moduleName, Text.literal("").append(modules.get(moduleName)).append(arrow(false, false, moduleName)).append(" "));
                        }
                        modules.put(moduleName, Text.literal("").append(modules.get(moduleName)).append(moduleNameText).append(" "));
                    } else if (i == getEnabled(player).size() - 1) {
                        modules.put(moduleName, Text.literal("").append(modules.get(moduleName))
                                .append(arrow(true, false, moduleName)).append(" ")
                                .append(xButton(moduleName)).append(" ")
                                .append(arrow(false, true, moduleName)).append(" ")
                                .append(moduleNameText).append(" "));
                    } else {
                        modules.put(moduleName, Text.literal("").append(modules.get(moduleName))
                                .append(arrow(true, false, moduleName)).append(" ")
                                .append(xButton(moduleName)).append(" ")
                                .append(arrow(false, false, moduleName)).append(" ")
                                .append(moduleNameText).append(" "));
                    }
                    //ADDING 12HR / 24HR
                    if (moduleName.equals("time")) {
                        if (PlayerData.get.hud.setting.time24h(player)) {
                            modules.put(moduleName, Text.literal("").append(modules.get(moduleName))
                                    .append(CUtl.button(CUtl.SBtn("time.24hr"), CUtl.sTC(), 1, "/hud edit setting time 12hr",
                                            CUtl.lang("button.time.hover",
                                                    CUtl.lang("button.time.12hr").setStyle(CUtl.sS())))));
                        } else {
                            modules.put(moduleName, Text.literal("").append(modules.get(moduleName))
                                    .append(CUtl.button(CUtl.SBtn("time.12hr"), CUtl.sTC(),1,"/hud edit setting time 24hr",
                                            CUtl.lang("button.time.hover",
                                                    CUtl.lang("button.time.24hr").setStyle(CUtl.sS())))));
                        }
                    }
                }
            }
            if (getDisabled(player).size() > 0) {
                for (int i = 0; i < getDisabled(player).size(); i++) {
                    String moduleName = getDisabled(player).get(i);
                    modules.put(moduleName, Text.literal("").append(Text.literal("").append(moduleName(player, moduleName,
                            CUtl.lang("button.module.enable.hover").setStyle(CUtl.C('a')))).setStyle(CUtl.C('7'))).styled(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hud edit state " + moduleName + " true"))));
                }
            }

            Text msg = Text.literal("");
            if (abovemsg != null) msg = Text.literal("").append(abovemsg).append("\n");

            msg = Text.literal("").append(msg).append(" ").append(lang("ui.edit").setStyle(CUtl.pS()))
                    .append(Text.literal("\n                                               \n").styled(style -> style.withStrikethrough(true)));
            if (!getEnabled(player).isEmpty()) {
                for (String s: getEnabled(player)) {
                    msg = Text.literal("").append(msg).append(modules.get(s)).append(Text.literal("\n"));
                }
            } else {
                msg = Text.literal("").append(msg).append(" ")
                        .append(lang("module.none").setStyle(CUtl.C('c'))).append(Text.literal("\n "))
                        .append(lang("module.none_2").setStyle(CUtl.C('c'))).append(Text.literal("\n"));
            }
            if (!getDisabled(player).isEmpty()) {
                msg = Text.literal("").append(msg).append(Text.literal("                                               ").styled(style -> style.withStrikethrough(true)))
                        .append(Text.literal("\n"))
                        .append(lang("ui.edit.disabled").setStyle(CUtl.sS()))
                        .append(Text.literal("\n"));
                Text disabled = Text.literal("");
                for (int i = 0; i < getDisabled(player).size(); i++) {
                    if (i==3) disabled = Text.literal("").append(disabled).append("\n");
                    disabled = Text.literal("").append(disabled).append(" ").append(modules.get(getDisabled(player).get(i)));
                }
                msg = Text.literal("").append(msg).append(disabled).append(Text.literal("\n"));
            }
            msg = Text.literal("").append(msg)
                    .append(Text.literal("          "))
                    .append(CUtl.button(CUtl.SBtn("reset"), CUtl.TC('c'),1,"/hud edit reset",
                            CUtl.lang("button.reset.hover_edit").setStyle(CUtl.C('c'))))
                    .append(Text.literal("  "))
                    .append(CUtl.CButton.back("/hud"))
                    .append(Text.literal("\n"))
                    .append(Text.literal("                                               ").styled(style -> style.withStrikethrough(true)));
            player.sendMessage(msg);
        }
    }
    public static class color {
        public static void reset(ServerPlayerEntity player, String type, boolean Return) {
            String langType;
            if (type == null) {
                PlayerData.set.hud.primary(player, defaultFormat(1));
                PlayerData.set.hud.secondary(player, defaultFormat(2));
                langType = "all";
            } else if (type.equals("pri")) {
                PlayerData.set.hud.primary(player, defaultFormat(1));
                langType = "primary";
            } else if (type.equals("sec")) {
                PlayerData.set.hud.secondary(player, defaultFormat(2));
                langType = "secondary";
            } else return;
            Text text = Text.literal("").append(CUtl.tag())
                    .append(lang("color.reset",
                            lang("color.reset_2").setStyle(CUtl.C('c')),
                            lang("color."+langType)));
            if (Return) UI(player, text);
            else player.sendMessage(text);
        }
        public static void setColor(ServerPlayerEntity player, String type, String color, boolean Return) {
            if (type.equals("primary")) {
                color = Utl.color.fix(color,true,config.defaults.HUDPrimaryColor);
                if (getHUDColors(player)[0].equals(color)) return;
                PlayerData.set.hud.primary(player, color+"-"+getHUDBold(player,1)+"-"+getHUDItalics(player,1));
            } else if (type.equals("secondary")){
                color = Utl.color.fix(color,true,config.defaults.HUDSecondaryColor);
                if (getHUDColors(player)[1].equals(color)) return;
                PlayerData.set.hud.secondary(player, color+"-"+getHUDBold(player,2)+"-"+getHUDItalics(player,2));
            } else return;
            Text text = Text.literal("").append(CUtl.tag())
                    .append(lang("color.set",
                            lang("color."+type),
                            Text.literal("").append(addColor(player,Utl.color.formatPlayer(color,true),
                                    type.equals("primary")?1:2,15,20))));
            if (Return) changeUI(player, type.substring(0,3),text);
            else player.sendMessage(text);
        }
        public static void setBold(ServerPlayerEntity player, String type, boolean state, boolean Return) {
            if (type.equals("primary")) {
                if (getHUDBold(player, 1)==state) return;
                PlayerData.set.hud.primary(player, getHUDColors(player)[0]+"-"+state+"-"+getHUDItalics(player,1));
            } else if (type.equals("secondary")){
                if (getHUDBold(player, 2)==state) return;
                PlayerData.set.hud.secondary(player, getHUDColors(player)[1]+"-"+state+"-"+getHUDItalics(player,2));
            } else return;
            Text text = Text.literal("").append(CUtl.tag())
                    .append(lang("color.set.bold",
                            CUtl.lang("button."+ (state ? "on" : "off")).setStyle(CUtl.C(state ? 'a':'c')),
                            lang("color."+type)));
            if (Return) changeUI(player, type.substring(0,3),text);
            else player.sendMessage(text);
        }
        public static void setItalics(ServerPlayerEntity player, String type, boolean state, boolean Return) {
            if (type.equals("primary")) {
                if (getHUDItalics(player, 1)==state) return;
                PlayerData.set.hud.primary(player, getHUDColors(player)[0]+"-"+getHUDBold(player,1)+"-"+state);
            } else if (type.equals("secondary")){
                if (getHUDItalics(player, 2)==state) return;
                PlayerData.set.hud.secondary(player, getHUDColors(player)[1]+"-"+getHUDBold(player,2)+"-"+state);
            } else return;
            Text text = Text.literal("").append(CUtl.tag())
                    .append(lang("color.set.italics",
                            CUtl.lang("button."+ (state ? "on" : "off")).setStyle(CUtl.C(state ? 'a':'c')),
                            lang("color."+type)));
            if (Return) changeUI(player, type.substring(0,3),text);
            else player.sendMessage(text);
        }
        //"red", "dark_red", "gold", "yellow", "green", "dark_green", "aqua", "dark_aqua",
        // "blue", "dark_blue", "pink", "purple", "white", "gray", "dark_gray", "black"
        public static String defaultFormat(int i) {
            if (i==1) return config.HUDPrimaryColor+"-"+config.HUDPrimaryBold+"-"+config.HUDPrimaryItalics;
            return config.HUDSecondaryColor+"-"+config.HUDSecondaryBold+"-"+config.HUDSecondaryItalics;
        }
        public static String[] getHUDColors(ServerPlayerEntity player) {
            String[] p = PlayerData.get.hud.primary(player).split("-");
            String[] s = PlayerData.get.hud.secondary(player).split("-");
            return (p[0]+" "+s[0]).split(" ");
        }
        public static Boolean getHUDBold(ServerPlayerEntity player, int i) {
            String[] p = PlayerData.get.hud.primary(player).split("-");
            String[] s = PlayerData.get.hud.secondary(player).split("-");
            if (i==1) return Boolean.parseBoolean(p[1]);
            return Boolean.parseBoolean(s[1]);
        }
        public static Boolean getHUDItalics(ServerPlayerEntity player, int i) {
            String[] p = PlayerData.get.hud.primary(player).split("-");
            String[] s = PlayerData.get.hud.secondary(player).split("-");
            if (i==1) return Boolean.parseBoolean(p[2]);
            return Boolean.parseBoolean(s[2]);
        }
        public static TextColor getColorHUD(ServerPlayerEntity player, Integer i) {
            String str = getHUDColors(player)[i-1];
            if (str.charAt(0) == '#') return TextColor.parse(str);
            return Utl.color.getTC(getHUDColors(player)[i-1]);
        }
        public static Text addColor(ServerPlayerEntity player, MutableText txt, int i, int start, int step) {
            if (getHUDColors(player)[i-1].equals("rainbow")) return Text.literal("").append(Utl.color.rainbow(txt.getString(),start,step))
                    .styled(style -> style.withItalic(getHUDItalics(player,i)).withBold(getHUDBold(player,i)));
            return txt.styled(style -> style.withColor(getColorHUD(player,i))
                    .withItalic(getHUDItalics(player,i)).withBold(getHUDBold(player,i)));
        }
        public static Text addColor(ServerPlayerEntity player, String txt, int i, int start, int step) {
            if (getHUDColors(player)[i-1].equals("rainbow")) return Text.literal("").append(Utl.color.rainbow(txt,start,step))
                    .styled(style -> style.withItalic(getHUDItalics(player,i)).withBold(getHUDBold(player,i)));
            return Text.literal(txt).styled(style -> style.withColor(getColorHUD(player,i))
                    .withItalic(getHUDItalics(player,i)).withBold(getHUDBold(player,i)));
        }
        public static void UI(ServerPlayerEntity player, Text abovemsg) {
            Text msg = Text.literal("");
            if (abovemsg != null) msg = Text.literal("").append(msg).append(abovemsg).append("\n");

            msg = Text.literal("").append(msg).append(" ").append(lang("ui.color").setStyle(CUtl.pS()))
                    .append(Text.literal("\n                                \n").styled(style -> style.withStrikethrough(true)));

            //PRIMARY
            msg = Text.literal("").append(msg).append(" ")
                    .append(CUtl.button(player,CUtl.SBtn("color.primary"), 1,15, 20, 1, "/hud color edt pri",
                            CUtl.lang("button.color.edit.hover",
                                    addColor(player,CUtl.lang("button.color.primary"),1,15,20))))
                    .append(Text.literal(" "));
            //SECONDARY
            msg = Text.literal("").append(msg)
                    .append(CUtl.button(player,CUtl.SBtn("color.secondary"), 2,15,20, 1, "/hud color edt sec",
                            CUtl.lang("button.color.edit.hover",
                                    addColor(player,CUtl.lang("button.color.secondary"),2,15,20))))
                    .append(Text.literal("\n\n      "));


            //RESET
            msg = Text.literal("").append(msg)
                    .append(CUtl.button(CUtl.SBtn("reset"), CUtl.TC('c'), 1, "/hud color rset",
                            CUtl.lang("button.reset.hover_color",
                                    CUtl.lang("button.all").setStyle(CUtl.C('c')))))
                    .append(Text.literal("  "))
                    .append(CUtl.CButton.back("/hud"));

            msg = Text.literal("").append(msg).append(Text.literal("\n                                ").styled(style -> style.withStrikethrough(true)));
            player.sendMessage(msg, false);
        }
        public static Text colorButton(String text, TextColor color) {
            return Text.literal("[").append(Text.literal(text).styled(style -> style.withColor(color))).append(Text.literal("]"));
        }
        public static void changeUI(ServerPlayerEntity player, String type, Text abovemsg) {
            Text msg = Text.literal("");
            if (abovemsg != null) msg = Text.literal("").append(msg).append(abovemsg).append("\n");

            ArrayList<String> allStr = new ArrayList<>(Arrays.asList(
                    "red", "dark_red", "gold", "yellow", "green", "dark_green", "aqua", "dark_aqua",
                    "blue", "dark_blue", "pink", "purple", "white", "gray", "dark_gray", "black"));

            Text red = colorButton("RED", CUtl.TC('c'));
            Text dark_red = colorButton("D", CUtl.TC('4'));
            Text gold = colorButton("G", CUtl.TC('6'));
            Text yellow =  colorButton("YELLOW", CUtl.TC('e'));
            Text green = colorButton("GREEN", CUtl.TC('a'));
            Text dark_green = colorButton("D", CUtl.TC('2'));
            Text aqua = colorButton("AQUA", CUtl.TC('b'));
            Text dark_aqua = colorButton("D", CUtl.TC('3'));
            Text blue = colorButton("BLUE", CUtl.TC('9'));
            Text dark_blue = colorButton("D", CUtl.TC('1'));
            Text pink = colorButton("PINK", CUtl.TC('d'));
            Text purple = colorButton("P", CUtl.TC('5'));
            Text white = colorButton("WHITE", CUtl.TC('f'));
            Text gray = colorButton("GRAY", CUtl.TC('7'));
            Text dark_gray = colorButton("D", CUtl.TC('8'));
            Text black = colorButton("B", CUtl.TC('0'));

            Text reset = colorButton(CUtl.SBtn("reset"), CUtl.TC('c'));
            Text back = CUtl.CButton.back("/hud color");


            ArrayList<Text> allObj = new ArrayList<>(Arrays.asList(
                    red, dark_red, gold, yellow, green, dark_green, aqua, dark_aqua, blue, dark_blue, pink, purple, white, gray, dark_gray, black));

            for (int i = 0; i < allObj.size(); i++) {
                int finalI = i;
                allObj.set(i, Text.literal("").append(allObj.get(i)).styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        CUtl.lang("button.color.hover",
                                Utl.color.set(allStr.get(finalI),Utl.color.formatPlayer(allStr.get(finalI),true)))))));
            }

            int typ;
            if (type.equalsIgnoreCase("pri")) {
                typ = 1;
                msg = Text.literal("").append(msg).append(" ").append(lang("ui.color.primary").setStyle(CUtl.pS()))
                        .append(": ")
                        .append(addColor(player,lang("ui.color.preview"),typ,15,20))
                        .append(Text.literal("\n                           \n").styled(style -> style.withStrikethrough(true)));
                reset = Text.literal("").append(reset).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hud color rset pri"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                CUtl.lang("button.reset.hover_color",
                                        addColor(player,lang("color.primary"),typ,15,20)))));
                for (int i = 0; i < allObj.size(); i++) {
                    int finalI = i;
                    allObj.set(i, Text.literal("").append(allObj.get(i)).styled(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hud color set primary " + allStr.get(finalI)))));
                }
            } else if (type.equalsIgnoreCase("sec")) {
                typ = 2;
                msg = Text.literal("").append(msg).append(" ").append(lang("ui.color.secondary").setStyle(CUtl.pS()))
                        .append(": ")
                        .append(addColor(player,lang("ui.color.preview"),typ,15,20))
                        .append(Text.literal("\n                           \n").styled(style -> style.withStrikethrough(true)));
                reset = Text.literal("").append(reset).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hud color rset sec"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                CUtl.lang("button.reset.hover_color",
                                        addColor(player,lang("color.secondary"),typ,15,20)))));
                for (int i = 0; i < allObj.size(); i++) {
                    int finalI = i;
                    allObj.set(i, Text.literal("").append(allObj.get(i)).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hud color set secondary " + allStr.get(finalI)))));
                }
            } else return;

            msg = Text.literal("").append(msg)
                    .append(" ")
                    .append(allObj.get(0))
                    .append(Text.literal(" "))
                    .append(allObj.get(1))
                    .append("\n ")
                    .append(allObj.get(3))
                    .append(Text.literal(" "))
                    .append(allObj.get(2))
                    .append("\n ")
                    .append(allObj.get(4))
                    .append(Text.literal(" "))
                    .append(allObj.get(5))
                    .append("\n ")
                    .append(allObj.get(6))
                    .append(Text.literal(" "))
                    .append(allObj.get(7))
                    .append("\n ")
                    .append(allObj.get(8))
                    .append(Text.literal(" "))
                    .append(allObj.get(9))
                    .append("\n ")
                    .append(allObj.get(10))
                    .append(Text.literal(" "))
                    .append(allObj.get(11))
                    .append("\n ")
                    .append(allObj.get(13))
                    .append(Text.literal(" "))
                    .append(allObj.get(14))
                    .append("\n ")
                    .append(allObj.get(12))
                    .append(Text.literal(" "))
                    .append(allObj.get(15))
                    .append(Text.literal("\n\n "))
                    .append(Text.literal("")
                            .append("[").append(Utl.color.rainbow(CUtl.SBtn("color.rgb"),15,95)).append("]")
                            .styled(style -> style
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            CUtl.lang("button.color.rgb.hover",
                                                    Utl.color.rainbow(CUtl.lang("button.color.rgb.hover_2").getString(),15,20))))
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                            "/hud color set "+(typ == 1 ? "primary" : "secondary")+" rainbow"))))
                    .append(Text.literal(" "))
                    .append(CUtl.button(CUtl.SBtn("color.custom"),
                            CUtl.HEX(getHUDColors(player)[typ-1].contains("#")? getHUDColors(player)[typ-1]: "#ff97e0"), 2,
                            "/hud color set "+(typ == 1 ? "primary" : "secondary")+" ",
                            CUtl.lang("button.color.custom.hover",
                                    CUtl.lang("button.color.custom.hover_2").setStyle(
                                            CUtl.HEXS(getHUDColors(player)[typ-1].contains("#")? getHUDColors(player)[typ-1]: "#ff97e0")))))
                    .append(Text.literal("\n\n "))
                    .append(CUtl.button(CUtl.SBtn("color.bold"),CUtl.TC(getHUDBold(player, typ) ? 'a' : 'c'), 1,
                            "/hud color bold "+(typ == 1 ? "primary" : "secondary")+" "+(getHUDBold(player, typ) ? "false" : "true"),
                            CUtl.lang("button.color.bold.hover",
                                    CUtl.lang("button."+(getHUDBold(player,typ)?"off":"on")).setStyle(CUtl.C(getHUDBold(player,typ)?'c':'a')),
                                    lang("color."+(typ==1?"primary":"secondary")))))
                    .append(Text.literal(" "))
                    .append(CUtl.button(CUtl.SBtn("color.italics"),CUtl.TC(getHUDItalics(player, typ) ? 'a' : 'c'), 1,
                            "/hud color italics "+(typ == 1 ? "primary" : "secondary")+" "+(getHUDItalics(player, typ) ? "false" : "true"),
                            CUtl.lang("button.color.italics.hover",
                                    CUtl.lang("button."+(getHUDItalics(player,typ)?"off":"on")).setStyle(CUtl.C(getHUDItalics(player,typ)?'c':'a')),
                                    lang("color."+(typ==1?"primary":"secondary")))))
                    .append(Text.literal("\n\n "))
                    .append(reset)
                    .append(Text.literal(" ")).append(back).append("\n");
            msg = Text.literal("").append(msg).append(Text.literal("                           ").styled(style -> style.withStrikethrough(true)));

            player.sendMessage(msg, false);
        }
    }
    public static void toggle(ServerPlayerEntity player, Boolean state, boolean Return) {
        if (state == null) {
            if (PlayerData.get.hud.state(player)) player.sendMessage(Text.of(""), true);
            PlayerData.set.hud.state(player, !PlayerData.get.hud.state(player));
        } else {
            if (!state) player.sendMessage(Text.of(""),true);
            PlayerData.set.hud.state(player, state);
        }
        Text text = Text.literal("").append(CUtl.tag())
                .append(lang("toggle",
                        CUtl.lang("button."+(PlayerData.get.hud.state(player)?"on":"off")).setStyle(CUtl.C(PlayerData.get.hud.state(player)?'a':'c'))));
        if (Return) UI(player, text);
        else player.sendMessage(text);
    }
    public static void UI(ServerPlayerEntity player, Text abovemsg) {
        MutableText msg = Text.literal("");
        if (abovemsg != null) msg.append(abovemsg).append("\n");
        msg.append(" ")
                .append(lang("ui").setStyle(CUtl.pS()))
                .append(Text.literal("\n                                 \n").styled(style -> style.withStrikethrough(true)))
                .append(Text.literal(" "));
        //COLOR
        msg.append(CUtl.CButton.hud.color()).append(" ");
        //EDIT
        msg.append(CUtl.CButton.hud.edit()).append(" ");
        //TOGGLE
        char color = 'c';
        String type = "false";
        if (!PlayerData.get.hud.state(player)) { type = "true"; color = 'a'; }
        msg.append(CUtl.CButton.hud.toggle(color, type)).append("\n\n ");
        //BACK
        msg.append(CUtl.CButton.back("/directionhud"));
        msg.append(Text.literal("\n                                 ").styled(style -> style.withStrikethrough(true)));
        player.sendMessage(msg);
    }
    //782
}
