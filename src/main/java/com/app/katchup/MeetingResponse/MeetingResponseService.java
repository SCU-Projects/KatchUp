package com.app.katchup.MeetingResponse;

import com.app.katchup.Meeting.MeetingService;
import com.app.katchup.Meeting.model.Meeting;
import com.app.katchup.MeetingResponse.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingResponseService {
    @Autowired
    MeetingResponseRepository meetingResponseRepo;

    @Autowired
    MeetingService meetingService;

    public List<Inbox> getInboxForUserName(String userName){

        List<MeetingID> meetingIdList = meetingResponseRepo.findAllMeetingIdsbyUserName(userName);
        List<Meeting> meetingDetailsList = new ArrayList<>();

        if(meetingIdList.size() > 0){
            List<String> meetingIdsList = meetingIdList.stream().map(meetingID -> meetingID.getMeetingId()).collect(Collectors.toList());
            meetingDetailsList = meetingService.getMeetingDetailsForMeetingIds(meetingIdsList);
        }

        List<Inbox> inboxList = meetingDetailsList.stream().map(meeting -> {
                Inbox inbox = new Inbox();
                inbox.setMeetingId(meeting.getMeetingId());
                inbox.setSubject(meeting.getSubject());
                inbox.setVenue(meeting.getVenue());
                inbox.setHost(meeting.getHost());
                inbox.setStartDateTime(meeting.getStartDateTime());
                inbox.setStatus(meeting.getStatus());
                inbox.setEndDateTime(meeting.getEndDateTime());
                inbox.setPassword(meeting.getPassword());
                if (meeting.getSeats() == -1)
                    inbox.setSeats(1000);
                else
                    inbox.setSeats(meeting.getSeats());
                return inbox;
        }).collect(Collectors.toList());

        return inboxList;
    }

    public MeetingInboxResponse postInboxForUserName(MeetingInboxResponse meetingInboxResponse){
        meetingResponseRepo.save(meetingInboxResponse);
        return meetingInboxResponse;
    }

 //update decision for client
   public Decision putResponseForMeeting(Meeting meeting, String userName, MeetingRequestBody requestBody) throws Exception {
        MeetingInboxResponse meetingResponse = meetingResponseRepo.findByUserNameAndMeetingID(userName, meeting.getMeetingId());
        if(meetingResponse == null)
            return null;
        meetingResponse.setDecision(requestBody.getDecision());
        if(requestBody.getDecision() == Decision.POLL){
            if(meeting.isPollAllowed()) {
                meetingResponse.setAlternativeStartDateTime(requestBody.getStartDateTime());
                meetingResponse.setAlternativeEndDateTime(requestBody.getEndDateTime());
            }
            else
                throw new Exception("Sorry! The meeting host didn't enable the polled option");
        }
        meetingResponseRepo.save(meetingResponse);
        return meetingResponse.getDecision();
   }

   public MeetingInboxResponse getResponseForMeeting(String userName, String meetingId){
        MeetingInboxResponse meetingResponse = meetingResponseRepo.findByUserNameAndMeetingID(userName, meetingId);
        return meetingResponse;
   }

    public MeetingStats getStatsForMeeting(Meeting meeting) {
        List<MeetingInboxResponse> meetingInboxResponseList = meetingResponseRepo.findAllbyMeetingID(meeting.getMeetingId());
        List<String> acceptedInvitees = getUserNameFromMeetingResponses(Decision.ACCEPT, meetingInboxResponseList);
        List<String> declinedInvitees = getUserNameFromMeetingResponses(Decision.DECLINE, meetingInboxResponseList);
        List<PolledParticipants> polledInvitees = getPolledParticipantsFromMeetingResponses(meetingInboxResponseList);
        List<String> goWithMajorityInvitees = getUserNameFromMeetingResponses(Decision.GO_WITH_MAJORITY, meetingInboxResponseList);

        MeetingStats meetingStats = new MeetingStats();
        meetingStats.setMeetingId(meeting.getMeetingId());
        int seatsAvailable = meeting.getSeats() == -1 ? 1000 : meeting.getSeats();
        meetingStats.setSeatsAvailable(seatsAvailable);
        meetingStats.setTotalResponses(meetingInboxResponseList.size());

        if(acceptedInvitees.size() > declinedInvitees.size())
            meetingStats.setSeatsOccupied(acceptedInvitees.size() + goWithMajorityInvitees.size());
        else
            meetingStats.setSeatsOccupied(acceptedInvitees.size());

        meetingStats.setTotalSeats(seatsAvailable + meetingStats.getSeatsOccupied());
        InviteesResponse inviteesResponse = new InviteesResponse();
        inviteesResponse.setAccepted(acceptedInvitees);
        inviteesResponse.setDeclined(declinedInvitees);
        inviteesResponse.setPolled(polledInvitees);
        inviteesResponse.setGoneWithMajority(goWithMajorityInvitees);
        meetingStats.setInviteesResponse(inviteesResponse);
        return meetingStats;
    }

    private List<String> getUserNameFromMeetingResponses(Decision decision, List<MeetingInboxResponse> meetingInboxResponseList){
        List<String> filteredInvitees = meetingInboxResponseList.stream()
                .filter(response -> response.getDecision() == decision)
                .map(MeetingInboxResponse::getUserName)
                .collect(Collectors.toList());
        return filteredInvitees;
    }

    private List<PolledParticipants> getPolledParticipantsFromMeetingResponses(List<MeetingInboxResponse> meetingInboxResponseList) {
        List<PolledParticipants> polledParticipantsList = meetingInboxResponseList.stream()
                .filter(response -> response.getDecision() == Decision.POLL)
                .map(response -> {
                    PolledParticipants participant = new PolledParticipants();
                    participant.setUserName(response.getUserName());
                    participant.setAlternativeStartDateTime(response.getAlternativeStartDateTime());
                    participant.setAlternativeEndDateTime(response.getAlternativeEndDateTime());
                    return participant;
                })
                .collect(Collectors.toList());
        return polledParticipantsList;
    }
}
