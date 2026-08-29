CREATE DATABASE vehicle_service_db;

USE vehicle_service_db;

CREATE TABLE service_bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_no VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    cost DOUBLE NOT NULL
);
