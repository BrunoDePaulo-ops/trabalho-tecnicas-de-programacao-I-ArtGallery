package dev.meuprojeto.artgallery.model;

import java.util.Vector;

public abstract class Obra {
    private String titulo;
    private String autor;
    private boolean ativa;
    private Vector<Avaliacao> avaliacoes;

    public Obra(String titulo, String autor){
        this.titulo = titulo;
        this.autor = autor;
        this.ativa = true;
        this.avaliacoes = new Vector<>();
    }
    public String getTitulo(){
        return titulo;
    }
    public void setTitulo(String titulo){
        this.titulo = titulo;
    }
    public String getAutor(){
        return autor;
    }
    public void setAutor(String autor){
        this.autor = autor;
    }
    public boolean isAtiva(){
        return ativa;
    }
    public void setAtiva(boolean ativa){
        this.ativa = ativa;
    }
    public void adicionarAvaliacao(Avaliacao avaliacao){
        if (avaliacao != null){
            avaliacoes.add(avaliacao);
        }
    }
    public double mediaAvaliacoes(){
        if (avaliacoes.isEmpty()){
            return 0;
        }
        int soma = 0;
        for(Avaliacao a : avaliacoes){
            soma += a.getNota();
        }
        return (double) soma / avaliacoes.size();
    }
    public abstract String exibirDetalhes();
}