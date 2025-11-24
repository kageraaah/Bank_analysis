Bank Analysis System

A Java desktop application for managing and analyzing bank account data using a MySQL database and graphical charts.

🔧 Technologies Used

Java (JDK 17+)

Swing / JavaFX

MySQL Database

MySQL Connector/J

JFreeChart

📂 Project Structure
/src
 ├── BankAppGUI.java
 ├── DatabaseConnection.java
 ├── AccountDAO.java
 ├── Account.java
 └── controllers/

🗄️ Database Information

Database Name: bank_analysis
Main Table: accounts

Example structure:

CREATE TABLE accounts (
    account_id INT PRIMARY KEY,
    account_name VARCHAR(255),
    balance DOUBLE,
    account_type VARCHAR(50)
);

⚙️ How to Run
1. Install Requirements

Java JDK

MySQL Server

IntelliJ IDEA

MySQL Connector/J

JFreeChart

2. Create the Database
CREATE DATABASE bank_analysis;
USE bank_analysis;

3. Configure Connection

In DatabaseConnection.java:

String url = "jdbc:mysql://localhost:3306/bank_analysis";
String user = "root";
String password = "YOUR_PASSWORD";

4. Run the App

Open the project in IntelliJ

Run BankAppGUI.java

📊 Features

View account data

Analyze bank accounts

Show charts and histograms

Add / update / delete accounts (if implemented)
