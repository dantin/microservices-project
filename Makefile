
SOURCE_DIR := $(shell pwd)
CORE_PATH := microservices/core

CORE_SERVICES := $(shell ls $(SOURCE_DIR)/$(CORE_PATH))

.PHONY: package
package:
	@echo "publish to local maven repository"
	@$(foreach s,$(CORE_SERVICES),\
		echo "publish $(s)"; \
		cd $(SOURCE_DIR)/$(CORE_PATH)/$(s); \
		./gradlew clean publishToMavenLocal; \
	)

.PHONY: clean
clean:
	@echo "clean project"
	@find . -type d \( -name '.idea' -o -name '.gradle' -o -name 'build' \) -exec rm -rf '{}' '+'
	@find . -type f \( -name "*.ipr" -o -name "*.iws" -o -name "*.iml" \) -delete
