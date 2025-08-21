document.addEventListener('DOMContentLoaded', function() {
    const grauPolinomio = document.getElementById('grauPolinomio');
    const coeficientesContainer = document.getElementById('coeficientesContainer');
    const polynomialPreview = document.getElementById('polynomialPreview');
    const bisseccaoForm = document.getElementById('bisseccaoForm');
    const resultContainer = document.getElementById('resultContainer');
    const resultContent = document.getElementById('resultContent');
    const logsContainer = document.getElementById('logsContainer');

    function gerarCamposCoeficientes() {
        const grau = parseInt(grauPolinomio.value);
        coeficientesContainer.innerHTML = '';

        for (let i = grau; i >= 0; i--) {
            const div = document.createElement('div');
            div.className = 'mb-2';

            const input = document.createElement('input');
            input.type = 'number';
            input.step = 'any';
            input.className = 'form-control coef-input';
            input.id = `coef${i}`;
            input.placeholder = '0';
            input.value = i === grau ? '1' : '0';

            const label = document.createElement('label');
            label.className = 'form-label';
            label.innerHTML = `x<sup>${i}</sup> +`;

            div.appendChild(input);
            div.appendChild(label);
            coeficientesContainer.appendChild(div);
        }

        atualizarPreviewPolinomio();
    }

    function atualizarPreviewPolinomio() {
        const grau = parseInt(grauPolinomio.value);
        let polinomioStr = 'f(x) = ';

        for (let i = grau; i >= 0; i--) {
            const input = document.getElementById(`coef${i}`);
            let coef = input ? parseFloat(input.value) || 0 : 0;

            let coefStr = coef.toString();
            if (coefStr.endsWith('.0')) coefStr = coefStr.slice(0, -2);

            if (i === grau) {
                polinomioStr += `${coefStr}x${formatarExpoente(i)}`;
            } else {
                const sinal = coef >= 0 ? ' + ' : ' - ';
                const coefAbs = Math.abs(coef);
                let coefAbsStr = coefAbs.toString();
                if (coefAbsStr.endsWith('.0')) coefAbsStr = coefAbsStr.slice(0, -2);

                if (i > 0) {
                    polinomioStr += `${sinal}${coefAbsStr}x${formatarExpoente(i)}`;
                } else {
                    polinomioStr += `${sinal}${coefAbsStr}`;
                }
            }
        }

        polynomialPreview.innerHTML = polinomioStr;
    }

    function formatarExpoente(num) {
        const superscripts = ['⁰', '¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸', '⁹'];
        if (num === 1) return '';
        if (num < 10) return superscripts[num];
        return num.toString().split('').map(d => superscripts[parseInt(d)]).join('');
    }

    async function enviarParaBackend(dados) {
        try {
            const response = await fetch('http://localhost:8080/calculo', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(dados)
            });

            if (!response.ok) {
                let erroMsg = 'Erro na requisição';
                try {
                    const erroJson = await response.json();
                    if (erroJson.erro) erroMsg = erroJson.erro;
                } catch {}
                throw new Error(erroMsg);
            }

            return await response.json();
        } catch (error) {
            console.error('Erro:', error);
            return { mensagem: "Erro ao conectar com o servidor: " + error.message };
        }
    }

function exibirResultados(resultado) {
    const resultContent = document.getElementById('resultContent');
    const resultContainer = document.getElementById('resultContainer');
    const iterationsContainer = document.getElementById('iterationsContainer');
    const logsContainer = document.getElementById('logsContainer');
    const iterationsTableBody = document.getElementById('iterationsTableBody');
    const logsTableBody = document.getElementById('logsTableBody');

    // Limpar conteúdos anteriores
    resultContent.innerHTML = '';
    iterationsTableBody.innerHTML = '';
    logsTableBody.innerHTML = '';
    iterationsContainer.style.display = 'none';
    logsContainer.style.display = 'none';

    // Mensagem de erro
    if (resultado.mensagem && !resultado.raiz) {
        resultContent.innerHTML = `<div class="alert alert-danger">${resultado.mensagem}</div>`;
        resultContainer.style.display = 'block';
        return;
    }

    // Exibir raiz e número de iterações
    resultContent.innerHTML = `
        <div class="mb-3">
            <h5>Total de iterações: <strong>${resultado.iteracoes}</strong></h5>
        </div>
    `;

    // Exibir logs detalhados
    if (resultado.logs && Array.isArray(resultado.logs) && resultado.logs.length > 0) {
        resultado.logs.forEach(log => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${log.iteracao}</td>
                <td>${log.a}</td>
                <td>${log.b}</td>
                <td>${log.x}</td>
                <td>${log.fx}</td>
                <td>${log.fa}</td>
                <td>${log.fb}</td>
                <td>${log.erro}</td>
            `;
            logsTableBody.appendChild(row);
        });
        logsContainer.style.display = 'block';
    }

    resultContainer.style.display = 'block';
}
    grauPolinomio.addEventListener('change', gerarCamposCoeficientes);

    coeficientesContainer.addEventListener('input', e => {
        if (e.target && e.target.id.startsWith('coef')) {
            atualizarPreviewPolinomio();
        }
    });

    bisseccaoForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const grau = parseInt(grauPolinomio.value);
        const coeficientes = [];

        for (let i = grau; i >= 0; i--) {
            const input = document.getElementById(`coef${i}`);
            coeficientes.push(parseFloat(input.value) || 0);
        }

        const precisao = parseInt(document.getElementById('precisao').value) || 6;

        const dadosEnvio = {
            coeficientes: coeficientes,
            intervaloA: -1000, // valor fixo, pode criar inputs se quiser
            intervaloB: 1000,
            precisao: precisao  // enviado como expoente para o backend converter 10^-precisao
        };

        // Loading UI
        const submitBtn = bisseccaoForm.querySelector('button[type="submit"]');
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Calculando...';

        try {
            const resultado = await enviarParaBackend(dadosEnvio);
            exibirResultados(resultado);
        } catch (error) {
            resultContent.innerHTML = `<div class="alert alert-danger">Erro ao processar a requisição: ${error.message}</div>`;
            resultContainer.style.display = 'block';
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = 'Calcular Raiz';
        }
    });

    gerarCamposCoeficientes();
});

function renderLogs(logs) {
    const tbody = document.getElementById("logsTableBody");
    tbody.innerHTML = "";
    logs.forEach(item => {
        const row = document.createElement("tr");
        item.forEach(val => {
            const cell = document.createElement("td");
            cell.textContent = val;
            row.appendChild(cell);
        });
        tbody.appendChild(row);
    });
    document.getElementById("logsContainer").style.display = "block";
}