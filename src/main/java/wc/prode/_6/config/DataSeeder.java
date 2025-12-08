package wc.prode._6.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import wc.prode._6.entity.Match;
import wc.prode._6.entity.Phase;
import wc.prode._6.entity.ProdeGroup;
import wc.prode._6.entity.Team;
import wc.prode._6.repository.MatchRepository;
import wc.prode._6.repository.ProdeGroupRepository;
import wc.prode._6.repository.TeamRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final ProdeGroupRepository prodeGroupRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (teamRepository.count() == 0) {
            log.info("Seeding teams data...");
            seedTeams();
        }

        if (matchRepository.count() == 0) {
            log.info("Seeding matches data...");
            seedMatches();
        }

        if (prodeGroupRepository.count() == 0) {
            log.info("Seeding default group...");
            seedDefaultGroup();
        }

        log.info("Data seeding completed!");
    }

    private void seedDefaultGroup() {
        // Crear grupo por defecto con contraseña "prode2026"
        ProdeGroup defaultGroup = ProdeGroup.builder()
                .name("Grupo Principal")
                .password(passwordEncoder.encode("prode2026"))
                .build();
        prodeGroupRepository.save(defaultGroup);
        log.info("Default group created: 'Grupo Principal' with password 'prode2026'");
    }

    private void seedTeams() {
        List<String> teams = Arrays.asList(
                "Argentina", "Brasil", "Uruguay", "Colombia", "Ecuador", "Perú", "Chile", "Paraguay",
                "Estados Unidos", "México", "Canadá", "Costa Rica", "Panamá", "Jamaica", "Honduras", "El Salvador",
                "España", "Francia", "Inglaterra", "Alemania", "Italia", "Países Bajos", "Bélgica", "Portugal",
                "Croacia", "Dinamarca", "Suiza", "Polonia", "Suecia", "Noruega", "Austria", "República Checa",
                "Japón", "Corea del Sur", "Australia", "Irán", "Arabia Saudita", "Qatar", "Emiratos Árabes Unidos", "China",
                "Senegal", "Marruecos", "Túnez", "Egipto", "Nigeria", "Camerún", "Ghana", "Costa de Marfil"
        );

        for (String teamName : teams) {
            Team team = Team.builder()
                    .name(teamName)
                    .flagUrl("https://flagsapi.com/" + getCountryCode(teamName) + "/flat/64.png")
                    .build();
            teamRepository.save(team);
        }
    }

    private String getCountryCode(String countryName) {
        // Mapeo simplificado de nombres a códigos ISO
        return switch (countryName) {
            case "Argentina" -> "AR";
            case "Brasil" -> "BR";
            case "Uruguay" -> "UY";
            case "Colombia" -> "CO";
            case "Ecuador" -> "EC";
            case "Perú" -> "PE";
            case "Chile" -> "CL";
            case "Paraguay" -> "PY";
            case "Estados Unidos" -> "US";
            case "México" -> "MX";
            case "Canadá" -> "CA";
            case "Costa Rica" -> "CR";
            case "Panamá" -> "PA";
            case "Jamaica" -> "JM";
            case "Honduras" -> "HN";
            case "El Salvador" -> "SV";
            case "España" -> "ES";
            case "Francia" -> "FR";
            case "Inglaterra" -> "GB";
            case "Alemania" -> "DE";
            case "Italia" -> "IT";
            case "Países Bajos" -> "NL";
            case "Bélgica" -> "BE";
            case "Portugal" -> "PT";
            case "Croacia" -> "HR";
            case "Dinamarca" -> "DK";
            case "Suiza" -> "CH";
            case "Polonia" -> "PL";
            case "Suecia" -> "SE";
            case "Noruega" -> "NO";
            case "Austria" -> "AT";
            case "República Checa" -> "CZ";
            case "Japón" -> "JP";
            case "Corea del Sur" -> "KR";
            case "Australia" -> "AU";
            case "Irán" -> "IR";
            case "Arabia Saudita" -> "SA";
            case "Qatar" -> "QA";
            case "Emiratos Árabes Unidos" -> "AE";
            case "China" -> "CN";
            case "Senegal" -> "SN";
            case "Marruecos" -> "MA";
            case "Túnez" -> "TN";
            case "Egipto" -> "EG";
            case "Nigeria" -> "NG";
            case "Camerún" -> "CM";
            case "Ghana" -> "GH";
            case "Costa de Marfil" -> "CI";
            default -> "XX";
        };
    }

    private void seedMatches() {
        List<Team> teams = teamRepository.findAll();
        
        if (teams.size() < 2) {
            log.warn("Not enough teams to create matches");
            return;
        }

        // Crear algunos partidos de ejemplo para la fase de grupos
        // Fechas aproximadas del Mundial 2026 (junio-julio 2026)
        LocalDateTime baseDate = LocalDateTime.of(2026, 6, 11, 12, 0);
        
        // Grupos A-L (12 grupos en el Mundial 2026)
        String[] groups = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
        
        // Crear algunos partidos de ejemplo, distribuidos en grupos
        int matchCount = 0;
        int teamsPerGroup = 4; // 4 equipos por grupo
        
        for (int groupIndex = 0; groupIndex < Math.min(groups.length, teams.size() / teamsPerGroup); groupIndex++) {
            String groupLetter = groups[groupIndex];
            int teamStartIndex = groupIndex * teamsPerGroup;
            
            // Crear partidos dentro del grupo (todos contra todos)
            for (int i = 0; i < teamsPerGroup && (teamStartIndex + i) < teams.size(); i++) {
                for (int j = i + 1; j < teamsPerGroup && (teamStartIndex + j) < teams.size(); j++) {
                    Team homeTeam = teams.get(teamStartIndex + i);
                    Team awayTeam = teams.get(teamStartIndex + j);
                    
                    Match match = Match.builder()
                            .date(baseDate.plusDays(matchCount / 3).plusHours((matchCount % 3) * 4))
                            .city(getRandomCity())
                            .stadium(getRandomStadium())
                            .phase(Phase.GROUP)
                            .group(groupLetter)
                            .homeTeam(homeTeam)
                            .awayTeam(awayTeam)
                            .build();
                    
                    matchRepository.save(match);
                    matchCount++;
                }
            }
        }

        // Crear algunos partidos de otras fases
        LocalDateTime knockoutDate = LocalDateTime.of(2026, 6, 28, 16, 0);
        for (int i = 0; i < 4 && i < teams.size() - 1; i++) {
            Team homeTeam = teams.get(i * 2);
            Team awayTeam = teams.get(i * 2 + 1);
            
            Match match = Match.builder()
                    .date(knockoutDate.plusDays(i))
                    .city(getRandomCity())
                    .stadium(getRandomStadium())
                    .phase(Phase.ROUND_OF_16)
                    .homeTeam(homeTeam)
                    .awayTeam(awayTeam)
                    .build();
            
            matchRepository.save(match);
        }
    }

    private String getRandomCity() {
        List<String> cities = Arrays.asList(
                "Nueva York", "Los Ángeles", "Miami", "Dallas", "Boston", "Atlanta", "Seattle", "San Francisco",
                "Vancouver", "Toronto", "Montreal", "Ciudad de México", "Guadalajara", "Monterrey"
        );
        return cities.get((int) (Math.random() * cities.size()));
    }

    private String getRandomStadium() {
        List<String> stadiums = Arrays.asList(
                "MetLife Stadium", "SoFi Stadium", "Hard Rock Stadium", "AT&T Stadium", "Gillette Stadium",
                "Mercedes-Benz Stadium", "Lumen Field", "Levi's Stadium", "BC Place", "BMO Field",
                "Estadio Azteca", "Estadio Akron", "Estadio BBVA"
        );
        return stadiums.get((int) (Math.random() * stadiums.size()));
    }
}

