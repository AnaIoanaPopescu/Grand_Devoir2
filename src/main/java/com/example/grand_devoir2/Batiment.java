package com.example.grand_devoir2;

import java.io.Serializable;

public class Batiment implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Effet{
        FONTAINE_VIE, MONUMENT_EPEE, CABANE_BOIS, TOUR_MAGIE, COFETERIE;
    }
    private String nom;
    private Effet effet;
    // Transient keyword excludes this field from serialization
    private transient HelloController controller;

    public Batiment(String nom, Effet effet,HelloController controller) {
        this.nom = nom;
        this.effet = effet;
        this.controller = controller;
    }
    // Getters and setters
    public String getNom() {
        return nom;
    }

    public Effet getEffet() {
        return effet;
    }

    public void setController(HelloController controller) {
        this.controller = controller;
    }
    public void activerEffet(Joueur joueur, Carte carte)
    {
        switch(effet)
        {
            case FONTAINE_VIE:
                joueur.sante=100; //sante maximale
                joueur.ramasser(new Objet("Vitamines", 30, 0, 0)); //adaugam un obiect in inventarul jucatorului
                System.out.println("Tu te sent comme a vingt ans! Un objet 'Vitamines' a été ajouté à votre inventaire.");
                break;
            case MONUMENT_EPEE:
                //joueur.attaque *= 1.1; //bonus attaque de 10%
                joueur.ramasser(new Objet("Épée Magique", 0, 15, 0));
                System.out.println("Votre attaque a augmenté! Un objet 'Épée Magique' a été ajouté à votre inventaire.");
                break;
            case CABANE_BOIS:
                controller.addResourceToInventory("Bois", 2); // Adaugă lemn în inventar
                joueur.ramasser(new Objet("Hache en Bois", 0, 6, 0));
                System.out.println("Vous avez trouvé des ressources de bois! Un objet 'Hache en Bois' a été ajouté à votre inventaire.");
                break;
            case TOUR_MAGIE:
                joueur.defense *= 1.2; // Increase defense by 20%
                joueur.ramasser(new Objet("Bouclier Magique", 0, 0, 15));
                System.out.println("Votre défense a augmenté grâce à la magie! Un objet 'Bouclier Magique' a été ajouté à votre inventaire.");
                break;
            case COFETERIE:
                controller.addResourceToInventory("Céréales", 5); // Adaugă cereale în inventar
                joueur.ramasser(new Objet("Bonbons Énergétiques", 10, 0, 0));
                System.out.println("Votre quantité de cereales a augmenté de 5 unités! Un objet 'Bonbons Énergétiques' a été ajouté à votre inventaire.");
                break;
            default:
                System.out.println("Effet non autorisé ou inconnu.");
                break;
        }
    }
    // Optional: Override toString for debugging purposes
    @Override
    public String toString() {
        return "Batiment{" +
                "nom='" + nom + '\'' +
                ", effet=" + effet +
                '}';
    }
}
