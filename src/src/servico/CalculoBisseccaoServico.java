package src.servico;

import java.util.ArrayList;
import java.util.List;

import src.entidade.envio.CalculoBisseccaoEnvio;
import src.entidade.retorno.CalculoBisseccaoRetorno;
import src.entidade.retorno.LogBisseccao;

/**
 * Serviço responsável pelo cálculo de raízes de funções polinomiais usando o Método da Bissecção.
 * Trata também raízes exatas (f(x) == 0) e gera logs detalhados das iterações.
 */
public class CalculoBisseccaoServico {

    /**
     * Calcula o valor da função polinomial para um x dado.
     * @param coeficientes coeficientes do polinômio [a_n, ..., a_0]
     * @param x ponto em que a função será avaliada
     * @return valor de f(x)
     */
    private double f(double[] coeficientes, double x) {
        double resultado = 0;
        for (int i = 0; i < coeficientes.length; i++) {
            resultado += coeficientes[i] * Math.pow(x, coeficientes.length - 1 - i);
        }
        return resultado;
    }

    /**
     * Encontra todos os subintervalos no intervalo [intervaloA, intervaloB] que contêm uma raiz.
     * Identifica raízes exatas (f(x) == 0) e evita duplicação.
     * @param coeficientes coeficientes do polinômio
     * @param intervaloA início do intervalo
     * @param intervaloB fim do intervalo
     * @param passo tamanho do passo para varredura
     * @param mensagens lista de mensagens sobre raízes exatas
     * @return lista de subintervalos [a,b] ou [x,x] se raiz exata
     */
    private List<double[]> encontrarTodosIntervalosComRaiz(double[] coeficientes, double intervaloA, double intervaloB, double passo, List<String> mensagens) {
        List<double[]> intervalos = new ArrayList<>();
        double a = intervaloA;
        double b = a + passo;
        double ultimaRaizExata = Double.NaN; // marca para evitar duplicados

        while (b <= intervaloB) {
            double fa = f(coeficientes, a);
            double fb = f(coeficientes, b);

            // Se a função é exatamente zero no ponto a
            if (fa == 0 && (Double.isNaN(ultimaRaizExata) || ultimaRaizExata != a)) {
                intervalos.add(new double[]{a, a});
                mensagens.add("Raiz exata encontrada no ponto: " + a);
                ultimaRaizExata = a;
            }
            // Se a função é exatamente zero no ponto b
            else if (fb == 0 && (Double.isNaN(ultimaRaizExata) || ultimaRaizExata != b)) {
                intervalos.add(new double[]{b, b});
                mensagens.add("Raiz exata encontrada no ponto: " + b);
                ultimaRaizExata = b;
            }
            // Se houver mudança de sinal, há uma raiz no intervalo (a,b)
            else if (fa * fb < 0) {
                intervalos.add(new double[]{a, b});
                ultimaRaizExata = Double.NaN; // reinicia para próxima detecção
            }

            a = b;
            b = b + passo;
        }

        return intervalos;
    }

