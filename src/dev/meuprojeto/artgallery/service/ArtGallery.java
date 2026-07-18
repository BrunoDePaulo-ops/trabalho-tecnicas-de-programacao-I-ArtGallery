package dev.meuprojeto.artgallery.service;

import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import dev.meuprojeto.artgallery.model.Obra;
import dev.meuprojeto.artgallery.model.Avaliacao;
import dev.meuprojeto.artgallery.model.Exposicao;
import dev.meuprojeto.artgallery.repository.IRepositoryObra;
import dev.meuprojeto.artgallery.exception.ObraJaCadastradaException;
import dev.meuprojeto.artgallery.exception.ObraNaoEncontradaException;
import dev.meuprojeto.artgallery.exception.NotaInvalidaException;

public class ArtGallery implements IArtGallery {
    private IRepositoryObra repository;

    public ArtGallery(IRepositoryObra repository) {
        this.repository = repository;
    }
    @Override
    public void criarExposicao(String nome) {
        repository.criarExposicao(nome);
    }

    @Override
    public void publicObra(Obra obra) throws ObraJaCadastradaException {
        repository.cadastrar(obra);
    }

    @Override
    public void removerObra(String titulo) throws ObraNaoEncontradaException {
        Obra obra = repository.buscar(titulo);
        if (obra == null) {
            throw new ObraNaoEncontradaException("Obra com título '" + titulo + "' não encontrada.");
        }
        repository.remover(titulo);
    }

    @Override
    public void avaliarObra(String titulo, Avaliacao avaliacao) throws ObraNaoEncontradaException, NotaInvalidaException {
        Obra obra = repository.buscar(titulo);
        if (obra == null) {
            throw new ObraNaoEncontradaException("Obra com título '" + titulo + "' não encontrada.");
        }
        if (!obra.isAtiva()) {
            throw new ObraNaoEncontradaException("Obra '" + titulo + "' está inativa e não pode receber avaliações.");
        }
        /*obra.adicionarAvaliacao(avaliacao);*/
        repository.adicionarAvaliacao(obra.getId(), avaliacao);
    }

    @Override
    public Vector<Obra> listarObras() {
        Vector<Obra> todas = repository.listar();
        Vector<Obra> ativas = new Vector<>();
        for (Obra obra : todas) {
            if (obra.isAtiva()) {
                ativas.add(obra);
            }
        }
        return ativas;
    }

    @Override
    public Vector<Obra> buscarPorAutor(String autor) {
        Vector<Obra> todas = repository.listar();
        Vector<Obra> resultado = new Vector<>();
        for (Obra obra : todas) {
            if (obra.isAtiva() && obra.getAutor().equalsIgnoreCase(autor)) {
                resultado.add(obra);
            }
        }
        return resultado;
    }

    @Override
    public Vector<Obra> topObras() {
        Vector<Obra> ativas = listarObras();
        Vector<Obra> ordenadas = new Vector<>(ativas);
        
        Collections.sort(ordenadas, new Comparator<Obra>() {
            @Override
            public int compare(Obra o1, Obra o2) {
                double media1 = o1.mediaAvaliacoes();
                double media2 = o2.mediaAvaliacoes();
                if (media2 > media1) return 1;
                if (media2 < media1) return -1;
                return 0;
            }
        });
        return ordenadas;
    }
    
    @Override
    public Vector<Exposicao> listarExposicoes() {
        return repository.listarExposicoes();
    }
    
    @Override
    public void adicionarObraAExposicao(String nomeExposicao, Obra obra) {
        repository.adicionarObraAExposicao(nomeExposicao, obra);
    }

    @Override
    public void removerExposicao(String nome) {
        repository.removerExposicao(nome);
    }

    @Override
    public void removerObraDaExposicao(String nomeExposicao, String tituloObra) {
        repository.removerObraDaExposicao(nomeExposicao, tituloObra);
    }
    
    @Override
    public Vector<Obra> listarObrasDaExposicao(String nomeExposicao) {
        return repository.listarObrasDaExposicao(nomeExposicao);
    }
    
    @Override
        public void atualizarObra(int id, Obra obra) throws ObraNaoEncontradaException {
        repository.atualizar(id, obra);
    }

    @Override
    public Obra buscar(String titulo) {
        return repository.buscar(titulo);
    }
}