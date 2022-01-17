package commands.classic.audio;

import audio.manager.GuildAudioManager;
import audio.manager.GuildAudioState;
import audio.manager.IGuildAudioManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShuffleQueueCommand extends Command {
    private final IGuildAudioManager audioManager;

    public ShuffleQueueCommand(GuildAudioManager audioManager, Category category) {
        this.audioManager = audioManager;
        this.name = "shuffle";
        this.help = "Shuffles current queue";
        this.category = category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild().getSelfMember().getVoiceState().getChannel() == null) {
            event.reply("I'm not in a voice channel");
            return;
        }
        if (event.getMember().getVoiceState().getChannel() == null) {
            event.reply(":x: You're not in a voice channel");
            return;
        }
        GuildAudioState audioState = audioManager.getAudioState(event.getGuild());
        if (audioState.scheduler.getQueue().size() > 1)
            shuffleQueue(audioState);
        event.reply(":white_check_mark: Queue shuffled");
    }

    private void shuffleQueue(GuildAudioState audioState) {
        List<AudioTrack> tracks = new ArrayList<>(audioState.scheduler.getQueue());
        Collections.shuffle(tracks);

        audioState.scheduler.getQueue().clear();
        audioState.scheduler.getQueue().addAll(tracks);
    }
}
