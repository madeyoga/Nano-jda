package awaiter;

import audio.manager.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import awaiter.models.SearchCommandWaitingState;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearchCommandResponseWaiter extends ListenerAdapter implements IResponseWaiter<SearchCommandWaitingState> {
    private final GuildAudioManager audioManager;
    private final Map<String, SearchCommandWaitingState> waitingForUsers;

    public SearchCommandResponseWaiter(GuildAudioManager audioManager) {
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
        super.onButtonClick(event);
        if (event.getGuild() == null) return;
        if (event.getUser().isBot()) return;

        String[] id = event.getComponentId().split("-");

        event.deferReply(true).queue();

        SearchCommandWaitingState state = waitingForUsers.getOrDefault(id[0], null);
        if (state == null) {
            event.getHook().setEphemeral(true)
                    .editOriginal(":x: Could not execute button event: Null waiting state").queue();
            return;
        }

        int choiceIndex = Integer.parseInt(id[1]) - 1;
        AudioTrack track = state.getChoices().get(choiceIndex);
        track.setUserData(event.getUser().getId());
        GuildAudioManager.play(event.getMember(), audioManager.getAudioState(event.getGuild()), track);

        String response = ":musical_note: Added to queue: " + track.getInfo().title;

        event.getHook().setEphemeral(false).editOriginal(response).queue();

        waitingForUsers.remove(id[0]);
    }
}
