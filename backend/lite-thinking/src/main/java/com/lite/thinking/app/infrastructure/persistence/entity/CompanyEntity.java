package com.lite.thinking.app.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyEntity extends BaseEntity {

    @Id
    @Column(name = "nit", length = 50)
    private String nit;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 50)
    private String phone;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductEntity> products = new ArrayList<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @Builder.Default
    private List<InventoryEntity> inventories = new ArrayList<>();
}
