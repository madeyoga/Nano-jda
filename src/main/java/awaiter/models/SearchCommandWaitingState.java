package awaiter.models;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public class SearchCommandWaitingState implements IWaitingState {

    private final List<AudioTrack> choices;
    private final String authorId;

    private final String eventId;
    private final String channelId;
    private final long sentTime;

    public SearchCommandWaitingState(List<AudioTrack> choices, String authorId, String eventId, String channelId) {
        this.choices = choices;
        this.authorId = authorId;
        this.eventId = eventId;
        this.channelId = channelId;
        this.sentTime = System.currentTimeMillis();
    }

    @Override
    public String getIdentifier() {
        return eventId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public List<AudioTrack> getChoices() {
        return choices;
    }

    public String getEventId() {
        return eventId;
    }

    public String getChannelId() {
        return channelId;
    }

    public long getSentTime() {
        return sentTime;
    }
}
