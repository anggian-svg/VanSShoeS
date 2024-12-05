package com.login.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Foerign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "id_stok", referencedColumnName = "id")
    Stok stok;

    @ManyToOne
    @JoinColumn(name = "id_ukuran", referencedColumnName = "id")
    Ukuran ukuran;

    @ManyToOne
    @JoinColumn(name = "id_produk", referencedColumnName = "id")
    Produk produk;

}
