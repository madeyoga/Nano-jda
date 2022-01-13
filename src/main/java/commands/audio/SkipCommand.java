package commands.audio;

import audio.manager.GuildAudioState;
import audio.manager.IGuildAudioManager;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import utilities.EventValidator;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SkipCommand extends SlashCommand {
    private final IGuildAudioManager audioManager;

    public SkipCommand(IGuildAudioManager audioManager,Category category) {
        this.audioManager = audioManager;
        this.category = category;

        this.name = "skip";
        this.help = "Skips current playing audio track";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (!EventValidator.isValidAuthorVoice(event)) {
            event.reply("You are not in a voice channel").setEphemeral(true).queue();
            return;
        }
        if (!audioManager.isGuildRegistered(event.getGuild())) {
            event.reply("Not playing anything").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        GuildAudioState state = audioManager.getAudioState(event.getGuild());
        // only author can skip audio track
        if (!event.getUser().getId().equals(state.player.getPlayingTrack().getUserData(String.class))) {
            event.getHook().setEphemeral(true).editOriginal(":x: Currently, only author can skip the audio track").queue();
            return;
        }

        state.scheduler.nextTrack();
        AudioTrack playingTrack = state.player.getPlayingTrack();
        if (playingTrack == null) {
            event.getHook().editOriginal(":mega: Finished playing current queue.").queue();
            audioManager.stopAndLeaveVoiceChannel(event.getGuild());
            return;
        }

        event.getHook().editOriginal(":musical_note: Now playing " + playingTrack.getInfo().title).queue();
    }
}
