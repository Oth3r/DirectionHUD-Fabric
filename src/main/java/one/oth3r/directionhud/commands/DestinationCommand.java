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
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;

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
            if (config.deathsaving && PlayerData.get.dest.setting.lastdeath(player)) builder.suggest("lastdeath");
            if (config.DESTSaving) {
                builder.suggest("add");
                builder.suggest("saved");
            }
            builder.suggest("set");
            builder.suggest("clear");
            builder.suggest("settings");
            if (PlayerData.get.dest.setting.send(player) && DirectionHUD.server.isRemote())
                builder.suggest("send");
            if (PlayerData.get.dest.setting.track(player) && DirectionHUD.server.isRemote())
                builder.suggest("track");
            return builder.buildFuture();
        }
        if (pos > args.length) {
            return builder.buildFuture();
        }
        //SAVED
        if (args[1].equalsIgnoreCase("saved")) {
            return Destination.commandSuggester.savedCMD(player,builder,pos-2,Utl.trimStart(args,2));
        }
        //ADD
        if (args[1].equalsIgnoreCase("add")) {
            return Destination.commandSuggester.addCMD(player,builder,pos-2,Utl.trimStart(args,2));
        }
        if (args[1].equalsIgnoreCase("settings")) {
            return Destination.commandSuggester.settingsCMD(builder,pos-2,Utl.trimStart(args,2));
        }
        if (args[1].equalsIgnoreCase("set")) {
            return Destination.commandSuggester.setCMD(player,builder,pos-2,Utl.trimStart(args,2));
        }
        if (args[1].equalsIgnoreCase("send")) {
            return Destination.commandSuggester.sendCMD(player,builder,pos-2,Utl.trimStart(args,2));
        }
        if (args[1].equalsIgnoreCase("track")) {
            return Destination.commandSuggester.trackCMD(player,builder,pos-2,Utl.trimStart(args,2));
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
        //SET
        if (args[0].equalsIgnoreCase("set")) {
            return Destination.commandExecutor.setCMD(player, Utl.trimStart(args,1));
        }
        //CLEAR
        if (args[0].equalsIgnoreCase("clear")) {
            Destination.clear(player, null);
            return 1;
        }
        //SAVED
        if (args[0].equalsIgnoreCase("saved")) {
            return Destination.commandExecutor.savedCMD(player,Utl.trimStart(args,1));
        }
        //ADD
        if (args[0].equalsIgnoreCase("add")) {
            return Destination.commandExecutor.addCMD(player,Utl.trimStart(args,1));
        }
        //REMOVE (HIDDEN)
        if (args[0].equalsIgnoreCase("remove")) {
            return Destination.commandExecutor.removeCMD(player,Utl.trimStart(args,1));
        }
        //LASTDEATH
        if (args[0].equalsIgnoreCase("lastdeath")) {
            return Destination.commandExecutor.lastdeathCMD(player,Utl.trimStart(args,1));
        }
        //SETTINGS
        if (args[0].equalsIgnoreCase("settings")) {
            return Destination.commandExecutor.settingsCMD(player,Utl.trimStart(args,1));
        }
        //SEND
        if (args[0].equalsIgnoreCase("send")) {
            return Destination.commandExecutor.sendCMD(player,Utl.trimStart(args,1));
        }
        //TRACK
        if (args[0].equalsIgnoreCase("track")) {
            return Destination.commandExecutor.trackCMD(player,Utl.trimStart(args,1));
        }
        player.sendMessage(CUtl.error(CUtl.lang("error.command")));
        return 1;
    }
}
