package util;

import exceptions.EntradaInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorTest {

    @Test
    void valorMaiorQueZeroNaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> Validador.validarValor(150.0));
    }

    @Test
    void valorZeroDeveLancarEntradaInvalidaException() {
        assertThrows(EntradaInvalidaException.class, () -> Validador.validarValor(0));
    }

    @Test
    void valorNegativoDeveLancarEntradaInvalidaException() {
        assertThrows(EntradaInvalidaException.class, () -> Validador.validarValor(-10));
    }

    @Test
    void tipoReceitaOuDespesaNaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> Validador.validarTipo("receita"));
        assertDoesNotThrow(() -> Validador.validarTipo("DESPESA"));
    }

    @Test
    void tipoInvalidoDeveLancarEntradaInvalidaException() {
        assertThrows(EntradaInvalidaException.class, () -> Validador.validarTipo("investimento"));
    }

    @Test
    void tipoNuloDeveLancarEntradaInvalidaException() {
        assertThrows(EntradaInvalidaException.class, () -> Validador.validarTipo(null));
    }

    @Test
    void descricaoValidaNaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> Validador.validarDescricao("Salário"));
    }

    @Test
    void descricaoVaziaOuNulaDeveLancarEntradaInvalidaException() {
        assertThrows(EntradaInvalidaException.class, () -> Validador.validarDescricao(""));
        assertThrows(EntradaInvalidaException.class, () -> Validador.validarDescricao("   "));
        assertThrows(EntradaInvalidaException.class, () -> Validador.validarDescricao(null));
    }

    @Test
    void indiceNaoNegativoNaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> Validador.validarIndice(0));
        assertDoesNotThrow(() -> Validador.validarIndice(5));
    }

    @Test
    void indiceNegativoDeveLancarEntradaInvalidaException() {
        assertThrows(EntradaInvalidaException.class, () -> Validador.validarIndice(-1));
    }
}
