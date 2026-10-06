package vista.swing;

import modelo.Color;

final class Paleta {

    private Paleta() {
    }

    static java.awt.Color awt(Color color) {
        return switch (color) {
            case ROJO ->
                new java.awt.Color(0xD6, 0x33, 0x33);
            case AZUL ->
                new java.awt.Color(0x2F, 0x6B, 0xD6);
            case AMARILLO ->
                new java.awt.Color(0xE8, 0xC1, 0x2B);
            case VERDE ->
                new java.awt.Color(0x3F, 0xA7, 0x4F);
        };
    }
}
