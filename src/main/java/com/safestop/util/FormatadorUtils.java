package com.safestop.util;

public class FormatadorUtils {

    /**
     * Aplica máscara de telefone (XX) XXXXX-XXXX ou (XX) XXXX-XXXX.
     */
    public static String formatarTelefone(String telefone) {
        if (telefone == null || telefone.isEmpty()) {
            return telefone;
        }

        String digitos = telefone.replaceAll("\\D", "");

        if (digitos.length() == 11) {
            return String.format("(%s) %s-%s",
                    digitos.substring(0, 2),
                    digitos.substring(2, 7),
                    digitos.substring(7));
        } else if (digitos.length() == 10) {
            return String.format("(%s) %s-%s",
                    digitos.substring(0, 2),
                    digitos.substring(2, 6),
                    digitos.substring(6));
        } else {
            return telefone;
        }
    }
}