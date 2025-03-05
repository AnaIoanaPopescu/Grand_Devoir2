package com.example.grand_devoir2;

import java.io.Serializable;

public abstract class Ressource implements Collectable, Serializable {
    protected int quantite;
    protected static Qualite qualite;
    private static final long serialVersionUID = 1L;

    public Ressource(int quantite, Qualite qualite) {
        this.quantite = quantite;
        Ressource.qualite = qualite; // Static field shared across instances
    }
    public abstract int gather();
    public abstract String toString();
    @Override
    public String description() {
        return "Ressource de type " + this.getClass().getSimpleName()
                + " avec quantité: " + quantite
                + " et qualité: " + qualite;
    }
    // Custom serialization to handle static field
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject(); // Serialize non-static fields
        out.writeObject(qualite); // Manually serialize the static field
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject(); // Deserialize non-static fields
        Ressource.qualite = (Qualite) in.readObject(); // Manually deserialize the static field
    }
    // Getters
    public int getQuantite() {
        return quantite;
    }

    public Qualite getQualite() {
        return qualite;
    }
}
