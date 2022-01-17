package commands.classic.audio;

import audio.manager.GuildAudioManager;
import audio.manager.GuildAudioState;
import audio.manager.IGuildAudioManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class NowPlayCommand extends Command {
    private final IGuildAudioManager audioManager;

    public NowPlayCommand(GuildAudioManager audioManager, Category category) {
        this.audioManager = audioManager;
        this.name = "now_playing";
        this.help = "Shows current playing audio";
        this.guildOnly = true;
        this.category = category;
        this.aliases = new String[] {"np"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild().getSelfMember().getVoiceState().getChannel() == null) {
            event.reply(":x: Not playing anything");
            return;
        }

        GuildAudioState audioState = audioManager.getAudioState(event.getGuild());

        AudioTrack playingTrack = audioState.player.getPlayingTrack();
        if (playingTrack == null) {
            event.reply(":x: Not playing anything");
            return;
        }

        String songName = playingTrack.getInfo().title;
        event.reply(":musical_note: Now playing: " + songName);
    }
}
