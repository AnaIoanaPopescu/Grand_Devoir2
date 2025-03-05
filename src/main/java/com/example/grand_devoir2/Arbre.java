package com.example.grand_devoir2;

import java.io.Serializable;

public class Arbre extends Ressource implements Collectable, Serializable
{
    private static final long serialVersionUID = 1L;
    public Arbre(int quantite, Qualite qualite){

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
        return "Arbre a la quantite de bois:" + quantite + ", la qualite: " + qualite;
    }

    @Override
    public void collect(int quantity) {
        this.quantite+=quantity;
        System.out.println("Vous avez colectes: " + quantity + "unites de bois.");
    }

    @Override
    public String description() {
        return "Bois de qualite: " + qualite + " avec une quantite de: " + quantite;
    }
    // Custom serialization logic (optional)
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject(); // Serialize the non-static fields
    }
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject(); // Deserialize the non-static fields
    }
}
