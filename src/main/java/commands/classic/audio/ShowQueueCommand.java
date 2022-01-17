package commands.classic.audio;

import audio.manager.GuildAudioState;
import audio.manager.IGuildAudioManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import utilities.StyledEmbedBuilder;

public class ShowQueueCommand extends Command {
    private final IGuildAudioManager audioManager;

    public ShowQueueCommand(IGuildAudioManager audioManager, Category category) {
        this.audioManager = audioManager;
        this.name = "queue";
        this.help = "Shows current playing queue";
        this.guildOnly = true;
        this.category = category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!audioManager.isGuildRegistered(event.getGuild())) {
            event.reply(":x: Could not execute show_queue command: Queue is empty");
            return;
        }

        GuildAudioState state = audioManager.getAudioState(event.getGuild());
        if (state.scheduler.getQueue().isEmpty()) {
            event.reply("Queue is currently empty, add audio track to queue using /play or /ytsearch");
            return;
        }
        StyledEmbedBuilder embedBuilder = new StyledEmbedBuilder();
        embedBuilder.setAuthor(event.getGuild().getName() + "'s queue", event.getGuild().getIconUrl(),
                event.getAuthor().getAvatarUrl());
        StringBuilder stringBuilder = new StringBuilder();
        int index = 1;
        for (AudioTrack track : state.scheduler.getQueue()) {
            stringBuilder.append(index)
                    .append(". [**")
                    .append(track.getInfo().title)
                    .append("**](")
                    .append(track.getInfo().uri)
                    .append(")\n");
            index += 1;

            if (index == 5) {
                break;
            }
        }
        embedBuilder.addField("Top 5 entries in queue", stringBuilder.toString(), false);
        embedBuilder.setFooter("Thank you for using " + event.getJDA().getSelfUser().getName(),
                event.getJDA().getSelfUser().getAvatarUrl());
        event.reply(embedBuilder.build());
    }
}
