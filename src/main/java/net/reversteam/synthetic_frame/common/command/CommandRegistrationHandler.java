package net.reversteam.synthetic_frame.common.command;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.reversteam.synthetic_frame.SyntheticFrame;

@Mod.EventBusSubscriber(modid = SyntheticFrame.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandRegistrationHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SynthFrameCommand.register(event.getDispatcher());
    }
}