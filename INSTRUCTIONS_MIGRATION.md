# Instructions pour corriger l'erreur de création de campagne

## Problème
L'erreur "Data truncated for column 'state'" se produit car la colonne `state` dans la table `campaigns` est un ENUM MySQL qui n'inclut pas la valeur `PENDING`.

## Solution : Exécuter la migration SQL

### Option 1 : Via MySQL Workbench (Recommandé)
1. Ouvrez MySQL Workbench
2. Connectez-vous à votre base de données (localhost:3306, utilisateur: ahmed, mot de passe: ahmed)
3. Sélectionnez la base de données `m3fund`
4. Exécutez cette commande SQL :

```sql
ALTER TABLE campaigns 
MODIFY COLUMN state ENUM('PENDING', 'IN_PROGRESS', 'FINISHED') NOT NULL;
```

### Option 2 : Via la ligne de commande MySQL
Si MySQL est installé et dans votre PATH :

```bash
mysql -u ahmed -pahmed m3fund -e "ALTER TABLE campaigns MODIFY COLUMN state ENUM('PENDING', 'IN_PROGRESS', 'FINISHED') NOT NULL;"
```

### Option 3 : Via le client MySQL en ligne de commande
1. Ouvrez une invite de commande
2. Naviguez vers le dossier d'installation de MySQL (ex: `C:\Program Files\MySQL\MySQL Server 8.0\bin`)
3. Exécutez :
```bash
mysql.exe -u ahmed -pahmed m3fund
```
4. Puis dans le prompt MySQL, exécutez :
```sql
ALTER TABLE campaigns MODIFY COLUMN state ENUM('PENDING', 'IN_PROGRESS', 'FINISHED') NOT NULL;
```

### Option 4 : Utiliser le script SQL fourni
Le fichier `fix_campaign_state.sql` contient la commande. Vous pouvez l'exécuter via :
- MySQL Workbench : File > Run SQL Script
- Ou copier-coller le contenu dans votre client MySQL

## Alternative : Utiliser VARCHAR au lieu d'ENUM
Si vous préférez plus de flexibilité, vous pouvez convertir la colonne en VARCHAR :

```sql
ALTER TABLE campaigns MODIFY COLUMN state VARCHAR(20) NOT NULL;
```

## Vérification
Après avoir exécuté la migration, vérifiez que la colonne a été mise à jour :

```sql
DESCRIBE campaigns;
```

La colonne `state` devrait maintenant accepter les valeurs : PENDING, IN_PROGRESS, FINISHED

## Après la migration
Une fois la migration exécutée, redémarrez votre application Spring Boot et essayez de créer une nouvelle campagne. L'erreur devrait être résolue.

