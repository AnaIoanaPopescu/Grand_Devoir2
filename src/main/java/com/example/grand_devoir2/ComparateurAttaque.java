package com.example.grand_devoir2;

import java.util.Comparator;

public class ComparateurAttaque implements Comparator<Objet>, ComparableParCharacteristique {
    @Override
    public int compare(Objet o1, Objet o2)
    {
        int attaque1 = o1.getBonusAttaque();
        int attaque2 = o2.getBonusAttaque();

        // Comparaison entre attaque1 et attaque2
        if (attaque1 < attaque2) {
            return -1; // o1 a une attaque inférieure à o2
        } else if (attaque1 > attaque2) {
            return 1;  // o1 a une attaque supérieure à o2
        } else {
            return 0;  // o1 et o2 ont la même attaque
        }
    }
    @Override
    public int comparerAttaque(Object o)
    {
        // Compară caracteristica de atac a obiectului
        Objet objet = (Objet) o;
        return objet.getBonusAttaque();
    }

    @Override
    public int comparerDefense(Object o) {
        // Compară caracteristica de apărare (dacă este necesar)
        Objet objet = (Objet) o;
        return objet.getBonusDefense();
    }

    @Override
    public int comparerSante(Object o) {
        // Compară caracteristica de sănătate (dacă este necesar)
        Objet objet = (Objet) o;
        return objet.getBonusSante();
    }
}