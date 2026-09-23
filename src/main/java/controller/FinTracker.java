package controller;

import model.Transacao;
import repository.RepositorioGenerico;

import java.util.List;

/**
 * Controlador responsavel pela logica de negocio do FinTrack. Usa
 * internamente um RepositorioGenerico&lt;Transacao&gt; (Generics) para
 * armazenar as transacoes em memoria.
 */
public class FinTracker {
    private final RepositorioGenerico<Transacao> repositorio = new RepositorioGenerico<>();

    public void addTransacao(Transacao transacao) {
        repositorio.adicionar(transacao);
    }

    public void listarTransacoes() {
        List<Transacao> transacoes = repositorio.listarTodos();
        for (int i = 0; i < transacoes.size(); i++) {
            System.out.println(i + 1 + ". " + transacoes.get(i));
        }
    }

    public double calcularSaldo() {
        double saldo = 0;

        for (Transacao transacao : repositorio.listarTodos()) {

            if (transacao.getTipo().equalsIgnoreCase("receita")) {
                saldo += transacao.getValor();
            } else {
                saldo -= transacao.getValor();
            }
        }

        return saldo;
    }

    public void removerTransacao(int indice) {
        if (indice >= 0 && indice < repositorio.total()) {
            repositorio.removerPorIndice(indice);
        }
    }

    public List<Transacao> listarTodas() {
        return repositorio.listarTodos();
    }
}
