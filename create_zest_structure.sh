#!/bin/bash

# Base directory for the source code
BASE_DIR="src/main/java/com/ses/zest"

# Check if the base directory exists, create it if not
if [ ! -d "$BASE_DIR" ]; then
    mkdir -p "$BASE_DIR"
    echo "Created base directory: $BASE_DIR"
else
    echo "Base directory already exists: $BASE_DIR"
fi

# Create vertical slice directories
SLICES="common user event encryption security"
for slice in $SLICES; do
    SLICE_DIR="$BASE_DIR/$slice"
    mkdir -p "$SLICE_DIR"
    echo "Created slice directory: $SLICE_DIR"
done

# Create subdirectories for slices that need them
SUB_DIRS="domain application ports adapters"

for sub_dir in $SUB_DIRS; do
    mkdir -p "$BASE_DIR/user/$sub_dir"
    echo "Created subdirectory: $BASE_DIR/user/$sub_dir"
    mkdir -p "$BASE_DIR/event/$sub_dir"
    echo "Created subdirectory: $BASE_DIR/event/$sub_dir"
    mkdir -p "$BASE_DIR/encryption/$sub_dir"
    echo "Created subdirectory: $BASE_DIR/encryption/$sub_dir"
    mkdir -p "$BASE_DIR/security/$sub_dir"
    echo "Created subdirectory: $BASE_DIR/security/$sub_dir"
done

echo "Folder structure creation complete!"
