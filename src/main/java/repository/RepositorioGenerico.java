package repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Repositorio generico em memoria, capaz de armazenar qualquer tipo de
 * elemento (T). E usado pelo controller.FinTracker para guardar as
 * transacoes cadastradas na versao console/GUI antes/durante a
 * persistencia em banco de dados.
 *
 **/
public class RepositorioGenerico<T> {

    private final List<T> elementos = new ArrayList<>();

    /**
     * Adiciona um unico elemento ao repositorio.
     */
    public void adicionar(T elemento) {
        elementos.add(elemento);
    }

    /**
     * Adiciona todos os elementos de uma colecao de T ou de qualquer
     * subtipo de T (ex.: List&lt;TransacaoMensal&gt; dentro de um
     * RepositorioGenerico&lt;Transacao&gt;).
     */
    public void adicionarTodos(Collection<? extends T> novosElementos) {
        elementos.addAll(novosElementos);
    }

    /**
     * Remove um elemento especifico do repositorio.
     *
     * @return true se o elemento existia e foi removido.
     */
    public boolean remover(T elemento) {
        return elementos.remove(elemento);
    }

    /**
     * Remove o elemento na posicao indicada.
     *
     * @return o elemento removido.
     */
    public T removerPorIndice(int indice) {
        return elementos.remove(indice);
    }

    /**
     * Retorna uma copia da lista de todos os elementos armazenados,
     * preservando a ordem de inclusao.
     */
    public List<T> listarTodos() {
        return new ArrayList<>(elementos);
    }

    /**
     * Copia todos os elementos deste repositorio para qualquer colecao
     * de destino que aceite T ou um supertipo de T (ex.: copiar um
     * RepositorioGenerico&lt;TransacaoMensal&gt; para uma
     * List&lt;Transacao&gt;).
     */
    public void copiarPara(Collection<? super T> destino) {
        destino.addAll(elementos);
    }

    public T obter(int indice) {
        return elementos.get(indice);
    }

    public int total() {
        return elementos.size();
    }

    public boolean estaVazio() {
        return elementos.isEmpty();
    }
}
