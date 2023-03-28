package one.oth3r.directionhud.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oth3r.directionhud.utils.CUtl;

import java.util.concurrent.CompletableFuture;


public class HUDCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("hud")
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
    }
    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder, int pos) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        assert player != null;
        String[] args = context.getInput().split(" ");
        if (pos == 1) {
            builder.suggest("edit");
            builder.suggest("color");
            builder.suggest("toggle");
            return builder.buildFuture();
        }
        if (pos != args.length) {
            return builder.buildFuture();
        }
        if (pos == 4 && args[2].equals("set")) {
            return builder.suggest("ffffff").buildFuture();
        }
        return builder.buildFuture();
    }
    private static int command(ServerCommandSource source, String arg) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 1;
        String[] args;

        //trims the words before the text
        //find the index of the word
        int index = arg.indexOf("hud");
        //trims everything before the word
        if (index != -1) arg = arg.substring(index).trim();
        args = arg.split(" ");
        if (args[0].equalsIgnoreCase("hud"))
            args = arg.replaceFirst("hud ", "").split(" ");

        if (args[0].equalsIgnoreCase("hud")) {
            HUD.UI(player, null);
            return 1;
        }
        //MODULES
        if (args[0].equalsIgnoreCase("edit")) {
            //UI
            if (args.length == 1) {
                HUD.order.UI(player, null, null);
                return 1;
            }
            //RESET
            if (args[1].equals("reset") && args.length == 2) {
                HUD.order.reset(player, true);
                return 1;
            }

            //MOVE UP / DOWN CMD
            if (args[1].equals("move") && args.length == 4) {
                HUD.order.move(player, args[2], args[3], true);
                return 1;
            }

            //TOGGLE
            if (args[1].equals("state") && args.length == 4) {
                HUD.order.toggle(player, args[2], Boolean.parseBoolean(args[3]), true);
                return 1;
            }

            //SETTING
            if (args[1].equals("setting") && args.length == 4) {
                HUD.order.setting(player, args[2], args[3], true);
                return 1;
            }
            return 1;
        }

        //COLOR
        if (args[0].equalsIgnoreCase("color")) {
            if (args.length == 1) {
                HUD.color.UI(player, null);
                return 1;

            }

            //COLOR
            if (args.length == 3 && args[1].equals("edt")) {
                if (args[2].equals("pri")) {
                    HUD.color.changeUI(player, "pri", null);
                }
                if (args[2].equals("sec")) {
                    HUD.color.changeUI(player, "sec", null);
                }
                return 1;
            }

            //RESET
            if (args[1].equals("rset")) {
                if (args.length == 2) {
                    HUD.color.reset(player, null, true);
                    return 1;
                }
                if (args.length == 3) {
                    HUD.color.reset(player, args[2], true);
                    return 1;
                }
            }

            //SET COLOR
            if (args[1].equals("set") && args.length == 4) {
                HUD.color.setColor(player, args[2], args[3], true);
                return 1;
            }
            if (args[1].equals("bold") && args.length == 4) {
                HUD.color.setBold(player, args[2], Boolean.parseBoolean(args[3]), true);
                return 1;
            }
            if (args[1].equals("italics") && args.length == 4) {
                HUD.color.setItalics(player, args[2], Boolean.parseBoolean(args[3]), true);
                return 1;
            }
            return 1;
        }

        //TOGGLE
        if (args[0].equalsIgnoreCase("toggle")) {
            if (args.length == 1) {
                HUD.toggle(player, null, false);
                return 1;
            }
            if (args.length != 2) return 1;
            HUD.toggle(player, Boolean.parseBoolean(args[1]), true);
            return 1;
        }

        player.sendMessage(CUtl.error(CUtl.lang("error.command")));
        return 1;
    }
}
