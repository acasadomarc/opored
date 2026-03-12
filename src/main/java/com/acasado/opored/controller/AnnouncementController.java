package com.acasado.opored.controller;

import com.acasado.opored.dto.AnnouncementDTO;
import com.acasado.opored.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/announcements",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Announcements", description = "Announcement management endpoints")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all announcements")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<AnnouncementDTO>> getAllAnnouncements() {
        log.info("getAllAnnouncements");
        List<AnnouncementDTO> announcements = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(announcements);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get announcement by ID")
    @ApiResponse(responseCode = "200", description = "Announcement found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> getAnnouncementById(
            @Parameter(description = "Announcement ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getAnnouncementById with id {}", id);
        AnnouncementDTO announcementDTO = announcementService.getAnnouncementById(id);
        return ResponseEntity.ok(announcementDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_CREATE)")
    @Operation(summary = "Create a new announcement")
    @ApiResponse(responseCode = "201", description = "Announcement created")
    @PostMapping
    public ResponseEntity<AnnouncementDTO> createAnnouncement(
            @RequestBody @NotNull @Valid AnnouncementDTO announcementDTO) {
        log.info("createAnnouncement with id: {}", announcementDTO.getId());
        AnnouncementDTO announcementDTOCreated = announcementService.createAnnouncement(announcementDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(announcementDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Update an announcement")
    @ApiResponse(responseCode = "200", description = "Announcement updated")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> updateAnnouncement(@RequestBody @NotNull @Valid AnnouncementDTO announcementDTO) {
        log.info("updateAnnouncement");
        AnnouncementDTO announcementDTOUpdated = announcementService.updateAnnouncement(
                announcementDTO.getId(),
                announcementDTO.getTitle(),
                announcementDTO.getContent(),
                announcementDTO.getRelatedLinks()
        );
        return ResponseEntity.ok(announcementDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_DELETE)")
    @Operation(summary = "Delete announcement")
    @ApiResponse(responseCode = "204", description = "Announcement deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAnnouncement(
            @Parameter(description = "Announcement ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteAnnouncement with id {}", id);
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}