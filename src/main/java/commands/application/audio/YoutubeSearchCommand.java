package commands.application.audio;

import audio.manager.GuildAudioManager;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import awaiter.IResponseListener;
import awaiter.SearchCommandResponseListener;
import awaiter.models.SearchCommandWaitingState;
import utilities.TimeFormatter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class YoutubeSearchCommand extends SlashCommand {
    private final GuildAudioManager audioManager;
    private final IResponseListener<SearchCommandWaitingState> waiter;

    public YoutubeSearchCommand(GuildAudioManager audioManager, Category category, SearchCommandResponseListener waiter) {
        this.audioManager = audioManager;
        this.waiter = waiter;
        this.category = category;
        this.name = "ytsearch";
        this.help = "Search and pick audio track to play from youtube.";
        this.options = Collections.singletonList(
            new OptionData(OptionType.STRING, "query", "Specify the search keywords").setRequired(true)
        );
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        if (event.getMember().getVoiceState().getChannel() == null) {
            event.reply(":x: You're not in a voice channel").queue();
            return;
        }

        final String arguments = event.getOption("query").getAsString();
        audioManager.getPlayerManager().loadItem("ytsearch:" + arguments,
            new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    List<AudioTrack> tracks = new ArrayList<>();
                    tracks.add(track);

                    waiter.register(new SearchCommandWaitingState(tracks,
                            event.getUser().getId(), event.getId(), event.getChannel().getId()));

                    String response = String.format(
                            "Search result for: %s\n\n1. %s [%s]",
                            arguments, track.getInfo().title, TimeFormatter.durationFormat(track.getDuration()));
                    event.getHook().sendMessage(response)
                            .addActionRow(Button.primary(event.getId() + "-1", "1"))
                            .queue();
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    List<AudioTrack> tracks = new ArrayList<>();

                    Collection<Button> actions = new ArrayList<>();

                    StringBuilder builder = new StringBuilder();
                    builder.append("Search result for: ").append(arguments).append("\n");
                    for (int i = 0; i < playlist.getTracks().size(); i++) {
                        String currentIndex = String.format("%s", i + 1);

                        AudioTrack track = playlist.getTracks().get(i);
                        String row = String.format("\n%s. %s [%s]", currentIndex, track.getInfo().title,
                                TimeFormatter.durationFormat(track.getDuration()));
                        builder.append(row);

                        actions.add(Button.primary(event.getId() + "-" + currentIndex, currentIndex));

                        tracks.add(track);

                        if (i == 4) break;
                    }

                    waiter.register(new SearchCommandWaitingState(tracks,
                            event.getUser().getId(), event.getId(), event.getChannel().getId()));

                    event.reply(builder.toString())
                            .addActionRow(actions)
                            .queue();
                }

                @Override
                public void noMatches() {
                    event.getHook().editOriginal(":x: Nothing found by **" + arguments + "**").queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.getHook().editOriginal(":x: Could not load: " + exception.getMessage()).queue();
                }
            }
        );
    }
}
