package com.app.katchup.MeetingResponse;

import com.app.katchup.MeetingResponse.model.MeetingID;
import com.app.katchup.MeetingResponse.model.MeetingInboxResponse;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingResponseRepository extends CrudRepository<MeetingInboxResponse, String>{
    @Query(value = "{'userName' : ?0}", fields = "{'meetingId':1}")
    List<MeetingID> findAllMeetingIdsbyUserName(String userName);

    @Query(value = "{'userName' : ?0, 'meetingId' : ?1 }")
    MeetingInboxResponse findByUserNameAndMeetingID(String userName, String meetingId);

    @Query(value = "{'meetingId' : ?0}")
    List<MeetingInboxResponse> findAllbyMeetingID(String meetingId);
}

