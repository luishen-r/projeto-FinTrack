package util;

import exceptions.EntradaInvalidaException;

/**
 * Centraliza as regras de validacao de dados do FinTrack, reaproveitadas
 * tanto pela versao console (app.Main) quanto pela versao grafica
 * (ui.NovaTransacaoController). Manter a validacao em um unico lugar
 * evita duplicacao de regras e facilita a escrita de testes unitarios
 * (ver util.ValidadorTest).
 */
public class Validador {

    private Validador() {
        // classe utilitaria, nao deve ser instanciada
    }

    /**
     * Valida se o valor informado e maior que zero.
     */
    public static void validarValor(double valor) throws EntradaInvalidaException {
        if (valor <= 0) {
            throw new EntradaInvalidaException("Valor deve ser maior que zero.");
        }
    }

    /**
     * Valida se o tipo informado e "receita" ou "despesa" (nao diferencia
     * maiusculas/minusculas).
     */
    public static void validarTipo(String tipo) throws EntradaInvalidaException {
        if (tipo == null
                || (!tipo.equalsIgnoreCase("receita") && !tipo.equalsIgnoreCase("despesa"))) {
            throw new EntradaInvalidaException("Tipo inválido! Digite 'Receita' ou 'Despesa'.");
        }
    }

    /**
     * Valida se a descricao informada nao esta vazia.
     */
    public static void validarDescricao(String descricao) throws EntradaInvalidaException {
        if (descricao == null || descricao.isBlank()) {
            throw new EntradaInvalidaException("Descrição não pode ser vazia.");
        }
    }

    /**
     * Valida se um indice (usado para remover uma transacao da lista) nao e
     * negativo.
     */
    public static void validarIndice(int indice) throws EntradaInvalidaException {
        if (indice < 0) {
            throw new EntradaInvalidaException("O índice não pode ser negativo.");
        }
    }
}
