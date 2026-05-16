package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanSeasonResultDto;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanSeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/clan/seasons")
@RequiredArgsConstructor
public class ClanSeasonAdminRestController {

    private final ClanSeasonService clanSeasonService;

    @PostMapping("/end")
    public ResponseEntity<ClanSeasonResultDto> processEndOfSeason() {
        return ResponseEntity.ok(clanSeasonService.processEndOfSeason());
    }
}
