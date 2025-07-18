# Spring AI Document Q&A WebApp

This is a full-stack web application built with **Spring Boot (Java 17)** and **Angular 14** that allows users to upload documents, chunk them into semantic segments, store vector embeddings in an **in-memory Redis** database, and perform question-answering based on the content of those documents using **Spring AI**.

---

## 🧠 Features

- Upload documents (PDF) - Future Scope: Other file formats
- Automatic chunking and vectorization of document content
- Embedding storage in Redis (vector index)
- Question-answering based on document content using LLM (via Spring AI)
- Angular-based user interface
- RESTful API for backend operations
- Using Docker for image creation and deployment on Docker desktop
---

## 🛠️ Tech Stack

| Layer       | Technology                         |
|-------------|-------------------------------------|
| Frontend    | Angular 14, TypeScript, Bootstrap   |
| Backend     | Spring Boot, Java 17, Spring AI     |
| AI Model    | Local LLM (via Ollama or other API) |
| Storage     | Redis (Vector DB + in-memory)       |
| Build Tool  | Maven                               |
| Deploy Tool  | Docker Desktop                               |

---

## 📁 Project Structure

