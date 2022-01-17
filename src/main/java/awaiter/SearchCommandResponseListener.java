package awaiter;

import audio.manager.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import awaiter.models.SearchCommandWaitingState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearchCommandResponseListener extends ListenerAdapter implements IResponseListener<SearchCommandWaitingState> {
    private final GuildAudioManager audioManager;
    private final Map<String, SearchCommandWaitingState> waitingForUsers;

    public SearchCommandResponseListener(GuildAudioManager audioManager) {
        this.audioManager = audioManager;
        this.waitingForUsers = new ConcurrentHashMap<>();
    }

    @Override
    public void register(SearchCommandWaitingState state) {
        waitingForUsers.put(state.getIdentifier(), state);
    }

    @Override
    public SearchCommandWaitingState getState(String identifier) {
        return waitingForUsers.get(identifier);
    }

    /**
     * Currently using button event id as identifier
     * @param event
     */
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getGuild() == null) return;
        if (event.getUser().isBot()) return;

        String[] id = event.getComponentId().split("-");

        event.deferReply().queue();

        SearchCommandWaitingState state = waitingForUsers.getOrDefault(id[0], null);
        if (state == null) {
            event.getHook().setEphemeral(true)
                    .sendMessage(":x: Could not execute button event: Null waiting state").queue();
            return;
        }
        if (!state.getAuthorId().equals(event.getUser().getId())) {
            event.getHook().setEphemeral(true)
                    .sendMessage(":x: Could not execute button event: Not author").queue();
            return;
        }

        if (id[1].equalsIgnoreCase("cancel")) {
            event.getMessage().editMessage(":x: Cancelled").queue();
        }
        else {
            int choiceIndex = Integer.parseInt(id[1]) - 1;
            AudioTrack track = state.getChoices().get(choiceIndex);
            track.setUserData(event.getUser().getId());
            GuildAudioManager.play(event.getMember(), audioManager.getAudioState(event.getGuild()), track);

//            event.getMessage().editMessage(":musical_note: Added to queue: " + track.getInfo().title).queue();
            event.getHook().editOriginal(":musical_note: Added to queue: " + track.getInfo().title).queue();;
        }

//        event.getHook().deleteOriginal().queue();
        event.getMessage().editMessageComponents(new ArrayList<>()).queue();

        waitingForUsers.remove(id[0]);
    }

    /**
     * Using author id as identifier.
     * @param event
     */
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getMessage().getContentRaw().matches("\\d+")) return;
        if (!waitingForUsers.containsKey(event.getAuthor().getId())) return;

        SearchCommandWaitingState state = waitingForUsers.getOrDefault(event.getAuthor().getId(), null);
        if (state == null) {
            return;
        }

        int choiceIndex = Integer.parseInt(event.getMessage().getContentRaw()) - 1;
        AudioTrack track = state.getChoices().get(choiceIndex);
        track.setUserData(event.getAuthor().getId());
        GuildAudioManager.play(event.getMember(), audioManager.getAudioState(event.getGuild()), track);

        event.getChannel().sendMessage(":musical_note: Added to queue: " + track.getInfo().title).queue();

        waitingForUsers.remove(event.getAuthor().getId());
    }
}
