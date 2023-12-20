# Verify that all models with a "loader": "forge:obj" entry also have a "porting_lib:loader": "porting_lib:obj" entry
import sys

import pygame
import os
import json

os.chdir("..")


directories = [
    "common/src/main/resources/assets/railways/models",
    "common/src/generated/resources/assets/railways/models"
]

bad_files = []


def recursive_scan(directory: str):
    for filename in os.listdir(directory):
        filepath = os.path.join(directory, filename)
        if os.path.isfile(filepath) and filename.endswith(".json"):
            with open(filepath, "r") as f:
                contents: dict[str] = json.load(f)
                if "loader" in contents and contents["loader"] == "forge:obj":
                    if "porting_lib:loader" not in contents or contents["porting_lib:loader"] != "porting_lib:obj":
                        bad_files.append(filepath)
                        print(f"File {filepath} has a \"loader\": \"forge:obj\" entry,"
                              f" but no \"porting_lib:loader\": \"porting_lib:obj\" entry")
        elif os.path.isdir(filepath):
            recursive_scan(filepath)


for dir in directories:
    recursive_scan(dir)

if len(bad_files) > 0:
    print(f"{len(bad_files)} files have a \"loader\": \"forge:obj\" entry,"
          f" but no \"porting_lib:loader\": \"porting_lib:obj\" entry.")
    if input("Do you want to automatically fix this? (y/n) ").lower() == "y":
        for file in bad_files:
            if "common/src/generated" in file:
                print(f"Skipping {file} because it is in the generated sources directory", file=sys.stderr)
                continue
            # don't want to mess with the formatting of the file,
            # so load the whole thing, identify the line, and replace it
            with open(file, "r") as f:
                contents = f.read()
            lines = contents.split("\n")
            for i, line in enumerate(lines):
                if '"loader"' in line and '"forge:obj"' in line:
                    lines.insert(i + 1, line.replace('"loader"', '"porting_lib:loader"').replace('"forge:obj"', '"porting_lib:obj"'))
                    if not line.endswith(","):
                        lines[i] = line + ","
                    break
            with open(file, "w") as f:
                f.write("\n".join(lines))
        print("Fixed!")
