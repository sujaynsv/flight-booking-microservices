echo “Eureka Service Registry"
java -jar target/eureka-server-0.0.1.jar

echo “Config Server Starting”java -jar target/config-server-1.0.0.jar
echo "Starting Email Service."
java -jar target/config-server-0.0.jar 

echo "Starting Flight Service"
java -jar target/flight-service-0.0.1.jar 

echo "Starting Booking Service:
java -jar target/booking-service-0.0.1.jar 

echo "Starting API Gateway”
java -jar target/api-gateway-0.0.1.jar 

echo "All services started!"
# Keep script running so background processes stay attached to this terminal
wait
