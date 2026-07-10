package dev.meuprojeto.artgallery.model;

public class ArteGenerativa extends Obra {
    private String algoritmo;
    private long seed;

    public ArteGenerativa(String titulo, String autor, String algoritmo, long seed) {
        super(titulo, autor);
        this.algoritmo = algoritmo;
        this.seed = seed;
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public void setAlgoritmo(String algoritmo) {
        this.algoritmo = algoritmo;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public String exibirDetalhes() {
        return "Título: " + getTitulo() +
               " | Autor: " + getAutor() +
               " | Tipo: Arte Generativa" +
               " | Algoritmo: " + algoritmo +
               " | Seed: " + seed;
    }
}
