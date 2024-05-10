cd ".\downloader\target"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10 localhost"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10 localhost"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10 localhost"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10 localhost"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10 localhost"
cd ..\..\url-queue\target
start cmd /k "java -jar url-queue-1.0.0-SNAPSHOT.jar 9080 9081 https://www.uc.pt/"
cd ..\..\rmi-gateway\target
start cmd /k "java -jar rmi-gateway-1.0.0-SNAPSHOT.jar 224.3.2.1 4321 9081 localhost 1099"
cd ..\..\barrel\target
start cmd /k "java -jar barrel-1.0.0-SNAPSHOT.jar 0 224.3.2.1 4321 localhost 1099"
start cmd /k "java -jar barrel-1.0.0-SNAPSHOT.jar 1 224.3.2.1 4321 localhost 1099"
cd ..\..\rmi-client\target
start cmd /k "java -jar rmi-client-1.0.0-SNAPSHOT.jar localhost 1099"
start cmd /k "java -jar rmi-client-1.0.0-SNAPSHOT.jar localhost 1099"
cd "..\..\web-server"
start cmd /k "mvnw spring-boot:run"