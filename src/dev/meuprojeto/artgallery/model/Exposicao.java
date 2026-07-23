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
        Vector<Obra> ativas = new Vector<>();
        for (Obra obra : obras) {
            if (obra.isAtiva()) {
                ativas.add(obra);
            }
        }
        return ativas;
    }
    public boolean contemObra(Obra obra) {
        if (obra == null) {
            return false;
        }
        for (Obra o : obras) {
            if (o.getTitulo().trim().equalsIgnoreCase(obra.getTitulo().trim()) &&
                o.getAutor().trim().equalsIgnoreCase(obra.getAutor().trim())) {
                return true;
            }
        }
        return false;
    }

    public int totalObras() {
        return obras.size();
    }
}