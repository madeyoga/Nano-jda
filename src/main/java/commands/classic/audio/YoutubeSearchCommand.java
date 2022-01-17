package commands.classic.audio;

import audio.manager.GuildAudioManager;
import awaiter.IResponseListener;
import awaiter.SearchCommandResponseListener;
import awaiter.models.SearchCommandWaitingState;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import utilities.TimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class YoutubeSearchCommand extends Command {
    private final GuildAudioManager audioManager;
    private final IResponseListener<SearchCommandWaitingState> waiter;

    public YoutubeSearchCommand(GuildAudioManager audioManager, Category category, SearchCommandResponseListener waiter) {
        this.audioManager = audioManager;
        this.waiter = waiter;
        this.category = category;
        this.name = "search";
        this.help = "Search and pick audio track to play from youtube.";
        this.guildOnly = true;
        this.aliases = new String[] {"s"};
    }

    @Override
    protected void execute(CommandEvent event) {

        if (event.getMember().getVoiceState().getChannel() == null) {
            event.reply(":x: You're not in a voice channel");
            return;
        }
        final String arguments = event.getArgs();

        if (arguments.isEmpty()) {
            event.reply(":x: Could not execute search command. Query option was null.");
            return;
        }

        audioManager.getPlayerManager().loadItem("ytsearch:" + arguments,
            new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    List<AudioTrack> tracks = new ArrayList<>();
                    tracks.add(track);

                    waiter.register(new SearchCommandWaitingState(tracks,
                            event.getAuthor().getId(), event.getEvent().getMessageId(), event.getChannel().getId()));

                    String response = String.format(
                            "Search result for: %s\n\n1. %s [%s]",
                            arguments, track.getInfo().title, TimeFormatter.durationFormat(track.getDuration()));
                    event.getChannel().sendMessage(response).queue();
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    List<AudioTrack> tracks = new ArrayList<>();

                    StringBuilder builder = new StringBuilder();
                    builder.append("Search result for: ").append(arguments).append("\n");
                    for (int i = 0; i < playlist.getTracks().size(); i++) {
                        String currentIndex = String.format("%s", i + 1);

                        AudioTrack track = playlist.getTracks().get(i);
                        String row = String.format("\n%s. %s [%s]", currentIndex, track.getInfo().title,
                                TimeFormatter.durationFormat(track.getDuration()));
                        builder.append(row);

                        tracks.add(track);

                        if (i == 4) break;
                    }

                    waiter.register(new SearchCommandWaitingState(tracks,
                            event.getAuthor().getId(), event.getMessage().getId(), event.getChannel().getId()));

                    event.reply(builder.toString());
                }

                @Override
                public void noMatches() {
                    event.getChannel().sendMessage(":x: Nothing found by **" + arguments + "**").queue();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.getChannel().sendMessage(":x: Could not load: " + exception.getMessage()).queue();
                }
            }
        );
    }
}
