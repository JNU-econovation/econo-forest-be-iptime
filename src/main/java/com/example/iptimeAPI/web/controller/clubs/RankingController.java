package com.example.iptimeAPI.web.controller.clubs;

import com.example.iptimeAPI.domain.clubRoom.ClubRoomLogService;
import com.example.iptimeAPI.domain.macAddress.MacAddressService;
import com.example.iptimeAPI.service.clubRoom.LogPeriod;
import com.example.iptimeAPI.web.dto.MemberRankingDTO;
import com.example.iptimeAPI.web.dto.MemberRankingInfoDTO;
import com.example.iptimeAPI.web.fegin.FeignUserInfo;
import com.example.iptimeAPI.web.fegin.UserInfo;
import com.example.iptimeAPI.web.response.ApiResponse;
import com.example.iptimeAPI.web.response.ApiResponseGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Api(tags = {"Ranking API"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class RankingController {
    private final MacAddressService macAddressService;
    private final ClubRoomLogService clubRoomLogService;
    private final FeignUserInfo feignUserInfo;

    @GetMapping
    public ApiResponse<ApiResponse.withData> rankings(@ApiParam(example = "month") @RequestParam String period) {
        List<Long> memberIds = macAddressService.browseMacAddressesMembers();
        Map<Long, List<Long>> rankings = clubRoomLogService.calcRankings(memberIds, LogPeriod.valueOf(period.toUpperCase()));
        List<List<Long>> rankingMemberIds = new ArrayList<>(shuffleRankings(rankings));

        List<MemberRankingDTO> memberRankingDTOS = new ArrayList<>();
        for (int i = 0, j = 1; i < rankingMemberIds.size(); i++, j++) {
            for (Long memberId : rankingMemberIds.get(i)) {
                memberRankingDTOS.add(new MemberRankingDTO(j, feignUserInfo.getUserInfo(memberId)));
            }
        }

        return ApiResponseGenerator.success(memberRankingDTOS, HttpStatus.OK, HttpStatus.OK.value() + "500", "ranking result period : " + period);
    }

    private List<List<Long>> shuffleRankings(Map<Long, List<Long>> rankings) {
        return rankings.values()
                .stream()
                .peek(Collections::shuffle)
                .collect(Collectors.toList());
    }


    @GetMapping("/member")
    public ApiResponse<ApiResponse.withData> memberRankingCountInfo(@RequestHeader(value = "Authorization") String accessToken, @RequestParam String period) {
        LogPeriod periodType = LogPeriod.valueOf(period.toUpperCase());
        List<Long> memberIds = macAddressService.browseMacAddressesMembers();
        Map<Long, List<Long>> rankings = clubRoomLogService.calcRankings(memberIds, periodType);

        UserInfo user = feignUserInfo.getUserInfoByToken(accessToken);
        Long memberRanking = clubRoomLogService.calcRanking(rankings, user.getId());
        Long visitCount = clubRoomLogService.calcVisitCount(user.getId(), periodType);

        MemberRankingInfoDTO memberRankingInfoDTO = new MemberRankingInfoDTO(user.getYear(), user.getName(), user.getId(), memberRanking, visitCount);
        return ApiResponseGenerator.success(memberRankingInfoDTO, HttpStatus.OK, HttpStatus.OK.value() + "500", "ranking result period : " + period);
    }
}