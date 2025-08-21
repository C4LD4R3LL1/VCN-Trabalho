package src.entidade.retorno;

public class LogBisseccao {
    private int iteracao;
    private double a;
    private double b;
    private double x;
    private double erro;
    private double fa;
    private double fb;
    private double fx;

    public LogBisseccao(int iteracao, double a, double b, double x, double erro, double fa, double fb, double fx) {
        this.iteracao = iteracao;
        this.a = a;
        this.b = b;
        this.x = x;
        this.erro = erro;
        this.fa = fa;
        this.fb = fb;
        this.fx = fx;
    }

	public int getIteracao() {
		return iteracao;
	}

	public void setIteracao(int iteracao) {
		this.iteracao = iteracao;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getErro() {
		return erro;
	}

	public void setErro(double erro) {
		this.erro = erro;
	}

	public double getFa() {
		return fa;
	}

	public void setFa(double fa) {
		this.fa = fa;
	}

	public double getFb() {
		return fb;
	}

	public void setFb(double fb) {
		this.fb = fb;
	}

	public double getFx() {
		return fx;
	}

	public void setFx(double fx) {
		this.fx = fx;
	}

   
}
