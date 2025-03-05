package com.example.grand_devoir2;

import java.io.Serializable;

public interface Collectable extends Serializable {
    // Metodă pentru a colecta resursa/obiectul în inventar
    void collect(int quantity);

    // Metodă pentru a returna descrierea resursei/obiectului
    String description();
}
