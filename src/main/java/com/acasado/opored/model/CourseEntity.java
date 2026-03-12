package com.acasado.opored.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "courses")
@SQLRestriction("is_deleted = false")
public class CourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Lob
    @Column(name = "description", columnDefinition = "text")
    private String description;

    @NotNull
    @Column(name = "price", nullable = false)
    private Float price;

    @NotNull
    @Column(name = "discount_percentage")
    private Float discountPercentage;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private ProfessorEntity professor;

    @OneToMany(mappedBy = "course")
    private Set<ContentEntity> contents = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "rating_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<RatingCourseEntity> ratings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<PurchaseEntity> purchases = new LinkedHashSet<>();

    public CourseEntity(String name, String description, Float price, Set<ContentEntity> contents, Set<RatingCourseEntity> ratings) {
        setName(name);
        setDescription(description);
        setPrice(price);
        setDiscountPercentage(0.0F); // To avoid null errors
        setContents(contents);
        setRatings(ratings);
    }
}
