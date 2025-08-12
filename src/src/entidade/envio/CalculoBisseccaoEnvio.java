package src.entidade.envio;

public class CalculoBisseccaoEnvio {
    private double[] coeficientes;
    private double intervaloA;
    private double intervaloB;
    private int precisao;  // será o expoente para 10^(-precisao)

    public CalculoBisseccaoEnvio(double[] coeficientes, double intervaloA, double intervaloB, int precisao) {
        this.coeficientes = coeficientes;
        this.intervaloA = intervaloA;
        this.intervaloB = intervaloB;
        this.precisao = precisao;
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

    // Retorna a precisão real para usar no cálculo: 10^(-precisao)
    public double getPrecisaoReal() {
        return Math.pow(10, -this.precisao);
    }
}
