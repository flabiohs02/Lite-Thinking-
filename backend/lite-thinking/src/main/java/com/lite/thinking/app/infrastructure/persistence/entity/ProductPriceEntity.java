package com.lite.thinking.app.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPriceEntity {

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
}
