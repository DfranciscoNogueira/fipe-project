package br.com.load.common;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "brand", uniqueConstraints = {
        @UniqueConstraint(name = "uk_brand_type_code", columnNames = {"vehicle_type", "code"})
})
public class Brand extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "vehicle_type", nullable = false, length = 20)
    public String vehicleType; // cars | motorcycles | trucks

    @Column(nullable = false, length = 32)
    public String code;

    @Column(nullable = false, length = 120)
    public String name;

}
