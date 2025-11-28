/* Creación del esquema inicial de HotelBook */

/* Tabla de habitaciones */
CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    capacity INTEGER NOT NULL,
    base_price_per_night NUMERIC(10,2) NOT NULL,
    active BOOLEAN NOT NULL,
    internal_notes TEXT,
    CONSTRAINT uq_rooms_code UNIQUE (code)
);

/* Tabla de huéspedes */
CREATE TABLE guests (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(150) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    phone_number VARCHAR(50),
    document_number VARCHAR(100),
    CONSTRAINT uq_guests_email UNIQUE (email)
);

/* Tabla de reservas */
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_price NUMERIC(12,2) NOT NULL,
    status VARCHAR(50) NOT NULL,

    room_id BIGINT NOT NULL,
    guest_id BIGINT NOT NULL,

    CONSTRAINT fk_bookings_room
        FOREIGN KEY (room_id)
        REFERENCES rooms (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_bookings_guest
        FOREIGN KEY (guest_id)
        REFERENCES guests (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
