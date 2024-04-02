cd ".\downloader\target"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 0"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 1"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 2"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 3"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 4"
cd ..\..\url-queue\target
start cmd /k "java -jar url-queue-1.0.0-SNAPSHOT.jar"
cd ..\..\rmi-gateway\target
start cmd /k "java -jar rmi-gateway-1.0.0-SNAPSHOT.jar"
cd ..\..\barrel\target
start cmd /k "java -jar barrel-1.0.0-SNAPSHOT.jar 0"
start cmd /k "java -jar barrel-1.0.0-SNAPSHOT.jar 1"
cd ..\..\rmi-client\target
start cmd /k "java -jar rmi-client-1.0.0-SNAPSHOT.jar"