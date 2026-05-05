# Plan de Implementación: App de Presupuestos - MB Cerramientos

## Descripción General
App Android (Jetpack Compose) para crear presupuestos técnicos profesionales en PDF para clientes de cerramientos y aberturas.

### Stack Tecnológico
- **Android**: API 28+ (Target: 36)
- **UI**: Jetpack Compose
- **Base de datos**: Room (SQLite)
- **Generación PDF**: iText o similar
- **Compartir**: WhatsApp, Email, Archivos
- **Arquitectura**: MVVM con ViewModel

---

## FASES DE DESARROLLO

### ✅ FASE 1: Base de la Aplicación y Estructura de Datos
**Rama**: `phase-1-base-structure`

**Objetivos**:
- Estructura del proyecto organizada
- Modelos de datos principales
- Base de datos Room
- Navegación básica

**Tareas**:
- [ ] Crear estructura de carpetas (data, ui, domain, presentation)
- [ ] Definir modelos: 
  - `Budget` (Presupuesto)
  - `Client` (Cliente)
  - `BudgetItem` (Item del presupuesto)
  - `ItemType` (Tipo: ventana, puerta, baranda, etc)
  - `AppSettings` (Configuración)
- [ ] Implementar base de datos Room
  - DAOs para cada entidad
  - Migrations
- [ ] Configurar navegación Compose (NavGraph)
- [ ] Pantalla inicial (Home) con navegación base
- [ ] Implementar ViewModels básicos

**Dependencias a agregar**:
```
- androidx.room:room-runtime
- androidx.room:room-ktx
- androidx.room:room-compiler
- androidx.navigation:navigation-compose
- org.jetbrains.kotlinx:kotlinx-serialization-json
```

---

### ✅ FASE 2: Gestión de Presupuestos
**Rama**: `phase-2-budget-management`
**Basada en**: `phase-1-base-structure`

**Objetivos**:
- CRUD de presupuestos
- Listado y búsqueda
- Detalles del presupuesto

**Tareas**:
- [ ] Pantalla "Nuevo Presupuesto"
  - Formulario con datos del cliente
  - Validaciones
  - Guardar en BD
- [ ] Pantalla "Mis Presupuestos" (Listado)
  - RecyclerView o LazyColumn
  - Búsqueda y filtros
  - Acciones (editar, duplicar, eliminar)
- [ ] Pantalla "Detalle Presupuesto"
  - Ver datos completos
  - Opciones de acción
- [ ] Pantalla "Editar Presupuesto"
  - Modificar datos del cliente
  - Cambios de estado
- [ ] Implementar ViewModel para presupuestos
- [ ] Repository para acceso a datos

---

### ✅ FASE 3: Sistema de Items y Especificaciones
**Rama**: `phase-3-items-system`
**Basada en**: `phase-2-budget-management`

**Objetivos**:
- Agregar items al presupuesto
- Tipos de aberturas configurables
- Cálculo de costos

**Tareas**:
- [ ] Pantalla "Agregar Item"
  - Selector de tipo (ventana, puerta, baranda, etc)
  - Campos dinámicos por tipo
  - Especificaciones técnicas
- [ ] Modelos para cada tipo:
  - Window (Ventana): sistema de perfiles, vidrios, medidas
  - Door (Puerta): tipo, material, medidas
  - Railing (Baranda): material, altura, medidas
  - Custom (Customizable)
- [ ] Pantalla "Editar Item"
- [ ] Pantalla "Eliminar Item"
- [ ] Cálculo automático de cantidad y subtotales
- [ ] ViewModel para items
- [ ] Validaciones de campos

**Campos técnicos por tipo**:
```
VENTANA:
- Dimensiones (ancho x alto)
- Sistema de perfiles (ej: CORR. SLIDING MARCO 2)
- Tipo de vidrio (ej: TERMOPANEL DVH 3+3/12/6)
- Cantidad

PUERTA:
- Dimensiones (ancho x alto)
- Tipo de puerta
- Material
- Accesorios
- Cantidad

BARANDA:
- Dimensiones
- Material
- Diseño
- Cantidad
```

---

### ✅ FASE 4: Generación de PDF Profesional
**Rama**: `phase-4-pdf-generation`
**Basada en**: `phase-3-items-system`

**Objetivos**:
- Generar PDFs profesionales
- Template personalizable
- Incluir logo y configuración

**Tareas**:
- [ ] Agregar dependencia iText o Apache PDFBox
- [ ] Crear clase `PDFGenerator`
- [ ] Diseñar template del presupuesto:
  - Header con logo y datos de empresa
  - Datos del cliente
  - Tabla de items con especificaciones
  - Subtotal, total (mano de obra + items)
  - Términos y condiciones (footer)
- [ ] Implementar generación de PDF
  - Desde pantalla de presupuesto
  - Guardarlo en almacenamiento
  - Obtener URI para compartir
