package com.acasado.opored.controller;

import com.acasado.opored.dto.PurchaseDTO;
import com.acasado.opored.service.PurchaseService;
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
        value = "/api/purchases",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Purchases", description = "Purchase management endpoints")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PreAuthorize("hasAuthority(@authorities.ROOT)")
    @Operation(summary = "Get all purchases")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<PurchaseDTO>> getAllPurchases() {
        log.info("getAllPurchases");
        List<PurchaseDTO> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_READ)")
    @Operation(summary = "Get purchase by ID")
    @ApiResponse(responseCode = "200", description = "Purchase found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(
            @Parameter(description = "Purchase ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getPurchaseById");
        PurchaseDTO purchaseDTO = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(purchaseDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.STUDENT_CREATE)")
    @Operation(summary = "Create a new purchase")
    @ApiResponse(responseCode = "201", description = "Purchase created")
    @PostMapping
    public ResponseEntity<PurchaseDTO> createPurchase(
            @RequestBody @NotNull @Valid PurchaseDTO purchaseDTO) {
        log.info("createPurchase");
        PurchaseDTO purchaseDTOCreated = purchaseService.createPurchase(purchaseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseDTOCreated);
    }
}