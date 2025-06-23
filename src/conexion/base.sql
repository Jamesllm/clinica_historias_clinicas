CREATE DATABASE historias_clinicas;

CREATE SCHEMA personas;
CREATE SCHEMA clinica;
CREATE SCHEMA pagos;


CREATE TABLE personas.genero (
  idGenero SERIAL PRIMARY KEY,
  nombre VARCHAR(50),
  estado BOOLEAN DEFAULT TRUE
);


CREATE TABLE personas.persona (
  idPersona SERIAL PRIMARY KEY,
  nombre VARCHAR(50),
  apellidoPaterno VARCHAR(50),
  apellidoMaterno VARCHAR(50),
  fechaNacimiento DATE,
  direccion TEXT,
  telefono VARCHAR(20),
  dni VARCHAR(15),
  idGenero INT REFERENCES personas.genero(idGenero)
);

CREATE TABLE clinica.especialidad (
  idEspecialidad SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL
);

CREATE TABLE personas.usuario (
  idUsuario SERIAL PRIMARY KEY,
  idPersona INT REFERENCES personas.persona(idPersona),
  turno VARCHAR(20),
  idEspecialidad INT REFERENCES clinica.especialidad(idEspecialidad)
);

CREATE TABLE personas.paciente (
  idPaciente SERIAL PRIMARY KEY,
  idPersona INT REFERENCES personas.persona(idPersona),
  fechaEntrada TIMESTAMP,
  fechaSalida TIMESTAMP
);

CREATE TABLE clinica.medicamento (
  idMedicamento SERIAL PRIMARY KEY,
  nombre VARCHAR(100),
  presentacion VARCHAR(50)
);

CREATE TABLE clinica.consultaMedica (
  idConsulta SERIAL PRIMARY KEY,
  idPaciente INT REFERENCES personas.paciente(idPaciente),
  idUsuario INT REFERENCES personas.usuario(idUsuario),
  diagnostico TEXT,
  tratamiento TEXT,
  fechaRegistro DATE,
  idMedicamento INT REFERENCES clinica.medicamento(idMedicamento)
);

CREATE TABLE pagos.comprobantePago (
  idComprobantePago SERIAL PRIMARY KEY,
  idConsulta INT REFERENCES clinica.consultaMedica(idConsulta),
  fechaEmision DATE,
  formaPago VARCHAR(20)
);


-- Géneros
INSERT INTO personas.genero(nombre) VALUES('Masculino');
INSERT INTO personas.genero(nombre) VALUES('Femenino');

-- Especialidades
INSERT INTO clinica.especialidad(nombre) VALUES('Medicina General');
INSERT INTO clinica.especialidad(nombre) VALUES('Pediatría');
INSERT INTO clinica.especialidad(nombre) VALUES('Ginecología');

-- Personas
INSERT INTO personas.persona(nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, direccion, telefono, dni, idGenero)
VALUES ('Juan', 'Pérez', 'García', '1990-05-10', 'Av. Siempre Viva 123', '999888777', '12345678', 1);

INSERT INTO personas.persona(nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, direccion, telefono, dni, idGenero)
VALUES ('Ana', 'López', 'Martínez', '1985-08-22', 'Calle Falsa 456', '988777666', '87654321', 2);

-- Usuarios (médicos)
INSERT INTO personas.usuario(idPersona, turno, idEspecialidad)
VALUES (1, 'Mañana', 1);

INSERT INTO personas.usuario(idPersona, turno, idEspecialidad)
VALUES (2, 'Tarde', 2);

-- Pacientes
INSERT INTO personas.paciente(idPersona, fechaEntrada, fechaSalida)
VALUES (1, '2024-06-01 08:00:00', '2024-06-05 10:00:00');

INSERT INTO personas.paciente(idPersona, fechaEntrada, fechaSalida)
VALUES (2, '2024-06-03 09:00:00', '2024-06-04 12:00:00');

-- Medicamentos
INSERT INTO clinica.medicamento(nombre, presentacion)
VALUES ('Paracetamol', 'Tabletas 500mg');

INSERT INTO clinica.medicamento(nombre, presentacion)
VALUES ('Amoxicilina', 'Cápsulas 250mg');

-- Consultas médicas
INSERT INTO clinica.consultaMedica(idPaciente, idUsuario, diagnostico, tratamiento, fechaRegistro, idMedicamento)
VALUES (1, 1, 'Gripe', 'Reposo y paracetamol', '2024-06-01', 1);

INSERT INTO clinica.consultaMedica(idPaciente, idUsuario, diagnostico, tratamiento, fechaRegistro, idMedicamento)
VALUES (2, 2, 'Infección', 'Amoxicilina por 7 días', '2024-06-03', 2);

-- Comprobantes de pago
INSERT INTO pagos.comprobantePago(idConsulta, fechaEmision, formaPago)
VALUES (1, '2024-06-01', 'Efectivo');

INSERT INTO pagos.comprobantePago(idConsulta, fechaEmision, formaPago)
VALUES (2, '2024-06-03', 'Tarjeta');