cd ".\downloader\target"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10"
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10"
cd ..\..\url-queue\target
start cmd /k "java -jar url-queue-1.0.0-SNAPSHOT.jar 9080 9081"
cd ..\..\rmi-gateway\target
start cmd /k "java -jar rmi-gateway-1.0.0-SNAPSHOT.jar 224.3.2.1 4321 9081 localhost 1099"
cd ..\..\barrel\target
start cmd /k "java -jar barrel-1.0.0-SNAPSHOT.jar 0 224.3.2.1 4321 localhost 1099"
start cmd /k "java -jar barrel-1.0.0-SNAPSHOT.jar 1 224.3.2.1 4321 localhost 1099"
cd ..\..\rmi-client\target
start cmd /k "java -jar rmi-client-1.0.0-SNAPSHOT.jar localhost 1099"
start cmd /k "java -jar rmi-client-1.0.0-SNAPSHOT.jar localhost 1099"
