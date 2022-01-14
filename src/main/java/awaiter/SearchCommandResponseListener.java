package awaiter;

import audio.manager.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import awaiter.models.SearchCommandWaitingState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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

        int choiceIndex = Integer.parseInt(id[1]) - 1;
        AudioTrack track = state.getChoices().get(choiceIndex);
        track.setUserData(event.getUser().getId());
        GuildAudioManager.play(event.getMember(), audioManager.getAudioState(event.getGuild()), track);

        event.getMessage().editMessageComponents(new ArrayList<>()).queue();

        event.getMessage().editMessage(":musical_note: Added to queue: " + track.getInfo().title).queue();

//        event.getHook().editOriginal(":musical_note: Added to queue: " + track.getInfo().title).queue();
        event.getHook().deleteOriginal().queue();

        waitingForUsers.remove(id[0]);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
    }
}
