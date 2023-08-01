import sys

import pygame
import os

os.chdir("..")


directories = []
for mod_id in ["biomesoplenty", "blue_skies", "byg", "hexcasting", "railways", "twilightforest"]:
    item_textures = f"common/src/main/resources/assets/{mod_id}/textures/item/"
    directories.extend([
        os.path.join(item_textures, "track"),
        os.path.join(item_textures, "track_incomplete"),
    ])

textures = []

for directory in directories:
    for filename in os.listdir(directory):
        filepath = os.path.join(directory, filename)
        if os.path.isfile(filepath) and filename.endswith(".png") and filename.startswith("track_"):
            if "_wide" in filename or "_narrow" in filename:
                continue
            textures.append(filepath)
        else:
            print(filename, file=sys.stderr)

texture_mappings = {k: k.replace(".png", "_wide.png") for k in textures}
overlay = pygame.image.load("development_assets/track_broad.png")
for fromPath, toPath in texture_mappings.items():
    img = pygame.image.load(fromPath)
    surf = pygame.Surface((img.get_width(), img.get_height()), pygame.SRCALPHA)
    surf.blit(img, (0, 0))
    animated = False
    for i in range(img.get_height() // overlay.get_height()):
        surf.blit(overlay, (0, i * overlay.get_height()))
        if i > 0:
            animated = True
    if animated:
        print(f"Animated: {fromPath}, {toPath}")
    pygame.image.save(surf, toPath)
    # print(f"{fromPath} -> {toPath}")
print(f"{len(texture_mappings)} images written")
