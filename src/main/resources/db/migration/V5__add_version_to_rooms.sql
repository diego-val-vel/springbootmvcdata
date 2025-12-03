/*
 * Agrega la columna de version a la tabla rooms para habilitar
 * concurrencia optimista a nivel de entidad RoomEntity.
 */
ALTER TABLE rooms
    ADD COLUMN version integer NOT NULL DEFAULT 0;
