#!/usr/bin/env python3
"""
Script para procesar el JSON de FIFA y generar:
- team.json: Lista de equipos únicos
- matches_2026.json: Lista de partidos formateados
"""

import json
import re
from datetime import datetime
from typing import Dict, List, Optional, Set
from pathlib import Path


def get_spanish_name(names: List[Dict]) -> Optional[str]:
    """Obtiene el nombre en español de una lista de nombres localizados"""
    if not names:
        return None
    for name in names:
        if name.get("Locale") == "es-ES":
            return name.get("Description")
    return names[0].get("Description") if names else None


def extract_group_letter(group_name: str) -> Optional[str]:
    """Extrae la letra del grupo (ej: 'Grupo A' -> 'A')"""
    if not group_name:
        return None
    match = re.search(r"Grupo\s+([A-L])", group_name, re.IGNORECASE)
    if match:
        return match.group(1).upper()
    return None


def map_stage_to_phase(stage_name: str) -> str:
    """Mapea el nombre de la fase de FIFA al enum Phase de Java"""
    if not stage_name:
        return "GROUP"
    
    stage_lower = stage_name.lower()
    
    # Mapeo basado en el enum Phase de Java:
    # GROUP, ROUND_OF_32, ROUND_OF_16, QUARTER_FINAL, SEMI_FINAL, THIRD_PLACE, FINAL
    
    if "dieciseisavo" in stage_lower or "treintaidosavo" in stage_lower or "round of 32" in stage_lower:
        return "ROUND_OF_32"
    elif "octavo" in stage_lower or "round of 16" in stage_lower:
        return "ROUND_OF_16"
    elif "cuarto" in stage_lower or "quarter" in stage_lower:
        return "QUARTER_FINAL"
    elif "semifinal" in stage_lower or "semi" in stage_lower:
        return "SEMI_FINAL"
    elif "tercer" in stage_lower or "third" in stage_lower:
        return "THIRD_PLACE"
    elif "final" in stage_lower and "semifinal" not in stage_lower:
        return "FINAL"
    else:
        # Por defecto, fase de grupos
        return "GROUP"


def get_team_flag_url(country_code: str) -> str:
    """Genera la URL de la bandera usando el código de país"""
    if not country_code:
        return "https://flagsapi.com/XX/flat/64.png"
    return f"https://flagsapi.com/{country_code}/flat/64.png"


def process_fifa_json(input_file: str, output_dir: str = "."):
    """
    Procesa el JSON de FIFA y genera team.json y matches_2026.json
    
    Args:
        input_file: Ruta al archivo matches.json de FIFA
        output_dir: Directorio donde guardar los archivos de salida
    """
    input_path = Path(input_file)
    if not input_path.exists():
        print(f"Error: No se encontró el archivo {input_file}")
        return
    
    print(f"Leyendo archivo: {input_file}")
    with open(input_path, 'r', encoding='utf-8') as f:
        fifa_data = json.load(f)
    
    # Almacenar equipos únicos
    teams_set: Set[tuple] = set()  # (name, country_code)
    teams_list: List[Dict] = []
    
    # Lista de partidos procesados
    matches_list: List[Dict] = []
    
    # El JSON puede tener dos estructuras:
    # 1. Un objeto directo con Groups (fase de grupos)
    # 2. Un objeto con KnockoutStages (etapas eliminatorias)
    
    # Primero intentar como objeto directo (fase de grupos)
    if "Groups" in fifa_data:
        stage_name = get_spanish_name(fifa_data.get("Name", []))
        phase = map_stage_to_phase(stage_name)
        
        groups = fifa_data.get("Groups", [])
        for group in groups:
            group_name = get_spanish_name(group.get("Name", []))
            group_letter = extract_group_letter(group_name) if group_name else None
            
            group_matches = group.get("Matches", [])
            for match in group_matches:
                process_match(match, phase, group_letter, teams_set, teams_list, matches_list)
        
        # También procesar partidos directos del stage si existen
        stage_matches = fifa_data.get("Matches", [])
        for match in stage_matches:
            process_match(match, phase, None, teams_set, teams_list, matches_list)
    
    # Si no tiene Groups, intentar con KnockoutStages
    elif "KnockoutStages" in fifa_data:
        knockout_stages = fifa_data.get("KnockoutStages", [])
        
        for stage in knockout_stages:
            stage_name = get_spanish_name(stage.get("Name", []))
            phase = map_stage_to_phase(stage_name)
            
            # Procesar grupos (fase de grupos)
            groups = stage.get("Groups", [])
            for group in groups:
                group_name = get_spanish_name(group.get("Name", []))
                group_letter = extract_group_letter(group_name) if group_name else None
                
                group_matches = group.get("Matches", [])
                for match in group_matches:
                    process_match(match, phase, group_letter, teams_set, teams_list, matches_list)
            
            # Procesar partidos directos de eliminatoria (sin grupos)
            stage_matches = stage.get("Matches", [])
            for match in stage_matches:
                process_match(match, phase, None, teams_set, teams_list, matches_list)
    
    else:
        print("⚠️  Advertencia: No se encontró estructura 'Groups' ni 'KnockoutStages' en el JSON")
        print(f"   Claves disponibles: {list(fifa_data.keys())[:10]}")
    
    # Ordenar equipos por nombre
    teams_list.sort(key=lambda x: x["name"])
    
    # Ordenar partidos por fecha
    matches_list.sort(key=lambda x: x["date"])
    
    # Guardar team.json
    teams_output = Path(output_dir) / "team.json"
    print(f"Guardando {len(teams_list)} equipos en {teams_output}")
    with open(teams_output, 'w', encoding='utf-8') as f:
        json.dump(teams_list, f, ensure_ascii=False, indent=2)
    
    # Guardar matches_2026.json
    matches_output = Path(output_dir) / "matches_2026.json"
    print(f"Guardando {len(matches_list)} partidos en {matches_output}")
    with open(matches_output, 'w', encoding='utf-8') as f:
        json.dump(matches_list, f, ensure_ascii=False, indent=2)
    
    print(f"\n✅ Proceso completado!")
    print(f"   - Equipos: {len(teams_list)}")
    print(f"   - Partidos: {len(matches_list)}")


