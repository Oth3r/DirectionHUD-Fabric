package one.oth3r.directionhud.utils;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import one.oth3r.directionhud.DirectionHUD;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utl {
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean inBetween(int i, int min, int max) {
        return i >= min && i <= max;
    }
    public static boolean inBetweenD(double i, double min, double max) {
        if (min > max) {
            return i >= min || i <= max;
        }
        return i >= min && i <= max;
    }
    public static double sub(double i, double sub, double max) {
        double s = i - sub;
        if (s < 0) s = max - (s*-1);
        return s;
    }
    public static String createID() {
        return RandomStringUtils.random(8, true, true);
    }
    public static String[] trimStart(String[] arr, int numToRemove) {
        if (numToRemove > arr.length) {
            return new String[0];
        }
        String[] result = new String[arr.length - numToRemove];
        System.arraycopy(arr, numToRemove, result, 0, result.length);
        return result;
    }
    public static SuggestionsBuilder xyzSuggester(ServerPlayerEntity player, SuggestionsBuilder builders, String type) {
        SuggestionsBuilder builder = new SuggestionsBuilder(builders.getInput(),builders.getStart());
        if (type.equalsIgnoreCase("x")) {
            builder.suggest(player.getBlockX());
            builder.suggest(player.getBlockX()+" "+player.getBlockZ());
            builder.suggest(player.getBlockX()+" "+player.getBlockY()+" "+player.getBlockZ());
            return builder;
        }
        if (type.equalsIgnoreCase("y")) {
            builder.suggest(player.getBlockY());
            builder.suggest(player.getBlockY()+" "+player.getBlockZ());
            return builder;
        }
        if (type.equalsIgnoreCase("z")) return builder.suggest(player.getBlockZ());
        return builder;
    }
    public static class xyz {
        public static String fix(String loc) {
            ArrayList<String> sp = new ArrayList<>(Arrays.asList(loc.split(" ")));
            if (sp.size() == 1) sp = new ArrayList<>(Arrays.asList(loc.split("_")));
            if (sp.size() == 1) return "0 0 0";
            if (!isInt(sp.get(0))) sp.set(0, "0");
            if (sp.size() == 3) {
                if ((!sp.get(1).equals("n") && !isInt(sp.get(1)))) sp.set(1, "0");
                if (!isInt(sp.get(2))) sp.set(2, "0");
            } else {
                if (!isInt(sp.get(1))) sp.set(1, "0");
            }
            if (sp.size() == 2)
                return xzBounds(Integer.parseInt(sp.get(0))) + " " + xzBounds(Integer.parseInt(sp.get(1)));
            if (sp.get(1).equals("n"))
                return xzBounds(Integer.parseInt(sp.get(0))) + " n " + xzBounds(Integer.parseInt(sp.get(2)));
            return xzBounds(Integer.parseInt(sp.get(0))) + " " + yBounds(Integer.parseInt(sp.get(1))) + " " + xzBounds(Integer.parseInt(sp.get(2)));
        }
        public static int yBounds(int s) {
            if (s > 20000000) return 20000000;
            return Math.max(s, -64);
        }
        public static int xzBounds(int s) {
            if (s > 30000000) return 30000000;
            return Math.max(s, -30000000);
        }
        public static String divide(String xyz) {
            String[] split = xyz.split(" ");
            if (split.length == 2) {
                int x = Integer.parseInt(split[0]) / 8;
                int z = Integer.parseInt(split[1]) / 8;
                return Utl.xyz.fix(x + " " + z);
            }
            int x = Integer.parseInt(split[0]) / 8;
            int z = Integer.parseInt(split[2]) / 8;
            return Utl.xyz.fix(x + " " + split[1] + " " + z);
        }
        public static String multiply(String xyz) {
            String[] split = xyz.split(" ");
            if (split.length == 2) {
                int x = Integer.parseInt(split[0]) * 8;
                int z = Integer.parseInt(split[1]) * 8;
                return Utl.xyz.fix(x + " " + z);
            }
            int x = Integer.parseInt(split[0]) * 8;
            int z = Integer.parseInt(split[2]) * 8;
            return Utl.xyz.fix(x + " " + split[1] + " " + z);
        }
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public static boolean check(String xyz) {
            String[] split = xyz.split(" ");
            if (split.length == 1) return false;
            if (!isInt(split[0])) return false;
            if (split.length == 2 && isInt(split[1])) return true;
            if (split.length == 3 && split[1].equals("n") && isInt(split[2])) return true;
            return split.length == 3 && isInt(split[2]) && isInt(split[2]);
        }
        //DATA/FILE FORMAT: ADD UNDERSCORES AND N IF NEEDED //DISPLAY FORMAT?
        public static String DFormat(String xyz) {
            String[] split = xyz.split(" ");
            if (split.length == 2) {
                return split[0] + "_n_" + split[1];
            }
            return split[0] + "_" + split[1] + "_" + split[2];
        }
        //PLAYER FORMAT: REMOVE UNDERSCORES AND N
        public static String PFormat(String xyz) {
            if (xyz.contains("_")) {
                String[] split = xyz.split("_");
                if (split[1].equals("n")) return split[0] + " " + split[2];
                return split[0] + " " + split[1] + " " + split[2];
            }
            String[] split = xyz.split(" ");
            if (split[1].equals("n")) return split[0] + " " + split[2];
            return split[0] + " " + split[1] + " " + split[2];
        }
        //CODE FORMAT: REMOVE UNDERSCORES AND KEEP N
        public static String CFormat(String xyz) {
            String[] split = xyz.split("_");
            return split[0] + " " + split[1] + " " + split[2];
        }
    }
    public static class player {
        public static List<String> getList() {
            ArrayList<String> array = new ArrayList<>(List.of());
            for (ServerPlayerEntity p : DirectionHUD.server.getPlayerManager().getPlayerList()) {
                array.add(p.getName().getString());
            }
            return array;
        }
        public static String name(ServerPlayerEntity player) {
            return player.getName().getString();
        }
        public static String dim(ServerPlayerEntity player) {
            return player.getWorld().getRegistryKey().getValue().getPath();
        }
        public static String XYZ(ServerPlayerEntity player) {
            return player.getBlockX()+" "+player.getBlockY()+" "+player.getBlockZ();
        }
        public static void sendAs(String command, ServerPlayerEntity player) {
            try {
                ParseResults<ServerCommandSource> parse =
                        DirectionHUD.commandManager.getDispatcher().parse(command, player.getCommandSource());
                DirectionHUD.commandManager.getDispatcher().execute(parse);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
    }
    public static class dim {
        public static String convertXYZ(ServerPlayerEntity player, String xyz, String DIM) {
            String playerDIM = CFormat(Utl.player.dim(player));
            DIM = CFormat(DIM);
            if (playerDIM.equalsIgnoreCase("the_end") || DIM.equalsIgnoreCase("the_end")) return xyz;
            if (playerDIM.equals(DIM)) return xyz;
            if (playerDIM.equalsIgnoreCase("overworld") && DIM.equalsIgnoreCase("the_nether")) return Utl.xyz.multiply(xyz);
            if (playerDIM.equalsIgnoreCase("the_nether") && DIM.equalsIgnoreCase("overworld")) return Utl.xyz.divide(xyz);
            return xyz;
        }
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public static boolean checkValid(String s) {
            return s.equalsIgnoreCase("overworld") || s.equalsIgnoreCase("nether") || s.equalsIgnoreCase("end") ||
                    s.equalsIgnoreCase("the_nether") || s.equalsIgnoreCase("the_end");
        }
        public static String CFormat(String dim) {
            if (dim.equalsIgnoreCase("nether") || dim.equalsIgnoreCase("the_nether")) return "the_nether";
            if (dim.equalsIgnoreCase("end") || dim.equalsIgnoreCase("the_end")) return "the_end";
            return "overworld";
        }
        public static String PFormat(String dim) {
            if (dim.equalsIgnoreCase("nether") || dim.equalsIgnoreCase("the_nether")) return "nether";
            if (dim.equalsIgnoreCase("end") || dim.equalsIgnoreCase("the_end")) return "end";
            return "overworld";
        }
        public static boolean showConvertButton(String playerDIM, String DIM) {
            playerDIM = CFormat(playerDIM);
            DIM = CFormat(DIM);
            return !playerDIM.equalsIgnoreCase(DIM) && !playerDIM.equalsIgnoreCase("THE_END") && !DIM.equalsIgnoreCase("THE_END");
        }
        public static int getInt(String dim) {
            if (dim.equalsIgnoreCase("nether") || dim.equalsIgnoreCase("the_nether")) return 2;
            if (dim.equalsIgnoreCase("end") || dim.equalsIgnoreCase("the_end")) return 3;
            return 1;
        }
        public static List<String> getList() {
            return new ArrayList<>(Arrays.asList(
                    "overworld", "nether", "end"));
        }
        public static String getHEX(String dim) {
            dim = CFormat(dim);
            if (dim.equals("the_nether")) return "#e8342e";
            if (dim.equals("the_end")) return "#edffb0";
            return "#55FF55";
        }
        public static String getLetter(String dim) {
            dim = CFormat(dim);
            if (dim.equals("the_nether")) return "N";
            if (dim.equals("the_end")) return "E";
            return "O";
        }
    }

    public static class color {
        // red, dark_red, gold, yellow, green, dark_green, aqua, dark_aqua, blue, dark_blue, pink, purple, white, gray, dark_gray, black
        public static List<String> getList() {
            return new ArrayList<>(Arrays.asList(
                    "red", "dark_red", "gold", "yellow", "green", "dark_green", "aqua", "dark_aqua",
                    "blue", "dark_blue", "pink", "purple", "white", "gray", "dark_gray", "black","ffffff"));
        }
        //todo maybe change this
        public static TextColor getTC(String color) {
            if (color.equals("red")) return CUtl.TC('c');
            if (color.equals("dark_red")) return CUtl.TC('4');
            if (color.equals("gold")) return CUtl.TC('6');
            if (color.equals("yellow")) return CUtl.TC('e');
            if (color.equals("green")) return CUtl.TC('a');
            if (color.equals("dark_green")) return CUtl.TC('2');
            if (color.equals("aqua")) return CUtl.TC('b');
            if (color.equals("dark_aqua")) return CUtl.TC('3');
            if (color.equals("blue")) return CUtl.TC('9');
            if (color.equals("dark_blue")) return CUtl.TC('1');
            if (color.equals("pink")) return CUtl.TC('d');
            if (color.equals("purple")) return CUtl.TC('5');
            if (color.equals("white")) return CUtl.TC('f');
            if (color.equals("gray")) return CUtl.TC('7');
            if (color.equals("dark_gray")) return CUtl.TC('8');
            if (color.equals("black")) return CUtl.TC('0');
            if (color.charAt(0)=='#') return CUtl.HEX(color);
            return CUtl.TC('f');
        }
        public static int getCodeRGB(String color) {
            if (color.equals("red")) return 16733525;
            if (color.equals("dark_red")) return 11141120;
            if (color.equals("gold")) return 16755200;
            if (color.equals("yellow")) return 16777045;
            if (color.equals("green")) return 5635925;
            if (color.equals("dark_green")) return 43520;
            if (color.equals("aqua")) return 5636095;
            if (color.equals("dark_aqua")) return 43690;
            if (color.equals("blue")) return 5592575;
            if (color.equals("dark_blue")) return 170;
            if (color.equals("pink")) return 16733695;
            if (color.equals("purple")) return 11141290;
            if (color.equals("white")) return 16777215;
            if (color.equals("gray")) return 11184810;
            if (color.equals("dark_gray")) return 5592405;
            if (color.equals("black")) return 0;
            if (color.equals("rainbow")) return 16777215;
            if (color.charAt(0)=='#') return hexToRGB(color);
            return 16777215;
        }
        public static int hexToRGB(String hexColor) {
            // Remove the # symbol if it exists
            if (hexColor.charAt(0) == '#') {
                hexColor = hexColor.substring(1);
            }
            // Convert the hex string to an integer
            int colorValue = Integer.parseInt(hexColor, 16);
            // Separate the red, green, and blue values from the integer
            int red = (colorValue >> 16) & 0xFF;
            int green = (colorValue >> 8) & 0xFF;
            int blue = colorValue & 0xFF;
            // Combine the values into an RGB integer
            return (red << 16) | (green << 8) | blue;
        }
        private static boolean checkValid(String s) {
            List<String> colors = new ArrayList<>(Arrays.asList(
                    "red", "dark_red", "gold", "yellow", "green", "dark_green", "aqua", "dark_aqua",
                    "blue", "dark_blue", "pink", "purple", "white", "gray", "dark_gray", "black"));
            if (s.charAt(0) == '#') return true;
            return colors.contains(s);
        }
        public static String fix(String s,boolean enableRainbow, String Default) {
            if (checkValid(s)) return s.toLowerCase();
            if (s.equals("rainbow") && enableRainbow) return s;
            if (s.equalsIgnoreCase("light_purple")) return "pink";
            if (s.equalsIgnoreCase("dark_purple")) return "purple";
            if (s.length() == 6) return "#"+s;
            return Default;
        }
        public static String formatPlayer(String s, boolean caps) {
            if (caps) s=s.toUpperCase();
            else s=s.toLowerCase();
            if (checkValid(s.toLowerCase())) return s.replace('_', ' ');
            if (s.length() == 6) return "#"+s;
            if (s.equalsIgnoreCase("rainbow")) return s;
            return caps? "WHITE":"white";
        }
        public static MutableText rainbow(String string, float start, float step) {
            float hue = start % 360f;
            MutableText text = Text.literal("");
            for (int i = 0; i < string.codePointCount(0, string.length()); i++) {
                if (string.charAt(i) == ' ') {
                    text.append(Text.literal(" "));
                    continue;
                }
                Color color = Color.getHSBColor(hue / 360.0f, 1.0f, 1.0f);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                String hexColor = String.format("#%02x%02x%02x", red, green, blue);
                text.append(Text.literal(Character.toString(string.codePointAt(i))).styled(style -> style.withColor(CUtl.HEX(hexColor))));
                hue = ((hue % 360f)+step)%360f;
            }
            return text;
        }
        public static Color toColor(String string) {
            if (string.equals("red")) return Color.decode("#FF5555");
            if (string.equals("dark_red")) return Color.decode("#AA0000");
            if (string.equals("gold")) return Color.decode("#FFAA00");
            if (string.equals("yellow")) return Color.decode("#FFFF55");
            if (string.equals("green")) return Color.decode("#55FF55");
            if (string.equals("dark_green")) return Color.decode("#00AA00");
            if (string.equals("aqua")) return Color.decode("#55FFFF");
            if (string.equals("dark_aqua")) return Color.decode("#00AAAA");
            if (string.equals("blue")) return Color.decode("#5555FF");
            if (string.equals("dark_blue")) return Color.decode("#0000AA");
            if (string.equals("pink")) return Color.decode("#FF55FF");
            if (string.equals("purple")) return Color.decode("#AA00AA");
            if (string.equals("white")) return Color.decode("#FFFFFF");
            if (string.equals("gray")) return Color.decode("#AAAAAA");
            if (string.equals("dark_gray")) return Color.decode("#555555");
            if (string.equals("black")) return Color.decode("#000000");
            if (string.charAt(0)=='#') return Color.decode(string);
            return Color.WHITE;
        }
    }
}
