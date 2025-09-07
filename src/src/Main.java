package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Desktop;
import java.io.File;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import src.entidade.envio.CalculoBisseccaoEnvio;
import src.entidade.envio.CalculoGaussEnvio;
import src.entidade.retorno.CalculoBisseccaoRetorno;
import src.entidade.retorno.LogBisseccao;
import src.servico.CalculoBisseccaoServico;
import src.servico.CalculoGaussServico;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/calculo", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
            	exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            	exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            	exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            	
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }
                try {
                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "utf-8"));
                        StringBuilder json = new StringBuilder();
                        String linha;
                        while ((linha = br.readLine()) != null) {
                            json.append(linha);
                        }

                        CalculoBisseccaoEnvio envio = parseJsonParaEnvio(json.toString());

                        CalculoBisseccaoServico servico = new CalculoBisseccaoServico();
                        CalculoBisseccaoRetorno retorno = servico.calcularRaiz(envio);

                        String respostaJson = retornoParaJson(retorno);

                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, respostaJson.getBytes().length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(respostaJson.getBytes());
                        }
                    } else {
                        String response = "Use POST para calcular";
                        exchange.sendResponseHeaders(405, response.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                } catch (Exception e) {
                    String erro = "{\"erro\":\"" + e.getMessage() + "\"}";
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(500, erro.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(erro.getBytes());
                    }
                }
            }
        });

        server.createContext("/calculoGauss", new HttpHandler() {
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            try {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "utf-8"));
                    StringBuilder json = new StringBuilder();
                    String linha;
                    while ((linha = br.readLine()) != null) {
                        json.append(linha);
                    }

                    CalculoGaussEnvio envio = parseJsonParaEnvioGauss(json.toString());
                    CalculoGaussServico servico = new CalculoGaussServico();
                    var retorno = servico.resolver(envio);

                    String respostaJson = retornoParaJsonGauss(retorno);

                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, respostaJson.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(respostaJson.getBytes());
                    }
                } else {
                    String response = "Use POST para calcular";
                    exchange.sendResponseHeaders(405, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } catch (Exception e) {
                String erro = "{\"erro\":\"" + e.getMessage() + "\"}";
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, erro.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(erro.getBytes());
                }
            }
        }
    });

        server.setExecutor(null);
        server.start();
        System.out.println("\nServidor rodando na porta 8080");
        try {
            File arquivo = new File("/Users/nxmultiservicos/Documents/Luis/VCN-Trabalho/front/index.html");
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(arquivo.toURI());
            } else {
                System.out.println("Abertura automática não suportada neste sistema.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CalculoBisseccaoEnvio parseJsonParaEnvio(String json) throws Exception {
        Pattern coefPattern = Pattern.compile("\"coeficientes\"\\s*:\\s*\\[(.*?)\\]");
        Matcher coefMatcher = coefPattern.matcher(json);
        double[] coeficientes = null;
        if (coefMatcher.find()) {
            String coefString = coefMatcher.group(1);
            String[] parts = coefString.split(",");
            coeficientes = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                coeficientes[i] = Double.parseDouble(parts[i].trim());
            }
        } else {
            throw new Exception("Campo 'coeficientes' não encontrado");
        }

        Pattern aPattern = Pattern.compile("\"intervaloA\"\\s*:\\s*([-+]?[0-9]*\\.?[0-9]+)");
        Matcher aMatcher = aPattern.matcher(json);
        double intervaloA = 0;
        if (aMatcher.find()) {
            intervaloA = Double.parseDouble(aMatcher.group(1));
        } else {
            throw new Exception("Campo 'intervaloA' não encontrado");
        }

        Pattern bPattern = Pattern.compile("\"intervaloB\"\\s*:\\s*([-+]?[0-9]*\\.?[0-9]+)");
        Matcher bMatcher = bPattern.matcher(json);
        double intervaloB = 0;
        if (bMatcher.find()) {
            intervaloB = Double.parseDouble(bMatcher.group(1));
        } else {
            throw new Exception("Campo 'intervaloB' não encontrado");
        }

        Pattern precPattern = Pattern.compile("\"precisao\"\\s*:\\s*(\\d+)");
        Matcher precMatcher = precPattern.matcher(json);
        int precisaoExp = 0;
        if (precMatcher.find()) {
            precisaoExp = Integer.parseInt(precMatcher.group(1));
        } else {
            throw new Exception("Campo 'precisao' não encontrado");
        }

        return new CalculoBisseccaoEnvio(coeficientes, intervaloA, intervaloB, precisaoExp);
    }

    private static String retornoParaJson(CalculoBisseccaoRetorno retorno) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"raiz\":").append(retorno.getRaiz()).append(",");
        sb.append("\"iteracoes\":").append(retorno.getIteracoes()).append(",");
        sb.append("\"mensagem\":\"").append(retorno.getMensagem()).append("\",");

        sb.append("\"logs\":[");
        if (retorno.getLogs() != null && !retorno.getLogs().isEmpty()) {
            for (int i = 0; i < retorno.getLogs().size(); i++) {
                LogBisseccao log = retorno.getLogs().get(i);
                sb.append("{");
                sb.append("\"iteracao\":").append(log.getIteracao()).append(",");
                sb.append("\"a\":").append(log.getA()).append(",");
                sb.append("\"b\":").append(log.getB()).append(",");
                sb.append("\"x\":").append(log.getX()).append(",");
                sb.append("\"fx\":").append(log.getErro()).append(",");
                sb.append("\"fa\":").append(log.getFa()).append(",");
                sb.append("\"fb\":").append(log.getFb()).append(",");
                sb.append("\"erro\":").append(log.getFx());
                sb.append("}");
                if (i < retorno.getLogs().size() - 1) sb.append(",");
            }
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    private static CalculoGaussEnvio parseJsonParaEnvioGauss(String json) throws Exception {
    // Extrair matriz A
    Pattern aPattern = Pattern.compile("\"a\"\\s*:\\s*\\[(.*?)]\\s*,\\s*\"b\"", Pattern.DOTALL);
    Matcher aMatcher = aPattern.matcher(json);
    int[][] a = null;
    if (aMatcher.find()) {
        String matrizStr = aMatcher.group(1).trim();
        String[] linhas = matrizStr.split("],");
        int n = linhas.length;
        a = new int[n][];
        for (int i = 0; i < n; i++) {
            String linha = linhas[i].replace("[", "").replace("]", "").trim();
            String[] valores = linha.split(",");
            a[i] = new int[valores.length];
            for (int j = 0; j < valores.length; j++) {
                a[i][j] = Integer.parseInt(valores[j].trim());
            }
        }
    } else {
        throw new Exception("Campo 'a' não encontrado");
    }

    // Extrair vetor b
    Pattern bPattern = Pattern.compile("\"b\"\\s*:\\s*\\[(.*)]");
    Matcher bMatcher = bPattern.matcher(json);
    int[] b = null;
    if (bMatcher.find()) {
        String vetorStr = bMatcher.group(1).trim();
        String[] valores = vetorStr.split(",");
        b = new int[valores.length];
        for (int i = 0; i < valores.length; i++) {
            b[i] = Integer.parseInt(valores[i].trim());
        }
    } else {
        throw new Exception("Campo 'b' não encontrado");
    }

    return new CalculoGaussEnvio(a, b);
}

    private static String retornoParaJsonGauss(src.entidade.retorno.CalculoGaussRetorno retorno) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // A
        sb.append("\"a\":[");
        double[][] matrizA = retorno.getA();
        for (int i = 0; i < matrizA.length; i++) {
                sb.append("[");
                for (int j = 0; j < matrizA[i].length; j++) {
                    sb.append(matrizA[i][j]);
                    if (j < matrizA[i].length - 1) sb.append(",");
                }
                sb.append("]");
                if (i < matrizA.length - 1) sb.append(",");
            }
        sb.append("],");
        // L
        sb.append("\"l\":[");
        double[][] matrizL = retorno.getL();
        for (int i = 0; i < matrizL.length; i++) {
                sb.append("[");
                for (int j = 0; j < matrizL[i].length; j++) {
                    sb.append(matrizL[i][j]);
                    if (j < matrizL[i].length - 1) sb.append(",");
                }
                sb.append("]");
                if (i < matrizL.length - 1) sb.append(",");
            }
        sb.append("],");
        // U
        sb.append("\"u\":[");
        double[][] matrizU = retorno.getU();
        for (int i = 0; i < matrizU.length; i++) {
                sb.append("[");
                for (int j = 0; j < matrizU[i].length; j++) {
                    sb.append(matrizU[i][j]);
                    if (j < matrizU[i].length - 1) sb.append(",");
                }
                sb.append("]");
                if (i < matrizU.length - 1) sb.append(",");
            }
        sb.append("],");
        // P
        sb.append("\"p\":[");
        double[][] matrizP = retorno.getP();
        for (int i = 0; i < matrizP.length; i++) {
                sb.append("[");
                for (int j = 0; j < matrizP[i].length; j++) {
                    sb.append(matrizP[i][j]);
                    if (j < matrizP[i].length - 1) sb.append(",");
                }
                sb.append("]");
                if (i < matrizP.length - 1) sb.append(",");
            }
        sb.append("],");

        // solução
        sb.append("\"solucao\":[");
        for (int i = 0; i < retorno.getSolucao().length; i++) {
            sb.append(retorno.getSolucao()[i]);
            if (i < retorno.getSolucao().length - 1) sb.append(",");
        }
        sb.append("],");

        // etapas
        sb.append("\"etapas\":[");
        List<double[][]> etapas = retorno.getEtapas();
        for (int k = 0; k < etapas.size(); k++) {
            double[][] matriz = etapas.get(k);
            sb.append("[");
            for (int i = 0; i < matriz.length; i++) {
                sb.append("[");
                for (int j = 0; j < matriz[i].length; j++) {
                    sb.append(matriz[i][j]);
                    if (j < matriz[i].length - 1) sb.append(",");
                }
                sb.append("]");
                if (i < matriz.length - 1) sb.append(",");
            }
            sb.append("]");
            if (k < etapas.size() - 1) sb.append(",");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }
}
