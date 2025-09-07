package src.servico;

import src.entidade.envio.CalculoGaussEnvio;
import src.entidade.retorno.CalculoGaussRetorno;

import java.util.ArrayList;
import java.util.List;

/**
 * CalculoGaussServico
 *
 * - Tenta primeiro LU (Doolittle) sem pivotamento (P = I).
 * - Se encontrar pivô (próximo de) zero, faz fallback para eliminação com pivotamento parcial (PA = LU).
 * - Retorna solução, etapas (matriz aumentada [A|b] após cada operação), e matrizes A, L, U, P.
 */
public class CalculoGaussServico {

    private static final double TOL = 1e-12;

    public CalculoGaussRetorno resolver(CalculoGaussEnvio envio) {
        int[][] Aint = envio.getA();
        int[] bInt = envio.getB();
        int n = bInt.length;

        // Monta A (double) e matriz aumentada original [A|b]
        double[][] A = new double[n][n];
        double[][] augmentedOriginal = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = Aint[i][j];
                augmentedOriginal[i][j] = A[i][j];
            }
            augmentedOriginal[i][n] = bInt[i];
        }

        // Inicializações para tentativa sem pivot
        double[][] L = new double[n][n];
        double[][] U = cloneMatriz(A);
        double[][] P = new double[n][n];
        for (int i = 0; i < n; i++) { L[i][i] = 1.0; P[i][i] = 1.0; }

        List<double[][]> etapas = new ArrayList<>();

        // ---------- TENTATIVA 1: LU sem pivotamento (Doolittle) ----------
        boolean pivotNeeded = false;
        double[][] matrizSemPivot = cloneMatriz(augmentedOriginal); // para registrar etapas

        for (int k = 0; k < n - 1; k++) {
            double piv = U[k][k];
            if (Math.abs(piv) < TOL) {
                pivotNeeded = true;
                break;
            }
            for (int i = k + 1; i < n; i++) {
                double fator = U[i][k] / piv;
                L[i][k] = fator;
                // atualiza U
                for (int j = k; j < n; j++) {
                    U[i][j] -= fator * U[k][j];
                }
                // atualiza matriz aumentada da mesma forma (inclui coluna b)
                for (int j = k; j <= n; j++) {
                    matrizSemPivot[i][j] -= fator * matrizSemPivot[k][j];
                }
                // registra etapa (após cada eliminação de linha)
                etapas.add(cloneMatriz(matrizSemPivot));
            }
        }

        if (!pivotNeeded) {
            // LU sem pivot foi bem sucedido — usar resultado U, L, P=I
            double[] x = backSubstitutionFromAugmented(matrizSemPivot);
            return new CalculoGaussRetorno(x, etapas, A, L, U, P);
        }

        // ---------- Fallback: usar eliminação com pivotamento parcial (PA = LU) ----------
        etapas.clear();
        // Recria L, U, P e a matriz aumentada a partir do original (não de estados intermediários)
        L = new double[n][n];
        for (int i = 0; i < n; i++) L[i][i] = 1.0;
        P = new double[n][n];
        for (int i = 0; i < n; i++) P[i][i] = 1.0;
        U = cloneMatriz(A);
        double[][] augmented = cloneMatriz(augmentedOriginal);

        for (int k = 0; k < n - 1; k++) {
            // encontra pivot — primeiro elemento não-nulo em |U[i][k]| (i >= k)
            // OBS: mais frágil numericamente que escolher o maior absoluto.
            int maxIndex = -1;
            for (int i = k; i < n; i++) {
                if (Math.abs(U[i][k]) > TOL) {
                    maxIndex = i;
                    break;
                }
            }
            if (maxIndex == -1) {
                throw new RuntimeException("Pivô nulo (coluna inteira nula) em k=" + k);
            }

            if (maxIndex != k) {
                // troca linhas em U, matriz aumentada e P
                swapRows(U, k, maxIndex);
                swapRows(augmented, k, maxIndex);
                swapRows(P, k, maxIndex);
                // troca parcial em L (somente colunas já preenchidas: 0..k-1)
                swapRowsPartial(L, k, maxIndex, k);
                // registra etapa da troca
                etapas.add(cloneMatriz(augmented));
            }

            double piv = U[k][k];
            if (Math.abs(piv) < TOL) {
                throw new RuntimeException("Pivô nulo (ou muito pequeno) mesmo após pivotamento em k=" + k);
            }

            // eliminações (preenche L e atualiza U e matriz aumentada)
            for (int i = k + 1; i < n; i++) {
                double fator = U[i][k] / piv;
                L[i][k] = fator;
                for (int j = k; j < n; j++) {
                    U[i][j] -= fator * U[k][j];
                }
                // aplica mesma operação na matriz aumentada
                for (int j = k; j <= n; j++) {
                    augmented[i][j] -= fator * augmented[k][j];
                }
                etapas.add(cloneMatriz(augmented));
            }
        }

        // retro-substituição usando a matriz aumentada final
        double[] x = backSubstitutionFromAugmented(augmented);
        return new CalculoGaussRetorno(x, etapas, A, L, U, P);
    }

    // ---------------- utilitárias ----------------

    // back substitution a partir de matriz aumentada final [U|b]
    private double[] backSubstitutionFromAugmented(double[][] augmented) {
        int n = augmented.length;
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double soma = augmented[i][n];
            double diag = augmented[i][i];
            for (int j = i + 1; j < n; j++) soma -= augmented[i][j] * x[j];
            x[i] = soma / diag;
        }
        return x;
    }

    private double[][] cloneMatriz(double[][] matriz) {
        double[][] copia = new double[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++) {
            System.arraycopy(matriz[i], 0, copia[i], 0, matriz[0].length);
        }
        return copia;
    }

    private void swapRows(double[][] M, int i, int j) {
        double[] tmp = M[i];
        M[i] = M[j];
        M[j] = tmp;
    }

    // troca colunas 0..(limite-1) das linhas i e j em M — usado para L ao pivotar
    private void swapRowsPartial(double[][] M, int i, int j, int limite) {
        if (limite <= 0) return;
        for (int c = 0; c < limite; c++) {
            double tmp = M[i][c];
            M[i][c] = M[j][c];
            M[j][c] = tmp;
        }
    }
}
