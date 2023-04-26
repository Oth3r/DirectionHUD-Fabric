package one.oth3r.directionhud.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.files.LangReader;

public class CUtl {
    public static CTxT tag() {
        return CTxT.of("DirectionHUD").btn(true).color(pTC()).append(" ");
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
    public static ClickEvent cEvent(int typ, String arg) {
        if (typ == 1) return new ClickEvent(ClickEvent.Action.RUN_COMMAND,arg);
        if (typ == 2) return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,arg);
        if (typ == 3) return new ClickEvent(ClickEvent.Action.OPEN_URL,arg);
        return null;
    }
    public static HoverEvent hEvent(Text text) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
    }
    public static HoverEvent hEvent(CTxT text) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.b());
    }
    public static Text error(CTxT s) {
        return tag().append(lang("error").color("FF4646")).append(" ").append(s).b();
    }
    public static Text usage(String s) {
        return tag().append(lang("usage").color("FF4646")).append(" ").append(s).b();
    }
    public static TextColor HEX(String s) {
        if (s.contains("#")) return TextColor.parse(s);
        return TextColor.parse("#"+s);
    }
    public static CTxT lang(String key) {
        if (DirectionHUD.isClient) {
            return CTxT.of(Text.translatable("key.directionhud."+key));
        } else {
            return LangReader.of("key.directionhud."+key).getTxT();
        }
    }
    public static MutableText tLang(String key) {
        if (DirectionHUD.isClient) {
            return Text.translatable("key.directionhud."+key);
        } else {
            return LangReader.of("key.directionhud."+key).getTxT().b();
        }
    }
    public static CTxT lang(String key, Object... args) {
        if (DirectionHUD.isClient) {
            Object[] fixedArgs = new Object[args.length];
            for (var i = 0;i < args.length;i++) {
                if (args[i] instanceof CTxT) fixedArgs[i] = ((CTxT) args[i]).b();
                else fixedArgs[i] = args[i];
            }
            return CTxT.of(Text.translatable("key.directionhud."+key, fixedArgs));
        } else {
            return LangReader.of("key.directionhud."+key, args).getTxT();
        }
    }
    public static CTxT TBtn(String TBtn) {
        return lang("button."+TBtn);
    }
    public static CTxT TBtn(String TBtn, Object... args) {
        return lang("button."+TBtn,args);
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
        public static CTxT back(String cmd) {
            return TBtn("back").btn(true).color(c.back).cEvent(1,cmd).hEvent(CTxT.of(cmd).color(c.back).append("\n").append(TBtn("back.hover")));
        }
        public static class dest {
            public static CTxT convert(String cmd) {
                return TBtn("dest.convert").btn(true).color(c.convert).cEvent(1,cmd).hEvent(TBtn("dest.convert.hover").color(c.convert));
            }
            public static CTxT set(String cmd) {
                return TBtn("dest.set").btn(true).color(c.set).cEvent(1,cmd).hEvent(TBtn("dest.set.hover").color(c.set));
            }
            public static CTxT edit(int t, String cmd) {
                return CTxT.of("✎").btn(true).color(c.edit).cEvent(t,cmd).hEvent(TBtn("dest.edit.hover")).color(c.edit);
            }
            public static CTxT settings() {
                return TBtn("dest.settings").btn(true).color(c.setting).cEvent(1,"/dest settings")
                        .hEvent(CTxT.of(commandUsage.destSettings()).color(c.setting).append("\n").append(TBtn("dest.settings.hover")));
            }
            public static CTxT saved() {
                return TBtn("dest.saved").btn(true).color(c.saved).cEvent(1,"/dest saved").hEvent(
                        CTxT.of(commandUsage.destSaved()).color(c.saved).append("\n").append(TBtn("dest.saved.hover")));
            }
            public static CTxT add() {
                return CTxT.of("+").btn(true).color(c.add).cEvent(1,"dest add ").hEvent(
                        CTxT.of(commandUsage.destAdd()).color(c.add).append("\n").append(TBtn("dest.add.hover",TBtn("dest.add.hover_2").color(c.add))));
            }
            public static CTxT set() {
                return TBtn("dest.set").btn(true).color(c.set).cEvent(2,"/dest set ").hEvent(
                        CTxT.of(commandUsage.destSet()).color(c.set).append("\n").append(TBtn("dest.set.hover_info")));
            }
            public static CTxT clear(ServerPlayerEntity player) {
                boolean o = !Destination.get(player, "xyz").equals("f");
                return CTxT.of("✕").btn(true).color(o?'c':'7').cEvent(o?1:0,"/dest clear").hEvent(
                        CTxT.of(commandUsage.destClear()).color(o?'c':'7').append("\n").append(TBtn("dest.clear.hover")));
            }
            public static CTxT lastdeath() {
                return TBtn("dest.lastdeath").btn(true).color(c.lastdeath).cEvent(1,"/dest lastdeath").hEvent(
                        CTxT.of(commandUsage.destLastdeath()).color(c.lastdeath).append("\n").append(TBtn("dest.lastdeath.hover")));
            }
            public static CTxT send() {
                return TBtn("dest.send").btn(true).color(c.send).cEvent(2,"dest send ").hEvent(
                        CTxT.of(commandUsage.destSend()).color(c.send).append("\n").append(TBtn("dest.send.hover")));
            }
            public static CTxT track() {
                return TBtn("dest.send").btn(true).color(c.track).cEvent(2,"dest track ").hEvent(
                        CTxT.of(commandUsage.destTrack()).color(c.track).append("\n").append(TBtn("dest.track.hover")));
            }
        }
        public static class hud {
            public static CTxT color() {
                return CTxT.of(Utl.color.rainbow(TBtn("hud.color").getString(),15,45)).btn(true).cEvent(1,"/hud color")
                        .hEvent(CTxT.of(Utl.color.rainbow(commandUsage.hudColor(),10f,23f)).append("\n").append(TBtn("hud.color.hover")));
            }
            public static CTxT edit() {
                return TBtn("hud.edit").btn(true).color(c.edit).cEvent(1,"/hud edit").hEvent(
                        CTxT.of(commandUsage.hudEdit()).color(c.edit).append("\n").append(TBtn("hud.edit.hover")));
            }
            public static CTxT toggle(Character color, String type) {
                return TBtn("hud.toggle").btn(true).color(color).cEvent(1,"/hud toggle "+type).hEvent(
                        CTxT.of(commandUsage.hudToggle()).color(color).append("\n").append(TBtn("hud.toggle.hover")));
            }
        }
        public static class dirHUD {
            public static CTxT hud() {
                return TBtn("dirhud.hud").btn(true).color(c.hud).cEvent(1,"/hud").hEvent(
                        CTxT.of(commandUsage.hud()).color(c.hud).append("\n").append(TBtn("dirhud.hud.hover")));
            }
            public static CTxT dest() {
                return TBtn("dirhud.dest").btn(true).color(c.dest).cEvent(1,"/dest").hEvent(
                        CTxT.of(commandUsage.dest()).color(c.dest).append("\n").append(TBtn("dirhud.dest.hover")));
            }
            public static CTxT defaults() {
                return TBtn("dirhud.defaults").btn(true).color(c.defaults).cEvent(1,"/dirhud defaults").hEvent(
                        CTxT.of(commandUsage.defaults()).color(c.defaults).append("\n").append(TBtn("dirhud.defaults.hover")));
            }
            public static CTxT reload() {
                return TBtn("dirhud.reload").btn(true).color(c.defaults).cEvent(1,"/dirhud reload").hEvent(
                        CTxT.of(commandUsage.defaults()).color(c.defaults).append("\n").append(TBtn("dirhud.reload.hover")));
            }
        }
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
