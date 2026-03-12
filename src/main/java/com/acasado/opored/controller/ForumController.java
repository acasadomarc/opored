package com.acasado.opored.controller;

import com.acasado.opored.dto.ForumDTO;
import com.acasado.opored.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/forums",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Forums", description = "Forum management endpoints")
public class ForumController {

    private final ForumService forumService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all forums")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<ForumDTO>> getAllForums() {
        log.info("getAllForums");
        List<ForumDTO> forums = forumService.getAllForums();
        return ResponseEntity.ok(forums);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get forum by ID")
    @ApiResponse(responseCode = "200", description = "Forum found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<ForumDTO> getForumById(
            @Parameter(description = "Forum ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getForumById with id {}", id);
        ForumDTO forumDTO = forumService.getForumById(id);
        return ResponseEntity.ok(forumDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Update forum")
    @ApiResponse(responseCode = "200", description = "Forum updated")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PutMapping("/{id}")
    public ResponseEntity<ForumDTO> updateForum(@RequestBody @NotNull @Valid ForumDTO forumDTO)
    {
        log.info("updateForum");
        ForumDTO forumDTOUpdated = forumService.updateForum(
                forumDTO.getId(),
                forumDTO.getName(),
                forumDTO.getDescription()
        );
        return ResponseEntity.ok(forumDTOUpdated);
    }
}