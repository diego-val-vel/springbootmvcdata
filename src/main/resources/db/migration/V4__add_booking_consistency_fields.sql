/*
 * Agrega columnas de apoyo para flujos transaccionales de reservas.
 * - guests.confirmed_bookings_count: número de reservas confirmadas por huésped.
 * - rooms.last_booking_date: fecha de la última reserva confirmada por habitación.
 */
alter table guests
    add column confirmed_bookings_count integer not null default 0;

alter table rooms
    add column last_booking_date date;
