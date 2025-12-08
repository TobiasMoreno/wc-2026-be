package wc.prode._6.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import wc.prode._6.dto.request.JoinGroupRequest;
import wc.prode._6.dto.response.ApiResponse;
import wc.prode._6.dto.response.ProdeGroupResponse;
import wc.prode._6.dto.response.RankingEntryResponse;
import wc.prode._6.service.ProdeGroupService;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class ProdeGroupController {

    private final ProdeGroupService prodeGroupService;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<ProdeGroupResponse>> joinGroup(
            @Valid @RequestBody JoinGroupRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        ProdeGroupResponse response = prodeGroupService.joinGroup(userEmail, request);
        ApiResponse<ProdeGroupResponse> apiResponse = ApiResponse.<ProdeGroupResponse>builder()
                .success(true)
                .message("Successfully joined group")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<RankingEntryResponse>>> getGroupRanking(
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<RankingEntryResponse> ranking = prodeGroupService.getGroupRanking(userEmail);
        ApiResponse<List<RankingEntryResponse>> apiResponse = ApiResponse.<List<RankingEntryResponse>>builder()
                .success(true)
                .message("Ranking retrieved successfully")
                .data(ranking)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}

