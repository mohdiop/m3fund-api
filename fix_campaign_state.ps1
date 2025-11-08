# Script PowerShell pour exécuter la migration SQL
# Assurez-vous que MySQL est accessible et que les identifiants sont corrects

$mysqlUser = "ahmed"
$mysqlPassword = "ahmed"
$database = "m3fund"
$mysqlPath = "mysql"  # Ajustez si MySQL n'est pas dans le PATH

Write-Host "Exécution de la migration SQL pour corriger la colonne state..." -ForegroundColor Yellow

# Lire le script SQL
$sqlScript = Get-Content -Path "fix_campaign_state.sql" -Raw

# Exécuter le script SQL
$env:MYSQL_PWD = $mysqlPassword
$sqlScript | & $mysqlPath -u $mysqlUser -D $database

if ($LASTEXITCODE -eq 0) {
    Write-Host "Migration réussie ! La colonne state a été mise à jour." -ForegroundColor Green
} else {
    Write-Host "Erreur lors de l'exécution de la migration." -ForegroundColor Red
    Write-Host "Vous pouvez exécuter manuellement le script fix_campaign_state.sql dans MySQL" -ForegroundColor Yellow
}

