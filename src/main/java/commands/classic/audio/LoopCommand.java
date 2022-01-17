package commands.classic.audio;

import audio.manager.GuildAudioState;
import audio.manager.IGuildAudioManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import utilities.EventValidator;

public class LoopCommand extends Command {
    private final IGuildAudioManager audioManager;

    public LoopCommand(IGuildAudioManager audioManager, Category category) {
        this.audioManager = audioManager;
        this.name = "loop";
        this.help = "Put current audio track to the last entry of the queue after finished playing";
        this.category = category;
        this.aliases = new String[] {"repeat"};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!EventValidator.isValidAuthorVoice(event)) {
            event.reply(":x: You are not connected to any voice channel");
            return;
        }

        if (!audioManager.isGuildRegistered(event.getGuild())) {
            event.reply(":x: Not playing anything");
            return;
        }

        GuildAudioState state = audioManager.getAudioState(event.getGuild());
        state.scheduler.setRepeatMode(!state.scheduler.isRepeatMode());
        if (state.scheduler.isRepeatMode()) {
            event.reply(":repeat: Enabled");
        }
        else {
            event.reply(":arrow_right_hook:");
        }
    }
}
