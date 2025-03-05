package com.example.grand_devoir2;

import java.io.Serializable;

public class Roche extends Ressource implements Collectable, Serializable {
    private static final long serialVersionUID = 1L;
    public Roche(int quantite, Qualite qualite){
        super(quantite, qualite);
    }

    @Override
    public int gather()
    {
        switch (qualite) {
            case COMMUNE:
                return quantite + 1;
            case RARE:
                return quantite + 2; // Bonus for rare resources
            case EPIQUE:
                return quantite + 5; // Bonus for epic resources
            default:
                return quantite;
        }
    }

    @Override
    public String toString() {
        return "La quantite de pierre: " + quantite+ ", qualite: " +qualite;
    }

    @Override
    public void collect(int quantity) {
        this.quantite += quantity;
        System.out.println("Vous avez collectes: " + quantity + " unites de pierre.");
    }

    @Override
    public String description() {
        return "La quantite de pierre: " + quantite+ ", qualite: " +qualite;
    }
}
