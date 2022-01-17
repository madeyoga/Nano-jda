package commands.classic.audio;

import audio.manager.GuildAudioManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Member;

public class JoinCommand extends Command {

    public JoinCommand(Category category){
        this.name = "join";
        this.help = "Join author's voice channel";
        this.guildOnly = true;
        this.category = category;
        this.aliases = new String[] {"summon"};
    }

    @Override
    protected void execute(CommandEvent event) {
        Member author = event.getMember();
        if (author.getVoiceState().getChannel() == null) {
            event.reply(":x: | You are not in a voice channel");
            return;
        }
        GuildAudioManager.connectToAuthorVoiceChannel(author);
        event.reply(":mega: Joined `" + author.getVoiceState().getChannel().getName() + "`");
    }
}
