# Agencia Viajes – Catálogo de Destinos Turísticos

Aplicación móvil desarrollada en **Kotlin** para Android que permite gestionar un catálogo de destinos turísticos. Integra **autenticación de usuarios** con **Firebase Authentication** y un sistema **CRUD completo** (Crear, Leer, Actualizar, Eliminar) utilizando **Firebase Firestore**.

Las imágenes de los destinos se almacenan en **almacenamiento local** del dispositivo y se muestran con **Glide**.

**Alumno:** Kevin Alexander Figueroa Rodriguez  
**Materia:** Desarrollo de Software para Móviles – DSM  
**Universidad:** Universidad Don Bosco  

---

## Características

### Autenticación
- Registro de nuevos usuarios con correo y contraseña (Firebase Auth).
- Inicio de sesión (*Login*) con persistencia de sesión.
- Cierre de sesión desde el menú del catálogo (*Logout*).

### Gestión de Destinos (CRUD)
- **Crear:** Formulario con nombre, país (Spinner), precio, descripción (mín. 20 caracteres) e imagen desde la galería.
- **Leer:** Listado en `RecyclerView` con `CardView` (foto, nombre, país, precio y descripción).
- **Actualizar:** Edición de cualquier dato del paquete, incluyendo la imagen.
- **Eliminar:** Borrado con confirmación previa mediante `AlertDialog`.

### Validaciones
- Campos no nulos ni vacíos.
- Precio obligatorio y mayor a 0.
- Imagen obligatoria al crear un destino.
- Descripción con mínimo 20 caracteres.
- Mensajes de error y advertencia en pantalla.

### Multimedia
- Selección de imagen desde la galería del dispositivo.
- Almacenamiento local de las fotos en el almacenamiento interno de la app.
- Carga de imágenes en la lista con la librería **Glide**.


---

## Tecnologías y Dependencias

| Tecnología | Uso |
|------------|-----|
| **Kotlin** | Lenguaje principal |
| **Android Studio** | IDE |
| **Firebase Authentication** | Login y registro |
| **Firebase Firestore** | Base de datos del catálogo |
| **Glide** | Carga y visualización de imágenes |
| **Material Components** | UI (Toolbar, CardView, FAB, TextInputLayout) |
| **RecyclerView** | Lista de destinos |
| **ViewBinding** | Enlace de vistas |
| **Coroutines** | Operaciones asíncronas |

## Estructura del Proyecto

```text
com.example.viajesapp/
├── data/
│   ├── model/
│   │   └── Destino.kt                 # Modelo de datos
│   └── repository/
│       ├── AuthRepository.kt          # Login / Registro / Logout
│       └── DestinoRepository.kt       # CRUD + guardado local de imágenes
├── ui/
│   ├── auth/
│   │   ├── LoginActivity.kt
│   │   └── RegisterActivity.kt
│   ├── catalogo/
│   │   ├── CatalogoActivity.kt        # Lista de destinos
│   │   └── DestinoAdapter.kt          # Adaptador RecyclerView + Glide
│   └── destino/
│       └── FormDestinoActivity.kt     # Crear / Editar destino
└── res/
    ├── layout/                        # activity_login, activity_catalogo, etc.
    ├── values/
    │   ├── strings.xml                # Todos los textos de la app
    │   ├── colors.xml                 # Paleta (#512DA8)
    │   └── themes.xml
    └── menu/
        └── menu_catalogo.xml          # Opción de cerrar sesión
```

## Reglas de Seguridad (Firestore)
```text
textrules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /destinos/{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
Nota: Las imágenes se guardan en el almacenamiento local del dispositivo (filesDir/destinos_images/), no en Firebase Storage.
```
Nota: Las imágenes se guardan en el almacenamiento local del dispositivo (filesDir/destinos_images/), no en Firebase Storage.

## Requisitos de Ejecución

Clonar el repositorio.
Colocar el archivo google-services.json (generado en la consola de Firebase) dentro del directorio app/.
En Firebase Console:
Activar Authentication → Correo electrónico/Contraseña.
Crear la base de datos Firestore y publicar las reglas de seguridad.

Sincronizar el proyecto con Gradle (Sync Project with Gradle Files).
Compilar y ejecutar en un emulador o dispositivo físico Android.


---

### Qué debes completar tú

1. Kevin Alexander Figueroa Rodriguez  
2.  
