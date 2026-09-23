package db;

import model.Transacao;


public class TransacaoRegistro {

    private final int id;
    private final Transacao transacao;

    public TransacaoRegistro(int id, Transacao transacao) {
        this.id = id;
        this.transacao = transacao;
    }

    public int getId() {
        return id;
    }

    public Transacao getTransacao() {
        return transacao;
    }
}
