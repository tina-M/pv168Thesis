CREATE TABLE student (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(30),
    surname VARCHAR(30)
);