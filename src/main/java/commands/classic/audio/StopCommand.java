package commands.classic.audio;

import audio.manager.GuildAudioManager;
import audio.manager.GuildAudioState;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import utilities.EventValidator;

public class StopCommand extends Command {

    private final GuildAudioManager audioManager;

    public StopCommand(GuildAudioManager audioManager, Category category){
        this.audioManager = audioManager;
        this.name = "stop";
        this.help = "Stop playing audio and leave voice channel";
        this.guildOnly = true;
        this.category = category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!EventValidator.isValidAuthorVoice(event)) return;
        VoiceChannel selfVoiceChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
        if (selfVoiceChannel == null) {
            event.reply(":x: | I'm currently not in a voice channel");
            return;
        }

        stopAndLeaveVoice(event.getGuild());

        event.reply(":mega: Finished playing current queue.");
    }

    private void stopAndLeaveVoice(Guild guild) {
        guild.getAudioManager().closeAudioConnection();
        GuildAudioState audioState = audioManager.getAudioState(guild);
        audioState.player.destroy();
        audioState.scheduler.getQueue().clear();
        audioManager.removeAudioState(guild);
    }
}
