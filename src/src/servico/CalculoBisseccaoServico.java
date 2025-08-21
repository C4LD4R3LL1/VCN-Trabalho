package src.servico;

import java.util.ArrayList;
import java.util.List;

import src.entidade.CalculoBisseccao;
import src.entidade.envio.CalculoBisseccaoEnvio;
import src.entidade.retorno.CalculoBisseccaoRetorno;
import src.entidade.retorno.LogBisseccao;

public class CalculoBisseccaoServico {

    private double f(double[] coeficientes, double x) {
        double resultado = 0;
        for (int i = 0; i < coeficientes.length; i++) {
            resultado += coeficientes[i] * Math.pow(x, coeficientes.length - 1 - i);
        }
        return resultado;
    }

    private List<double[]> encontrarTodosIntervalosComRaiz(double[] coeficientes, double intervaloA, double intervaloB, double passo) {
        List<double[]> intervalos = new ArrayList<>();
        double a = intervaloA;
        double b = a + passo;

        while (b <= intervaloB) {
            double fa = f(coeficientes, a);
            double fb = f(coeficientes, b);

            if (fa == 0) {
                intervalos.add(new double[]{a, a});
            } else if (fb == 0) {
                intervalos.add(new double[]{b, b});
            } else if (fa * fb < 0) {
                intervalos.add(new double[]{a, b});
            }

            a = b;
            b = b + passo;
        }
        return intervalos;
    }

    private RaizResultado calcularRaizNoIntervalo(double[] coeficientes, double a, double b, double precisao) {
        List<Double> aproximacoes = new ArrayList<>();
        List<Double> erros = new ArrayList<>();
        List<LogBisseccao> logs = new ArrayList<>();

        double raiz = 0;
        int iteracoes = 0;
        double fRaiz = Double.MAX_VALUE;

        while (true) {
            double fa = f(coeficientes, a);
            double fb = f(coeficientes, b);

            raiz = (a + b) / 2.0;
            double erro = f(coeficientes, raiz);

            if (iteracoes > 0) {
            	fRaiz = Math.abs(raiz - aproximacoes.get(iteracoes - 1));
            } else {
            	fRaiz = 0.0;
            }

            aproximacoes.add(raiz);
            erros.add(fRaiz);

            logs.add(new LogBisseccao(
                    iteracoes + 1,
                    a, b, raiz,
                    fRaiz,
                    fa, fb, erro
            ));

            if (fa * erro < 0) {
                b = raiz;
            } else {
                a = raiz;
            }

            iteracoes++;

            if (Math.abs(erro) < precisao) break;
        }

        return new RaizResultado(raiz, iteracoes, aproximacoes, erros, logs);
    }

    public CalculoBisseccaoRetorno calcularRaiz(CalculoBisseccaoEnvio envio) {
        if (envio.getCoeficientes() == null || envio.getCoeficientes().length == 0) {
            return new CalculoBisseccaoRetorno(0, 0, null, null, "Coeficientes não fornecidos", null);
        }

        double intervaloA = envio.getIntervaloA();
        double intervaloB = envio.getIntervaloB();
        double[] coeficientes = envio.getCoeficientes();
        double precisao = Math.pow(10, -envio.getPrecisao());
        double passo = 1;

        List<double[]> intervalos = encontrarTodosIntervalosComRaiz(coeficientes, intervaloA, intervaloB, passo);

        if (intervalos.isEmpty()) {
            return new CalculoBisseccaoRetorno(0, 0, null, null, "Não existe raiz no intervalo informado", null);
        }

        List<Double> todasAproximacoes = new ArrayList<>();
        List<Double> todosErros = new ArrayList<>();
        List<LogBisseccao> todosLogs = new ArrayList<>();
        double ultimaRaiz = 0;
        int totalIteracoes = 0;

        for (double[] sub : intervalos) {
            RaizResultado resultado = calcularRaizNoIntervalo(coeficientes, sub[0], sub[1], precisao);
            ultimaRaiz = resultado.raiz;
            totalIteracoes += resultado.iteracoes;
            todasAproximacoes.addAll(resultado.aproximacoes);
            todosErros.addAll(resultado.erros);
            todosLogs.addAll(resultado.logs);
        }

        String mensagem = "Cálculo realizado com sucesso. Encontrados " + intervalos.size() + " subintervalos no intervalo de: " + envio.getIntervaloA() + " até " + envio.getIntervaloB()+ ".";

        CalculoBisseccao calculo = new CalculoBisseccao(coeficientes, intervaloA, intervaloB,
                envio.getPrecisao(), ultimaRaiz, totalIteracoes, todasAproximacoes, todosErros, mensagem);

        return new CalculoBisseccaoRetorno(ultimaRaiz, totalIteracoes, todasAproximacoes, todosErros, mensagem, todosLogs);
    }

    private static class RaizResultado {
        double raiz;
        int iteracoes;
        List<Double> aproximacoes;
        List<Double> erros;
        List<LogBisseccao> logs;

        RaizResultado(double raiz, int iteracoes, List<Double> aproximacoes, List<Double> erros, List<LogBisseccao> logs) {
            this.raiz = raiz;
            this.iteracoes = iteracoes;
            this.aproximacoes = aproximacoes;
            this.erros = erros;
            this.logs = logs;
        }
    }
}
