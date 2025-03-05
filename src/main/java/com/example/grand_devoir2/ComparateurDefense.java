package com.example.grand_devoir2;

import java.util.Comparator;

public class ComparateurDefense implements Comparator<Objet>, ComparableParCharacteristique {

    // Méthode de comparaison pour trier les objets selon leur bonus de défense
    @Override
    public int compare(Objet o1, Objet o2)
    {
        int defense1 = o1.getBonusDefense();
        int defense2 = o2.getBonusDefense();

        // Comparaison entre defense1 et defense2
        if (defense1 < defense2) {
            return -1; // o1 a une défense inférieure à o2
        } else if (defense1 > defense2) {
            return 1;  // o1 a une défense supérieure à o2
        } else {
            return 0;  // o1 et o2 ont la même défense
        }
    }
    @Override
    public int comparerAttaque(Object o) {
        // Nu este relevant pentru ComparateurDefense, dar trebuie implementat
        return 0;
    }

    @Override
    public int comparerDefense(Object o) {
        // Compară caracteristica de apărare
        Objet objet = (Objet) o;
        return objet.getBonusDefense();
    }

    @Override
    public int comparerSante(Object o) {
        // Nu este relevant pentru ComparateurDefense, dar trebuie implementat
        return 0;
    }
}