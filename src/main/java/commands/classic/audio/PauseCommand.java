package commands.classic.audio;

import audio.manager.GuildAudioState;
import audio.manager.IGuildAudioManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import utilities.EventValidator;

public class PauseCommand extends Command {
    private final IGuildAudioManager audioManager;

    public PauseCommand(IGuildAudioManager audioManager, Category category) {
        this.audioManager = audioManager;
        this.category = category;

        this.name = "pause";
        this.help = "Pause current playing audio track. Or resume if paused.";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!audioManager.isGuildRegistered(event.getGuild())) {
            event.reply(":x: Not playing anything");
            return;
        }
        if (!EventValidator.isValidAuthorVoice(event)) {
            event.reply(":x: You are not in voice channel");
            return;
        }
        GuildAudioState state = audioManager.getAudioState(event.getGuild());

        state.player.setPaused(!state.player.isPaused());

        if (state.player.isPaused())
            event.reply(":pause_button: paused");
        else
            event.reply(":arrow_forward: resumed");
    }
}
