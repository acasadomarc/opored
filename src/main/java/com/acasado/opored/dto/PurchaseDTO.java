package com.acasado.opored.dto;

import com.acasado.opored.model.PurchaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Purchase transaction details")
public class PurchaseDTO {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "2026-11-05")
    private LocalDate purchaseDate;

    @Schema(example = "49.99")
    @NotNull
    private Float price;

    @Schema(description = "Method used for payment", example = "Credit Card")
    @NotBlank
    private String paymentMethod;

    @Schema(description = "ID of the purchased course", example = "10")
    private Integer courseId;

    @Schema(description = "ID of the student who made the purchase", example = "42")
    private Integer studentId;

    public PurchaseDTO(PurchaseEntity purchase) {
        setId(purchase.getId());
        setPurchaseDate(purchase.getPurchaseDate());
        setPrice(purchase.getPrice());
        setPaymentMethod(purchase.getPaymentMethod());
        setCourseId(purchase.getCourse().getId());
        setStudentId(purchase.getStudent().getId());
    }
}