package commands.classic.audio;

import audio.manager.GuildAudioManager;
import audio.manager.IGuildAudioManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class PlayCommand extends Command {
    private final IGuildAudioManager audioManager;

    public PlayCommand(GuildAudioManager audioManager, Category category) {
        this.audioManager = audioManager;
        this.name = "play";
        this.help = "Play or queue an audio track";
        this.guildOnly = true;
        this.category = category;
        this.aliases = new String[] {"p"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getMember().getVoiceState() == null) return;

        String args = event.getArgs();
        if (args.isEmpty()) {
            event.reply(":x: | Could not execute play command. Query option was null");
            return;
        }

        audioManager.loadAndPlay(event, args);
    }
}
