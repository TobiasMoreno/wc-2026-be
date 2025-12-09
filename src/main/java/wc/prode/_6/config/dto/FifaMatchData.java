package wc.prode._6.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FifaMatchData {
    @JsonProperty("IdSeason")
    private String idSeason;
    
    @JsonProperty("IdCompetition")
    private String idCompetition;
    
    @JsonProperty("KnockoutStages")
    private List<KnockoutStage> knockoutStages;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KnockoutStage {
        @JsonProperty("IdStage")
        private String idStage;
        
        @JsonProperty("SequenceOrder")
        private Integer sequenceOrder;
        
        @JsonProperty("Name")
        private List<Name> name;
        
        @JsonProperty("Groups")
        private List<Group> groups;
        
        @JsonProperty("Matches")
        private List<Match> matches;
        
        public String getStageName() {
            if (name != null && !name.isEmpty()) {
                return name.stream()
                    .filter(n -> "es-ES".equals(n.locale))
                    .map(n -> n.description)
                    .findFirst()
                    .orElse(name.get(0).description);
            }
            return null;
        }
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Group {
        @JsonProperty("IdGroup")
        private String idGroup;
        
        @JsonProperty("Name")
        private List<Name> name;
        
        @JsonProperty("Matches")
        private List<Match> matches;
        
        public String getGroupName() {
            if (name != null && !name.isEmpty()) {
                return name.stream()
                    .filter(n -> "es-ES".equals(n.locale))
                    .map(n -> n.description)
                    .findFirst()
                    .orElse(name.get(0).description);
            }
            return null;
        }
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Match {
        @JsonProperty("IdMatch")
        private String idMatch;
        
        @JsonProperty("IdStage")
        private String idStage;
        
        @JsonProperty("IdGroup")
        private String idGroup;
        
        @JsonProperty("Date")
        private String date;
        
        @JsonProperty("LocalDate")
        private String localDate;
        
        @JsonProperty("Stadium")
        private Stadium stadium;
        
        @JsonProperty("TeamA")
        private String teamA;
        
        @JsonProperty("TeamB")
        private String teamB;
        
        @JsonProperty("HomeTeam")
        private TeamInfo homeTeam;
        
        @JsonProperty("AwayTeam")
        private TeamInfo awayTeam;
        
        @JsonProperty("HomeTeamScore")
        private Integer homeTeamScore;
        
        @JsonProperty("AwayTeamScore")
        private Integer awayTeamScore;
        
        @JsonProperty("MatchNumber")
        private Integer matchNumber;
        
        @JsonProperty("PlaceHolderA")
        private String placeHolderA;
        
        @JsonProperty("PlaceHolderB")
        private String placeHolderB;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stadium {
        @JsonProperty("IdStadium")
        private String idStadium;
        
        @JsonProperty("Name")
        private List<Name> name;
        
        @JsonProperty("CityName")
        private List<Name> cityName;
        
        @JsonProperty("IdCountry")
        private String idCountry;
        
        @JsonProperty("Capacity")
        private Integer capacity;
        
        public String getStadiumName() {
            if (name != null && !name.isEmpty()) {
                return name.stream()
                    .filter(n -> "es-ES".equals(n.locale))
                    .map(n -> n.description)
                    .findFirst()
                    .orElse(name.get(0).description);
            }
            return null;
        }
        
        public String getCityName() {
            if (cityName != null && !cityName.isEmpty()) {
                return cityName.stream()
                    .filter(n -> "es-ES".equals(n.locale))
                    .map(n -> n.description)
                    .findFirst()
                    .orElse(cityName.get(0).description);
            }
            return null;
        }
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamInfo {
        @JsonProperty("IdTeam")
        private String idTeam;
        
        @JsonProperty("TeamName")
        private List<Name> teamName;
        
        @JsonProperty("IdCountry")
        private String idCountry;
        
        public String getTeamName() {
            if (teamName != null && !teamName.isEmpty()) {
                return teamName.stream()
                    .filter(n -> "es-ES".equals(n.locale))
                    .map(n -> n.description)
                    .findFirst()
                    .orElse(teamName.get(0).description);
            }
            return null;
        }
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name {
        @JsonProperty("Locale")
        private String locale;
        
        @JsonProperty("Description")
        private String description;
    }
}

