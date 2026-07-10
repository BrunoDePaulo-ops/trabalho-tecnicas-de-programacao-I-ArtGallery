package dev.meuprojeto.artgallery.repository;

import java.util.Vector;

import dev.meuprojeto.artgallery.model.Exposicao;
import dev.meuprojeto.artgallery.model.Obra;
import dev.meuprojeto.artgallery.exception.ObraJaCadastradaException;
import dev.meuprojeto.artgallery.exception.ObraNaoEncontradaException;

public interface IRepositoryObra {
    void cadastrar(Obra obra) throws ObraJaCadastradaException;
    Obra buscar(String titulo);
    void atualizar(Obra obra) throws ObraNaoEncontradaException;
    void remover(String titulo);
    Vector<Obra> listar();
    void criarExposicao(String nome);
    Vector<Exposicao> listarExposicoes();
    void adicionarObraAExposicao(String nomeExposicao, Obra obra);
    void removerExposicao(String nome);
}