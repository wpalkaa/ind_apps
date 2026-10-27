package pl.edu.ug.cmms;

import java.time.LocalDate;
import java.util.Objects;

public record Machine(
        String id,
        String name,
        MachineType type,
        String hall,
        LocalDate commissionedAt
) {
    public enum MachineType {
        CNC_LATHE,
        CONVEYOR,
        MILLING_MACHINE,
        WELDING_ROBOT,
        INJECTION_MOLDER,
        HYDRAULIC_PRESS,
        COMPRESSOR
    }

    public Machine {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identyfikator maszyny nie może być pusty.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nazwa maszyny nie może być pusta.");
        }
        if (hall == null || hall.isBlank()) {
            throw new IllegalArgumentException("Hala nie może być pusta.");
        }
        Objects.requireNonNull(type, "Typ maszyny nie może być null.");
        Objects.requireNonNull(commissionedAt, "Data uruchomienia nie może być null.");
    }
}