package src.entidade.retorno;

import java.util.List;

public class CalculoBisseccaoRetorno {
    private double raiz;
    private int iteracoes;
    private List<Double> aproximacoes;
    private List<Double> erros;
    private String mensagem;
    private List<LogBisseccao> logs;

    public CalculoBisseccaoRetorno(double raiz, int iteracoes, List<Double> aproximacoes,
            List<Double> erros, String mensagem, List<LogBisseccao> logs) {
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

    public List<LogBisseccao> getLogs() {
		return logs;
	}

	public void setLogs(List<LogBisseccao> logs) {
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
