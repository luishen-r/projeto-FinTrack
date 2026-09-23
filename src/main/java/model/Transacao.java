package model;

import java.time.LocalDate;

public class Transacao {
    private String descricao;
    private double valor;
    private String tipo;
    private LocalDate data;


    public Transacao(String descricao, double valor, String tipo) {
        this(descricao, valor, tipo, LocalDate.now());
    }

    /**
     * Construtor completo, incluindo a data da transacao (coluna "data"
     * da tabela "transacoes" no banco de dados).
     */
    public Transacao(String descricao, double valor, String tipo, LocalDate data) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Tipo: " + tipo +
                "\nDescricao: " + descricao +
                "\nValor: " + util.Formatador.formatarMoeda(valor) +
                "\nData: " + data;
    }
}
