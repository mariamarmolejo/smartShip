package com.smartship.domain.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dimensiones {

    private Double largo;
    private Double ancho;
    private Double alto;
}
