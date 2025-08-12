package src.entidade;

import java.util.List;

public class CalculoBisseccao {
    private Long id;
    private double[] coeficientes;
    private double intervaloA;
    private double intervaloB;
    private int precisao;  // expoente para 10^(-precisao)
    private double raiz;
    private int iteracoes;
    private List<Double> aproximacoes;
    private List<Double> erros;
    private String mensagem;

    public CalculoBisseccao(double[] coeficientes, double intervaloA, double intervaloB, int precisao,
                            double raiz, int iteracoes, List<Double> aproximacoes, List<Double> erros, String mensagem) {
        this.coeficientes = coeficientes;
        this.intervaloA = intervaloA;
        this.intervaloB = intervaloB;
        this.precisao = precisao;
        this.raiz = raiz;
        this.iteracoes = iteracoes;
        this.aproximacoes = aproximacoes;
        this.erros = erros;
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double[] getCoeficientes() {
        return coeficientes;
    }

    public void setCoeficientes(double[] coeficientes) {
        this.coeficientes = coeficientes;
    }

    public double getIntervaloA() {
        return intervaloA;
    }

    public void setIntervaloA(double intervaloA) {
        this.intervaloA = intervaloA;
    }

    public double getIntervaloB() {
        return intervaloB;
    }

    public void setIntervaloB(double intervaloB) {
        this.intervaloB = intervaloB;
    }

    public int getPrecisao() {
        return precisao;
    }

    public void setPrecisao(int precisao) {
        this.precisao = precisao;
    }

    public double getRaiz() {
        return raiz;
    }

    public void setRaiz(double raiz) {
        this.raiz = raiz;
    }

    public int getIteracoes() {
        return iteracoes;
    }

    public void setIteracoes(int iteracoes) {
        this.iteracoes = iteracoes;
    }

    public List<Double> getAproximacoes() {
        return aproximacoes;
    }

    public void setAproximacoes(List<Double> aproximacoes) {
        this.aproximacoes = aproximacoes;
    }

    public List<Double> getErros() {
        return erros;
    }

    public void setErros(List<Double> erros) {
        this.erros = erros;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    
    // Retorna a precisão real calculada: 10^(-precisao)
    public double getPrecisaoReal() {
        return Math.pow(10, -this.precisao);
    }
}
