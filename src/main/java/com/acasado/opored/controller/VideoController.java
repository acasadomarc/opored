package com.acasado.opored.controller;

import com.acasado.opored.dto.VideoDTO;
import com.acasado.opored.service.VideoService;
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
        value = "/api/videos",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Videos", description = "Video management endpoints")
public class VideoController {

    private final VideoService videoService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all videos")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<VideoDTO>> getAllVideos() {
        log.info("getAllVideos");
        List<VideoDTO> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_CREATE)")
    @Operation(summary = "Create a new video")
    @ApiResponse(responseCode = "201", description = "Video created")
    @PostMapping
    public ResponseEntity<VideoDTO> createVideo(
            @RequestBody @NotNull @Valid VideoDTO videoDTO) {
        log.info("createVideo");
        VideoDTO videoDTOCreated = videoService.createVideo(videoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(videoDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_UPDATE)")
    @Operation(summary = "Update a video")
    @ApiResponse(responseCode = "200", description = "Video updated")
    @ApiResponse(responseCode = "404", description = "Video not found")
    @PutMapping("/me")
    public ResponseEntity<VideoDTO> updateMyVideo(
            @RequestBody @NotNull @Valid VideoDTO videoDTO) {
        log.info("updateVideo");
        VideoDTO videoDTOUpdated = videoService.updateVideo(videoDTO);
        return ResponseEntity.ok(videoDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.PROFESSOR_DELETE)")
    @Operation(summary = "Delete video")
    @ApiResponse(responseCode = "204", description = "Video deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideo(
            @Parameter(description = "Video ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteCourse with id {}", id);
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}