package wc.prode._6.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
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

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final ProdeGroupRepository prodeGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) {
        if (teamRepository.count() == 0) {
            log.info("Seeding teams data...");
            seedTeams();
        }

        if (matchRepository.count() == 0) {
            log.info("Seeding matches data...");
            // Crear equipo "Por definir" si no existe (para partidos de knockout con equipos pendientes)
            createPlaceholderTeam();
            
            // Intentar importar desde matches_2026.json (fase de grupos)
            int groupMatches = importMatchesFromJson("matches_2026.json");
            
            // Intentar importar desde knockout_2026.json (fases eliminatorias)
            int knockoutMatches = importMatchesFromJson("knockout_2026.json");
            
            if (groupMatches > 0 || knockoutMatches > 0) {
                log.info("Matches imported successfully: {} group matches, {} knockout matches", 
                    groupMatches, knockoutMatches);
            } else {
                log.info("JSON files not found or empty, using default seed data");
                seedMatches();
            }
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
        try {
            ClassPathResource resource = new ClassPathResource("team.json");
            if (!resource.exists()) {
                log.warn("team.json not found in classpath, using default teams");
                seedDefaultTeams();
                return;
            }
            
            InputStream inputStream = resource.getInputStream();
            List<Map<String, Object>> teamsData = objectMapper.readValue(
                inputStream, 
                new TypeReference<List<Map<String, Object>>>() {}
            );
            
            if (teamsData == null || teamsData.isEmpty()) {
                log.warn("team.json is empty, using default teams");
                seedDefaultTeams();
                return;
            }
            
            int importedTeams = 0;
            for (Map<String, Object> teamData : teamsData) {
                String name = (String) teamData.get("name");
                String flagUrl = (String) teamData.get("flagUrl");
                
                if (name == null || name.isEmpty()) {
                    continue;
                }
                
                // Verificar si el equipo ya existe
                if (teamRepository.findByName(name).isEmpty()) {
                    Team team = Team.builder()
                            .name(name)
                            .flagUrl(flagUrl != null ? flagUrl : "https://flagsapi.com/XX/flat/64.png")
                            .build();
                    teamRepository.save(team);
                    importedTeams++;
                }
            }
            
            log.info("Imported {} teams from team.json", importedTeams);
            
        } catch (Exception e) {
            log.error("Error importing teams from team.json, using default teams", e);
            seedDefaultTeams();
        }
    }
    
    /**
     * Crea el equipo placeholder "Por definir" para partidos de knockout con equipos pendientes
     */
    private void createPlaceholderTeam() {
        String placeholderName = "Por definir";
        if (teamRepository.findByName(placeholderName).isEmpty()) {
            Team placeholder = Team.builder()
                    .name(placeholderName)
                    .flagUrl("https://flagsapi.com/XX/flat/64.png")
                    .build();
            teamRepository.save(placeholder);
            log.info("Created placeholder team: '{}'", placeholderName);
        }
    }
    
    private void seedDefaultTeams() {
        List<String> teams = Arrays.asList(
                "Argentina", "Brasil", "Uruguay", "Colombia", "Ecuador", "Perú", "Chile", "Paraguay",
                "Estados Unidos", "México", "Canadá", "Costa Rica", "Panamá", "Jamaica", "Honduras", "El Salvador",
                "España", "Francia", "Inglaterra", "Alemania", "Italia", "Países Bajos", "Bélgica", "Portugal",
                "Croacia", "Dinamarca", "Suiza", "Polonia", "Suecia", "Noruega", "Austria", "República Checa",
                "Japón", "Corea del Sur", "Australia", "Irán", "Arabia Saudita", "Qatar", "Emiratos Árabes Unidos", "China",
                "Senegal", "Marruecos", "Túnez", "Egipto", "Nigeria", "Camerún", "Ghana", "Costa de Marfil"
        );

        for (String teamName : teams) {
            if (teamRepository.findByName(teamName).isEmpty()) {
                Team team = Team.builder()
                        .name(teamName)
                        .flagUrl("https://flagsapi.com/XX/flat/64.png")
                        .build();
                teamRepository.save(team);
            }
        }
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
    
    /**
     * Importa partidos desde un archivo JSON
     * @param fileName nombre del archivo JSON (matches_2026.json o knockout_2026.json)
     * @return número de partidos importados
     */
    private int importMatchesFromJson(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            if (!resource.exists()) {
                log.warn("{} not found in classpath", fileName);
                return 0;
            }
            
            InputStream inputStream = resource.getInputStream();
            List<Map<String, Object>> matchesData = objectMapper.readValue(
                inputStream, 
                new TypeReference<List<Map<String, Object>>>() {}
            );
            
            if (matchesData == null || matchesData.isEmpty()) {
                log.warn("{} is empty or has no matches", fileName);
                return 0;
            }
            
            int importedMatches = 0;
            int skippedMatches = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            
            // Para knockout_2026.json, permitir partidos con "Por definir"
            boolean allowPlaceholders = fileName.equals("knockout_2026.json");
            
            for (Map<String, Object> matchData : matchesData) {
                if (importMatch(matchData, formatter, allowPlaceholders)) {
                    importedMatches++;
                } else {
                    skippedMatches++;
                }
            }
            
            log.info("Imported {} matches from {} ({} skipped)", importedMatches, fileName, skippedMatches);
            return importedMatches;
            
        } catch (Exception e) {
            log.error("Error importing matches from {}", fileName, e);
            return 0;
        }
    }
    
    /**
     * Importa un partido individual desde el JSON procesado
     * @param matchData datos del partido
     * @param formatter formateador de fecha
     * @param allowPlaceholders si true, permite equipos "Por definir"
     */
    private boolean importMatch(Map<String, Object> matchData, DateTimeFormatter formatter, boolean allowPlaceholders) {
        try {
            // Obtener nombres de equipos
            String homeTeamName = (String) matchData.get("homeTeam");
            String awayTeamName = (String) matchData.get("awayTeam");
            
            // Validar nombres de equipos
            if (homeTeamName == null || awayTeamName == null || homeTeamName.isEmpty() || awayTeamName.isEmpty()) {
                return false;
            }
            
            // Si no se permiten placeholders, saltar partidos con "Por definir"
            if (!allowPlaceholders && ("Por definir".equals(homeTeamName) || "Por definir".equals(awayTeamName))) {
                return false;
            }
            
            // Buscar equipos en la base de datos
            Team homeTeam = teamRepository.findByName(homeTeamName)
                .orElse(null);
            Team awayTeam = teamRepository.findByName(awayTeamName)
                .orElse(null);
            
            // Si no se encuentran los equipos, crear un log de advertencia pero continuar
            if (homeTeam == null) {
                if (allowPlaceholders && "Por definir".equals(homeTeamName)) {
                    // El equipo placeholder debería existir ya (creado en createPlaceholderTeam)
                    homeTeam = teamRepository.findByName("Por definir").orElse(null);
                }
                if (homeTeam == null) {
                    log.warn("Team not found: {}, skipping match", homeTeamName);
                    return false;
                }
            }
            
            if (awayTeam == null) {
                if (allowPlaceholders && "Por definir".equals(awayTeamName)) {
                    // El equipo placeholder debería existir ya (creado en createPlaceholderTeam)
                    awayTeam = teamRepository.findByName("Por definir").orElse(null);
                }
                if (awayTeam == null) {
                    log.warn("Team not found: {}, skipping match", awayTeamName);
                    return false;
                }
            }
            
            // Parsear fecha
            String dateStr = (String) matchData.get("date");
            if (dateStr == null || dateStr.isEmpty()) {
                log.warn("Match date is missing, skipping match");
                return false;
            }
            
            LocalDateTime matchDate;
            try {
                matchDate = LocalDateTime.parse(dateStr, formatter);
            } catch (Exception e) {
                log.warn("Could not parse date for match: {}", dateStr, e);
                return false;
            }
            
            // Obtener información del partido
            String city = (String) matchData.get("city");
            if (city == null || city.isEmpty()) {
                city = "Ciudad no especificada";
            }
            
            String stadium = (String) matchData.get("stadium");
            if (stadium == null || stadium.isEmpty()) {
                stadium = "Estadio no especificado";
            }
            
            // Obtener fase
            String phaseStr = (String) matchData.get("phase");
            Phase phase;
            try {
                phase = Phase.valueOf(phaseStr != null ? phaseStr.toUpperCase() : "GROUP");
            } catch (IllegalArgumentException e) {
                log.warn("Invalid phase: {}, using GROUP", phaseStr);
                phase = Phase.GROUP;
            }
            
            // Obtener grupo (opcional)
            String group = (String) matchData.get("group");
            
            // Obtener scores (pueden ser null)
            Integer homeScore = null;
            Integer awayScore = null;
            Object homeScoreObj = matchData.get("homeScore");
            Object awayScoreObj = matchData.get("awayScore");
            
            if (homeScoreObj != null) {
                if (homeScoreObj instanceof Integer) {
                    homeScore = (Integer) homeScoreObj;
                } else if (homeScoreObj instanceof Number) {
                    homeScore = ((Number) homeScoreObj).intValue();
                }
            }
            
            if (awayScoreObj != null) {
                if (awayScoreObj instanceof Integer) {
                    awayScore = (Integer) awayScoreObj;
                } else if (awayScoreObj instanceof Number) {
                    awayScore = ((Number) awayScoreObj).intValue();
                }
            }
            
            // Crear el partido
            Match match = Match.builder()
                .date(matchDate)
                .city(city)
                .stadium(stadium)
                .phase(phase)
                .group(group)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeScore(homeScore)
                .awayScore(awayScore)
                .build();
            
            matchRepository.save(match);
            return true;
            
        } catch (Exception e) {
            log.error("Error importing match: {}", e.getMessage(), e);
            return false;
        }
    }
}

