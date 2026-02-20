package id.ac.ui.cs.advprog.yomubackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

//test
@Entity
@Data
public class Kalkulasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int angkaPertama;
    private int angkaKedua;
    private int hasil;
}