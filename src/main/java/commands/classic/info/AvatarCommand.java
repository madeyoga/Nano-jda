package commands.classic.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class AvatarCommand extends Command {

    public AvatarCommand(Category category) {
        this.name = "avatar";
        this.help = "Get user's avatar";
        this.category = category;
        this.guildOnly = true;
        this.aliases = new String[] {"ava"};
    }

    @Override
    protected void execute(CommandEvent event) {
        List<User> mentionedUser = event.getMessage().getMentionedUsers();

        if (mentionedUser.size() < 1) {
            event.reply(":x: | Could not execute play command. Query option was null");
            return;
        }

        User user = mentionedUser.get(0);
        String url = user.getAvatarUrl();
        if (url == null) {
            event.reply(user.getName() + " has not set an avatar ");
            return;
        }
        event.reply(user.getAvatarUrl());
    }
}
