package dev.meuprojeto.artgallery.model;

public class Modelagem3D extends Obra {
    private int numeroPoligonos;
    private String engine;

    public Modelagem3D(String titulo, String autor, int numeroPoligonos, String engine) {
        super(titulo, autor);
        this.numeroPoligonos = numeroPoligonos;
        this.engine = engine;
    }

    public int getNumeroPoligonos() {
        return numeroPoligonos;
    }

    public void setNumeroPoligonos(int numeroPoligonos) {
        this.numeroPoligonos = numeroPoligonos;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    @Override
    public String exibirDetalhes() {
        return "Título: " + getTitulo() +
               " | Autor: " + getAutor() +
               " | Tipo: Modelagem 3D" +
               " | Polígonos: " + numeroPoligonos +
               " | Engine: " + engine;
    }
}
