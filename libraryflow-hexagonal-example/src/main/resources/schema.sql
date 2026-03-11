-- Separate Schemas für die hexagonale Architektur: Jeder Driven Adapter nutzt sein eigenes Schema.
CREATE SCHEMA IF NOT EXISTS BOOKS;
CREATE SCHEMA IF NOT EXISTS USERS;
CREATE SCHEMA IF NOT EXISTS LOANS;
