package commands.classic.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class PingCommand extends Command {
    public PingCommand(Category category) {
        this.name = "ping";
        this.help = "Check Nano's latency";
        this.category = category;
    }

    @Override
    protected void execute(CommandEvent event) {
        long latency = System.currentTimeMillis() - event.getMessage().getTimeCreated().toInstant().toEpochMilli();
        event.reply(":hourglass_flowing_sand: " + latency + "ms.");
    }
}
