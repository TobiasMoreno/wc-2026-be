# World Cup Prode 2026 - Backend API

Backend REST API desarrollado con Spring Boot 3.3.0 y Java 21 para gestionar datos del Mundial 2026 y permitir a los usuarios guardar partidos favoritos, resultados personalizados y predicciones.

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 3.3.0**
- **Spring Security** con JWT
- **Spring Data JPA** + Hibernate
- **MySQL 8.0**
- **MapStruct** para mapeo de entidades
- **Jakarta Validation**
- **Lombok**
- **Swagger/OpenAPI** para documentación

## 📋 Requisitos Previos

- Java 21 o superior
- Maven 3.6+
- Docker y Docker Compose (opcional)
- MySQL 8.0 (si no usas Docker)

## 🛠️ Configuración

### Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
SPRING_PROFILES_ACTIVE=dev
MYSQL_DATABASE=world_cup_prode_db
SPRING_DATASOURCE_USERNAME=prode_user
SPRING_DATASOURCE_PASSWORD=prode_password
JWT_SECRET=your-super-secret-jwt-key-minimum-256-bits-long-for-security
JWT_EXPIRATION=86400000
GOOGLE_CLIENT_ID=your-google-oauth-client-id
```

### Ejecución con Docker Compose

```bash
docker-compose up -d
```

Esto iniciará:
- MySQL en el puerto 3307
- Backend en el puerto 8080

### Ejecución Local

1. Asegúrate de tener MySQL corriendo
2. Ejecuta la aplicación:

```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## 📚 Documentación API

Una vez iniciada la aplicación, accede a la documentación Swagger en:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/v3/api-docs

## 🔐 Autenticación

La aplicación usa **OAuth2.0** para autenticación. Los endpoints de autenticación son públicos, pero el resto requiere un token JWT válido.

### Flujo de Autenticación

1. **Registro/Login**: El frontend debe enviar los datos del usuario obtenidos de OAuth2.0
2. **Token JWT**: El backend devuelve un token JWT que debe incluirse en las peticiones siguientes
3. **Headers**: Incluir el token en el header `Authorization: Bearer <token>`

## 📡 Endpoints Principales

### Autenticación

#### POST `/auth/register`
Registra un nuevo usuario.

**Request:**
```json
{
  "email": "user@example.com",
  "name": "John Doe",
  "picture": "https://example.com/picture.jpg"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "user@example.com",
    "name": "John Doe",
    "picture": "https://example.com/picture.jpg",
    "role": "USER"
  }
}
```

#### POST `/auth/login`
Inicia sesión (crea usuario si no existe).

**Request:** (mismo formato que register)

**Response:** (mismo formato que register)

### Partidos

#### GET `/matches`
Obtiene todos los partidos (público).

**Response:**
```json
{
  "success": true,
  "message": "Matches retrieved successfully",
  "data": [
    {
      "id": 1,
      "date": "2026-06-11T12:00:00",
      "city": "Nueva York",
      "stadium": "MetLife Stadium",
      "phase": "GROUP",
      "homeTeam": {
        "id": 1,
        "name": "Argentina",
        "flagUrl": "https://flagsapi.com/AR/flat/64.png"
      },
      "awayTeam": {
        "id": 2,
        "name": "Brasil",
        "flagUrl": "https://flagsapi.com/BR/flat/64.png"
      },
      "homeScore": null,
      "awayScore": null
    }
  ]
}
```

#### GET `/matches/{id}`
Obtiene un partido por ID (público).

#### GET `/matches/phase/{phase}`
Obtiene partidos por fase: `GROUP`, `ROUND_OF_32`, `ROUND_OF_16`, `QUARTER_FINAL`, `SEMI_FINAL`, `THIRD_PLACE`, `FINAL` (público).

### Resultados de Usuario

