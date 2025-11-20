package io.github.structure_dump.strucdump;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Mod.EventBusSubscriber(bus =  Mod.EventBusSubscriber.Bus.FORGE)
public class StructureDumpCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("dumpstructures")
                        .executes(StructureDumpCommand::dumpStructures)
        );
    }

    private static int dumpStructures(CommandContext<CommandSourceStack> context) {
        try {
            ServerLevel world = context.getSource().getLevel();
            File dumpFile = new File("structure_dump.txt");

            try (FileWriter writer = new FileWriter(dumpFile)) {
                writer.write("Registered Structures:\n\n");

                for (Map.Entry<ResourceKey<Structure>, Structure> entry : world.registryAccess()
                        .registryOrThrow(Registries.STRUCTURE).entrySet()) {
                    writer.write(entry.getKey().location() + "\n");
                }
            }

            context.getSource().sendSuccess(
                    () -> net.minecraft.network.chat.Component.literal("Structure dump written to: " + dumpFile.getAbsolutePath()),
                    false
            );
        } catch (IOException e) {
            context.getSource().sendFailure(
                    net.minecraft.network.chat.Component.literal("Structure dump could not be created.")
            );
            e.printStackTrace();
        }

        return Command.SINGLE_SUCCESS;
    }
}