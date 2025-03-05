package com.example.grand_devoir2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestionFichiers {

    // Méthode pour charger les objets depuis un fichier
    public static List<Objet> chargerObjets(String nomFichier) {
        List<Objet> objets = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier)))
        {
            String ligne;
            while ((ligne = br.readLine()) != null)
            {
                String[] details = ligne.split(",");
                if (details.length == 4)
                {
                    String nom = details[0].trim();
                    int bonusSante = Integer.parseInt(details[1].trim());
                    int bonusAttaque = Integer.parseInt(details[2].trim());
                    int bonusDefense = Integer.parseInt(details[3].trim());
                    objets.add(new Objet(nom, bonusSante, bonusAttaque, bonusDefense));
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return objets;
    }

    // Méthode pour charger les ennemis depuis un fichier
    public static List<Ennemi> chargerEnnemis(String nomFichier, List<Objet> objets) {
        List<Ennemi> ennemis = new ArrayList<>();
        boolean ennemisSection = false;

        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                // Detect "Ennemis:" section
                if (ligne.trim().equals("Ennemis:")) {
                    ennemisSection = true;
                    continue;
                }

                // Parse only if inside the "Ennemis:" section
                if (ennemisSection && !ligne.trim().isEmpty()) {
                    String[] details = ligne.split(",");
                    if (details.length == 5) {
                        String nom = details[0].trim();
                        int attaque = Integer.parseInt(details[1].trim());
                        int defense = Integer.parseInt(details[2].trim());
                        int sante = Integer.parseInt(details[3].trim());
                        String nomObjet = details[4].trim();

                        // Find the capture object by name
                        Objet capture = objets.stream()
                                .filter(obj -> obj.getNom().equalsIgnoreCase(nomObjet))
                                .findFirst()
                                .orElse(null);

                        // Create and add the enemy
                        ennemis.add(new Ennemi(nom, attaque, defense, sante, capture));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ennemis;
    }

    // Méthode pour sauvegarder l'état du jeu
    public static void sauvegarderEtat(String nomFichier, Joueur joueur, Carte carte, List<Ennemi> ennemis) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nomFichier))) {
            // Save player details
            bw.write("Joueur:");
            bw.newLine();
            bw.write("Nom: " + joueur.getNom());
            bw.newLine();
            bw.write("Santé: " + joueur.getSante());
            bw.newLine();
            bw.write("Bois: " + joueur.getBois());
            bw.newLine();
            bw.write("Pierre: " + joueur.getPierre());
            bw.newLine();
            bw.write("Nourriture: " + joueur.getNourriture());
            bw.newLine();

            // Save map matrix
            bw.write("Carte:");
            bw.newLine();
            for (int[] row : carte.getMatrix()) {
                for (int cell : row) {
                    bw.write(cell + " ");
                }
                bw.newLine();
            }

            // Save enemies
            bw.write("Ennemis:");
            bw.newLine();
            for (Ennemi ennemi : ennemis) {
                bw.write(ennemi.getNom() + "," + ennemi.getAttaque() + "," +
                        ennemi.getDefense() + "," + ennemi.getSante() + "," +
                        (ennemi.getCapture() != null ? ennemi.getCapture().getNom() : "None"));
                bw.newLine();
            }

            bw.flush();
            System.out.println("État du jeu sauvegardé dans " + nomFichier);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde de l'état du jeu : " + e.getMessage());
        }
    }
}
