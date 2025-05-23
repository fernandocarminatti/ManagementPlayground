CREATE SCHEMA IF NOT EXISTS hospital_finance;

CREATE TABLE hospital_finance.supplier (
    id SERIAL PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    email VARCHAR(50),
    telefone VARCHAR(30)
);

CREATE TABLE hospital_finance.nota_fiscal (
    id SERIAL PRIMARY KEY,
    number VARCHAR(50) NOT NULL,
    issue_date DATE NOT NULL,
    supplier_id INTEGER NOT NULL REFERENCES hospital_finance.supplier(id),
    total_value NUMERIC(15, 2) NOT NULL,
    file_reference VARCHAR(200),
    CONSTRAINT supplier_unique_id UNIQUE (number, supplier_id)
);

CREATE TABLE hospital_finance.boleto (
    id SERIAL PRIMARY KEY,
    nota_fiscal_id INTEGER REFERENCES hospital_finance.nota_fiscal(id),
    due_date DATE NOT NULL,
    total_value NUMERIC(15, 2) NOT NULL,
    typeable_line VARCHAR(100) NOT NULL UNIQUE,
    payment_status SMALLINT NOT NULL,
    file_reference VARCHAR(200)
);

CREATE TABLE hospital_finance.comprovante_pagamento (
    id SERIAL PRIMARY KEY,
    boleto_id INTEGER NOT NULL UNIQUE REFERENCES hospital_finance.boleto(id),
    data_pagamento DATE NOT NULL,
    valor_pago NUMERIC(15, 2) NOT NULL,
    file_reference VARCHAR(200) NOT NULL
);