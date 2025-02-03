FinancialTrack es una aplicación diseñada para ayudar a los usuarios a gestionar sus finanzas personales.
El proyecto ofrece funcionalidades para el control de ingresos, gastos, presupuestos y categorías, 
permitiendo a los usuarios realizar un seguimiento efectivo de sus transacciones financieras. 
Los usuarios pueden asignar transacciones a categorías específicas y establecer límites de presupuesto en diferentes 
periodos para controlar sus gastos.

La aplicación utiliza autenticación y autorización mediante Spring Security, asegurando que cada usuario 
solo pueda gestionar sus propios datos. Además, se implementan excepciones personalizadas para manejar errores
específicos de la lógica de negocio.


El propósito de este proyecto es proporcionar una solución integral para la gestión financiera personal.

Las principales funcionalidades incluyen:

Gestión de categorías: Definir categorías para clasificar ingresos y gastos.
Control de transacciones: Registrar y actualizar transacciones financieras.
Establecimiento de presupuestos: Definir límites de gastos por categoría para un período de tiempo determinado.
Análisis financiero: Consultar estadísticas, como el porcentaje de presupuesto consumido.



# Tabla de Endpoints del proyecto

| Método | Endpoint                              | Descripción                                                                      |
|--------|---------------------------------------|----------------------------------------------------------------------------------|
| GET    | /api/v1/categories                    | Obtiene todas las categorías del usuario autenticado por tipo (ingresos/gastos). |
| POST   | /api/v1/categories                    | Crea una nueva categoría para el usuario autenticado.                            |
| PUT    | /api/v1/categories/{id}               | Actualiza una categoría específica del usuario.                                  |
| DELETE | /api/v1/categories/{id}               | Elimina una categoría del usuario autenticado si no tiene dependencias.          |
| GET    | /api/v1/categories/all                | Obtiene todas las categorías del usuario autenticado.                            |
| GET    | /api/v1/categories/search             | Busca una categoría por nombre.                                                  |
| GET    | /api/v1/transactions                  | Obtiene todas las transacciones del usuario autenticado.                         |
| POST   | /api/v1/transactions                  | Crea una nueva transacción para el usuario autenticado.                          |
| PUT    | /api/v1/transactions/{id}             | Actualiza una transacción existente del usuario.                                 |
| DELETE | /api/v1/transactions/{id}             | Elimina una transacción del usuario autenticado.                                 |
| GET    | /api/v1/budgets                       | Obtiene todos los presupuestos del usuario autenticado.                          |
| POST   | /api/v1/budgets                       | Crea o actualiza un presupuesto.                                                 |
| GET    | /api/v1/budgets/completion/{budgetId} | Obtiene el porcentaje de cumplimiento del presupuesto.                           |
| DELETE | /api/v1/budgets/{id}                  | Elimina un presupuesto específico.                                               |


El sistema utiliza Spring Security para la autenticación y autorización. Solo los usuarios autenticados pueden acceder a sus recursos,
como categorías, transacciones y presupuestos. 
Cada endpoint verifica el correo electrónico del usuario autenticado mediante SecurityContextHolder.


# Entidades principales del proyecto

User: Representa a los usuarios que utilizan la aplicación.
Category: Define categorías de transacciones (por ejemplo, "Alquiler", "Salario").
Transaction: Registra detalles de ingresos o gastos realizados por un usuario.
Budget: Establece límites de gastos por categoría y período de tiempo.
Authentication: Maneja la autenticación y el control de acceso a través de tokens JWT.

# Tecnologías utilizadas
Java 17	
Spring Boot	Framework 
Spring Security	Framework 
JWT (JSON Web Tokens)	
Spring Data JPA	
Hibernate	
PostgreSQL
Maven
JUnit
Mockito
Swagger / Springdoc OpenAPI (Documentación de API)
