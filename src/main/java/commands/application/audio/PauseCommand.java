package commands.application.audio;

import audio.manager.GuildAudioState;
import audio.manager.IGuildAudioManager;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import utilities.EventValidator;

public class PauseCommand extends SlashCommand {
    private final IGuildAudioManager audioManager;

    public PauseCommand(IGuildAudioManager audioManager, Category category) {
        this.audioManager = audioManager;
        this.category = category;

        this.name = "pause";
        this.help = "Pause current playing audio track. Or resume if paused.";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (!audioManager.isGuildRegistered(event.getGuild())) {
            event.reply(":x: Not playing anything").setEphemeral(true).queue();
            return;
        }
        if (!EventValidator.isValidAuthorVoice(event)) {
            event.reply(":x: You are not in voice channel").setEphemeral(true).queue();
            return;
        }
        GuildAudioState state = audioManager.getAudioState(event.getGuild());

        state.player.setPaused(!state.player.isPaused());

        if (state.player.isPaused())
            event.reply(":pause_button:").queue();
        else
            event.reply(":arrow_forward:").queue();
    }
}
