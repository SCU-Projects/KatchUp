package katchup.MeetingResponse.repository.node0;

import katchup.Meeting.model.Meeting;
import katchup.MeetingResponse.model.Decision;
import katchup.MeetingResponse.model.MeetingID;
import katchup.MeetingResponse.model.MeetingInboxResponse;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingResponseNode0Repository extends CrudRepository<MeetingInboxResponse, String> {
    @Query(value = "{'userName' : ?0}", fields = "{'meetingId':1}")
    List<MeetingID> findAllMeetingIdsbyUserName(String userName);

    @Query(value = "{'userName' : ?0, 'meetingId' : ?1 }")
    MeetingInboxResponse findByUserNameAndMeetingID(String userName, String meetingId);

    @Query(value = "{'meetingId' : ?0}")
    List<MeetingInboxResponse> findAllbyMeetingID(String meetingId);

    @Query(value = "{'userName' : ?0, 'decision': ?1}", fields = "{'meetingId':1}")
    List<MeetingID> findAllMeetingIdsbyUserNameAndDecision(String userName, Decision decision);

}
