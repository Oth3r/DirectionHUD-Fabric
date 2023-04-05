package one.oth3r.directionhud.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.files.LangReader;

public class CUtl {
    public static MutableText tag() {
        return Text.literal("").append(Text.literal("["))
                .append(Text.literal("DirectionHUD").setStyle(pS()))
                .append(Text.literal("] "));
    }
    public static MutableText tag(Text t) {
        return tag().append(t);
    }
    public static Style pS() {
        return Style.EMPTY.withColor(HEX(c.pri));
    }
    public static TextColor pTC() {
        return HEX(c.pri);
    }
    public static Style sS() {
        return Style.EMPTY.withColor(HEX(c.sec));
    }
    public static TextColor sTC() {
        return HEX(c.sec);
    }
    public static Style C(Character code) {
        return Style.EMPTY.withColor(Formatting.byCode(code));
    }
    public static TextColor TC(Character code) {
        return TextColor.fromFormatting(Formatting.byCode(code));
    }
    public static Text error(Text s) {
        return tag().append(lang("error").setStyle(HEXS("FF4646"))).append(" ").append(s);
    }
    public static Text usage(String s) {
        return tag().append(lang("usage").setStyle(HEXS("FF4646"))).append(" ").append(s);
    }
    public static TextColor HEX(String s) {
        if (s.contains("#")) return TextColor.parse(s);
        return TextColor.parse("#"+s);
    }
    public static Style HEXS(String s) {
        if (s.contains("#")) return Style.EMPTY.withColor(TextColor.parse(s));
        return Style.EMPTY.withColor(TextColor.parse("#"+s));
    }
    public static MutableText lang(String key) {
        if (DirectionHUD.isClient) {
            return Text.translatable("key.directionhud."+key);
        } else {
            return LangReader.of("key.directionhud."+key).getMutableText();
        }
    }
    public static MutableText lang(String key, Object... args) {
        if (DirectionHUD.isClient) {
            return Text.translatable("key.directionhud."+key, args);
        } else {
            return LangReader.of("key.directionhud."+key, args).getMutableText();
        }
    }
    public static class c {
        public static String convert = "#ffa93f";
        public static String set = "#fff540";
        public static String saved = "#1ee16f";
        public static String add = "#36ff89";
        public static String setting = "#e9e9e9";
        public static String lastdeath = "#ac4dff";
        public static String send = "#52e1ff";
        public static String track = "#ff6426";
        public static String edit = "#5070ff";
        public static String dest = "#29a2ff";
        public static String hud = "#29ff69";
        public static String defaults = "#ff6629";
        public static String back = "#ff9500";
        public static String sec = "#ffee35";
        public static String pri = "#2993ff";
    }
    public static class CButton {
        public static Text back(String cmd) {
            return button(SBtn("back"), HEX(c.back),1, cmd, Text.literal("")
                    .append(Text.literal(cmd).setStyle(HEXS(c.back)))
                    .append("\n")
                    .append(TBtn("back.hover")));
        }
        public static class dest {
            public static Text convert(String cmd) {
                return button(SBtn("dest.convert"), HEX(c.convert), 1, cmd, TBtn("dest.convert.hover").setStyle(HEXS(c.convert)));
            }
            public static Text set(String cmd) {
                return button(SBtn("dest.set"), HEX(c.set), 1, cmd, TBtn("dest.set.hover").setStyle(HEXS(c.set)));
            }
            public static Text edit(int t, String cmd) {
                return button("✎", HEX(c.edit), t, cmd, TBtn("dest.edit.hover").setStyle(HEXS(c.edit)));
            }
            public static Text settings() {
                return button(SBtn("dest.settings"), HEX(c.setting),1, "/dest settings", Text.literal("")
                        .append(Text.literal(commandUsage.destSettings()).setStyle(HEXS(c.setting)))
                        .append("\n").append(TBtn("dest.settings.hover")));
            }
            public static Text saved() {
                return button(SBtn("dest.saved"), HEX(c.saved),1, "/dest saved", Text.literal("")
                        .append(Text.literal(commandUsage.destSaved()).setStyle(HEXS(c.saved)))
                        .append("\n").append(TBtn("dest.saved.hover")));
            }
            public static Text add() {
                return button("+", HEX(c.add),2, "/dest add ", Text.literal("")
                        .append(Text.literal(commandUsage.destAdd()).setStyle(HEXS(c.add)))
                        .append("\n").append(TBtn("dest.add.hover",
                                TBtn("dest.add.hover_2").setStyle(HEXS(c.add)))));
            }
            public static Text set() {
                return button(SBtn("dest.set"), HEX(c.set),2, "/dest set ", Text.literal("")
                        .append(Text.literal(commandUsage.destSet()).setStyle(HEXS(c.set)))
                        .append("\n").append(TBtn("dest.set.hover_info")));
            }
            public static Text clear(ServerPlayerEntity player) {
                boolean o = !Destination.get(player, "xyz").equals("f");
                return button("✕", TC(o?'c':'7'), o?1:0, "/dest clear", Text.literal("")
                        .append(Text.literal(commandUsage.destClear()).setStyle(C(o?'c':'7')))
                        .append("\n").append(TBtn("dest.clear.hover")));
            }
            public static Text lastdeath() {
                return button(SBtn("dest.lastdeath"), HEX(c.lastdeath),1, "/dest lastdeath", Text.literal("")
                        .append(Text.literal(commandUsage.destLastdeath()).setStyle(HEXS(c.lastdeath)))
                        .append("\n").append(TBtn("dest.lastdeath.hover")));
            }
            public static Text send() {
                return button(SBtn("dest.send"), HEX(c.send), 2, "/dest send ", Text.literal("")
                        .append(Text.literal(commandUsage.destSend()).setStyle(HEXS(c.send)))
                        .append("\n").append(TBtn("dest.send.hover")));
            }
            public static Text track() {
                return button(SBtn("dest.track"), HEX(c.track), 2, "/dest track ", Text.literal("")
                        .append(Text.literal(commandUsage.destTrack()).setStyle(HEXS(c.track)))
                        .append("\n").append(TBtn("dest.track.hover")));
            }
        }
        public static class hud {
            public static Text color() {
                return Text.literal("").append(Text.literal("")
                        .append("[")
                        .append(Utl.color.rainbow(SBtn("hud.color"),15f,45f))
                        .append("]")).styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hud color"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("")
                                .append(Utl.color.rainbow(commandUsage.hudColor(),10f,23f))
                                .append("\n").append(TBtn("hud.color.hover")))));
            }
            public static Text edit() {
                return button(SBtn("hud.edit"), HEX(c.edit), 1, "/hud edit", Text.literal("")
                        .append(Text.literal(commandUsage.hudEdit()).setStyle(HEXS(c.edit)))
                        .append("\n").append(TBtn("hud.edit.hover")));
            }
            public static Text toggle(Character color, String type) {
                return button(SBtn("hud.toggle"), TC(color), 1, "/hud toggle "+ type, Text.literal("")
                        .append(Text.literal(commandUsage.hudToggle()).setStyle(C(color)))
                        .append("\n").append(TBtn("hud.toggle.hover")));
            }
        }
        public static class dirHUD {
            public static Text hud() {
                return button(SBtn("dirhud.hud"), HEX(c.hud), 1, "/hud", Text.literal("")
                        .append(Text.literal(commandUsage.hud()).setStyle(HEXS(c.hud)))
                        .append("\n").append(TBtn("dirhud.hud.hover")));
            }
            public static Text dest() {
                return button(SBtn("dirhud.dest"), HEX(c.dest), 1, "/dest", Text.literal("")
                        .append(Text.literal(commandUsage.dest()).setStyle(HEXS(c.dest)))
                        .append("\n").append(TBtn("dirhud.dest.hover")));
            }
            public static Text defaults() {
                return button(SBtn("dirhud.defaults"), HEX(c.defaults), 1, "/dirhud defaults", Text.literal("")
                        .append(Text.literal(commandUsage.defaults()).setStyle(HEXS(c.defaults)))
                        .append("\n").append(TBtn("dirhud.defaults.hover")));
            }
            public static Text reload() {
                return button(SBtn("dirhud.reload"), HEX(c.defaults), 1, "/dirhud reload", Text.literal("")
                        .append(Text.literal(commandUsage.defaults()).setStyle(HEXS(c.defaults)))
                        .append("\n").append(TBtn("dirhud.reload.hover")));
            }
        }
    }
    public static Text button(String bText, TextColor color, int t, String cmd, Text hoverText) {
        if (t == 0) return Text.literal("").append(Text.literal("")
                .append(Text.literal("[").setStyle(C('f')))
                .append(Text.literal(bText).styled(style -> style.withColor(color)))
                .append(Text.literal("]").setStyle(C('f'))).styled(style -> style
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
        if (t == 1) return Text.literal("").append(Text.literal("")
                .append(Text.literal("[").setStyle(C('f')))
                .append(Text.literal(bText).styled(style -> style.withColor(color)))
                .append(Text.literal("]").setStyle(C('f'))).styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
        if (t == 2) return Text.literal("").append(Text.literal("")
                .append(Text.literal("[").setStyle(C('f')))
                .append(Text.literal(bText).styled(style -> style.withColor(color)))
                .append(Text.literal("]").setStyle(C('f'))).styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
        return Text.literal("");
    }
    //button matching player color
    public static Text button(ServerPlayerEntity player, String bText, int typ, int start, int step , int t, String cmd, Text hoverText) {
        Text button = HUD.color.addColor(player,bText,typ,start,step);
        if (t == 0) return Text.literal("").append(Text.literal("")
                .append(Text.literal("[").setStyle(C('f')))
                .append(button)
                .append(Text.literal("]").setStyle(C('f'))).styled(style -> style
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
        if (t == 1) return Text.literal("").append(Text.literal("")
                .append(Text.literal("[").setStyle(C('f')))
                .append(button)
                .append(Text.literal("]").setStyle(C('f'))).styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
        if (t == 2) return Text.literal("").append(Text.literal("")
                .append(Text.literal("[").setStyle(C('f')))
                .append(button)
                .append(Text.literal("]").setStyle(C('f'))).styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
        return Text.literal("");
    }
    public static Text button(String bText, TextColor color, int t, String cmd) {
        if (t == 1) return Text.literal("").append(Text.literal("")
                .append(Text.literal("[").setStyle(C('f')))
                .append(Text.literal(bText).styled(style -> style.withColor(color)))
                .append(Text.literal("]").setStyle(C('f'))).styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd))));
        if (t == 2) return Text.literal("").append(Text.literal("")
                .append(Text.literal("[").setStyle(C('f')))
                .append(Text.literal(bText).styled(style -> style.withColor(color)))
                .append(Text.literal("]").setStyle(C('f'))).styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd))));
        return Text.literal("");
    }
    public static Text button(String bText, TextColor color, Text hoverText) {
        return Text.literal("").append(Text.literal("")
                .append(Text.literal("[").setStyle(C('f')))
                .append(Text.literal(bText).styled(style -> style.withColor(color)))
                .append(Text.literal("]").setStyle(C('f'))).styled(style -> style
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))));
    }
    public static Text button(String bText, TextColor color) {
        return Text.literal("").append(Text.literal("")
                .append(Text.literal("["))
                .append(Text.literal(bText).styled(style -> style.withColor(color)))
                .append(Text.literal("]")));
    }
    public static String SBtn(String key) {
        return TBtn(key).getString();
    }
    public static MutableText TBtn(String key) {
        return lang("button."+key);
    }
    public static MutableText TBtn(String key, Object... args) {
        return lang("button."+key,args);
    }
    public static class commandUsage {
        public static String hud() {return "/hud";}
        public static String hudToggle() {return "/hud toggle";}
        public static String hudColor() {return "/hud color";}
        public static String hudEdit() {return "/hud edit";}
        public static String dest() {return "/dest | /destination";}
        public static String destAdd() {return "/dest (saved) add <name> (x) (y) (z) (dimension) (color)";}
        public static String destSet() {return "/dest set <x> (y) <z> (dimension) | /dest set saved <name> (convert)";}
        public static String destLastdeath() {return "/dest lastdeath";}
        public static String destClear() {return "/dest clear";}
        public static String destSaved() {return "/dest saved";}
        public static String destSettings() {return "/dest settings";}
        public static String destSend() {return "/dest send <IGN> <name> | /dest send <IGN> (name) <x> (y) <z> (dimension)";}
        public static String destTrack() {return "/dest track <IGN>";}
        public static String defaults() {return "/dirhud defaults";}
    }
}
