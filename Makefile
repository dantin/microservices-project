
.PHONY: clean
clean:
	@echo "clean project"
	@find . -type d \( -name '.idea' -o -name '.gradle' \) -exec rm -rf '{}' '+'
	@find . -type f \( -name "*.ipr" -o -name "*.iws" -o -name "*.iml" \) -delete
