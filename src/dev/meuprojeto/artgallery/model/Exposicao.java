package dev.meuprojeto.artgallery.model;

import java.util.Vector;

public class Exposicao {
    private String nome;
    private Vector<Obra> obras;

    public Exposicao(String nome) {
        this.nome = nome;
        this.obras = new Vector<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void adicionarObra(Obra obra) {
        if (obra != null && obra.isAtiva()) {
            obras.add(obra);
        }
    }

    public Vector<Obra> listarObras() {
        return obras;
    }

    public int totalObras() {
        return obras.size();
    }
}