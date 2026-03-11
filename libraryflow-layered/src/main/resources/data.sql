-- Bücher
INSERT INTO book (id, isbn, title, author, available) VALUES (1, '978-0-13-468599-1', 'Clean Architecture', 'Robert C. Martin', true);
INSERT INTO book (id, isbn, title, author, available) VALUES (2, '978-0-13-235088-4', 'Clean Code', 'Robert C. Martin', true);
INSERT INTO book (id, isbn, title, author, available) VALUES (3, '978-0-201-63361-0', 'Design Patterns', 'Gang of Four', true);
INSERT INTO book (id, isbn, title, author, available) VALUES (4, '978-0-321-12521-7', 'Domain-Driven Design', 'Eric Evans', false);
INSERT INTO book (id, isbn, title, author, available) VALUES (5, '978-0-13-411334-0', 'Get Your Hands Dirty on Clean Architecture', 'Tom Hombergs', true);

-- Nutzer
INSERT INTO app_user (id, name, email) VALUES (1, 'Alice Schmidt', 'alice@example.com');
INSERT INTO app_user (id, name, email) VALUES (2, 'Bob Müller', 'bob@example.com');
INSERT INTO app_user (id, name, email) VALUES (3, 'Charlie Weber', 'charlie@example.com');

-- Ausleihen (Buch 4 ist ausgeliehen)
INSERT INTO loan (id, book_id, user_id, borrow_date, due_date, returned) VALUES (1, 4, 1, '2026-01-20', '2026-02-03', false);

-- ID-Sequenzen nach den manuellen Inserts zurücksetzen
ALTER TABLE book ALTER COLUMN id RESTART WITH 100;
ALTER TABLE app_user ALTER COLUMN id RESTART WITH 100;
ALTER TABLE loan ALTER COLUMN id RESTART WITH 100;
