package dev.meuprojeto.artgallery.model;

public class PinturaDigital extends Obra {
    private String resolucao;
    private String softwareUtilizado;

    public PinturaDigital(String titulo, String autor, String resolucao, String softwareUtilizado) {
        super(titulo, autor);
        this.resolucao = resolucao;
        this.softwareUtilizado = softwareUtilizado;
    }

    public String getResolucao() {
        return resolucao;
    }

    public void setResolucao(String resolucao) {
        this.resolucao = resolucao;
    }

    public String getSoftwareUtilizado() {
        return softwareUtilizado;
    }

    public void setSoftwareUtilizado(String softwareUtilizado) {
        this.softwareUtilizado = softwareUtilizado;
    }

    @Override
    public String exibirDetalhes() {
        return "Título: " + getTitulo() +
               " | Autor: " + getAutor() +
               " | Tipo: Pintura Digital" +
               " | Resolução: " + resolucao +
               " | Software: " + softwareUtilizado;
    }
}
