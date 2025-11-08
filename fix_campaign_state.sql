-- Script de migration pour ajouter PENDING à la colonne state
-- Exécutez ce script dans MySQL pour corriger l'erreur

USE m3fund;

-- Option 1: Modifier l'ENUM pour inclure PENDING (recommandé si vous voulez garder un ENUM)
ALTER TABLE campaigns 
MODIFY COLUMN state ENUM('PENDING', 'IN_PROGRESS', 'FINISHED') NOT NULL;

-- Option 2: Si vous préférez utiliser VARCHAR (plus flexible)
-- Décommentez la ligne suivante et commentez l'option 1
-- ALTER TABLE campaigns MODIFY COLUMN state VARCHAR(20) NOT NULL;

