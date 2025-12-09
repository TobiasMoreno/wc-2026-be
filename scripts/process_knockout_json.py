#!/usr/bin/env python3
"""
Script para procesar el JSON de knockout stages y generar knockout_2026.json
con los partidos de las fases eliminatorias.
"""

import json
import re
from datetime import datetime
from typing import Dict, List, Optional
from pathlib import Path


def get_spanish_name(names: List[Dict]) -> Optional[str]:
    """Obtiene el nombre en español de una lista de nombres localizados"""
    if not names:
        return None
    for name in names:
        if name.get("Locale") == "es-ES":
            return name.get("Description")
    return names[0].get("Description") if names else None


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
    elif "final" in stage_lower and "semifinal" not in stage_lower and "tercer" not in stage_lower:
        return "FINAL"
    else:
        # Por defecto, fase de grupos
        return "GROUP"


def process_knockout_json(input_file: str, output_dir: str = "."):
    """
    Procesa el JSON de knockout stages y genera knockout_2026.json
    
    Args:
        input_file: Ruta al archivo knockout_stages.json
        output_dir: Directorio donde guardar el archivo de salida
    """
    input_path = Path(input_file)
    if not input_path.exists():
        print(f"Error: No se encontró el archivo {input_file}")
        return
    
    print(f"Leyendo archivo: {input_file}")
    with open(input_path, 'r', encoding='utf-8') as f:
        knockout_data = json.load(f)
    
    # Lista de partidos procesados
    matches_list: List[Dict] = []
    
    # Procesar cada stage
    if isinstance(knockout_data, list):
        stages = knockout_data
    else:
        print("Error: El JSON no es un array")
        return
    
    for stage in stages:
        stage_name = get_spanish_name(stage.get("Name", []))
        phase = map_stage_to_phase(stage_name)
        
        print(f"Procesando fase: {stage_name} -> {phase}")
        
        # Procesar partidos directos del stage
        stage_matches = stage.get("Matches", [])
        for match in stage_matches:
            match_obj = process_match(match, phase)
            if match_obj:
                matches_list.append(match_obj)
    
    # Ordenar partidos por fecha
    matches_list.sort(key=lambda x: x["date"])
    
    # Guardar knockout_2026.json
    output_path = Path(output_dir) / "knockout_2026.json"
    print(f"Guardando {len(matches_list)} partidos en {output_path}")
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(matches_list, f, ensure_ascii=False, indent=2)
    
    print(f"\n✅ Proceso completado!")
    print(f"   - Partidos de eliminación: {len(matches_list)}")
    
    # Mostrar resumen por fase
    phase_counts = {}
    for match in matches_list:
        phase = match.get("phase", "UNKNOWN")
        phase_counts[phase] = phase_counts.get(phase, 0) + 1
    
    print(f"\n📊 Resumen por fase:")
    for phase, count in sorted(phase_counts.items()):
        print(f"   - {phase}: {count} partidos")


def process_match(match: Dict, phase: str) -> Optional[Dict]:
    """Procesa un partido individual de knockout"""
    
    # Obtener equipos (pueden ser null si hay placeholders)
    home_team_info = match.get("HomeTeam")
    away_team_info = match.get("AwayTeam")
    
    # Extraer nombres de equipos (pueden ser None si hay placeholders)
    home_team_name = None
    away_team_name = None
    
    if home_team_info:
        home_team_name = get_spanish_name(home_team_info.get("TeamName", []))
    
    if away_team_info:
        away_team_name = get_spanish_name(away_team_info.get("TeamName", []))
    
    # Obtener información del estadio
    stadium_info = match.get("Stadium", {})
    stadium_name = get_spanish_name(stadium_info.get("Name", [])) or "Estadio no especificado"
    city_name = get_spanish_name(stadium_info.get("CityName", [])) or "Ciudad no especificada"
    
    # Obtener fecha
    match_date = match.get("Date")
    if not match_date:
        return None
    
    # Obtener scores (pueden ser null)
    home_score = match.get("HomeTeamScore")
    away_score = match.get("AwayTeamScore")
    
    # Crear objeto de partido
    # Si un equipo es null, usamos "Por definir"
    match_obj = {
        "date": match_date,  # Mantener formato ISO de FIFA
        "city": city_name,
        "stadium": stadium_name,
        "phase": phase,
        "homeTeam": home_team_name or "Por definir",
        "awayTeam": away_team_name or "Por definir",
        "homeScore": home_score,
        "awayScore": away_score
    }
    
    # No incluir group para partidos de eliminación
    return match_obj


if __name__ == "__main__":
    import sys
    
    # Rutas por defecto
    default_input = "src/main/resources/knockout_stages.json"
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
    
    process_knockout_json(str(input_path), str(output_path))