- [ ] Diseño profesional:
  - Colores personalizados
  - Fuentes adecuadas
  - Márgenes y espaciado
  - Numeración de presupuestos

**Dependencias a agregar**:
```
- com.itextpdf:itext7-core:7.x.x
```

---

### ✅ FASE 5: Compartir y Exportar
**Rama**: `phase-5-sharing`
**Basada en**: `phase-4-pdf-generation`

**Objetivos**:
- Compartir PDF por varios canales
- Exportar archivos
- Integración con apps externas

**Tareas**:
- [ ] Compartir por WhatsApp
  - Intent con PDF
  - Mensaje personalizado
- [ ] Compartir por Email
  - Intent para email
  - Adjuntar PDF
- [ ] Compartir por otros medios (Telegram, etc)
- [ ] Guardar en archivo descargado
- [ ] Generar QR con link al presupuesto (opcional)
- [ ] Pantalla de opciones de compartir
- [ ] Manejo de permisos (READ/WRITE storage)

---

### ✅ FASE 6: Historial y Búsqueda
**Rama**: `phase-6-history-search`
**Basada en**: `phase-5-sharing`

**Objetivos**:
- Gestionar historial de presupuestos
- Búsqueda avanzada
- Filtros y ordenamiento

**Tareas**:
- [ ] Pantalla "Historial" mejorada
  - Lista ordenada por fecha
  - Búsqueda por nombre de cliente
  - Filtros: estado, fecha, monto
  - Ordenamiento: reciente, antiguo, por cliente
- [ ] Acciones rápidas:
  - Ver detalle
  - Duplicar presupuesto
  - Editar
  - Eliminar
  - Compartir
- [ ] Indicadores visuales:
  - Estado del presupuesto
  - Fecha
  - Cliente
  - Monto total

---

### ✅ FASE 7: Configuración Personalizada
**Rama**: `phase-7-settings`
**Basada en**: `phase-6-history-search`

**Objetivos**:
- Configuración de empresa
- Personalización visual
- Logo y colores

**Tareas**:
- [ ] Pantalla "Configuración"
- [ ] Datos de empresa:
  - Nombre empresa
  - RUT/CUIT
  - Dirección
  - Teléfono
  - Email
- [ ] Cargar logo/imagen
  - Selector de fotos
  - Preview
  - Recorte
- [ ] Personalización visual:
  - Color primario
  - Color secundario
  - Fuente (si aplica)
- [ ] Datos de mano de obra:
  - Precio por hora/item base
  - Descuentos
- [ ] Guardado en base de datos
- [ ] Persistencia de configuración

**Dependencias a agregar**:
```
- androidx.lifecycle:lifecycle-viewmodel-compose
```

---

### ✅ FASE 8: Pantalla de Términos y Condiciones
**Rama**: `phase-8-terms-conditions`
**Basada en**: `phase-7-settings`

**Objetivos**:
- Crear sección de términos y condiciones
- Incluir en PDF
- Editable por usuario

**Tareas**:
- [ ] Pantalla de edición de términos
  - Campo de texto editable
  - Presets sugeridos
  - Preview en PDF
- [ ] Almacenar términos en BD
- [ ] Incluir en PDF del presupuesto
  - Sección separada
  - Formato profesional
  - Al final del documento
- [ ] Versionar términos
- [ ] Pantalla de lectura de términos en presupuesto

**Contenido de ejemplo** (en imagen del usuario):
```
- Vanos correctamente recuadrados
- Vanos finalizados de todas las aberturas solicitadas
- Premarcos instalados
- Pared y recuadros de mampostería
- Dinteles y umbrales a nivel
- Libre de andamios y elementos que compliquen el tránsito
- Revoques interiores terminados
- Si el piso no está terminado, debe realizarse una faja de apoyo y nivelación
```

---

### ✅ FASE 9: Pruebas, Refinamiento y Preparación APK
**Rama**: `phase-9-testing-release`
**Basada en**: `phase-8-terms-conditions`

**Objetivos**:
- Tests automatizados
- UI/UX refinement
- Performance optimization
- Preparación para release

**Tareas**:
- [ ] Tests unitarios
  - Cálculos de presupuesto
  - Validaciones
- [ ] Tests de integración
  - Flujos principales
  - BD
- [ ] Tests de UI (Compose)
  - Pantallas principales
- [ ] Optimización
  - Memory leaks
  - Performance
  - Startup time
- [ ] UI/UX refinement
  - Animaciones
  - Feedback visual
  - Accesibilidad
- [ ] Configuración de firma APK
- [ ] Configuración de versión y release notes
- [ ] Documentación de usuario
- [ ] Testing en dispositivos reales
  - Diferentes tamaños de pantalla
  - Diferentes versiones de Android

---

## ESTRUCTURA DE CARPETAS PROPUESTA

