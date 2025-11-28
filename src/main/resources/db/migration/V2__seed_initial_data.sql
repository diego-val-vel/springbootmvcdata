/* Datos iniciales para HotelBook */

/* Habitaciones de ejemplo */
INSERT INTO rooms (id, code, name, capacity, base_price_per_night, active, internal_notes)
VALUES
    (1, 'STD-101', 'Standard King 101', 2,  900.00,  TRUE,  'Habitación estándar cercana al elevador'),
    (2, 'STD-102', 'Standard Double 102', 3,  950.00,  TRUE,  'Habitación estándar con dos camas matrimoniales'),
    (3, 'DLX-201', 'Deluxe King 201',    2, 1500.00,  TRUE,  'Vista parcial al mar'),
    (4, 'DLX-202', 'Deluxe Double 202',  4, 1600.00,  TRUE,  'Ideal para familias'),
    (5, 'STE-301', 'Suite Junior 301',   3, 2200.00,  TRUE,  'Suite junior con sala pequeña'),
    (6, 'STE-401', 'Suite Master 401',   4, 3200.00,  TRUE,  'Suite master con vista completa al mar');

/* Huéspedes de ejemplo */
INSERT INTO guests (id, first_name, last_name, email, phone_number, document_number)
VALUES
    (1, 'Ana',    'García',   'ana.garcia@example.com',   '+52 55 1111 1111', 'INE-AG-001'),
    (2, 'Carlos', 'Ramírez',  'carlos.ramirez@example.com','+52 55 2222 2222', 'PAS-CR-002'),
    (3, 'Lucía',  'Martínez', 'lucia.martinez@example.com','+52 55 3333 3333', 'INE-LM-003'),
    (4, 'Diego',  'Santos',   'diego.santos@example.com', '+52 55 4444 4444', 'PAS-DS-004');

/* Reservas de ejemplo */
INSERT INTO bookings (id, check_in_date, check_out_date, total_price, status, room_id, guest_id)
VALUES
    /* Ana se hospeda en una estándar */
    (1, DATE '2025-01-10', DATE '2025-01-12', 1800.00, 'CONFIRMED', 1, 1),

    /* Carlos reserva una deluxe para un fin de semana */
    (2, DATE '2025-02-14', DATE '2025-02-16', 3000.00, 'CONFIRMED', 3, 2),

    /* Lucía reserva una suite junior */
    (3, DATE '2025-03-01', DATE '2025-03-04', 6600.00, 'CREATED',   5, 3),

    /* Diego reserva la suite master para una noche especial */
    (4, DATE '2025-04-20', DATE '2025-04-21', 3200.00, 'CONFIRMED', 6, 4),

    /* Ana regresa y ahora se queda en una deluxe double */
    (5, DATE '2025-05-05', DATE '2025-05-07', 3200.00, 'CANCELLED', 4, 1);
