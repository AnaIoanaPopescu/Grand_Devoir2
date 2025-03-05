package com.example.grand_devoir2;

import java.util.Comparator;

public class ComparateurSante implements Comparator<Objet>, ComparableParCharacteristique {

    // Méthode de comparaison pour trier les objets selon leur bonus de santé
    @Override
    public int compare(Objet o1, Objet o2)
    {
        int sante1 = o1.getBonusSante();
        int sante2 = o2.getBonusSante();

        // Comparaison entre sante1 et sante2
        if (sante1 < sante2) {
            return -1; // o1 a une santé inférieure à o2
        } else if (sante1 > sante2) {
            return 1;  // o1 a une santé supérieure à o2
        } else {
            return 0;  // o1 et o2 ont la même santé
        }
    }
    @Override
    public int comparerAttaque(Object o) {
        return 0;
    }

    @Override
    public int comparerDefense(Object o) {
        return 0;
    }

    @Override
    public int comparerSante(Object o) {
        return 0;
    }
}
