package src.servico;

import java.util.ArrayList;
import java.util.List;

import src.entidade.CalculoBisseccao;
import src.entidade.envio.CalculoBisseccaoEnvio;
import src.entidade.retorno.CalculoBisseccaoRetorno;

public class CalculoBisseccaoServico {

    private double f(double[] coeficientes, double x) {
        double resultado = 0;
        for (int i = 0; i < coeficientes.length; i++) {
            resultado += coeficientes[i] * Math.pow(x, coeficientes.length - 1 - i);
        }
        return resultado;
    }

    private double[] encontrarIntervaloComRaiz(double[] coeficientes, double intervaloA, double intervaloB, double passo) {
        double a = intervaloA;
        double b = a + passo;
        while (b <= intervaloB) {
            double fa = f(coeficientes, a);
            double fb = f(coeficientes, b);
            if (fa == 0) {
                return new double[]{a, a};
            }
            if (fb == 0) {
                return new double[]{b, b};
            }
            if (fa * fb < 0) {
                return new double[]{a, b};
            }
            a = b;
            b = b + passo;
        }
        return null;
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

        double[] subintervalo = encontrarIntervaloComRaiz(coeficientes, intervaloA, intervaloB, passo);

        if (subintervalo == null) {
            return new CalculoBisseccaoRetorno(0, 0, null, null, "Não existe raiz no intervalo informado", null);
        }

        double a = subintervalo[0];
        double b = subintervalo[1];

        List<Double> aproximacoes = new ArrayList<>();
        List<Double> erros = new ArrayList<>();

        double raiz = 0;
        int iteracoes = 0;
        double erro = Double.MAX_VALUE;

        System.out.println("=== Início do cálculo por Bissecção ===");
        System.out.printf("%-10s %-15s %-15s %-15s %-15s%n", "Iter", "a", "b", "x", "Erro");
        
        List<String> logs = new ArrayList<>();
        logs.add("=== Início do cálculo por Bissecção ===");
        logs.add(String.format("%-10s %-15s %-15s %-15s %-15s", "Iter", "a", "b", "x", "Erro"));

        while (erro > precisao) {
            raiz = (a + b) / 2.0;
            double fRaiz = f(coeficientes, raiz);

            if (iteracoes > 0) {
                erro = Math.abs(raiz - aproximacoes.get(iteracoes - 1));
                erros.add(erro);
            } else {
                erros.add(Double.NaN);
            }

            aproximacoes.add(raiz);
            
            String linha = String.format("%-10d %-15.8f %-15.8f %-15.8f %-15.8f",
                    iteracoes + 1, a, b, raiz, (iteracoes > 0 ? erro : 0.0));
            
            System.out.printf(linha);
            
            logs.add(linha);

            if (f(coeficientes, a) * fRaiz < 0) {
                b = raiz;
            } else {
                a = raiz;
            }

            iteracoes++;

            if (fRaiz == 0) break;
        }

        System.out.println("=== Fim do cálculo ===");
        System.out.println("Raiz aproximada: " + raiz);
        System.out.println("Iterações: " + iteracoes);
        
        logs.add("=== Fim do cálculo ===");
        logs.add("Raiz aproximada: " + raiz);
        logs.add("Iterações: " + iteracoes);

        String mensagem = "Cálculo realizado com sucesso";

        CalculoBisseccao calculo = new CalculoBisseccao(coeficientes, subintervalo[0], subintervalo[1],
                envio.getPrecisao(), raiz, iteracoes, aproximacoes, erros, mensagem);

        return new CalculoBisseccaoRetorno(raiz, iteracoes, aproximacoes, erros, mensagem, logs != null ? logs : null);
    }
}
