-- Inserir Usuários (um admin e dois funcionários)
INSERT INTO Usuario (nome, email, telefone, senha, is_admin, ativo)
VALUES ('Admin Dono', 'admin@safestop.com', '41999999999', 'admin123', true, true);

INSERT INTO Usuario (nome, email, telefone, senha, is_admin, ativo)
VALUES ('Funcionario Zé', 'ze@safestop.com', '41888888888', 'func123', false, true);

-- Inserir Vagas (mais vagas para testar filtros e criação)
INSERT INTO Vaga (numero_vaga, tipo, ativo) VALUES ('A-001', 'COMUM', true);
INSERT INTO Vaga (numero_vaga, tipo, ativo) VALUES ('A-002', 'COMUM', true);
INSERT INTO Vaga (numero_vaga, tipo, ativo) VALUES ('A-003', 'COMUM', true);
INSERT INTO Vaga (numero_vaga, tipo, ativo) VALUES ('B-001', 'MOTO', true);
INSERT INTO Vaga (numero_vaga, tipo, ativo) VALUES ('B-002', 'MOTO', true);
INSERT INTO Vaga (numero_vaga, tipo, ativo) VALUES ('C-001', 'PCD', true);
INSERT INTO Vaga (numero_vaga, tipo, ativo) VALUES ('C-002', 'PCD', true);
INSERT INTO Vaga (numero_vaga, tipo, ativo) VALUES ('D-001', 'COMUM', true);
INSERT INTO Vaga (numero_vaga, tipo, ativo) VALUES ('D-002', 'COMUM', false); -- Vaga inativa para teste

-- Inserir Clientes (mais clientes)
INSERT INTO Cliente (nome, telefone) VALUES ('João Pereira', '(41) 99999-1111');
INSERT INTO Cliente (nome, telefone) VALUES ('Maria Souza', '(41) 98888-2222');
INSERT INTO Cliente (nome, telefone) VALUES ('Carlos Lima', '(41) 97777-3333');
INSERT INTO Cliente (nome, telefone) VALUES ('Ana Beatriz', '(41) 96666-4444');
INSERT INTO Cliente (nome, telefone) VALUES ('Ricardo Alves', '(41) 95555-5555');

-- Inserir Veículos (FORMATO NOVO: sem marca, cor, ano)
INSERT INTO Veiculo (placa, modelo, id_cliente) VALUES ('ABC1D23', 'Mobi', 1);
INSERT INTO Veiculo (placa, modelo, id_cliente) VALUES ('XYZ4E56', 'Onix', 2);
INSERT INTO Veiculo (placa, modelo, id_cliente) VALUES ('BRA2F10', 'Polo', 3);
INSERT INTO Veiculo (placa, modelo, id_cliente) VALUES ('GEM0I21', 'Creta', 4);
INSERT INTO Veiculo (placa, modelo, id_cliente) VALUES ('SQL8A99', 'Corolla', 5);

-- Inserir Tickets (3 ABERTOS e 1 FECHADO para histórico)
-- IDs dos Veículos: 1, 2, 3, 4, 5
-- IDs das Vagas: 1 (A-001), 2 (A-002), 3 (A-003), 4 (B-001), 5 (B-002), 6 (C-001), 7 (C-002), 8 (D-001), 9 (D-002)

-- Vaga A-001 Ocupada
INSERT INTO Ticket (id_veiculo, id_vaga, horario_entrada, status)
VALUES (1, 1, '2025-11-09 12:00:00', 'ABERTO');

-- Vaga A-002 Ocupada
INSERT INTO Ticket (id_veiculo, id_vaga, horario_entrada, status)
VALUES (2, 2, '2025-11-09 14:30:00', 'ABERTO');

-- Vaga B-001 (Moto) Ocupada
INSERT INTO Ticket (id_veiculo, id_vaga, horario_entrada, status)
VALUES (3, 4, '2025-11-09 15:00:00', 'ABERTO');

-- Ticket FECHADO (para histórico, não deve aparecer no dashboard)
INSERT INTO Ticket (id_veiculo, id_vaga, horario_entrada, horario_saida, valor, status)
VALUES (4, 6, '2025-11-08 10:00:00', '2025-11-08 11:30:00', 15.00, 'FECHADO');


-- Define os valores padrão do sistema (ID=1)
INSERT INTO Configuracao (id, minutos_tolerancia, valor_por_hora)
VALUES (1, 15, 7.50);