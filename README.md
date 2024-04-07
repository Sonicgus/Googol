Como executar Barrel:
Percorrer ./barrel/target/
Ficheiro e Pasta necessária: 
barrel-1.0.0-SNAPSHOT.jar
/lib/
Comando: java -jar barrel.jar <id> <MULTICAST_ADDRESS> <MULTICAST_PORT> <RMI_HOST> <RMI_GATEWAY_PORT>
Exemplo: java -jar barrel-1.0.0-SNAPSHOT.jar 0 224.3.2.1 4321 localhost 1099

Como executar Downloader:
Percorrer ./downloader/target/
Ficheiro e Pasta necessária: 
downloader-1.0.0-SNAPSHOT.jar
/lib/
Comando: java -jar downloader.jar <PORT_A> <PORT_B> <MULTICAST_ADDRESS> <MULTICAST_PORT> <MAXIMUM_REFERENCE_LINKS> <HOST_ADRESS>
Exemplo: java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10 localhost

Como executar Client:
Percorrer ./rmi-client/target/
Ficheiro e Pasta necessária: 
rmi-client-1.0.0-SNAPSHOT.jar
/lib/
Comando: java -jar rmi-client.jar <RMI_HOST> <RMI_GATEWAY_PORT>
Exemplo: java -jar rmi-client-1.0.0-SNAPSHOT.jar localhost 1099

Como executar Gateway:
Percorrer ./rmi-gateway/target/
Ficheiro e Pasta necessária: 
rmi-gateway-1.0.0-SNAPSHOT.jar
/lib/
Comando: java -jar rmi-gateway.jar <multicast_adress> <multicast_port> <PORT_B> <rmi-registry-name> <rmi-registry-port>
Exemplo: java -jar rmi-gateway-1.0.0-SNAPSHOT.jar 224.3.2.1 4321 9081 localhost 1099

Como executar URLQueue:
Percorrer ./url-queue/target/
Ficheiro e Pasta necessária: 
url-queue-1.0.0-SNAPSHOT.jar
/lib/
Comando: java -jar url-queue.jar <port-a> <port-b>
ou
Comando: java -jar url-queue.jar <port-a> <port-b> <initial_index_url>
Exemplo: java -jar url-queue-1.0.0-SNAPSHOT.jar 9080 9081 https://www.uc.pt/