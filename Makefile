# Variables
MAVEN := mvn
MODULES := common-lib user-service order-service
SERVICES := user-service order-service

# Native build options
NATIVE_OPTS := -Dnative -Dquarkus.native.enabled=true -Dquarkus.native.native-image-xmx=4g -DskipTests

# ===== Targets =====
.PHONY: clean-all clean-user clean-order build-all build-user build-order build-native build-native-all build-native-user build-native-order run-all run-user run-order run-native-all run-native-user run-native-order help

# ===== Main Commands =====
# Clean all services
clean-all:
	@echo "🧹 Cleaning services..."
	@for service in $(MODULES); do \
		if [ -d "$$service" ]; then \
			echo "  Cleaning $$service..."; \
			(cd $$service && $(MAVEN) clean); \
		fi \
	done

clean-user:
	@echo "Cleaning user-service..."
	cd user-service && $(MAVEN) clean

clean-order:
	@echo "Cleaning order-service..."
	cd order-service && $(MAVEN) clean

clean-common:
	@echo "Cleaning common-lib..."
	cd common-lib && $(MAVEN) clean

# Build JVM versions
build-all:
	@echo "🔨 Building services (JVM)..."
	@for service in $(MODULES); do \
		if [ -d "$$service" ]; then \
			echo "  Building $$service..."; \
			(cd $$service && $(MAVEN) clean install -DskipTests); \
		fi \
	done

build-user:
	@echo "🔨 Building user-service (JVM)..."
	cd user-service && $(MAVEN) clean install -DskipTests

build-order:
	@echo "🔨 Building order-service (JVM)..."
	cd order-service && $(MAVEN) clean install -DskipTests

build-common:
	@echo "🔨 Building common library (JVM)..."
	cd common-lib && $(MAVEN) clean install -DskipTests

# Build native executables
build-native-all:
	@echo "⚡ Building native executables..."
	@for service in $(SERVICES); do \
		if [ -d "$$service" ]; then \
			echo "  Building $$service..."; \
			(cd $$service && $(MAVEN) clean package $(NATIVE_OPTS)); \
		fi \
	done

build-native-user:
	@echo "⚡ Building user-service native executables..."
	cd user-service && $(MAVEN) clean package $(NATIVE_OPTS)

build-native-order:
	@echo "⚡ Building order-service native executables..."
	cd order-service && $(MAVEN) clean package $(NATIVE_OPTS)

# Run JVM services
run-all:
	@echo "🚀 Running services (JVM)..."
	@echo "Start each service in separate terminal:"
	@for service in $(SERVICES); do \
		if [ -d "$$service" ]; then \
			echo "  $$service: (cd $$service && java -jar target/quarkus-app/quarkus-run.jar)"; \
		fi \
	done

run-user:
	@echo "🚀 Running user-services (JVM)..."
	cd user-service && java -jar target/quarkus-app/quarkus-run.jar

run-order:
	@echo "🚀 Running order-services (JVM)..."
	cd order-service && java -jar target/quarkus-app/quarkus-run.jar

# Run native executables
run-native-all:
	@echo "⚡ Running native executables..."
	@echo "Start each service in separate terminal:"
	@for service in $(SERVICES); do \
		if [ -d "$$service" ]; then \
			if ls $$service/target/*-runner 1>/dev/null 2>&1; then \
				echo "  $$service: (cd $$service && ./target/*-runner)"; \
			else \
				echo "  $$service: No native executable found. Run 'make build-native-all' first."; \
			fi \
		fi \
	done

run-native-user:
	@echo "⚡ Running user-service native executables..."
	cd user-service && ./target/*-runner

run-native-order:
	@echo "⚡ Running order-service native executables..."
	cd order-service && ./target/*-runner

# ===== Help =====
help:
	@echo "📦 Quarkus Services Makefile"
	@echo ""
	@echo "Commands:"
	@echo "  clean-all     		- Clean all services"
	@echo "  build-all         	- Build JVM versions"
	@echo "  build-native-all   - Build native executables"
	@echo "  run-all            - Show commands to run JVM services"
	@echo "  run-native-all     - Show commands to run native services"
	@echo ""
	@echo "Individual builds:"
	@echo "  user			- Build only user-service (JVM)"
	@echo "  order      	- Build only order-service (JVM)"
	@echo "  native-user 	- Build user-service native"
	@echo "  native-order 	- Build order-service native"
	@echo ""
	@echo "Examples:"
	@echo "  make clean-all build-native-all	# Build all native"
	@echo "  make build-native-user   			# Build user service native"
	@echo "  make run-native-all            	# Run all native services"
	@echo "  make run-native-user           	# Run native user services"

# Default target
.DEFAULT_GOAL := help