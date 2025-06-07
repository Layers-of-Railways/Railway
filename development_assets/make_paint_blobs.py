import pygame
import os

pygame.init()


def apply_mask(surface: pygame.Surface, mask: pygame.Surface, dst: tuple[int, int], colorkey: tuple[int, int, int]):
    for x0 in range(mask.get_width()):
        x = x0 + dst[0]
        for y0 in range(mask.get_height()):
            y = y0 + dst[1]

            if mask.get_at((x0, y0))[:3] == colorkey:
                surface.set_at((x, y), (0, 0, 0, 0))


item_model = '''{
    "parent": "item/generated",
    "textures": {
        "layer0": "railways:fluid/paint_blob/<color>"
    }
}'''.replace(' '*4, '\t')


colors = ['brown', 'maroon', 'red', 'vermilion', 'orange', 'granite', 'dripstone', 'ochrum', 'yellow',
          'chartreuse', 'olive_green', 'lime', 'green', 'pine_green', 'cyan', 'sea_green', 'turquoise', 'light_blue', 'blue', 'royal_blue', 'purple',
          'magenta', 'pink', 'white', 'diorite', 'limestone', 'light_gray', 'tuff', 'gray', 'scorchia', 'black',
          'netherite']

base_path = "../common/src/main/resources/assets/railways/"

paint_still = os.path.join(base_path, "textures", "fluid", "paint_still")
paint_blob = os.path.join(base_path, "textures", "fluid", "paint_blob")
item_models = os.path.join(base_path, "models", "item", "palettes", "paint_blob")

paint_blob_mask = pygame.image.load("paint_blob_mask.png")

pygame.image.save(paint_blob_mask, '/tmp/mask.png')

for color in colors:
    fromPath = os.path.join(paint_still, f"{color}.png")
    toPath = os.path.join(paint_blob, f"{color}.png")

    img = pygame.image.load(fromPath)
    surf = pygame.Surface((img.get_width(), img.get_height()), pygame.SRCALPHA)
    surf.blit(img, (0, 0))
    animated = False
    for i in range(img.get_height() // paint_blob_mask.get_height()):
        apply_mask(surf, paint_blob_mask, (0, i * paint_blob_mask.get_height()), (0, 0, 0))
        if i > 0:
            animated = True
    if animated:
        print(f"Animated: {fromPath}, {toPath}")
    pygame.image.save(surf, toPath)

    fromMCMeta = os.path.join(paint_still, f"{color}.png.mcmeta")
    toMCMeta = os.path.join(paint_blob, f"{color}.png.mcmeta")
    os.system(f"cp \"{fromMCMeta}\" \"{toMCMeta}\"")

    item_model_path = os.path.join(item_models, f"{color}.json")
    with open(item_model_path, 'w') as f:
        f.write(item_model.replace("<color>", color))

    # print(f"{fromPath} -> {toPath}")
print(f"{len(colors)} images written")
