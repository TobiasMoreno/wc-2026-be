package wc.prode._6.service;

import wc.prode._6.dto.request.JoinGroupRequest;
import wc.prode._6.dto.response.ProdeGroupResponse;
import wc.prode._6.dto.response.RankingEntryResponse;

import java.util.List;

public interface ProdeGroupService {
    ProdeGroupResponse joinGroup(String userEmail, JoinGroupRequest request);
    List<RankingEntryResponse> getGroupRanking(String userEmail);
}

