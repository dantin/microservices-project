
SOURCE_DIR := $(shell pwd)

ARCHIVE_FILE := archive.tar.gz

SUPPORT_PATH   := microservices/support
CORE_PATH      := microservices/core
COMPOSITE_PATH := microservices/composite

SUPPORT_SERVICES   := $(shell ls $(SOURCE_DIR)/$(SUPPORT_PATH))
CORE_SERVICES      := $(shell ls $(SOURCE_DIR)/$(CORE_PATH))
COMPOSITE_SERVICES := $(shell ls $(SOURCE_DIR)/$(COMPOSITE_PATH))

.PHONY: package
package:
	@echo "publish to local maven repository"
	@$(foreach s,$(CORE_SERVICES),\
		echo "publish $(s)"; \
		cd $(SOURCE_DIR)/$(CORE_PATH)/$(s); \
		./gradlew clean publishToMavenLocal; \
	)

.PHONY: archive
archive: clean
	@echo "archive project"
	@a=$(ARCHIVE_FILE); \
		tar -pczf ../$$a \
		--exclude=.git \
		--exclude=.DS_Store \
		--exclude=$(ARCHIVE_FILE) .; \
		mv ../$$a .

.PHONY: unarchive
unarchive:
	@tar zxf $(ARCHIVE_FILE)

.PHONY: clean
clean:
	@echo "clean project"
	@rm -f $(ARCHIVE_FILE)
	@$(foreach s,$(SUPPORT_SERVICES),\
		echo "clean $(s)"; \
		cd $(SOURCE_DIR)/$(SUPPORT_PATH)/$(s); \
		./gradlew clean; \
	)
	@$(foreach s,$(CORE_SERVICES),\
		echo "clean $(s)"; \
		cd $(SOURCE_DIR)/$(CORE_PATH)/$(s); \
		./gradlew clean; \
	)
	@$(foreach s,$(COMPOSITE_SERVICES),\
		echo "clean $(s)"; \
		cd $(SOURCE_DIR)/$(COMPOSITE_PATH)/$(s); \
		./gradlew clean; \
	)
	@find . -type d \( -name '.idea' -o -name '.gradle' -o -name 'build' \) -exec rm -rf '{}' '+'
	@find . -type f \( -name "*.ipr" -o -name "*.iws" -o -name "*.iml" \) -delete
