-- Bücher (Schema BOOKS)
INSERT INTO BOOKS.BOOK (id, isbn, title, author, available) VALUES (1, '978-0-13-468599-1', 'Clean Architecture', 'Robert C. Martin', true);
INSERT INTO BOOKS.BOOK (id, isbn, title, author, available) VALUES (2, '978-0-13-235088-4', 'Clean Code', 'Robert C. Martin', true);
INSERT INTO BOOKS.BOOK (id, isbn, title, author, available) VALUES (3, '978-0-201-63361-0', 'Design Patterns', 'Gang of Four', true);
INSERT INTO BOOKS.BOOK (id, isbn, title, author, available) VALUES (4, '978-0-321-12521-7', 'Domain-Driven Design', 'Eric Evans', false);
INSERT INTO BOOKS.BOOK (id, isbn, title, author, available) VALUES (5, '978-0-13-411334-0', 'Get Your Hands Dirty on Clean Architecture', 'Tom Hombergs', true);

-- Nutzer (Schema USERS)
INSERT INTO USERS.APP_USER (id, name, email) VALUES (1, 'Alice Schmidt', 'alice@example.com');
INSERT INTO USERS.APP_USER (id, name, email) VALUES (2, 'Bob Müller', 'bob@example.com');
INSERT INTO USERS.APP_USER (id, name, email) VALUES (3, 'Charlie Weber', 'charlie@example.com');

-- Ausleihen (Schema LOANS) — Buch 4 ist an Alice ausgeliehen
INSERT INTO LOANS.LOAN (id, book_id, user_id, borrow_date, due_date, returned) VALUES (1, 4, 1, '2026-01-20', '2026-02-03', false);

-- ID-Sequenzen nach den manuellen Inserts zurücksetzen
ALTER TABLE BOOKS.BOOK ALTER COLUMN id RESTART WITH 100;
ALTER TABLE USERS.APP_USER ALTER COLUMN id RESTART WITH 100;
ALTER TABLE LOANS.LOAN ALTER COLUMN id RESTART WITH 100;
