package dev.meuprojeto.artgallery.service;

import java.util.Vector;
import dev.meuprojeto.artgallery.model.Obra;
import dev.meuprojeto.artgallery.model.Avaliacao;
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
}