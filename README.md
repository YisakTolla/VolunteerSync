# VolunteerSync

A modern web application that connects volunteers with local organizations through intelligent matching. Built with React frontend and Spring Boot backend.

## 🌟 Features

### For Volunteers
- **Smart Profile Creation**: Build comprehensive profiles with skills, interests, and availability
- **Intelligent Discovery**: AI-powered search and filtering to find perfect opportunities
- **Easy Application Process**: Apply to opportunities and connect with organizations seamlessly
- **Impact Tracking**: Monitor volunteer hours, achievements, and community impact

### For Organizations
- **Organization Verification**: Complete registration and verification process
- **Event Management**: Create one-time events and recurring volunteer opportunities
- **Application Management**: Review volunteers, conduct interviews, and build teams
- **Coordination Tools**: Schedule activities, track contributions, and generate reports

### Platform Features
- **🎯 Smart Matching**: AI-powered algorithm connecting volunteers with relevant opportunities
- **📱 Mobile Friendly**: Responsive design that works on desktop, tablet, and mobile
- **🛡️ Safe & Secure**: Organization verification and safety guidelines
- **🌍 Global Reach**: Connect worldwide or focus on local community

## 🏗️ Tech Stack

### Frontend
- **React 19.1.0** - Modern UI library
- **React Router DOM 7.6.3** - Client-side routing
- **Axios 1.10.0** - HTTP client for API requests
- **Lucide React 0.525.0** - Icon library
- **Vite 7.0.0** - Fast build tool and dev server

### Backend
- **Spring Boot 3.3.13** - Java web framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **JWT (JSON Web Tokens)** - Secure authentication
- **Google OAuth2** - Social authentication
- **H2 Database** - Development database
- **PostgreSQL/MySQL** - Production database support
- **Maven** - Dependency management

## 🚀 Getting Started

### Prerequisites
- Node.js 18+ 
- Java 21
- Maven 3.6+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd VolunteerSync
   ```

2. **Backend Setup**
   ```bash
   cd backend
   # Install dependencies and run
   mvn clean install
   mvn spring-boot:run
   ```
   The backend will start on `http://localhost:8080`

3. **Frontend Setup**
   ```bash
   cd frontend
   # Install dependencies
   npm install
   # Start development server
   npm run dev
   ```
   The frontend will start on `http://localhost:5173`

### Database Configuration

The application uses H2 database for development by default. Configuration is in `backend/src/main/resources/application.properties`:

```properties
# H2 Database (Development)
spring.datasource.url=jdbc:h2:file:./data/volunteersync
spring.datasource.username=sa
spring.datasource.password=password

# H2 Console (accessible at http://localhost:8080/h2-console)
spring.h2.console.enabled=true
```

For production, configure PostgreSQL or MySQL in the same file.

## 🎨 Project Structure

```
VolunteerSync/
├── frontend/                 # React frontend
│   ├── src/
│   │   ├── components/      # React components
│   │   ├── pages/          # Page components
│   │   └── styles/         # CSS files
│   ├── package.json
│   └── vite.config.js
├── backend/                 # Spring Boot backend
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   ├── pom.xml            # Maven configuration
│   └── data/              # H2 database files
└── README.md
```

## 🛠️ Development

### Frontend Commands
```bash
cd frontend
npm run dev      # Start development server
npm run build    # Build for production
npm run preview  # Preview production build
npm run lint     # Run ESLint
```

### Backend Commands
```bash
cd backend
mvn spring-boot:run          # Start application
mvn clean compile           # Compile source code
mvn test                   # Run tests
mvn clean install         # Clean, compile, test, and package
```

### Database Management
- **H2 Console**: Access at `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/volunteersync`
  - Username: `sa`
  - Password: `password`

## 🔧 Configuration

### Environment Variables
Create appropriate configuration files for different environments:

**Backend** (`application.properties`):
```properties
# JWT Configuration
app.jwt.secret=your-secret-key
app.jwt.expiration=86400000
app.jwt.refresh-expiration=604800000

# Google OAuth
google.oauth.client-id=your-google-client-id

# Database (for production)
spring.datasource.url=jdbc:postgresql://localhost:5432/volunteersync
spring.datasource.username=your-username
spring.datasource.password=your-password
```

## 🎯 Categories Supported

The platform supports various volunteer categories:
- 🌱 **Environmental** - Conservation and sustainability projects
- 🎓 **Education** - Tutoring and educational support
- 🏥 **Healthcare** - Medical assistance and health programs
- 🏠 **Housing** - Shelter and housing assistance
- 🍽️ **Food Security** - Food banks and nutrition programs
- 🎨 **Arts & Culture** - Cultural events and artistic programs

## 🔒 Security Features

- JWT-based authentication
- Google OAuth2 integration
- Organization verification process
- Secure password handling with Spring Security
- CORS configuration for cross-origin requests
- Input validation and sanitization


## 📦 Production Deployment

1. **Build the frontend:**
   ```bash
   cd frontend
   npm run build
   ```

2. **Package the backend:**
   ```bash
   cd backend
   mvn clean package
   ```

3. **Deploy the JAR file:**
   ```bash
   java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Built with Spring Boot and React
- Icons provided by Lucide React
- Database support from H2, PostgreSQL, and MySQL
- Authentication powered by JWT and Google OAuth2

---

**VolunteerSync** - Connecting hearts, hands, and communities through meaningful volunteer opportunities.