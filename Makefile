# ==============================================================================
# Quar-Kos Microservices Makefile
# ==============================================================================

# Variables
MAVEN := mvn
MODULES := common-lib user-service order-service
SERVICES := user-service order-service
QUARKUS := quarkus

# Native build options
NATIVE_OPTS := -Dnative -Dquarkus.native.enabled=true -Dquarkus.native.native-image-xmx=4g -DskipTests

# Parallel execution support
NPROC ?= $(shell nproc 2>/dev/null || echo 4)
MAKE_PARALLEL := -j$(NPROC)

# Colors
BLUE := $(shell printf '\033[0;34m')
GREEN := $(shell printf '\033[0;32m')
YELLOW := $(shell printf '\033[0;33m')
NC := $(shell printf '\033[0m')

# ==============================================================================
# Phony Targets Declaration
# ==============================================================================
.PHONY: all clean clean-all clean-% build build-all build-% build-native \
        build-native-all build-native-% run run-all run-% run-native \
        run-native-all run-native-% debug debug-% test test-all test-% \
        upgrade upgrade-all help

# ==============================================================================
# Helper Functions
# ==============================================================================
define run-maven
	@printf "$(BLUE)$(1) $(2)...$(NC)\n"
	@(cd $(2) && $(MAVEN) $(3) --batch-mode --show-version)
endef

define run-service
	@if [ -f "$(1)/target/quarkus-app/quarkus-run.jar" ]; then \
		printf "$(GREEN)Starting $(1)...$(NC)\n"; \
		cd $(1) && java -jar target/quarkus-app/quarkus-run.jar; \
	else \
		printf "$(YELLOW)No JAR found. Run 'make build' first.$(NC)\n"; \
	fi
endef

