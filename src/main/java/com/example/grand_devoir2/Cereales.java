package com.example.grand_devoir2;

import java.io.Serializable;

public class Cereales extends Ressource implements Collectable, Serializable
{
    private static final long serialVersionUID = 1L;

    public Cereales(int quantite, Qualite qualite){

        super(quantite, qualite);
    }
//copy tot si intreb clasa asta au nevoie de serializable si sa le faca serializable
    @Override
    public int gather()
    {
        switch (qualite) {
            case COMMUNE:
                return quantite +1;
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
        return "La quantite de cereales est:" + quantite + ", la qualite " + qualite;
    }
    @Override
    public void collect(int quantity) {
        this.quantite+=quantity;
        System.out.println("Vous avez colecteez: " + quantity + "unite de cereales.");
    }
    @Override
    public String description() {
        return "Céréales de calitate " + qualite + " cu o cantita de " + quantite;
    }
}
