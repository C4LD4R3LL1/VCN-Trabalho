package src.entidade.retorno;

import java.util.List;

public class CalculoGaussRetorno {

    private double[] solucao;            // Vetor solução x
    private List<double[][]> etapas;     // Etapas do processo de eliminação
    private double[][] A;                 // Matriz original
    private double[][] L;                 // Matriz L
    private double[][] U;                 // Matriz U
    private double[][] P;                 // Matriz P

    public CalculoGaussRetorno(double[] solucao, List<double[][]> etapas,
                               double[][] A, double[][] L, double[][] U, double[][] P) {
        this.solucao = solucao;
        this.etapas = etapas;
        this.A = A;
        this.L = L;
        this.U = U;
        this.P = P;
    }

    // Getters e setters
    public double[] getSolucao() { return solucao; }
    public void setSolucao(double[] solucao) { this.solucao = solucao; }

    public List<double[][]> getEtapas() { return etapas; }
    public void setEtapas(List<double[][]> etapas) { this.etapas = etapas; }

    public double[][] getA() { return A; }
    public void setA(double[][] a) { this.A = a; }

    public double[][] getL() { return L; }
    public void setL(double[][] l) { this.L = l; }

    public double[][] getU() { return U; }
    public void setU(double[][] u) { this.U = u; }

    public double[][] getP() { return P; }
    public void setP(double[][] p) { this.P = p; }
}