    /**
     * Aplica o método da bissecção em um subintervalo [a,b].
     * @param coeficientes coeficientes do polinômio
     * @param a limite inferior
     * @param b limite superior
     * @param precisao critério de parada (tolerância)
     * @return objeto RaizResultado com raiz aproximada, iterações, logs e erros
     */
private RaizResultado calcularRaizNoIntervalo(double[] coeficientes, double a, double b, double precisao) {
    List<Double> aproximacoes = new ArrayList<>();
    List<Double> erros = new ArrayList<>();
    List<LogBisseccao> logs = new ArrayList<>();

    double raiz = 0;
    int iteracoes = 0;

    if (a == b) {
        raiz = a;
        logs.add(new LogBisseccao(1, a, b, raiz, f(coeficientes, raiz), f(coeficientes, a), f(coeficientes, b), 0.0));
        aproximacoes.add(raiz);
        erros.add(0.0);
        return new RaizResultado(raiz, 1, aproximacoes, erros, logs);
    }

    double erro = 1; // erro inicial = tamanho do intervalo
    while (erro > precisao) {
        double fa = f(coeficientes, a);
        double fb = f(coeficientes, b);

        raiz = (a + b) / 2.0;
        double fx = f(coeficientes, raiz);

        // LOG da iteração atual: usa o erro antes de atualizar a/b
        logs.add(new LogBisseccao(iteracoes + 1, a, b, raiz, fx, fa, fb, erro));
        aproximacoes.add(raiz);
        erros.add(erro);

        // Atualiza intervalo
        if (fa * fx < 0) {
            b = raiz;
        } else {
            a = raiz;
        }

        // Atualiza erro para a próxima iteração
        erro = (b - a);
        iteracoes++;
    }
    if (erro < precisao) {
        // quando bater a parada
        double fa = f(coeficientes, a);
        double fb = f(coeficientes, b);
        raiz = (a + b) / 2.0;
        double fx = f(coeficientes, raiz);
        iteracoes++;
        logs.add(new LogBisseccao(iteracoes, a, b, raiz, fx, fa, fb, erro));
        aproximacoes.add(raiz);
        erros.add(erro);
    }
    return new RaizResultado(raiz, iteracoes, aproximacoes, erros, logs);
}

    /**
     * Método principal que recebe os coeficientes e intervalo, calcula as raízes e retorna resultado completo.
     * @param envio objeto com coeficientes, intervalo e precisão
     * @return retorno com raiz(s), logs e mensagens
     */
    public CalculoBisseccaoRetorno calcularRaiz(CalculoBisseccaoEnvio envio) {
        if (envio.getCoeficientes() == null || envio.getCoeficientes().length == 0) {
            return new CalculoBisseccaoRetorno(0, 0, null, null, "Coeficientes não fornecidos", null);
        }

        double intervaloA = envio.getIntervaloA();
        double intervaloB = envio.getIntervaloB();
        double[] coeficientes = envio.getCoeficientes();
        double precisao = Math.pow(10, -envio.getPrecisao());
        double passo = 1;

        // Lista de mensagens para raízes exatas
        List<String> mensagensRaizesExatas = new ArrayList<>();

        // Detecta todos os intervalos com raízes (exatas ou subintervalos)
        List<double[]> intervalos = encontrarTodosIntervalosComRaiz(coeficientes, intervaloA, intervaloB, passo, mensagensRaizesExatas);

        if (intervalos.isEmpty()) {
            return new CalculoBisseccaoRetorno(0, 0, null, null, "Não existe raiz no intervalo informado", null);
        }

        List<Double> todasAproximacoes = new ArrayList<>();
        List<Double> todosErros = new ArrayList<>();
        List<LogBisseccao> todosLogs = new ArrayList<>();
        double ultimaRaiz = 0;
        int totalIteracoes = 0;

        // Calcula raiz em cada subintervalo
        for (double[] sub : intervalos) {
            RaizResultado resultado = calcularRaizNoIntervalo(coeficientes, sub[0], sub[1], precisao);
            ultimaRaiz = resultado.raiz;
            totalIteracoes += resultado.iteracoes;
            todasAproximacoes.addAll(resultado.aproximacoes);
            todosErros.addAll(resultado.erros);
            todosLogs.addAll(resultado.logs);
        }

        // Mensagem final
        String mensagem;
        if (!mensagensRaizesExatas.isEmpty()) {
            mensagem = String.join("; ", mensagensRaizesExatas);
        } else {
            mensagem = "Cálculo realizado com sucesso. Encontrados " + intervalos.size() + " subintervalos no intervalo de: " + intervaloA + " até " + intervaloB + ".";
        }

        // Retorno final com logs detalhados
        return new CalculoBisseccaoRetorno(ultimaRaiz, totalIteracoes, todasAproximacoes, todosErros, mensagem, todosLogs);
    }

    /**
     * Classe interna para armazenar o resultado da bissecção em um intervalo.
     */
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
