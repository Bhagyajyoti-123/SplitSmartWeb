# SplitSmart Web 💰 — AI-Powered Expense Splitter

A full-stack web application to split group expenses, track balances, and get AI spending insights. Built with Spring Boot (Java) backend and vanilla JS frontend.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Data JPA
- **Database:** H2 (in-memory)
- **AI:** Google Gemini 2.0 Flash API
- **Frontend:** HTML, CSS, Vanilla JavaScript
- **Build:** Maven

## Features

- Create multiple expense groups
- Add members and track who paid what
- Smart debt simplification algorithm
- Category-wise spending breakdown
- AI-powered spending insights via Gemini

## How to Run

```bash
# 1. Add your Gemini API key in:
# src/main/resources/application.properties
# gemini.api.key=YOUR_KEY_HERE

# 2. Build and run
mvn spring-boot:run

# 3. Open browser
# http://localhost:8080
```

## Get Free Gemini API Key

1. Go to https://aistudio.google.com
2. Sign in with Google
3. Click Get API Key → Create API Key
4. Paste in application.properties

## Project Structure

```
src/main/java/com/splitsmart/
├── SplitSmartApplication.java
├── model/         (Group, Person, Expense, SettlementDTO)
├── service/       (SplitService, Repositories)
├── controller/    (SplitController - REST API)
└── ai/            (GeminiService)

src/main/resources/static/
├── index.html
├── css/style.css
└── js/app.js
```
