package dev.meuprojeto.artgallery.service;

import java.util.Vector;
import dev.meuprojeto.artgallery.model.Obra;
import dev.meuprojeto.artgallery.model.Avaliacao;
import dev.meuprojeto.artgallery.model.Exposicao;
import dev.meuprojeto.artgallery.exception.ObraJaCadastradaException;
import dev.meuprojeto.artgallery.exception.ObraNaoEncontradaException;
import dev.meuprojeto.artgallery.exception.NotaInvalidaException;

public interface IArtGallery {
    void publicObra(Obra obra) throws ObraJaCadastradaException;
    void removerObra(String titulo) throws ObraNaoEncontradaException;
    void avaliarObra(String titulo, Avaliacao avaliacao) throws ObraNaoEncontradaException, NotaInvalidaException;
    Vector<Obra> listarObras();
    Vector<Obra> buscarPorAutor(String autor);
    Vector<Obra> topObras();
    void criarExposicao(String nome);
    Vector<Exposicao> listarExposicoes();
    void adicionarObraAExposicao(String nomeExposicao, Obra obra);
    void removerExposicao(String nome);
    void removerObraDaExposicao(String nomeExposicao, String tituloObra);
    Vector<Obra> listarObrasDaExposicao(String nomeExposicao);
    void atualizarObra(int id, Obra obra) throws ObraNaoEncontradaException;
    Obra buscar(String titulo);
    Vector<Avaliacao> listarAvaliacoes(String tituloObra);
}