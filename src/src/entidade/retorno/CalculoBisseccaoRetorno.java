package src.entidade.retorno;

import java.util.ArrayList;
import java.util.List;

public class CalculoBisseccaoRetorno {
    private double raiz;
    private int iteracoes;
    private List<Double> aproximacoes;
    private List<Double> erros;
    private String mensagem;
    private List<String> logs = new ArrayList<>();;

    public CalculoBisseccaoRetorno(double raiz, int iteracoes, List<Double> aproximacoes,
            List<Double> erros, String mensagem, List<String> logs) {
	this.raiz = raiz;
	this.iteracoes = iteracoes;
	this.aproximacoes = aproximacoes;
	this.erros = erros;
	this.mensagem = mensagem;
	this.logs = logs;
	}

    public double getRaiz() {
        return raiz;
    }

    public List<String> getLogs() {
		return logs;
	}

	public void setLogs(List<String> logs) {
		this.logs = logs;
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
}
