/*
 * Ajuste de sequences después de insertar datos semilla con ids fijos.
 * Esto evita errores de "duplicate key value violates unique constraint"
 * cuando se insertan nuevos registros desde la aplicación.
 */

SELECT setval(
    pg_get_serial_sequence('rooms', 'id'),
    COALESCE((SELECT MAX(id) FROM rooms), 0)
);

SELECT setval(
    pg_get_serial_sequence('guests', 'id'),
    COALESCE((SELECT MAX(id) FROM guests), 0)
);

SELECT setval(
    pg_get_serial_sequence('bookings', 'id'),
    COALESCE((SELECT MAX(id) FROM bookings), 0)
);