def process_match(
    match: Dict,
    phase: str,
    group_letter: Optional[str],
    teams_set: Set[tuple],
    teams_list: List[Dict],
    matches_list: List[Dict]
):
    """Procesa un partido individual y extrae equipos"""
    
    # Obtener equipos (pueden ser null si hay placeholders)
    home_team_info = match.get("HomeTeam")
    away_team_info = match.get("AwayTeam")
    
    # Si ambos equipos son null, saltar este partido
    if not home_team_info and not away_team_info:
        return
    
    # Extraer nombres de equipos (pueden ser None si hay placeholders)
    home_team_name = None
    away_team_name = None
    
    if home_team_info:
        home_team_name = get_spanish_name(home_team_info.get("TeamName", []))
    
    if away_team_info:
        away_team_name = get_spanish_name(away_team_info.get("TeamName", []))
    
    # Si ambos nombres son None, saltar este partido
    if not home_team_name and not away_team_name:
        return
    
    # Agregar equipos al conjunto único (solo si están definidos)
    if home_team_info and home_team_name:
        home_country_code = home_team_info.get("IdCountry", "")
        if (home_team_name, home_country_code) not in teams_set:
            teams_set.add((home_team_name, home_country_code))
            teams_list.append({
                "name": home_team_name,
                "flagUrl": get_team_flag_url(home_country_code)
            })
    
    if away_team_info and away_team_name:
        away_country_code = away_team_info.get("IdCountry", "")
        if (away_team_name, away_country_code) not in teams_set:
            teams_set.add((away_team_name, away_country_code))
            teams_list.append({
                "name": away_team_name,
                "flagUrl": get_team_flag_url(away_country_code)
            })
    
    # Obtener información del estadio
    stadium_info = match.get("Stadium", {})
    stadium_name = get_spanish_name(stadium_info.get("Name", [])) or "Estadio no especificado"
    city_name = get_spanish_name(stadium_info.get("CityName", [])) or "Ciudad no especificada"
    
    # Obtener fecha
    match_date = match.get("Date")
    if not match_date:
        return
    
    # Crear objeto de partido
    # Nota: Si un equipo es null, usamos el nombre del placeholder o "Por definir"
    match_obj = {
        "date": match_date,  # Mantener formato ISO de FIFA
        "city": city_name,
        "stadium": stadium_name,
        "phase": phase,
        "homeTeam": home_team_name or "Por definir",
        "awayTeam": away_team_name or "Por definir",
        "homeScore": match.get("HomeTeamScore"),
        "awayScore": match.get("AwayTeamScore")
    }
    
    # Agregar grupo solo si existe
    if group_letter:
        match_obj["group"] = group_letter
    
    matches_list.append(match_obj)


if __name__ == "__main__":
    import sys
    
    # Rutas por defecto
    default_input = "src/main/resources/matches.json"
    default_output = "src/main/resources"
    
    # Verificar si se proporcionaron argumentos
    if len(sys.argv) > 1:
        input_file = sys.argv[1]
    else:
        input_file = default_input
    
    if len(sys.argv) > 2:
        output_dir = sys.argv[2]
    else:
        output_dir = default_output
    
    # Obtener el directorio del script
    script_dir = Path(__file__).parent
    project_root = script_dir.parent
    
    # Resolver rutas relativas al proyecto
    input_path = (project_root / input_file) if not Path(input_file).is_absolute() else Path(input_file)
    output_path = (project_root / output_dir) if not Path(output_dir).is_absolute() else Path(output_dir)
    
    # Crear directorio de salida si no existe
    output_path.mkdir(parents=True, exist_ok=True)
    
    print(f"📂 Directorio del proyecto: {project_root}")
    print(f"📥 Archivo de entrada: {input_path}")
    print(f"📤 Directorio de salida: {output_path}\n")
    
    process_fifa_json(str(input_path), str(output_path))