define run-native
	@if ls $(1)/target/*-runner 1>/dev/null 2>&1; then \
		printf "$(GREEN)Starting $(1) native...$(NC)\n"; \
		cd $(1) && ./target/*-runner; \
	else \
		printf "$(YELLOW)No native executable. Run 'make build-native' first.$(NC)\n"; \
	fi
endef

# ==============================================================================
# Default Target
# ==============================================================================
.DEFAULT_GOAL := help

# ==============================================================================
# Build Targets
# ==============================================================================
all: build

build: build-all

build-all: build-common build-services

build-common:
	$(call run-maven,🔨 Building common library,common-lib,clean install -DskipTests)

build-services:
	@printf "$(BLUE)🔨 Building services (JVM)...$(NC)\n"
	@for service in $(SERVICES); do \
		if [ -d "$$service" ]; then \
			printf "  $(GREEN)Building $$service...$(NC)\n"; \
			(cd $$service && $(MAVEN) clean install -DskipTests --batch-mode); \
		fi \
	done

build-%:
	@printf "$(BLUE)🔨 Building $*...$(NC)\n"
	$(call run-maven,🔨 Building,$*-service,clean install -DskipTests)

# ==============================================================================
# Native Build Targets
# ==============================================================================
build-native: build-native-all

build-native-all:
	@echo "$(BLUE)⚡ Building native executables...$(NC)"
	@for service in $(SERVICES); do \
		if [ -d "$$service" ]; then \
			echo "  $(GREEN)Building $$service native...$(NC)"; \
			(cd $$service && $(MAVEN) clean package $(NATIVE_OPTS) --batch-mode); \
		fi \
	done

build-native-%:
	@printf "$(BLUE)⚡ Building $* native...$(NC)\n"
	$(call run-maven,⚡ Building,$*-service,clean package $(NATIVE_OPTS))

# ==============================================================================
# Clean Targets
# ==============================================================================
clean: clean-all

clean-all:
	@echo "$(BLUE)🧹 Cleaning all modules...$(NC)"
	@for module in $(MODULES); do \
		if [ -d "$$module" ]; then \
			echo "  $(GREEN)Cleaning $$module...$(NC)"; \
			(cd $$module && $(MAVEN) clean --batch-mode); \
		fi \
	done

clean-%:
	@printf "$(BLUE)🧹 Cleaning $*...$(NC)\n"
	@if [ -d "$*-service" ]; then \
		$(call run-maven,🧹 Cleaning,$*-service,clean); \
	elif [ -d "$*-lib" ]; then \
		$(call run-maven,🧹 Cleaning,$*-lib,clean); \
	else \
		printf "$(YELLOW)Module $* not found$(NC)\n"; \
	fi

# ==============================================================================
# Run Targets
# ==============================================================================
run: run-all

run-all:
	@echo "$(GREEN)🚀 Services ready to run (JVM)$(NC)"
	@echo ""
	@echo "Run each service in separate terminals:"
	@for service in $(SERVICES); do \
		if [ -d "$$service" ]; then \
			echo "  $(BLUE)$$service:$(NC) make run-$$service"; \
		fi \
	done

run-%:
	$(call run-service,$*)

run-native: run-native-all

run-native-all:
	@echo "$(GREEN)⚡ Native executables ready$(NC)"
	@echo ""
	@echo "Run each service in separate terminals:"
	@for service in $(SERVICES); do \
		if [ -d "$$service" ]; then \
			if ls $$service/target/*-runner 1>/dev/null 2>&1; then \
				echo "  $(BLUE)$$service:$(NC) make run-native-$$service"; \
			else \
				echo "  $(YELLOW)$$service:$(NC) No native executable. Run 'make build-native-$$service'"; \
			fi \
		fi \
	done

run-native-%:
	$(call run-native,$*)

# ==============================================================================
# Debug Targets
# ==============================================================================
debug: debug-user debug-order

debug-%:
	@echo "$(GREEN)🐞 Debugging $*...$(NC)"
	cd $* && ./mvnw quarkus:dev

# ==============================================================================
# Test Targets
# ==============================================================================
test: test-all

test-all:
	@echo "$(BLUE)🧪 Running all tests...$(NC)"
	@for module in $(MODULES); do \
		if [ -d "$$module" ]; then \
			echo "  $(GREEN)Testing $$module...$(NC)"; \
			(cd $$module && $(MAVEN) test --batch-mode); \
		fi \
	done

test-%:
	@printf "$(BLUE)🧪 Testing $*...$(NC)\n"
	@if [ -d "$*-service" ]; then \
		$(call run-maven,🧪 Testing,$*-service,test); \
	elif [ -d "$*-lib" ]; then \
		$(call run-maven,🧪 Testing,$*-lib,test); \
	else \
		printf "$(YELLOW)Module $* not found$(NC)\n"; \
	fi

# ==============================================================================
# Upgrade Targets
# ==============================================================================
upgrade: upgrade-all

upgrade-all:
	@echo "$(BLUE)🔄 Updating Quarkus dependencies...$(NC)"
	@for module in $(MODULES); do \
		if [ -d "$$module" ]; then \
			echo "  $(GREEN)Updating $$module...$(NC)"; \
			(cd $$module && $(QUARKUS) update -y); \
		fi \
	done

# ==============================================================================
# Help Target
# ==============================================================================
help:
	@echo ""
	@echo "$(GREEN)📦 Quar-Kos Microservices - Makefile Commands$(NC)"
	@echo ""
	@echo "$(BLUE)Build Commands:$(NC)"
	@echo "  make build              - Build all modules (common-lib + services)"
	@echo "  make build-common       - Build common library only"
	@echo "  make build-<service>    - Build specific service (user, order)"
	@echo "  make build-native       - Build native executables for all services"
	@echo "  make build-native-<svc> - Build native executable for specific service"
	@echo ""
	@echo "$(BLUE)Clean Commands:$(NC)"
	@echo "  make clean              - Clean all modules"
	@echo "  make clean-<module>     - Clean specific module"
	@echo ""
	@echo "$(BLUE)Run Commands:$(NC)"
	@echo "  make run                - Show run instructions"
	@echo "  make run-<service>      - Run specific service (JVM)"
	@echo "  make run-native         - Show run native instructions"
	@echo "  make run-native-<svc>   - Run specific service native executable"
	@echo ""
	@echo "$(BLUE)Debug Commands:$(NC)"
	@echo "  make debug-<service>    - Debug specific service with Quarkus dev mode"
	@echo ""
	@echo "$(BLUE)Test Commands:$(NC)"
	@echo "  make test               - Run tests for all modules"
	@echo "  make test-<module>      - Run tests for specific module"
	@echo ""
	@echo "$(BLUE)Upgrade Commands:$(NC)"
	@echo "  make upgrade            - Update Quarkus dependencies"
	@echo ""
	@echo "$(BLUE)Examples:$(NC)"
	@echo "  make clean build                          # Clean and build all"
	@echo "  make build-native-user                    # Build user service native"
	@echo "  make run-user                             # Run user service"
	@echo "  make test                                 # Run all tests"
	@echo "  make clean build-native-all run-native    # Full native build & run"
	@echo ""