```
app/src/main/
├── java/com/example/myapplication/
│   ├── data/
│   │   ├── db/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── BudgetDao.kt
│   │   │   │   ├── ClientDao.kt
│   │   │   │   ├── BudgetItemDao.kt
│   │   │   │   └── SettingsDao.kt
│   │   │   └── entity/
│   │   │       ├── BudgetEntity.kt
│   │   │       ├── ClientEntity.kt
│   │   │       ├── BudgetItemEntity.kt
│   │   │       └── SettingsEntity.kt
│   │   ├── repository/
│   │   │   ├── BudgetRepository.kt
│   │   │   ├── ClientRepository.kt
│   │   │   └── SettingsRepository.kt
│   │   └── model/
│   │       ├── Budget.kt
│   │       ├── Client.kt
│   │       ├── BudgetItem.kt
│   │       ├── ItemType.kt
│   │       └── AppSettings.kt
│   ├── domain/
│   │   ├── usecase/
│   │   │   ├── CreateBudgetUseCase.kt
│   │   │   ├── GetBudgetsUseCase.kt
│   │   │   └── GeneratePdfUseCase.kt
│   │   └── model/
│   ├── presentation/
│   │   ├── viewmodel/
│   │   │   ├── BudgetViewModel.kt
│   │   │   ├── ClientViewModel.kt
│   │   │   ├── SettingsViewModel.kt
│   │   │   └── HomeViewModel.kt
│   │   ├── ui/
│   │   │   ├── screen/
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   ├── BudgetListScreen.kt
│   │   │   │   ├── CreateBudgetScreen.kt
│   │   │   │   ├── BudgetDetailScreen.kt
│   │   │   │   ├── AddItemScreen.kt
│   │   │   │   ├── SettingsScreen.kt
│   │   │   │   ├── TermsScreen.kt
│   │   │   │   └── ShareScreen.kt
│   │   │   ├── component/
│   │   │   │   ├── BudgetCard.kt
│   │   │   │   ├── ItemCard.kt
│   │   │   │   ├── FormFields.kt
│   │   │   │   └── ConfirmDialog.kt
│   │   │   ├── theme/
│   │   │   │   ├── Color.kt (extender con variables)
│   │   │   │   ├── Type.kt
│   │   │   │   └── Theme.kt
│   │   │   └── navigation/
│   │   │       └── NavGraph.kt
│   │   └── mapper/
│   │       ├── BudgetMapper.kt
│   │       └── ItemMapper.kt
│   ├── utils/
│   │   ├── pdf/
│   │   │   ├── PdfGenerator.kt
│   │   │   └── PdfTemplate.kt
│   │   ├── share/
│   │   │   ├── ShareManager.kt
│   │   │   └── ShareIntents.kt
│   │   ├── validation/
│   │   │   └── BudgetValidator.kt
│   │   └── Constants.kt
│   └── MainActivity.kt
└── res/
    ├── drawable/
    │   └── logo_mb.png
    └── values/
        └── strings.xml
```

---

## NOTAS IMPORTANTES

### Consideraciones de Diseño
1. **PDF Template**: Basarse en las imágenes proporcionadas
   - Header con logo MB Cerramientos
   - Tabla de items con especificaciones técnicas
   - Cálculos: Mano de Obra + Items = Total
   - Términos y condiciones al final

2. **Tipos de Items Extensibles**:
   - Usar arquitectura que permita agregar nuevos tipos fácilmente
   - Considerar ItemType como enum que pueda expandirse

3. **Persistencia de Datos**:
   - Room para presupuestos locales
   - Backup automático (opcional para después)

4. **Flujo de Usuario Esperado**:
   ```
   Home → Nuevo Presupuesto → Agregar Cliente → Agregar Items 
   → Revisar → Generar PDF → Compartir
   ```

5. **Permisos Requeridos** (AndroidManifest.xml):
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
   <uses-permission android:name="android.permission.CAMERA" /> (para foto de logo)
   ```

---

## Dependencias por Fase

### Fase 1
- androidx.room (core, ktx, compiler)
- androidx.navigation:navigation-compose

### Fase 4
- com.itextpdf:itext7-core

### Fase 5
- (Usa intents nativos de Android)

### Fase 7
- androidx.compose.material:material-icons-extended (para colores)

---

## Próximos Pasos
1. Crear rama `phase-1-base-structure`
2. Comenzar implementación desde la Fase 1
3. Al completar cada fase, hacer merge a la rama principal de esa fase
4. Cada nueva fase parte de la rama anterior

---

## Estimación de Tiempo (Aproximado)
- Fase 1-2: 2-3 horas
- Fase 3: 1-2 horas  
- Fase 4: 2-3 horas (PDF es complejo)
- Fase 5: 1 hora
- Fase 6: 1-2 horas
- Fase 7: 1-2 horas
- Fase 8: 1 hora
- Fase 9: 2-3 horas (tests + refinement)

**Total estimado**: 12-17 horas de desarrollo
