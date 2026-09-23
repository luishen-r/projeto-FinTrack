package model;

import java.time.LocalDate;

public class TransacaoMensal extends Transacao {
    private int mes;

    public TransacaoMensal(int mes, String descricao, double valor, String tipo) {
        super(descricao, valor, tipo);
        this.mes = mes;
    }

    public TransacaoMensal(int mes, String descricao, double valor, String tipo, LocalDate data) {
        super(descricao, valor, tipo, data);
        this.mes = mes;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    @Override
    public String toString() {
        return super.toString() + "\nMes: " + mes;
    }
}
