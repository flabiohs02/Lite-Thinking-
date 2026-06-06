package com.lite.thinking.app.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity extends BaseEntity {

    @Id
    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "characteristics", length = 1000)
    private String characteristics;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "product_prices",
            joinColumns = @JoinColumn(name = "product_code")
    )
    @Builder.Default
    private List<ProductPriceEntity> prices = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_nit", nullable = false)
    private CompanyEntity company;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_code"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private List<CategoryEntity> categories = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @Builder.Default
    private List<InventoryEntity> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItemEntity> orderItems = new ArrayList<>();
}
