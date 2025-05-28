package com.vfcastro.dev.parkour.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

@SuppressWarnings("UnstableApiUsage")
public class ParkourCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("parkour")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = ctx.getArgument("name", String.class);
                                    ctx.getSource().getExecutor().sendPlainMessage("Parkour created: " + name);
                                    return Command.SINGLE_SUCCESS;
                                }))
                )
                .then(Commands.literal("checkpoint")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = ctx.getArgument("name", String.class);
                                    ctx.getSource().getExecutor().sendPlainMessage("Parkour checkpoint: " + name);
                                    return Command.SINGLE_SUCCESS;
                                }))
                )
                .then(Commands.literal("finish")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = ctx.getArgument("name", String.class);
                                    ctx.getSource().getExecutor().sendPlainMessage("Parkour finished: " + name);
                                    return Command.SINGLE_SUCCESS;
                                }))
                );
    }

}
