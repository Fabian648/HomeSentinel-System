# Variablen für Pfade
BACKEND_DIR = home-sentinel
APP_DIR = mobile-app
SIMULATOR_DIR = sensor-simulator

.PHONY: help build-all run-system stop-system build-backend build-app build-simulator

help:
	@echo "HomeSentinel - Management"
	@echo "-------------------------"
	@echo "make build-all       - Kompiliert alle Komponenten"
	@echo "make run-system      - Startet Docker-Container (Backend, Redis, etc.)"
	@echo "make stop-system     - Stoppt alle Container"
	@echo "make build-backend   - Baut nur das Spring Boot Backend"
	@echo "make build-app       - Baut die Android App (Debug APK)"
	@echo "make build-simulator - Baut den Sensor-Simulator"

build-all: build-backend build-simulator build-app

build-backend:
	cd $(BACKEND_DIR) && ./gradlew build -x test

build-simulator:
	cd $(SIMULATOR_DIR) && ./gradlew build -x test

build-app:
	cd $(APP_DIR) && ./gradlew assembleDebug

run-system:
	docker compose up --build -d

stop-system:
	docker compose down
