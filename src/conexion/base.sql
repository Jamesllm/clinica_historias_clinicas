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


INSERT INTO personas.genero(nombre) VALUES('Masculino');
INSERT INTO personas.genero(nombre) VALUES('Femenino');