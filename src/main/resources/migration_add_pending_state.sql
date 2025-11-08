-- Migration: Ajouter PENDING à l'enum de la colonne state dans la table campaigns
-- Date: 2025-11-08
-- Description: Ajoute la valeur PENDING à l'enum de la colonne state pour permettre les campagnes non validées

-- Vérifier si la colonne est de type ENUM et la modifier
ALTER TABLE campaigns 
MODIFY COLUMN state ENUM('PENDING', 'IN_PROGRESS', 'FINISHED') NOT NULL;

-- Note: Si la colonne est déjà de type VARCHAR, cette commande la convertira en ENUM
-- Si vous préférez garder VARCHAR, utilisez plutôt:
-- ALTER TABLE campaigns MODIFY COLUMN state VARCHAR(20) NOT NULL;

