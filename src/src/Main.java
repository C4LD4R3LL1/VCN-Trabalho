package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import src.entidade.envio.CalculoBisseccaoEnvio;
import src.entidade.retorno.CalculoBisseccaoRetorno;
import src.servico.CalculoBisseccaoServico;

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

        server.setExecutor(null);
        server.start();
        System.out.println("Servidor rodando na porta 8080");
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
                sb.append("\"").append(retorno.getLogs().get(i).replace("\"", "\\\"")).append("\"");
                if (i < retorno.getLogs().size() - 1) sb.append(",");
            }
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }
}
