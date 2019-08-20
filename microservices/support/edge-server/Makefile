ROOT_DIR := $(abspath $(dir $(lastword $(MAKEFILE_LIST)))/../../)

include $(ROOT_DIR)/makefile.include

.PHONY: docker
docker: build
	docker build --force-rm -t dantin/edge-server .
