-- Migration: Corriger la colonne state dans la table campaigns pour accepter PENDING
-- Date: 2025-11-08
-- Description: Modifie la colonne state pour accepter la valeur PENDING

-- Option 1: Si la colonne est de type ENUM, modifier l'ENUM pour inclure PENDING
-- ALTER TABLE campaigns 
-- MODIFY COLUMN state ENUM('PENDING', 'IN_PROGRESS', 'FINISHED') NOT NULL;

-- Option 2: Si la colonne est de type VARCHAR (recommandé avec @Enumerated(EnumType.STRING))
-- Cette option est préférée car elle est plus flexible
ALTER TABLE campaigns 
MODIFY COLUMN state VARCHAR(20) NOT NULL;

-- Vérifier la structure après modification
-- DESCRIBE campaigns;

