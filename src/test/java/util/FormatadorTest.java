package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FormatadorTest {

    @Test
    void formatarMoedaDeveConterOSimboloDeRealEOValorFormatado() {
        String resultado = Formatador.formatarMoeda(1500.5);

        // O simbolo de moeda em pt-BR pode variar entre JDKs ("R$" ou "R$\u00A0"),
        // por isso verificamos apenas as partes essenciais do resultado.
        assertTrue(resultado.contains("R$"));
        assertTrue(resultado.contains("1.500,50"));
    }

    @Test
    void formatarMoedaDeveFuncionarComValorZero() {
        String resultado = Formatador.formatarMoeda(0.0);

        assertTrue(resultado.contains("R$"));
        assertTrue(resultado.contains("0,00"));
    }

    @Test
    void formatarMoedaDeveFuncionarComValorNegativo() {
        String resultado = Formatador.formatarMoeda(-250.0);

        assertTrue(resultado.contains("250,00"));
        assertTrue(resultado.contains("-"));
    }
}
