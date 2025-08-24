package br.com.load.common;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "vehicle_model", uniqueConstraints = {
        @UniqueConstraint(name = "uk_model_brand_code", columnNames = {"brand_id", "code"})
})
public class VehicleModel extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    public Brand brand;

    @Column(nullable = false, length = 32)
    public String code;

    @Column(nullable = false, length = 160)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String observations;

}
