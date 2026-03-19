package com.acasado.opored.controller;

import com.acasado.opored.dto.CategoryDTO;
import com.acasado.opored.service.CategoryService;
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
        value = "/api/categories",
        produces = "application/json")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.info("getAllCategories");
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PreAuthorize("hasAuthority(@authorities.USER_READ)")
    @Operation(summary = "Get category by ID")
    @ApiResponse(responseCode = "200", description = "Category found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("getCategoryById with id {}", id);
        CategoryDTO categoryDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDTO);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_CREATE)")
    @Operation(summary = "Create category")
    @ApiResponse(responseCode = "201", description = "Category created")
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @RequestBody @NotNull @Valid CategoryDTO categoryDTO) {
        log.info("createCategory with id: {}", categoryDTO.getId());
        CategoryDTO categoryDTOCreated = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTOCreated);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_UPDATE)")
    @Operation(summary = "Update category")
    @ApiResponse(responseCode = "200", description = "Category updated")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody @NotNull @Valid CategoryDTO categoryDTO, @PathVariable @NotNull Integer id)
    {
        log.info("updateCategory");
        CategoryDTO categoryDTOUpdated = categoryService.updateCategory(
                id,
                categoryDTO.getName(),
                categoryDTO.getDescription()
        );
        return ResponseEntity.ok(categoryDTOUpdated);
    }

    @PreAuthorize("hasAuthority(@authorities.ADMINISTRATION_DELETE)")
    @Operation(summary = "Delete category")
    @ApiResponse(responseCode = "204", description = "Category deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable @NotNull Integer id) {
        log.info("deleteCategory with id {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}