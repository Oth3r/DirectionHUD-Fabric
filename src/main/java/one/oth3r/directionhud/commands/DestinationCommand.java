package one.oth3r.directionhud.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.*;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class DestinationCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dest")
                .requires((commandSource) -> commandSource.hasPermissionLevel(0))
                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                .then(CommandManager.argument("args", StringArgumentType.string())
                        .suggests((context, builder) -> getSuggestions(context,builder,1))
                        .executes((context2) -> command(context2.getSource(), context2.getInput()))
                        .then(CommandManager.argument("args", StringArgumentType.string())
                                .suggests((context, builder) -> getSuggestions(context,builder,2))
                                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                .then(CommandManager.argument("args", StringArgumentType.string())
                                        .suggests((context, builder) -> getSuggestions(context,builder,3))
                                        .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                        .then(CommandManager.argument("args", StringArgumentType.string())
                                                .suggests((context, builder) -> getSuggestions(context,builder,4))
                                                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                .then(CommandManager.argument("args", StringArgumentType.string())
                                                        .suggests((context, builder) -> getSuggestions(context,builder,5))
                                                        .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                        .then(CommandManager.argument("args", StringArgumentType.string())
                                                                .suggests((context, builder) -> getSuggestions(context,builder,6))
                                                                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                                .then(CommandManager.argument("args", StringArgumentType.string())
                                                                        .suggests((context, builder) -> getSuggestions(context,builder,7))
                                                                        .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                                        .then(CommandManager.argument("args", StringArgumentType.string())
                                                                                .suggests((context, builder) -> getSuggestions(context,builder,8))
                                                                                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                                                .executes((context2) -> command(context2.getSource(), context2.getInput())))))))))));
        dispatcher.register(CommandManager.literal("destination").redirect(dispatcher.getRoot().getChild("dest"))
                .executes((context2) -> command(context2.getSource(), context2.getInput())));
    }
    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder, int pos) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        assert player != null;
        String[] args = context.getInput().split(" ");
        if (pos == 1) {
            builder.suggest("lastdeath");
            builder.suggest("set");
            builder.suggest("clear");
            builder.suggest("saved");
            builder.suggest("settings");
            if (PlayerData.get.dest.setting.send(player) && DirectionHUD.server.isRemote())
                builder.suggest("send");
            if (PlayerData.get.dest.setting.track(player) && DirectionHUD.server.isRemote())
                builder.suggest("track");
            return builder.buildFuture();
        }
        if (pos > 1 && args.length < 2) {
            return builder.buildFuture();
        }
        if (args[1].equalsIgnoreCase("saved")) {
            if (pos == 2) return builder.suggest("add").buildFuture();
            if (args.length > 2 && args[2].equalsIgnoreCase("add")) {
                // /dest saved add <name> (x) (y) (z) (dimension) (color)
                if (pos == 3) return builder.suggest("name").buildFuture();
                // /dest saved add <name> (x) (dimension) (color)
                if (pos == 4 && args.length == 5) {
                    if (!Utl.isInt(args[4])) {
                        for (String s : Utl.color.getList()) builder.suggest(s);
                        for (String s : Utl.dim.getList()) builder.suggest(s);
                        return builder.buildFuture();
                    }
                }
                if (pos == 4) return builder.suggest(player.getBlockX()).buildFuture();
                if (pos == 5) return builder.suggest(player.getBlockY()).buildFuture();
                if (pos == 6) {
                    builder.suggest(player.getBlockZ());
                    if (args.length == 7 && !Utl.isInt(args[6]))
                        for (String s : Utl.color.getList()) builder.suggest(s);
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
                if (pos == 7 && args.length > 6) {
                    if (Utl.isInt(args[6])) {
                        for (String s : Utl.dim.getList()) builder.suggest(s);
                        return builder.buildFuture();
                    }
                    if (Utl.dim.checkValid(args[6]))
                        for (String s : Utl.color.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
                if (pos == 8 && args.length > 7) {
                    if (Utl.isInt(args[6]) && Utl.dim.checkValid(args[7])) {
                        for (String s : Utl.color.getList()) builder.suggest(s);
                        return builder.buildFuture();
                    }
                }
                return builder.buildFuture();
            }

            if (args.length > 2 && args[2].equalsIgnoreCase("edit")) {
                // /dest saved edit <name, color, order, dim, loc> <arg> <name>
                if (pos == 5) {
                    if (args.length < 4) return builder.buildFuture();
                    if (args[3].equalsIgnoreCase("name")) return builder.suggest("name").buildFuture();
                    if (args[3].equalsIgnoreCase("color")) {
                        for (String s : Utl.color.getList()) builder.suggest(s);
                        return builder.buildFuture();
                    }
                    if (args[3].equalsIgnoreCase("order")) return builder.buildFuture();
                    if (args[3].equalsIgnoreCase("dim")) {
                        for (String s : Utl.dim.getList()) builder.suggest(s);
                        return builder.buildFuture();
                    }
                }
                if (args.length < 4) return builder.buildFuture();
                if (args[3].equalsIgnoreCase("loc")) {
                    if (pos == 5) return builder.suggest(player.getBlockX()).buildFuture();
                    if (pos == 6) return builder.suggest(player.getBlockY()).buildFuture();
                    if (pos == 7) return builder.suggest(player.getBlockZ()).buildFuture();
                }
            }
            return builder.buildFuture();
        }
        if (args[1].equalsIgnoreCase("settings")) {
            // /dest settings <particlesdestc, particleslinec> <color>
            if (pos == 3) {
                if (args.length > 2 && args[2].equals("particlesdestc") || args[2].equals("particleslinec")) {
                    for (String s : Utl.color.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
            }
            return builder.buildFuture();
        }
        if (args[1].equalsIgnoreCase("set")) {
            // /dest set <saved> <name>
            // /dest set <x> (y) <z>
            if (pos == 2) {
                builder.suggest("saved");
                builder.suggest(player.getBlockX());
                return builder.buildFuture();
            }
            if (pos == 3) {
                if (args.length < 3) return builder.buildFuture();
                if (args[2].equalsIgnoreCase("saved")) {
                    for (String s : Destination.saved.getNames(player)) builder.suggest(s);
                    return builder.buildFuture();
                }
                return builder.suggest(player.getBlockY()).buildFuture();
            }
            if (pos == 4) {
                if (args.length == 4 && !Utl.isInt(args[3])) {
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
                return builder.suggest(player.getBlockZ()).buildFuture();
            }
            if (pos == 5) {
                if (args.length > 4 && Utl.isInt(args[3]))
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                return builder.buildFuture();
            }
            return builder.buildFuture();
        }
        if (args[1].equalsIgnoreCase("send")) {
            // /dest send <player> <saved> <name>
            // /dest send <player> (name) <x> (y) <z> (dimension)
            if (pos == 2) {
                DirectionHUD.server.getPlayerManager().getPlayerList();
                for (ServerPlayerEntity p : DirectionHUD.server.getPlayerManager().getPlayerList()) {
                    if (p.equals(player)) continue;
                    builder.suggest(p.getName().getString());
                }
                return builder.buildFuture();
            }
            // /dest send <player> (saved, name, x)
            if (pos == 3) {
                builder.suggest("saved");
                builder.suggest(player.getBlockX());
                builder.suggest("name");
                builder.buildFuture();
            }
            // /dest send <player> <saved> <name>
            // /dest send <player> (name) <x>
            // /dest send <player> <x> (y)
            if (pos == 4) {
                if (args.length > 3) {
                    if (args[3].equalsIgnoreCase("saved")) {
                        for (String s : Destination.saved.getNames(player)) builder.suggest(s);
                        return builder.buildFuture();
                    }
                    if (!Utl.isInt(args[3])) {
                        return builder.suggest(player.getBlockX()).buildFuture();
                    }
                }
                return builder.suggest(player.getBlockY()).buildFuture();
            }
            // /dest send <player> (name) <x> (y)
            // /dest send <player> <x> (y) <z>
            if (pos == 5) {
                if (args.length > 3 && !Utl.isInt(args[3])) {
                    return builder.suggest(player.getBlockY()).buildFuture();
                }
                return builder.suggest(player.getBlockZ()).buildFuture();
            }
            // /dest send <player> (name) <x> (y) <z>
            // /dest send <player> <x> (y) <z> (dimension)
            if (pos == 6) {
                if (args.length > 3 && !Utl.isInt(args[3])) {
                    return builder.suggest(player.getBlockZ()).buildFuture();
                }
                for (String s : Utl.dim.getList()) builder.suggest(s);
                return builder.buildFuture();
            }
            // /dest send <player> (name) <x> (y) <z> (dimension)
            if (pos == 7) {
                if (args.length > 3 && !Utl.isInt(args[3])) {
                    for (String s : Utl.dim.getList()) builder.suggest(s);
                    return builder.buildFuture();
                }
            }
            return builder.buildFuture();
        }
        if (args[1].equalsIgnoreCase("track")) {
            // /dest track <player>
            if (pos == 2) {
                DirectionHUD.server.getPlayerManager().getPlayerList();
                for (ServerPlayerEntity p : DirectionHUD.server.getPlayerManager().getPlayerList()) {
                    if (p.equals(player)) continue;
                    builder.suggest(p.getName().getString());
                }
                return builder.buildFuture();
            }
            return builder.buildFuture();
        }
        return builder.buildFuture();
    }
    private static int command(ServerCommandSource source, String arg) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 1;
        String[] args;
        //trim all the arguments before the command
        List<String> keywords = Arrays.asList("dest", "destination");
        int index = Integer.MAX_VALUE;
        //finds the index for the words
        for (String keyword : keywords) {
            int keywordIndex = arg.indexOf(keyword);
            if (keywordIndex != -1 && keywordIndex < index) index = keywordIndex;
        }
        //trims the words before the text
        if (index != Integer.MAX_VALUE) arg = arg.substring(index).trim();
        args = arg.split(" ");
        if (args[0].equals("dest") || args[0].equals("destination"))
            args = arg.replaceFirst("(?i)dest(ination)?\\s+", "").split(" ");

        if (args[0].equalsIgnoreCase("dest") || args[0].equalsIgnoreCase("destination")) {
            Destination.UI(player);
            return 1;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (!Utl.inBetween(args.length, 3,5)) {
                player.sendMessage(CUtl.usage(CUtl.commandUsage.destSet()));
                return 1;
            }

            // /dest set saved <name>
            if (args.length == 3 && args[1].equalsIgnoreCase("saved")) {
                Destination.setName(player, args[2], false);
                return 1;
            }
            // /dest set <name> convert
            if (args.length == 4 && args[1].equalsIgnoreCase("saved") && args[3].equalsIgnoreCase("convert")) {
                Destination.setName(player, args[2], true);
                return 1;
            }

            // /dest set x z
            if (args.length == 3 && !args[2].equalsIgnoreCase("convert")) {
                Destination.set(true, player, args[1] +" n "+ args[2]);
                return 1;
            }

            // /dest set x z DIM (hidden)
            if (args.length == 4 && !Utl.isInt(args[3])) {
                Destination.setConvert(player, args[1] +" n "+ args[2], args[3]);
                return 1;
            }

            // /dest set x y z
            if (args.length == 4 && Utl.isInt(args[3])) {
                Destination.set(true, player, args[1] +" "+ args[2] +" "+ args[3]);
                return 1;
            }

            // /dest set x y z DIM (hidden)
            if (args.length == 5) {
                Destination.setConvert(player, args[1] +" "+ args[2] +" "+ args[3], args[4]);
                return 1;
            }
            return 1;
        }

        //CLEAR
        if (args[0].equalsIgnoreCase("clear")) {
            Destination.clear(true, player);
            return 1;
        }

        //SAVED
        if (args[0].equalsIgnoreCase("saved")) {
            if (args.length == 1) {
                Destination.saved.UI(player, 1);
                return 1;
            }
            if (args.length == 2 && Utl.isInt(args[1])) {
                Destination.saved.UI(player, Integer.parseInt(args[1]));
                return 1;
            }
            if (args[1].equalsIgnoreCase("edit")) {
                if (args.length == 2) return 1;
                if (args.length == 3) Destination.saved.viewDestinationUI(true, player, args[2]);
                if (args[2].equalsIgnoreCase("name")) {
                    if (args.length == 4) {
                        player.sendMessage(CUtl.lang("error.dest.edit.name"));
                    }
                    if (args.length == 5) {
                        Destination.saved.editName(true, player, args[3], args[4]);
                    }
                }
                if (args[2].equalsIgnoreCase("color")) {
                    if (args.length == 4) {
                        player.sendMessage(CUtl.lang("error.dest.edit.color"));
                    }
                    if (args.length == 5) {
                        Destination.saved.editColor(true, player, args[3], args[4]);
                    }
                }
                if (args[2].equalsIgnoreCase("order")) {
                    if (args.length == 4) {
                        player.sendMessage(CUtl.lang("error.dest.edit.order"));
                    }
                    if (args.length == 5) {
                        Destination.saved.editOrder(true, player, args[3], args[4]);
                    }
                }
                if (args[2].equalsIgnoreCase("dim")) {
                    if (args.length == 4) {
                        player.sendMessage(CUtl.lang("error.dest.edit.dimension"));
                    }
                    if (args.length == 5) {
                        Destination.saved.editDimension(true, player, args[3], args[4]);
                    }
                }
                if (args[2].equalsIgnoreCase("loc")) {
                    if (args.length == 4) {
                        player.sendMessage(CUtl.lang("error.dest.edit.location"));
                    }
                    if (args.length == 6) {
                        Destination.saved.editLocation(true, player, args[3], args[4] +" "+ args[5]);
                    }
                    if (args.length == 7) {
                        Destination.saved.editLocation(true, player, args[3], args[4] +" "+ args[5] +" "+ args[6]);
                    }
                }
                return 1;
            }
            //ADD
            if (args[1].equalsIgnoreCase("add")) {
                String playerDIM = player.getWorld().getRegistryKey().getValue().getPath();
                //dest add <name>
                if (args.length == 3) {
                    Destination.saved.add(true, player, args[2], player.getBlockX() + " " + player.getBlockZ(), playerDIM, null);
                    return 1;
                }
                if (!Utl.inBetween(args.length, 5, 8)) {
                    player.sendMessage(CUtl.usage(CUtl.commandUsage.destAdd()));
                    return 1;
                }
                //dest add <name> x y
                if (args.length == 5) {
                    Destination.saved.add(true, player, args[2], args[3] + " " + args[4], playerDIM, null);
                    return 1;
                }
                //dest add <name> x y color
                if (args.length == 6 && !Utl.isInt(args[5]) && !Utl.dim.checkValid(args[5])) {
                    Destination.saved.add(true, player, args[2],args[3] + " " + args[4], playerDIM, args[5]);
                    return 1;
                }
                //dest add <name> x y DIM
                if (args.length == 6 && !Utl.isInt(args[5])) {
                    Destination.saved.add(true, player, args[2],args[3] + " " + args[4], args[5], null);
                    return 1;
                }
                //dest add <name> x y z
                if (args.length == 6 && Utl.isInt(args[5])) {
                    Destination.saved.add(true, player, args[2], args[3] +" "+ args[4] +" "+ args[5], playerDIM, null);
                    return 1;
                }
                //dest add <name> x y DIM color
                if (args.length == 7 && !Utl.isInt(args[5])) {
                    Destination.saved.add(true, player, args[2], args[3] +" "+ args[4], args[5], args[6]);
                    return 1;
                }
                //dest add <name> x y z color
                if (args.length == 7 && !Utl.isInt(args[6]) && !Utl.dim.checkValid(args[6])) {
                    Destination.saved.add(true, player, args[2], args[3] +" "+ args[4] +" "+ args[5], playerDIM ,args[6]);
                    return 1;
                }
                if (args.length == 7) {
                    Destination.saved.add(true, player, args[2], args[3] +" "+ args[4] +" "+ args[5], args[6], null);
                }
                if (args.length == 8) {
                    Destination.saved.add(true, player, args[2], args[3] +" "+ args[4] +" "+ args[5], args[6], args[7]);
                }
                return 1;
            }
            return 1;
        }



        //REMOVE (HIDDEN)
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 2) Destination.saved.delete(true, player, args[1]);
            return 1;
        }

        //LASTDEATH
        if (args[0].equalsIgnoreCase("lastdeath")) {
            if (args.length == 1) {
                Destination.lastdeath.UI(player, null);
                return 1;
            }

            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("cl")) {
                    Destination.lastdeath.clear(true, player, "all");
                }
                if (args[1].equalsIgnoreCase("cl_ow")) {
                    Destination.lastdeath.clear(true, player, "ow");
                }
                if (args[1].equalsIgnoreCase("cl_n")) {
                    Destination.lastdeath.clear(true, player, "n");
                }
                if (args[1].equalsIgnoreCase("cl_e")) {
                    Destination.lastdeath.clear(true, player, "e");
                }
                return 1;
            }

            player.sendMessage(CUtl.usage(CUtl.commandUsage.destLastdeath()));
            return 1;
        }

        //SETTINGS
        if (args[0].equalsIgnoreCase("settings")) {
            if (args.length == 1) Destination.settings.UI(player, null);
            if (args.length == 3) Destination.settings.change(player, args[1], args[2], true);
            if (args.length == 4) Destination.settings.change(player, args[1], args[2], false);
            return 1;
        }

        //SEND
        if (args[0].equalsIgnoreCase("send")) {
            if (!Utl.inBetween(args.length, 4, 7)) {
                player.sendMessage(CUtl.usage(CUtl.commandUsage.destSend()));
                return 1;
            }

            // /dest send <IGN> saved <name>
            if (args[2].equalsIgnoreCase("saved")) {
                if (args.length > 4) {
                    player.sendMessage(CUtl.usage(CUtl.commandUsage.destSend()));
                    return 1;
                }
                Destination.player.send(player, args[1], args[3], "saved", null);
                return 1;
            }

            String pDIM = Utl.dim.PFormat(Utl.player.dim(player));
            //dest send <IGN> <xyz or xy> (dimension)
            //dest send <IGN> (name) <xyz or xy> (dimension)
            //dest send IGN x z
            if (args.length == 4) {
                Destination.player.send(player, args[1], args[2]+" "+args[3], pDIM, null);
            }

            //dest send IGN NAME x z
            if (args.length == 5 && !Utl.isInt(args[2])) {
                Destination.player.send(player, args[1], args[3]+" "+args[4], pDIM, args[2]);
                return 1;
            }
            //dest send IGN x z DIM
            if (args.length == 5 && !Utl.isInt(args[4])) {
                Destination.player.send(player, args[1], args[2]+" "+args[3], args[4], null);
                return 1;
            }
            //dest send IGN x y z
            if (args.length == 5) {
                Destination.player.send(player, args[1], args[2]+" "+args[3]+" "+args[4], pDIM, null);
            }

            //dest send IGN NAME x y z
            if (args.length == 6 && !Utl.isInt(args[2])) {
                Destination.player.send(player, args[1], args[3]+" "+args[4]+" "+args[5], pDIM, args[2]);
                return 1;
            }
            //dest send IGN NAME x z DIM
            if (args.length == 6 && !Utl.isInt(args[2]) && !Utl.isInt(args[5])) {
                Destination.player.send(player, args[1], args[3]+" "+args[4], args[5], args[2]);
                return 1;
            }
            //dest send IGN x y z DIM
            if (args.length == 6) {
                Destination.player.send(player, args[1], args[2]+" "+args[3]+" "+args[4], args[5], null);
            }

            //dest send IGN NAME x y z DIM
            if (args.length == 7 && !Utl.isInt(args[2])) {
                Destination.player.send(player, args[1], args[3]+" "+args[4]+" "+args[5], args[6],args[2]);
            }
            return 1;
        }

        //TRACK
        if (args[0].equalsIgnoreCase("track")) {
            //dest track <name>
            if (args.length == 2) {
                Destination.player.track(player, args[1]);
                return 1;
            }
            if (args.length != 4) {
                player.sendMessage(CUtl.usage(CUtl.commandUsage.destTrack()));
                return 1;
            }
            //dest track accept/deny <name> <id>
            if (args[1].equalsIgnoreCase("acp")) {
                Destination.player.trackAccept(player, args[2], args[3]);
                return 1;
            }
            if (args[1].equalsIgnoreCase("dny")) {
                Destination.player.trackDeny(player, args[2], args[3]);
                return 1;
            }
            player.sendMessage(CUtl.usage(CUtl.commandUsage.destTrack()));
            return 1;
        }
        player.sendMessage(CUtl.error(CUtl.lang("error.command")));
        return 1;
    }
}
