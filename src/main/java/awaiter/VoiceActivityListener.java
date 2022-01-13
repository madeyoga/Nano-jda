package awaiter;

import audio.manager.IGuildAudioManager;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VoiceActivityListener extends ListenerAdapter {
    private final IGuildAudioManager audioManager;

    public VoiceActivityListener(IGuildAudioManager audioManager) {
        this.audioManager = audioManager;
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        if (event.getMember().getUser().isBot()) return;

        VoiceChannel selfVoiceChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
        if (selfVoiceChannel == null) {
            return;
        }
        if (!event.getChannelLeft().getId().equals(selfVoiceChannel.getId())) {
            return;
        }

        if (selfVoiceChannel.getMembers().size() < 2) {
            audioManager.stopAndLeaveVoiceChannel(event.getGuild());
        }
    }
}
