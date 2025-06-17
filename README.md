# Event Management System

A comprehensive Spring Boot application for managing events, registrations, payments, and feedback. This system provides a complete solution for event organizers to handle the entire event lifecycle.

## 🚀 Features

- **Event Management**: Create, update, and manage events
- **Registration System**: Handle event registrations and ticket types
- **Payment Tracking**: Monitor payment status and transactions
- **Feedback System**: Collect and manage attendee feedback
- **Admin Dashboard**: Comprehensive admin interface for event management
- **Responsive Web Interface**: Modern HTML/CSS frontend

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2.5
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **Frontend**: Thymeleaf templates with HTML/CSS
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

Before running this project, make sure you have the following installed:

- **Java 17** or higher
- **Maven 3.6** or higher
- **MySQL 8.0** or higher
- **Git** (for cloning the repository)

## 🗄️ Database Setup

1. **Install MySQL** if you haven't already
2. **Create a new database**:
   ```sql
   CREATE DATABASE event;
   ```
3. **Create a MySQL user** (optional, you can use root):
   ```sql
   CREATE USER 'eventuser'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON event.* TO 'eventuser'@'localhost';
   FLUSH PRIVILEGES;
   ```

## ⚙️ Configuration

1. **Clone the repository**:
   ```bash
   git clone https://github.com/adi1x7/event-management-system.git
   cd event-management-system
   ```

2. **Update database configuration** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/event
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

   **Default configuration** (if using root user):
   ```properties
   spring.datasource.username=root
   spring.datasource.password=root123
   ```

## 🏃‍♂️ Running the Application

### Method 1: Using Maven

1. **Navigate to the project directory**:
   ```bash
   cd event-management-system
   ```

2. **Clean and compile the project**:
   ```bash
   mvn clean compile
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

### Method 2: Using JAR file

1. **Build the JAR file**:
   ```bash
   mvn clean package
   ```

2. **Run the JAR file**:
   ```bash
   java -jar target/eventsystem-1.0.0.jar
   ```

### Method 3: Using IDE

1. **Open the project** in your preferred IDE (IntelliJ IDEA, Eclipse, VS Code)
2. **Run** the `EventManagementApplication.java` class

## 🌐 Accessing the Application

Once the application is running, you can access it at:

- **Main Application**: http://localhost:8085
- **Admin Dashboard**: http://localhost:8085/admin
- **Events Page**: http://localhost:8085/events

## 📁 Project Structure

```
event-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/example/eventsysten/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── repository/          # Data repositories
│   │   │   └── EventManagementApplication.java
│   │   └── resources/
│   │       ├── new html files/      # Thymeleaf templates
│   │       ├── static/              # Static resources (CSS, images)
│   │       ├── application.properties
│   │       └── data.sql
├── target/                          # Compiled classes and JAR
├── pom.xml                          # Maven configuration
└── README.md
```

## 🔧 API Endpoints

The application provides the following REST endpoints:

### Events
- `GET /events` - List all events
- `POST /events` - Create a new event
- `PUT /events/{id}` - Update an event
- `DELETE /events/{id}` - Delete an event

### Registrations
- `GET /registrations` - List all registrations
- `POST /registrations` - Create a new registration

### Payments
- `GET /payments` - List all payments
- `POST /payments` - Process a payment

### Feedback
- `GET /feedback` - List all feedback
- `POST /feedback` - Submit new feedback

## 🎨 Frontend Pages

- **Home Page** (`index.html`) - Landing page with event listings
- **Admin Dashboard** (`admin.html`) - Administrative interface
- **Event Management** (`admin-events.html`) - Event CRUD operations
- **Registration** (`register.html`) - Event registration form
- **Payment Tracking** (`payment-tracking.html`) - Payment status
- **Feedback System** (`feedback-form.html`, `feedback-list.html`) - Feedback management

## 🚨 Troubleshooting

### Common Issues

1. **Database Connection Error**:
   - Ensure MySQL is running
   - Verify database credentials in `application.properties`
   - Check if the database `event` exists

2. **Port Already in Use**:
   - Change the port in `application.properties`:
     ```properties
     server.port=8086
     ```

3. **Java Version Issues**:
   - Ensure you have Java 17+ installed
   - Check with: `java -version`

4. **Maven Build Issues**:
   - Clean and rebuild: `mvn clean install`
   - Update Maven dependencies: `mvn dependency:resolve`

### Logs

Check the console output for detailed error messages. The application logs SQL queries and Spring Boot startup information.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -am 'Add feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 👥 Support

If you encounter any issues or have questions:

1. Check the troubleshooting section above
2. Review the application logs
3. Create an issue on GitHub with detailed information

## 🔄 Updates

To update the application:

1. Pull the latest changes: `git pull origin main`
2. Update dependencies: `mvn clean install`
3. Restart the application

---

**Happy Event Managing! 🎉** 