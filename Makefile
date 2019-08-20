# enable BASH-specific features
SHELL := /bin/bash

SOURCE_DIR := $(shell pwd)

ARCHIVE_FILE := archive.tar.gz
DIRS         := support core composite api
DEPEND_DIRS  := core

MODULES  := $(foreach dir, $(DIRS), $(wildcard microservices/$(dir)/*))
PACKAGES := $(foreach dir, $(DEPEND_DIRS), $(wildcard microservices/$(dir)/*))

.PHONY: package
package:
	@echo "publish to local maven repository"
	@for subdir in $(PACKAGES); \
		do \
		m=`echo $$subdir | cut -d/ -f 3`; \
		echo "publish module $$m to Maven..."; \
		make -C $(SOURCE_DIR)/$$subdir package; \
		done

.PHONY: docker
docker:
	@echo "build docker image"
	@for subdir in $(MODULES); \
		do \
		m=`echo $$subdir | cut -d/ -f 3`; \
		echo "build docker image for $$m..."; \
		make -C $(SOURCE_DIR)/$$subdir docker; \
		done

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
	@echo "clean archive file"
	@rm -f $(ARCHIVE_FILE)
	@echo "clean gradle files"
	@for subdir in $(MODULES); \
		do \
		m=`echo $$subdir | cut -d/ -f 3`; \
		echo "clean module $$m..."; \
		make -C $(SOURCE_DIR)/$$subdir clean; \
		done
	@echo "clean IDE files"
	@find . -type d \( -name '.idea' -o -name '.gradle' -o -name 'build' \) -exec rm -rf '{}' '+'
	@find . -type f \( -name "*.ipr" -o -name "*.iws" -o -name "*.iml" \) -delete
	@echo "clean docker images"
	@docker image prune -f

.PHONY: up
up:
	docker-compose -f docker-compose.yml up --force-recreate

.PHONY: down
down:
	docker-compose -f docker-compose.yml down --remove-orphans
