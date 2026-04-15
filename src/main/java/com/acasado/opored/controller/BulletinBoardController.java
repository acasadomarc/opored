package com.acasado.opored.controller;

import com.acasado.opored.dto.BulletinBoardDTO;
import com.acasado.opored.dto.BulletinBoardSummaryDTO;
import com.acasado.opored.service.BulletinBoardService;
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
        value = "/api/bulletinBoards",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bulletin Boards", description = "Bulletin Board management endpoints")
public class BulletinBoardController {

    private final BulletinBoardService bulletinBoardService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all bulletin boards")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<BulletinBoardDTO>> getAllBulletinBoards() {
        log.info("getAllBulletinBoards");
        List<BulletinBoardDTO> bulletinBoards = bulletinBoardService.getAllBulletinBoards();
        return ResponseEntity.ok(bulletinBoards);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all bulletin boards summarized")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping("/summarized")
    public ResponseEntity<List<BulletinBoardSummaryDTO>> getAllBulletinBoardsSummarized() {
        log.info("getAllBulletinBoardsSummarized");
        List<BulletinBoardSummaryDTO> bulletinBoards = bulletinBoardService.getAllBulletinBoardsSummarized();
        return ResponseEntity.ok(bulletinBoards);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get bulletin board by ID")
    @ApiResponse(responseCode = "200", description = "Bulletin board found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<BulletinBoardDTO> getBulletinBoardById(
            @Parameter(description = "Bulletin Board ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getBulletinBoardById with id {}", id);
        BulletinBoardDTO bulletinBoardDTO = bulletinBoardService.getBulletinBoardById(id);
        return ResponseEntity.ok(bulletinBoardDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Update bulletin board")
    @ApiResponse(responseCode = "200", description = "Bulletin board updated")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PutMapping("/{id}")
    public ResponseEntity<BulletinBoardDTO> updateBulletinBoard(@RequestBody @NotNull @Valid BulletinBoardDTO bulletinBoardDTO)
    {
        log.info("updateBulletinBoard");
        BulletinBoardDTO bulletinBoardDTOUpdated = bulletinBoardService.updateBulletinBoard(
                bulletinBoardDTO.getId(),
                bulletinBoardDTO.getName(),
                bulletinBoardDTO.getDescription()
        );
        return ResponseEntity.ok(bulletinBoardDTOUpdated);
    }
}