# 🧠 Adaptive Interview Simulator – AI-Powered Behavioral Feedback

An intelligent **AI-powered interview simulation platform** designed to help candidates practice interviews, receive real-time feedback, and improve their communication and behavioral skills.

Built with **Spring Boot**, **React (Vite)**, **MySQL**, and **JWT Authentication**, the platform adapts interview questions based on user performance and provides AI-driven feedback for continuous improvement.

---

## 🚀 Tech Stack

### 🖥️ Frontend
- **React (Vite)** for fast and modular UI
- **Tailwind CSS** and **Shadcn/UI** for responsive and modern design
- **Axios** for API communication
- **JWT Integration** for secure user sessions

### ⚙️ Backend
- **Spring Boot** (Java 17+) for REST API development
- **Spring Security** for authentication and role-based authorization
- **MySQL** for data persistence
- **Flyway** for database version control
- **JWT Tokens** for secure and stateless authentication

---

## 🧩 Core Features

- 🔐 **User Authentication:** Secure login and registration using JWT-based authentication  
- 👤 **Role Management:** Separate dashboards for Admin and Candidates  
- 🎯 **Adaptive Interview Flow:** Questions adjust based on previous answers and difficulty level  
- 🤖 **AI Behavioral Feedback:** Real-time insights on tone, confidence, and response quality  
- 📊 **Performance Analytics:** Track progress and detailed performance history  
- 🧱 **Database Versioning:** Managed using Flyway for seamless schema updates  
- 💡 **Responsive UI:** Consistent experience across all devices  

---

AdaptiveInterviewSimulator/
│
├── AdaptiveInterviewSimulatorBackend/ # Spring Boot backend
│ ├── src/main/java/com/AdaptiveInterviewSimulator
│ ├── src/main/resources
│ └── pom.xml
│
├── AdaptiveInterviewSimulatorFrontend/ # React frontend
│ ├── src/
│ ├── package.json
│ └── vite.config.js
│
└── README.md## 📁 Project Structure


---

## ⚡ Getting Started

### 1️⃣ Clone the repository
```bash
git clone https://github.com/<your-username>/AdaptiveInterviewSimulator.git
cd AdaptiveInterviewSimulator


2️⃣ Backend Setup
cd AdaptiveInterviewSimulatorBackend
mvn clean install
mvn spring-boot:run

Make sure you update application.properties with your MySQL credentials:

spring.datasource.url=jdbc:mysql://localhost:3306/adaptive_interview
spring.datasource.username=root
spring.datasource.password=root
flyway.enabled=true


3️⃣ Frontend Setup
cd ../AdaptiveInterviewSimulatorFrontend
npm install
npm run dev


Access frontend at: http://localhost:5173

Backend runs on: http://localhost:8080

🧠 Future Enhancements

🗣️ Integrate OpenAI or Hugging Face models for deeper interview feedback

🎥 Add video/audio input for tone and confidence detection

🧾 Export feedback reports as PDF

☁️ Deploy using Docker + AWS or Render

👨‍💻 Author

Sujeet Kumar
📧 [sujeetkumarroy9623@gmail.com
]
🔗 GitHub Profile

🪪 License

This project is licensed under the MIT License – feel free to use and modify with credit.

⭐ If you like this project, don't forget to star the repo!

---

Would you like me to create a **README version with emojis removed and simpler formatting** 
