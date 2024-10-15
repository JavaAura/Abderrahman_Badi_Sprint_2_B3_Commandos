 # Commandos

A secure web application for managing online orders, users, and products, developed using Java, Thymeleaf, and Hibernate.

## Table of Contents

- [Project Overview](#project-overview)
- [Installation](#installation)
- [Structure](#structure)
- [Features](#features)
- [Technologies](#technologies)


## Project Overview

**Context**:  
Commandos is a web application designed for an enterprise to enhance its online order management capabilities. The application allows customers to place orders and enables administrators to manage users and products efficiently.

**Objectives**:
- Implement user management with session-based authentication `HttpSession`, differentiating between Admin and Client roles.
- Use Thymeleaf as the template engine for dynamic page layouts and replacing JSP/JSTL.
- Implement CRUD operations for products, orders, and users.
- Write unit tests with JUnit and Mockito for business and data access components.
- Apply a layered architecture (MVC) to separate presentation, business, and persistence logic.

## Installation

### Prerequisites

- Java 8 or higher
- Apache Maven
- MySQL Server
- Apache Tomcat v9.x

### Setup your database:

1. Ensure your PostgreSQL server is running.
2. Navigate to the directory containing `Database.sql`.
3. Run the following command to create the database and tables:
   ```bash
   psql -U your_username -d your_database -f Database.sql

### Setup environment variable

1. **For windows:**
   ```cmd
   set DB_URL= **Your PostgreSQL URL**
   set DB_USER= "dbadmin"
   set DB_PASSWORD = "azerty"

2. **For linux based:**
   ```bash
   export DB_URL= **Your PostgreSQL URL**
   export DB_USER= "dbadmin"
   export DB_PASSWORD = "azerty"


### Steps

1. **Clone the repository:**

   ```sh
   git clone https://github.com/Yorften/Commandos.git
   cd 3CHAN

2. **Build the application:**
   ```sh
   mvn clean install

3. **Compile the application:**
   ```sh
   mvn package

4. **Deploy:**

- Deploy the WAR file in Apache Tomcat by copying the WAR from the /target folder to Tomcat's webapps directory.
- Start the Tomcat server and access the application in your browser at http://localhost:8080/3CHAN.

5. **Run with Eclipse/IntelliJ IDEA (optional)**

- Open the project in your ide.
- Build maven dependencies.
- Run the app or server.

## Structure

- **Model Layer**:  
  Defines entities such as `User`, `Product`, `Command`, and their relationships using JPA.
  
- **Controller Layer**:  
  Handles user interactions, receives input from the UI, and communicates with the Service layer to process the requests.
  
- **Repository Layer**:  
  Provides an abstraction for data access. This layer interacts with the database using JDBC and Hibernate ORM to handles CRUD operations.
  
- **Service Layer**:  
  Contains business logic and orchestrates operations between the Controller and Repository layers.
  
- **Presentation Layer**:  
  Thymeleaf templates are used to create dynamic and reusable layouts.

## Features

1. **Authentication**:
   - Session-based login system differentiating between Admin and Client roles.

2. **Product Management (Admin Only)**:
   - Create, update, delete, search, and paginate products

3. **User Management (Admin Only)**:
   - Manage users with role-based access (Admin/Client)

4. **Order Management**:
   - Clients can create, update, delete, and view orders.
   - Admins can view and manage the status of all orders.

5. **Advanced Features**:
   - Status tracking for orders (On hold, Processing, Shipped, etc..).
   - Pagination and search functionality for large datasets.


## Technologies

- **Java 8**: Core language used for development.
- **Thymeleaf**: For server-side rendering of views.
- **Apache Maven**: For dependency management and project build.
- **PostgreSQL**: Relational database for storing data.
- **Hibernate**: ORM for database access and management.
- **Apache Tomcat**: Web server for deploying the application.
- **JUnit**: For unit testing.
- **Mockito**: For mocking classes to unit test.
- **Tailwind CSS**: For responsive UI design.
- **JIRA**: For project management using Scrum methodology.
- **Git**: For version control with branches.