package com.mohdiop.m3fundapi.utility;

import org.gavaghan.geodesy.*;

public class GeoDistanceCalculator {

    // Utilisation de l'ellipsoïde WGS-84, le standard pour le GPS et la géodésie moderne.
    private static final Ellipsoid REFERENCE_ELLIPSOID = Ellipsoid.WGS84;
    private static final GeodeticCalculator CALCULATOR = new GeodeticCalculator();

    /**
     * Calcule la distance en kilomètres entre deux points géographiques
     * en utilisant la formule de Vincenty (implémentation de la bibliothèque).
     *
     * @param lat1 Latitude du premier point.
     * @param lon1 Longitude du premier point.
     * @param elevation1 Altitude du premier point par rapport au NMM (Niveau Moyen Marin = 0)
     * @param lat2 Latitude du deuxième point.
     * @param lon2 Longitude du deuxième point.
     * @param elevation2 Altitude du deuxième point par rapport au NMM (Niveau Moyen Marin = 0)
     * @return La distance en kilomètres si les coordonnées fournies ne sont pas erronées si non -1.
     */
    public static double calculateDistanceVincenty(double lat1, double lon1, double elevation1, double lat2, double lon2, double elevation2) {

        // 0. Coordonnées erronées retoure de -1
        if((lat1 == 0 && lon1 == 0) || (lat2 == 0 && lon2 == 0)) {
            return -1D;
        }

        // 1. Définir les coordonnées globales
        GlobalPosition start = new GlobalPosition(lat1, lon1, elevation1);
        GlobalPosition end = new GlobalPosition(lat2, lon2, elevation2);

        // 2. Calculer la mesure géodésique
        // GeodeticMeasurement contient la distance, l'azimut initial et final.
        GeodeticMeasurement measurement = CALCULATOR.calculateGeodeticMeasurement(
                REFERENCE_ELLIPSOID,
                start,
                end
        );

        // 3. Récupérer la distance en mètres et la convertir en kilomètres
        // La distance est retournée par la méthode en mètres.
        double distanceInMeters = measurement.getEllipsoidalDistance();

        return distanceInMeters / 1000.0; // Conversion en kilomètres
    }
}
