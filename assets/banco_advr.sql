-- ADVR / EPIC 1 — SQLite, versão 1. Datas de auditoria em UTC.
-- A aplicação executa este arquivo no onCreate() do SQLiteOpenHelper.
PRAGMA foreign_keys = ON;
CREATE TABLE usuarios (
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 nome TEXT NOT NULL CHECK(length(trim(nome)) >= 3),
 email TEXT NOT NULL COLLATE NOCASE UNIQUE,
 cpf TEXT NOT NULL UNIQUE CHECK(length(cpf) = 11 AND cpf NOT GLOB '*[^0-9]*'),
 telefone TEXT NOT NULL DEFAULT '',
 senha_hash TEXT NOT NULL,
 perfil TEXT NOT NULL DEFAULT 'CLIENTE' CHECK(perfil IN ('CLIENTE','ADVOGADO','ADMINISTRADOR')),
 metodo_login TEXT NOT NULL DEFAULT 'LOCAL' CHECK(metodo_login = 'LOCAL'),
 tentativas_login INTEGER NOT NULL DEFAULT 0 CHECK(tentativas_login >= 0),
 bloqueado_ate INTEGER NOT NULL DEFAULT 0,
 criado_em TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now'))
);
CREATE TABLE processos (
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 numero TEXT NOT NULL UNIQUE CHECK(length(trim(numero)) > 0),
 titulo TEXT NOT NULL CHECK(length(trim(titulo)) > 0),
 descricao TEXT NOT NULL DEFAULT '',
 status TEXT NOT NULL DEFAULT 'Em andamento',
 advogado_id INTEGER REFERENCES usuarios(id) ON DELETE RESTRICT,
 atualizado_em TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now'))
);
CREATE TABLE processo_clientes (
 processo_id INTEGER NOT NULL REFERENCES processos(id) ON DELETE CASCADE,
 cliente_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
 PRIMARY KEY (processo_id, cliente_id)
);
CREATE TABLE auditoria (
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 ator_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
 acao TEXT NOT NULL,
 entidade TEXT NOT NULL,
 entidade_id INTEGER NOT NULL,
 detalhes TEXT NOT NULL,
 criado_em TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now'))
);
CREATE INDEX idx_processos_advogado ON processos(advogado_id);
CREATE INDEX idx_vinculos_cliente ON processo_clientes(cliente_id);
CREATE INDEX idx_auditoria_data ON auditoria(criado_em);
PRAGMA user_version = 1;
