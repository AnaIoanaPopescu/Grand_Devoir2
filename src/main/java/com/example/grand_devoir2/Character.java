package com.example.grand_devoir2;
import java.io.Serial;
import java.io.Serializable;
public abstract class Character implements Serializable {
    protected String nom;
    protected int attaque;
    protected boolean statut = true;  //est vivant ou mort
    protected int defense;
    protected int sante;
    private static final long serialVersionUID = 1L;

    public Character(String nom, int attaque, int defense, int sante) {
        this.nom = nom;
        this.attaque = attaque;
        this.defense = defense;
        this.sante = sante;
    }

    public int getSante() {return sante;}

    public String getNom() {
        return nom;
    }
    public int getAttaque() {
        return attaque;
    }
    public int getDefense() {
        return defense;
    }

    public abstract void damage(Character adversaire);
    public abstract void takedamage(int damage);
    public abstract void die();
    // Optional: Validation during deserialization
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Validate fields to prevent corrupted state
        if (nom == null || attaque < 0 || defense < 0 || sante < 0) {
            throw new java.io.InvalidObjectException("Invalid data for Character.");
        }
    }

    @Override
    public String toString() {
        return "Character{" +
                "nom='" + nom + '\'' +
                ", attaque=" + attaque +
                ", statut=" + (statut ? "Alive" : "Dead") +
                ", defense=" + defense +
                ", sante=" + sante +
                '}';
    }

}
