package de.hnu.web;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import de.hnu.domain.RideOffer;
import de.hnu.repo.RideOfferRepository;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin(origins = "http://localhost:4200")
public class CityController {

    private final RideOfferRepository rideOfferRepo;

    // Predefined list of German cities commonly used for ridesharing
    private static final List<String> GERMAN_CITIES = Arrays.asList(
        "Munich", "Berlin", "Hamburg", "Frankfurt", "Cologne", "Düsseldorf",
        "Stuttgart", "Leipzig", "Dortmund", "Essen", "Bremen", "Dresden",
        "Hanover", "Nuremberg", "Duisburg", "Bochum", "Wuppertal", "Bielefeld",
        "Bonn", "Münster", "Karlsruhe", "Mannheim", "Augsburg", "Wiesbaden",
        "Gelsenkirchen", "Mönchengladbach", "Braunschweig", "Chemnitz", "Kiel",
        "Aachen", "Halle", "Magdeburg", "Freiburg", "Krefeld", "Lübeck",
        "Oberhausen", "Erfurt", "Mainz", "Rostock", "Kassel", "Hagen",
        "Hamm", "Saarbrücken", "Mülheim", "Potsdam", "Ludwigshafen",
        "Oldenburg", "Leverkusen", "Osnabrück", "Solingen", "Heidelberg",
        "Herne", "Neuss", "Darmstadt", "Paderborn", "Regensburg", "Ingolstadt",
        "Würzburg", "Wolfsburg", "Fürth", "Ulm", "Neu-Ulm", "Offenbach",
        "Heilbronn", "Pforzheim", "Göttingen", "Bottrop", "Trier", "Recklinghausen",
        "Reutlingen", "Bremerhaven", "Koblenz", "Bergisch Gladbach", "Jena",
        "Remscheid", "Erlangen", "Moers", "Siegen", "Hildesheim", "Salzgitter"
    );

    public CityController(RideOfferRepository rideOfferRepo) {
        this.rideOfferRepo = rideOfferRepo;
    }

    @GetMapping
    public List<String> getCities(@RequestParam(required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            return GERMAN_CITIES;
        }
        
        String lowerQuery = query.toLowerCase().trim();
        return GERMAN_CITIES.stream()
                .filter(city -> city.toLowerCase().contains(lowerQuery))
                .sorted((a, b) -> {
                    // Prioritize cities that start with the query
                    boolean aStarts = a.toLowerCase().startsWith(lowerQuery);
                    boolean bStarts = b.toLowerCase().startsWith(lowerQuery);
                    if (aStarts && !bStarts) return -1;
                    if (!aStarts && bStarts) return 1;
                    return a.compareTo(b);
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    @GetMapping("/popular")
    public List<String> getPopularCities() {
        // Return cities that have active ride offers
        List<RideOffer> offers = rideOfferRepo.findAll();
        
        return offers.stream()
                .flatMap(offer -> Arrays.asList(
                        offer.getDepartureCity(), 
                        offer.getDestinationCity()
                ).stream())
                .filter(city -> city != null)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
