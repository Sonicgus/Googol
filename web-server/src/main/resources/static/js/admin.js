function connect() {
    let socket = new SockJS("/ws");
    let stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            document.getElementById('results').textContent = JSON.parse(message.body).content;
        });
    });

    socket.onclose = function () {
        console.log("Conexão fechada");
        console.log("Tentando reconectar em 10 segundos...");
        // tenta reconectar após 10 segundos
        setTimeout(connect, 10000);
    };
}

// iniciar a conexão
connect();