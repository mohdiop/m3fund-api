package com.mohdiop.m3fundapi.entity.enums;

import java.util.Arrays;
import java.util.Set;

public enum ProjectDomain {
    AGRICULTURE,
    BREEDING,
    EDUCATION,
    HEALTH,
    MINE,
    CULTURE,
    ENVIRONMENT,
    COMPUTER_SCIENCE,
    SOLIDARITY,
    SHOPPING,
    SOCIAL;

    // Classe interne pour représenter une paire de domaines
    private static class DomainPair {
        final ProjectDomain domain1;
        final ProjectDomain domain2;

        public DomainPair(ProjectDomain d1, ProjectDomain d2) {
            // Normalise l'ordre pour que (A, B) soit égal à (B, A)
            if (d1.ordinal() < d2.ordinal()) {
                this.domain1 = d1;
                this.domain2 = d2;
            } else {
                this.domain1 = d2;
                this.domain2 = d1;
            }
        }

        // Implémentations standard pour HashSet (nécessaires pour contains())
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DomainPair that = (DomainPair) o;
            return domain1 == that.domain1 && domain2 == that.domain2;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{domain1, domain2});
        }
    }

    // Le Set statique et immuable qui définit la proximité (la clé de la fonction)
    private static final Set<DomainPair> CLOSE_DOMAINS;

    static {

        // --- Définitions de la proximité ---

        // Agriculture/Élevage et Environnement

        // Rendre le set immuable pour la sécurité des threads et la performance
        CLOSE_DOMAINS = Set.of(
                new DomainPair(AGRICULTURE, BREEDING),

                new DomainPair(AGRICULTURE, ENVIRONMENT),

                new DomainPair(BREEDING, ENVIRONMENT),

                // Santé et Solidarité
                new DomainPair(HEALTH, SOLIDARITY),

                // Informatique et Éducation/Social
                new DomainPair(COMPUTER_SCIENCE, EDUCATION), new DomainPair(COMPUTER_SCIENCE, SOCIAL),

                // Culture et Éducation/Social
                new DomainPair(CULTURE, EDUCATION), new DomainPair(CULTURE, SOCIAL),

                // Commerce/Achats et Social/Solidarité
                new DomainPair(SHOPPING, SOCIAL), new DomainPair(SHOPPING, SOLIDARITY),

                // MINE (généralement isolé ou proche de l'environnement)
                new DomainPair(MINE, ENVIRONMENT),

                // Social et Solidarité (très proches)
                new DomainPair(SOCIAL, SOLIDARITY));
    }

    /**
     * Vérifie si deux domaines de projet sont considérés comme "proches" (connexes).
     *
     * @param d1 Le premier domaine.
     * @param d2 Le second domaine.
     * @return true si les domaines sont définis comme proches ou sont identiques, false sinon.
     */
    public static boolean areDomainsClose(ProjectDomain d1, ProjectDomain d2) {
        // 1. Si les domaines sont identiques, ils sont considérés comme proches.
        if (d1 == d2) {
            return true;
        }

        // 2. Créer une paire normalisée et vérifier son existence dans le Set des connexions.
        DomainPair pair = new DomainPair(d1, d2);
        return CLOSE_DOMAINS.contains(pair);
    }
}
