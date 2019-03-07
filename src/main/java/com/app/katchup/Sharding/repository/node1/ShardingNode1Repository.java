package com.app.katchup.Sharding.repository.node1;

import com.app.katchup.Sharding.model.Sharding;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShardingNode1Repository extends CrudRepository<Sharding, String> {
    @Query(value = "{'meetingId' : ?0", fields = "{'meetingId':1, 'databaseId:1'}")
    List<Sharding> findAllByMeetingIds(List<String> meetingIdList);

    @Query(value = "{'meetingId' : ?0", fields = "{'meetingId':1, 'databaseId:1'}")
    Sharding findByMeetingId(String meetingId);
}
