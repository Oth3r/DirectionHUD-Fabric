package one.oth3r.directionhud.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oth3r.directionhud.DirectionHUD;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DirHUDCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dirhud")
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
        dispatcher.register(CommandManager.literal("directionhud").redirect(dispatcher.getRoot().getChild("dirhud"))
                .executes((context2) -> command(context2.getSource(), context2.getInput())));
    }
    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder, int pos) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        assert player != null;
        if (pos == 1) {
            if (!DirectionHUD.server.isRemote()) return builder.suggest("defaults").buildFuture();
            else if (player.hasPermissionLevel(2)) return builder.suggest("reload").buildFuture();
        }
        return builder.buildFuture();
    }
    private static int command(ServerCommandSource source, String arg) {
        ServerPlayerEntity player = source.getPlayer();
        String[] args;

        //trim all the arguments before the command
        List<String> keywords = Arrays.asList("dirhud", "directionhud");
        int index = Integer.MAX_VALUE;
        //finds the index for the words
        for (String keyword : keywords) {
            int keywordIndex = arg.indexOf(keyword);
            if (keywordIndex != -1 && keywordIndex < index) index = keywordIndex;
        }
        //trims the words before the text
        if (index != Integer.MAX_VALUE) arg = arg.substring(index).trim();
        args = arg.split(" ");
        if (args[0].equals("dirhud") || args[0].equals("directionhud"))
            args = arg.replaceFirst("(?i)dir(ection)?hud ", "").split(" ");

        if (player == null) {
            if (args[0].equalsIgnoreCase("reload") && DirectionHUD.server.isRemote()) {
                DirHUD.reload(null);
            }
            return 1;
        }

        if (args[0].equalsIgnoreCase("dirhud") || args[0].equalsIgnoreCase("directionhud")) {
            DirHUD.UI(player);
            return 1;
        }
        if (args[0].equalsIgnoreCase("defaults") && !DirectionHUD.server.isRemote()) {
            if (args.length == 1) {
                DirHUD.defaults(player);
            }
            if (args.length != 2) {
                return 1;
            }
            if (args[1].equalsIgnoreCase("set")) {
                DirHUD.setDefaults(player);
            }
            if (args[1].equalsIgnoreCase("reset")) {
                DirHUD.resetDefaults(player);
            }
        }
        if (args[0].equalsIgnoreCase("reload") && DirectionHUD.server.isRemote() && player.hasPermissionLevel(2)) {
            DirHUD.reload(player);
        }
        return 1;
    }
}
