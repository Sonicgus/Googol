cd ".\rmi-client\target
start cmd /k "java -jar rmi-client-1.0.0-SNAPSHOT.jar 192.168.46.131 1099"

cd ..\..\downloader\target
start cmd /k "java -jar downloader-1.0.0-SNAPSHOT.jar 9080 9081 224.3.2.1 4321 10 192.168.46.131"