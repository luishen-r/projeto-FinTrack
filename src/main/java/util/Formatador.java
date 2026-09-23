package util;

import java.text.NumberFormat;
import java.util.Locale;

public class Formatador {
    public static String formatarMoeda (double valor) {
        NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR" ));
        return formatador.format(valor);
    }
}
