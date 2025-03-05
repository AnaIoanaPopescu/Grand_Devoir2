package com.example.grand_devoir2;

import java.io.Serializable;
import java.io.IOException;

public class Objet implements Serializable {
    private String nom;
    private int BonusSante;
    private int BonusAttaque;
    private int BonusDefense;
    private static final long serialVersionUID = 1L;
    // Default constructor (optional)
    public Objet() {
        this.nom = "";
        this.BonusSante = 0;
        this.BonusAttaque = 0;
        this.BonusDefense = 0;
    }

    public Objet(String nom, int bonusSante, int bonusAttaque, int bonusDefense) {
        this.nom = nom;
        this.BonusSante = bonusSante;
        this.BonusAttaque = bonusAttaque;
        this.BonusDefense = bonusDefense;
    }

    public String getNom() {
        return nom;
    }
    public int getBonusSante() {
        return BonusSante;
    }
    public int getBonusAttaque() {
        return BonusAttaque;
    }
    public int getBonusDefense() {
        return BonusDefense;
    }

    // toString for debugging and logging
    @Override
    public String toString() {
        return "Objet{" +
                "nom='" + nom + '\'' +
                ", BonusSante=" + BonusSante +
                ", BonusAttaque=" + BonusAttaque +
                ", BonusDefense=" + BonusDefense +
                '}';
    }

    // Validation during deserialization
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Validate fields
        if (nom == null || BonusSante < 0 || BonusAttaque < 0 || BonusDefense < 0) {
            throw new java.io.InvalidObjectException("Invalid data for Objet.");
        }
    }
}