#### GET `/user/matches/results`
Obtiene todos los resultados guardados por el usuario (requiere autenticación).

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "User match results retrieved successfully",
  "data": [
    {
      "id": 1,
      "match": { ... },
      "homeScore": 2,
      "awayScore": 1
    }
  ]
}
```

#### POST `/user/matches/results`
Guarda o actualiza un resultado de partido.

**Request:**
```json
{
  "matchId": 1,
  "homeScore": 2,
  "awayScore": 1
}
```

#### DELETE `/user/matches/results/{matchId}`
Elimina un resultado guardado.

### Favoritos

#### GET `/user/matches/favorites`
Obtiene todos los partidos favoritos del usuario.

#### POST `/user/matches/favorites/{matchId}`
Agrega un partido a favoritos.

#### DELETE `/user/matches/favorites/{matchId}`
Elimina un partido de favoritos.

#### GET `/user/matches/favorites/{matchId}/check`
Verifica si un partido está en favoritos.

**Response:**
```json
{
  "success": true,
  "message": "Favorite status retrieved successfully",
  "data": true
}
```

### Preferencias de Usuario

#### GET `/user/preferences`
Obtiene las preferencias del usuario.

**Response:**
```json
{
  "success": true,
  "message": "User preferences retrieved successfully",
  "data": {
    "id": 1,
    "timezone": "America/New_York",
    "language": "es",
    "notificationsEnabled": true
  }
}
```

#### PUT `/user/preferences`
Actualiza las preferencias del usuario.

**Request:**
```json
{
  "timezone": "America/New_York",
  "language": "en",
  "notificationsEnabled": false
}
```

### Predicciones de Llaves

#### GET `/user/bracket`
Obtiene todas las predicciones de llaves del usuario.

**Response:**
```json
{
  "success": true,
  "message": "User bracket predictions retrieved successfully",
  "data": [
    {
      "id": 1,
      "match": { ... },
      "predictedWinner": {
        "id": 1,
        "name": "Argentina",
        "flagUrl": "https://flagsapi.com/AR/flat/64.png"
      }
    }
  ]
}
```

#### POST `/user/bracket`
Guarda o actualiza una predicción de llave.

**Request:**
```json
{
  "matchId": 1,
  "predictedWinnerId": 1
}
```

#### GET `/user/bracket/phase/{phase}`
Obtiene predicciones por fase.

## 🔗 Integración con Angular

### Configuración del Servicio HTTP

Crea un servicio Angular para manejar las peticiones HTTP:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8080';
  private token: string | null = null;

  constructor(private http: HttpClient) {
    // Recuperar token del localStorage
    this.token = localStorage.getItem('jwt_token');
  }

  setToken(token: string) {
    this.token = token;
    localStorage.setItem('jwt_token', token);
  }

  private getHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    if (this.token) {
      headers = headers.set('Authorization', `Bearer ${this.token}`);
    }
    
    return headers;
  }

  // Autenticación
  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/register`, data);
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, data);
  }

  // Partidos
  getMatches(): Observable<any> {
    return this.http.get(`${this.apiUrl}/matches`, { headers: this.getHeaders() });
  }

  getMatch(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/matches/${id}`, { headers: this.getHeaders() });
  }

  // Resultados de usuario
  getUserMatchResults(): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/matches/results`, { headers: this.getHeaders() });
  }

  saveUserMatchResult(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/matches/results`, data, { headers: this.getHeaders() });
  }

  // Favoritos
  getFavorites(): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/matches/favorites`, { headers: this.getHeaders() });
  }

  addFavorite(matchId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/matches/favorites/${matchId}`, {}, { headers: this.getHeaders() });
  }

  removeFavorite(matchId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/user/matches/favorites/${matchId}`, { headers: this.getHeaders() });
  }

  // Preferencias
  getPreferences(): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/preferences`, { headers: this.getHeaders() });
  }

  updatePreferences(data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/user/preferences`, data, { headers: this.getHeaders() });
  }

  // Predicciones
  getBracketPredictions(): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/bracket`, { headers: this.getHeaders() });
  }

  saveBracketPrediction(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/bracket`, data, { headers: this.getHeaders() });
  }
}
```

### Interceptor para JWT

Crea un interceptor para agregar automáticamente el token:

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem('jwt_token');
    
    if (token) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(cloned);
    }
    
    return next.handle(req);
  }
}
```

Regístralo en `app.config.ts`:

```typescript
import { provideHttpClient, withInterceptors } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor])
    )
  ]
};
```

### Manejo de OAuth2.0

Para integrar OAuth2.0 con Google en Angular:

```typescript
import { OAuthService } from 'angular-oauth2-oidc';

// Después de autenticación exitosa con Google
this.oauthService.loadUserProfile().then((profile: any) => {
  const loginData = {
    email: profile.email,
    name: profile.name,
    picture: profile.picture
  };
  
  this.apiService.login(loginData).subscribe({
    next: (response) => {
      if (response.success && response.data.token) {
        this.apiService.setToken(response.data.token);
        // Redirigir al dashboard
      }
    },
    error: (error) => {
      console.error('Login error:', error);
    }
  });
});
```

## 🗄️ Estructura de Base de Datos

### Entidades Principales

- **users**: Información de usuarios
- **teams**: Equipos participantes
- **matches**: Partidos oficiales
- **user_match_results**: Resultados ingresados por usuarios
- **user_favorite_matches**: Partidos marcados como favoritos
- **user_preferences**: Preferencias de usuario
- **user_bracket_predictions**: Predicciones de llaves

## 🧪 Testing

```bash
mvn test
```

## 📝 Notas

- Los datos se siembran automáticamente al iniciar la aplicación si la base de datos está vacía
- El token JWT expira según la configuración en `JWT_EXPIRATION` (por defecto 24 horas)
- Todos los endpoints de usuario requieren autenticación excepto `/auth/**` y `/matches/**`

## 🐛 Troubleshooting

### Error de conexión a MySQL
- Verifica que MySQL esté corriendo
- Revisa las credenciales en `.env`
- Asegúrate de que el puerto 3307 esté disponible

### Error de compilación con MapStruct
- Ejecuta `mvn clean install` para regenerar los mappers
- Verifica que Lombok esté configurado correctamente en tu IDE

## 📄 Licencia

Este proyecto es privado y está destinado únicamente para uso del Mundial 2026 Prode.

