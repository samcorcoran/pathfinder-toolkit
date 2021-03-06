package com.lateensoft.pathfinder.toolkit.views.encounter;

import com.google.common.collect.Lists;
import com.lateensoft.pathfinder.toolkit.model.party.EncounterParticipant;

import java.util.Collection;
import java.util.List;

/**
* @author tsiemens
*/
public class EncounterParticipantRowModel implements Comparable<EncounterParticipantRowModel> {
    private final EncounterParticipant participant;
    private int lastCheckRoll = 0;
    private RollState lastCheckRollState = null;

    public enum RollState { NORMAL, CRIT_FAIL, CRIT }

    public static List<EncounterParticipantRowModel> listFrom(Collection<EncounterParticipant> participants) {
        List<EncounterParticipantRowModel> rows = Lists.newArrayListWithCapacity(participants.size());
        for (EncounterParticipant participant : participants) {
            rows.add(new EncounterParticipantRowModel(participant));
        }
        return rows;
    }

    public EncounterParticipantRowModel(EncounterParticipant participant) {
        this.participant = participant;
    }

    public EncounterParticipant getParticipant() {
        return participant;
    }

    public int getLastCheckRoll() {
        return lastCheckRoll;
    }

    public void setLastCheckRoll(int lastCheckRoll) {
        this.lastCheckRoll = lastCheckRoll;
    }

    public RollState getLastCheckRollState() {
        return lastCheckRollState;
    }

    public void setLastCheckRollState(RollState lastCheckRollState) {
        this.lastCheckRollState = lastCheckRollState;
    }

    @Override
    public int compareTo(EncounterParticipantRowModel another) {
        return participant.compareTo(another.participant);
    }
}
