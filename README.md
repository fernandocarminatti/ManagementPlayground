# Hospital Finance Manager

## Overview

This is a small finance management system designed for a hospital setting. The system allows registering invoices ("Notas Fiscais") with optional payment slips ("Boletos") and tracking payment receipts ("Comprovantes de Pagamento"). Built using Java, Spring Framework, Thymeleaf, and PostgreSQL, it follows an MVC architecture.

The project serves as a scalable baseline and playground for future feature additions. It is not intended for real production use or legal compliance.

---

## Technology Stack

- Java 17+
- Spring Boot
- Thymeleaf (MVC views)
- PostgreSQL (database)
- Flyway (database migration)
- Maven or Gradle (build tool)

---

## Database Design

- Schema: `hospital_finance`
- Tables:
    - `supplier` — Suppliers of invoices
    - `nota_fiscal` — Invoices linked to suppliers
    - `boleto` — Payment slips related to invoices
    - `comprovante_pagamento` — Payment receipts linked to boletos

Payment status is stored as an integer enum.

---

## Setup Instructions

1. Install PostgreSQL and create a database.
2. Configure database connection in `application.yml`.
3. Ensure Flyway migration files are placed under `src/main/resources/db/migration`.
4. Run the application; Flyway will create schema and tables automatically.
5. Access the web interface served by Spring Boot.

---

## Notes

- All database objects are created in the `hospital_finance` schema.
- Flyway handles database versioning and migration.
- The system currently supports basic invoice and payment tracking.
- Future features will be added using additional schemas or tables as needed.

---

## Running

Use standard Spring Boot commands to build and run:

```bash
./mvnw spring-boot:run
# or
./gradlew bootRun
```

## License

- This project is for personal use and learning only. Not intended for production or distribution.