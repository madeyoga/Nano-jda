package commands.classic.audio;

import audio.manager.GuildAudioState;
import audio.manager.IGuildAudioManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import utilities.EventValidator;

public class SkipCommand extends Command {
    private final IGuildAudioManager audioManager;

    public SkipCommand(IGuildAudioManager audioManager,Category category) {
        this.audioManager = audioManager;
        this.category = category;

        this.name = "skip";
        this.help = "Skips current playing audio track";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!EventValidator.isValidAuthorVoice(event)) {
            event.reply("You are not in a voice channel");
            return;
        }
        if (!audioManager.isGuildRegistered(event.getGuild())) {
            event.reply(":x: Not playing anything");
            return;
        }

        GuildAudioState state = audioManager.getAudioState(event.getGuild());
        // only author can skip audio track
        if (!event.getAuthor().getId().equals(state.player.getPlayingTrack().getUserData(String.class))) {
            event.reply(":x: Currently, only author can skip the audio track");
            return;
        }

        state.scheduler.nextTrack();
        AudioTrack playingTrack = state.player.getPlayingTrack();
        if (playingTrack == null) {
            event.reply(":mega: Finished playing current queue.");
            audioManager.stopAndLeaveVoiceChannel(event.getGuild());
            return;
        }

        event.reply(":musical_note: Now playing " + playingTrack.getInfo().title);
    }
}
